package info.mmpa.concoction.panel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.Concoction;
import info.mmpa.concoction.ConcoctionStep;
import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.impl.PathModelSource;
import info.mmpa.concoction.input.model.path.ClassPathElement;
import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.input.model.path.SourcedPath;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.sink.FeedbackSink;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.dynamic.DynamicScanException;
import info.mmpa.concoction.util.UiUtils;
import info.mmpa.concoction.util.VerticalFlowLayout;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.coley.observables.ObservableInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static info.mmpa.concoction.util.UiUtils.icon;

/**
 * Panel for executing scans.
 */
public class ScanPanel extends JPanel implements ConcoctionStep {
	private static final Logger logger = LoggerFactory.getLogger(ScanPanel.class);
	private static final ResourceBundle bundle = UiUtils.getBundle();
	private final ConcoctionUxContext context;
	private final JLabel lblCurrentItem = new JLabel();
	private final JLabel lblInputs = new JLabel();
	private final JLabel lblInputsMatched = new JLabel();
	private final JLabel lblModels = new JLabel();
	private final JLabel lblModelsMatched = new JLabel();
	private final JPanel contents = new JPanel();
	private final JButton btnScan = new JButton();
	private final JButton btnStop = new JButton();
	private final JProgressBar progressBar = new JProgressBar();
	private final JButton btnExport = new JButton();
	// Scan state
	private final Map<Path, DetectionPanel> pathToDetectionPanels = new HashMap<>();
	private final ObservableInteger inputsWithMatchesOb = new ObservableInteger(0);
	private final ObservableInteger modelsMatchedOb = new ObservableInteger(0);
	private final Set<Path> pathsMatched = new HashSet<>();
	private final Set<DetectionArchetype> modelsMatched = Collections.newSetFromMap(new IdentityHashMap<>());
	private int inputCount;
	private int modelCount;
	private boolean isCancelled;
	private CompletableFuture<NavigableMap<Path, Results>> scanFuture = CompletableFuture.completedFuture(null);

	public ScanPanel(@Nonnull ConcoctionUxContext context) {
		this.context = context;
		initComponents();

		inputsWithMatchesOb.addChangeListener((observable, oldCount, count) -> lblInputsMatched.setText(String.format(bundle.getString("scan.input-match"), count, inputCount)));
		modelsMatchedOb.addChangeListener((observable, oldCount, count) -> lblModelsMatched.setText(String.format(bundle.getString("scan.model-match"), count, modelCount)));

		updateInputs();
	}

	@Override
	public void onShown() {
		// no-op
	}

	@Override
	public void onHidden() {
		stopScan();
	}

	/**
	 * Start a new scan.
	 */
	private void startScan() {
		isCancelled = false;
		btnScan.setEnabled(false);
		progressBar.setValue(0);

		// Clear old results.
		for (Component component : contents.getComponents()) {
			if (component instanceof DetectionPanel) {
				DetectionPanel detectionPanel = (DetectionPanel) component;
				detectionPanel.onClear();
			}
		}
		contents.removeAll();
		contents.repaint();

		// Reset observables and data tracking.
		modelsMatchedOb.setValue(0);
		inputsWithMatchesOb.setValue(0);
		modelsMatchedOb.setValue(0);
		pathToDetectionPanels.clear();
		pathsMatched.clear();
		modelsMatched.clear();

		// Get input/model lists and update counts.
		List<Path> inputPaths = context.getInputPaths();
		List<Path> modelPaths = context.getModelPaths();
		inputCount = inputPaths.size();
		modelCount = modelPaths.size();

		// Reset input match labels.
		updateInputs();

		// Create builder and load inputs/models.
		Concoction builder = Concoction.builder();
		setCurrent(bundle.getString("scan.loading-inputs"));

		progressBar.setMaximum(inputCount);
		for (Path inputPath : inputPaths) {
			try {
				builder.addInput(ArchiveLoadContext.RANDOM_ACCESS_JAR, inputPath);
			} catch (IOException ex) {
				logger.error("Failed loading input from '{}' - Skipped", inputPath.getFileName(), ex);
			}
			progressBar.setValue(progressBar.getValue() + 1);
		}

		progressBar.setValue(0);
		progressBar.setMaximum(modelCount);
		for (Path modelPath : modelPaths) {
			try {
				builder.addScanModel(modelPath);
			} catch (IOException ex) {
				logger.error("Failed loading model from '{}' - Skipped", modelPath.getFileName(), ex);
			}
			progressBar.setValue(progressBar.getValue() + 1);
		}


		// Set progress bar maximum to number of classes to scan.
		int totalInputClasses = builder.getInputModels().values().stream()
				.mapToInt(a -> a.primarySource().classes().size())
				.sum();
		progressBar.setValue(0);
		progressBar.setMaximum(totalInputClasses);

		// Setup feedback listener, which will update the UI with current info.
		builder.withFeedbackSink(new FeedbackSink() {
			@Override
			public boolean isCancelRequested() {
				return isCancelled;
			}

			@Override
			public InstructionFeedbackItemSink openClassFeedbackSink(@Nonnull ClassPathElement classPath) {
				return new InstructionFeedbackItemSink() {
					@Override
					public void onPreScan(@Nonnull ClassNode classNode) {
						setCurrent(UiUtils.filterClassName(classNode.name));
					}

					@Override
					public void onScanError(@Nonnull Throwable t) {
						logger.error("Encountered scan error during instruction phase", t);
					}

					@Override
					public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
						handleDetection(path, type, detection);
					}

					@Override
					public void onCompletion(@Nonnull Results results) {
						int size = results.size();
						if (UiUtils.debug) {
							if (size > 0) {
								logger.info("Insn scan for '{}' complete, found {} matches", classPath.getClassName(), size);
							} else {
								logger.info("Insn scan for '{}' complete, no matches", classPath.getClassName());
							}
						}
						progressBar.setValue(progressBar.getValue() + 1);
					}
				};
			}

			@Override
			public DynamicFeedbackItemSink openDynamicFeedbackSink() {
				progressBar.setIndeterminate(true);
				return new DynamicFeedbackItemSink() {
					@Override
					public void onMethodEnter(@Nonnull List<CallStackFrame> stack, @Nonnull CallStackFrame enteredMethodFrame) {
						setCurrent(UiUtils.filterClassName(enteredMethodFrame.getOwnerName()));
					}

					@Override
					public void onMethodExit(@Nonnull List<CallStackFrame> stack, @Nonnull CallStackFrame exitedMethodFrame) {
						// no-op
					}

					@Override
					public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
						handleDetection(path, type, detection);
					}

					@Override
					public void onCompletion(@Nonnull Results results) {
						if (UiUtils.debug) {
							logger.info("Dynamic scan for complete, found {} results", results.size());
						}
					}
				};
			}

			/**
			 * Updates the UI when detections are made.
			 *
			 * @param path
			 * 		Path to detection's source.
			 * @param type
			 * 		Detection kind.
			 * @param detection
			 * 		Detection instance.
			 */
			private void handleDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
				// Update the number of unique models matched.
				if (modelsMatched.add(type))
					modelsMatchedOb.setValue(modelsMatched.size());

				// Update the number of unique input files matched.
				Path associatedInput = null;
				if (path instanceof SourcedPath) {
					SourcedPath sourcedPath = (SourcedPath) path;
					ModelSource source = sourcedPath.getSource();
					if (source instanceof PathModelSource) {
						PathModelSource pathModelSource = (PathModelSource) source;
						associatedInput = pathModelSource.getSourcePath();
						if (pathsMatched.add(associatedInput))
							inputsWithMatchesOb.setValue(pathsMatched.size());
					}
				}

				// Create or update display in contents scroll view indicating the file has been matched.
				if (associatedInput != null) {
					DetectionPanel panel = pathToDetectionPanels.get(associatedInput);
					if (panel == null) {
						panel = new DetectionPanel(associatedInput, false);
						pathToDetectionPanels.put(associatedInput, panel);
						contents.add(panel);
					}
					panel.addDetectionOfType(type);
				}
			}
		});

		btnStop.setEnabled(true);
		scanFuture = CompletableFuture.supplyAsync(() -> {
			try {
				progressBar.setIndeterminate(false);
				return builder.scan();
			} catch (DynamicScanException ex) {
				throw new CompletionException(ex);
			}
		});
		scanFuture.whenComplete(this::onScanComplete);
	}

	/**
	 * Called when the scan ends.
	 *
	 * @param results
	 * 		Results of scan was successful.
	 * @param error
	 * 		Error if scan failed.
	 */
	private void onScanComplete(@Nullable NavigableMap<Path, Results> results, @Nullable Throwable error) {
		btnExport.setEnabled(results != null);
		btnScan.setEnabled(true);
		btnStop.setEnabled(false);
		progressBar.setIndeterminate(false);
		progressBar.setValue(progressBar.getMaximum());
		if (error != null)
			logger.error("Scan encountered error", error);
		else if (results != null)
			logger.info("Scan completed, {}/{} matched", results.size(), inputCount);
	}

	/**
	 * Stop the current scan.
	 */
	private void stopScan() {
		btnScan.setEnabled(true);
		btnStop.setEnabled(false);
		isCancelled = true;
		scanFuture.cancel(false);
	}

	/**
	 * @return {@code true} when a scan is in-progress.
	 */
	private boolean isScanInProgress() {
		return !scanFuture.isDone();
	}

	/**
	 * Prompts user if they want to navigate away and stop the scan.
	 *
	 * @return {@code true} when user is OK to leave the page.
	 */
	private boolean confirmLeave() {
		int result = JOptionPane.showConfirmDialog(context.getFrame(),
				bundle.getString("scan.stop.warn-message"),
				bundle.getString("scan.stop.warn-title"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return result == JOptionPane.YES_OPTION;
	}

	/**
	 * Update input statistic labels.
	 */
	private void updateInputs() {
		lblInputs.setText(bundle.getString("scan.input-prefix") + " " + inputCount);
		lblModels.setText(bundle.getString("scan.model-prefix") + " " + modelCount);
		lblInputsMatched.setText(String.format(bundle.getString("scan.input-match"), 0, inputCount));
		lblModelsMatched.setText(String.format(bundle.getString("scan.model-match"), 0, modelCount));
	}

	/**
	 * @param text
	 * 		Text of center 'status' label to set.
	 */
	private void setCurrent(@Nullable String text) {
		lblCurrentItem.setText(text);
	}

	private void initComponents() {
		// Base layout
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new FormLayout(
				"default, $lcgap, default:grow, $lcgap, default",
				"default, $lgap, top:default, $lgap, fill:default:grow, $lgap, default"));

		// Title
		JLabel lblTitle = new JLabel();
		lblTitle.setText(bundle.getString("scan-panel.title"));
		lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 22));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

		// Current item
		lblCurrentItem.setMinimumSize(new Dimension(0, 12));

		// Will contain visual output
		JScrollPane scrollWrapper = new JScrollPane(contents);
		VerticalFlowLayout contentLayout = new VerticalFlowLayout(VerticalFlowLayout.TOP);
		contents.setBorder(new EmptyBorder(15, 15, 15, 15));
		contents.setBackground(new Color(0x333333));
		contents.setLayout(contentLayout);

		// Scan buttons
		btnScan.setText(bundle.getString("scan.run"));
		btnStop.setText(bundle.getString("scan.stop"));
		btnScan.setIcon(icon(CarbonIcons.PLAY_FILLED));
		btnStop.setIcon(icon(CarbonIcons.STOP_FILLED));
		btnScan.addActionListener(e -> CompletableFuture.runAsync(this::startScan));
		btnStop.addActionListener(e -> stopScan());
		btnStop.setEnabled(false);

		// Progress bar
		progressBar.setStringPainted(true);

		// Exporting the results
		btnExport.addActionListener(e -> {
			// TODO: Write results to file.
			//  - The UI is for the end-user
			//  - The result file is for us, so probably JSON representation of the final results map
		});
		btnExport.setIcon(icon(CarbonIcons.DOCUMENT_EXPORT));
		btnExport.setText(bundle.getString("scan.export"));
		btnExport.setEnabled(false);

		// Going to the previous panel
		JButton btnPrevious = new JButton();
		btnPrevious.addActionListener(e -> {
			if (isScanInProgress() && confirmLeave())
				context.gotoPrevious();
			else
				context.gotoPrevious();
		});
		btnPrevious.setIcon(icon(CarbonIcons.PREVIOUS_FILLED));
		btnPrevious.setText(bundle.getString("input.previous"));

		// Lay everything out
		add(lblTitle, CC.xywh(1, 1, 5, 1));
		JPanel inputInfo = new JPanel();
		inputInfo.setLayout(new BorderLayout());
		inputInfo.add(BorderLayout.NORTH, lblInputs);
		inputInfo.add(BorderLayout.SOUTH, lblInputsMatched);
		add(inputInfo, CC.xy(1, 3, CC.DEFAULT, CC.TOP));
		add(lblCurrentItem, CC.xy(3, 3, CC.CENTER, CC.CENTER));

		JPanel modelInfo = new JPanel();
		modelInfo.setLayout(new BorderLayout());
		modelInfo.add(BorderLayout.NORTH, lblModels);
		modelInfo.add(BorderLayout.SOUTH, lblModelsMatched);
		add(modelInfo, CC.xy(5, 3, CC.DEFAULT, CC.TOP));

		add(scrollWrapper, CC.xywh(1, 5, 5, 1));

		JPanel scanActions = new JPanel();
		scanActions.setLayout(new FormLayout(
				"default, $lcgap, default",
				"default"));
		scanActions.add(btnScan, CC.xy(1, 1));
		scanActions.add(btnStop, CC.xy(3, 1));

		JPanel inputActions = new JPanel();
		inputActions.setLayout(new FormLayout(
				"default, $lcgap, default",
				"default"));
		inputActions.add(btnPrevious, CC.xy(1, 1));
		inputActions.add(btnExport, CC.xy(3, 1));

		// Wrap bottom bar to align buttons with the layout
		// used in 'TableModelPanel'
		JPanel actionsWrapper = new JPanel();
		actionsWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
		actionsWrapper.setLayout(new FormLayout(
				"left:default, $lcgap, default:grow:fill, $lcgap, right:default",
				"default"));

		actionsWrapper.add(scanActions, CC.xy(1, 1));
		actionsWrapper.add(progressBar, CC.xy(3, 1));
		actionsWrapper.add(inputActions, CC.xy(5, 1));

		add(actionsWrapper, CC.xywh(1, 7, 5, 1));
	}
}
