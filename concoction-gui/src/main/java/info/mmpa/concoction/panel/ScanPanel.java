package info.mmpa.concoction.panel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.Concoction;
import info.mmpa.concoction.ConcoctionStep;
import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.util.UiUtils;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static info.mmpa.concoction.util.UiUtils.icon;

/**
 * @author Matt
 */
public class ScanPanel extends JPanel implements ConcoctionStep {
	private static final Logger logger = LoggerFactory.getLogger(ScanPanel.class);
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
	private CompletableFuture<?> scanFuture = CompletableFuture.completedFuture(null);

	public ScanPanel(@Nonnull ConcoctionUxContext context) {
		this.context = context;
		initComponents();
	}

	@Override
	public void onShown() {
		ResourceBundle bundle = UiUtils.getBundle();
		int inputCount = 0;
		int modelCount = 0;
		lblInputs.setText(bundle.getString("scan.input-prefix") + " " + inputCount);
		lblModels.setText(bundle.getString("scan.model-prefix") + " " + modelCount);
		lblInputsMatched.setText(String.format(bundle.getString("scan.input-match"), 0, inputCount));
		lblModelsMatched.setText(String.format(bundle.getString("scan.model-match"), 0, modelCount));
	}

	@Override
	public void onHidden() {
		stopScan();
	}

	/**
	 * Start a new scan.
	 */
	private void startScan() {
		List<Path> inputPaths = context.getInputPaths();
		List<Path> modelPaths = context.getModelPaths();

		Concoction builder = Concoction.builder();

		for (Path inputPath : inputPaths) {
			try {
				builder.addInput(ArchiveLoadContext.RANDOM_ACCESS_JAR, inputPath);
			} catch (IOException ex) {
				logger.error("Failed loading input from '{}' - Skipped", inputPath.getFileName(), ex);
			}
		}
		for (Path modelPath : modelPaths) {
			try {
				builder.addScanModel(modelPath);
			} catch (IOException ex) {
				logger.error("Failed loading model from '{}' - Skipped", modelPath.getFileName(), ex);
			}
		}

		// TODO: Alternative scan call with progress reporting
		//  - Create future of scan
		//  - As items are completed, add a cell to 'contents' showing a summary of what was found per-path
		//      - Scroll to bottom unless user has scrolled up to look at something when new items are added
		//  - Enable btnExport when complete
		//  - Update labels & progressBar:
		//      lblInputsMatched.setText(String.format(bundle.getString("scan.input-match"), inputsWithDetections, inputCount));
		//      lblModelsMatched.setText(String.format(bundle.getString("scan.model-match"), uniqueModelsDetected, modelCount));
	}

	/**
	 * Stop the current scan.
	 */
	private void stopScan() {
		scanFuture.cancel(true);
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
		ResourceBundle bundle = UiUtils.getBundle();
		int result = JOptionPane.showConfirmDialog(context.getFrame(),
				bundle.getString("scan.stop.warn-message"),
				bundle.getString("scan.stop.warn-title"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return result == JOptionPane.YES_OPTION;
	}

	private void initComponents() {
		ResourceBundle bundle = UiUtils.getBundle();

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

		// Will contain visual output
		JScrollPane scrollWrapper = new JScrollPane();
		scrollWrapper.setViewportView(contents);
		contents.setBackground(new Color(0x333333));
		contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));

		// Scan buttons
		btnScan.setText(bundle.getString("scan.run"));
		btnStop.setText(bundle.getString("scan.stop"));
		btnScan.setIcon(icon(CarbonIcons.PLAY_FILLED));
		btnStop.setIcon(icon(CarbonIcons.STOP_FILLED));
		btnScan.addActionListener(e -> startScan());
		btnStop.addActionListener(e -> stopScan());

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

		add(progressBar, CC.xy(3, 7));

		JPanel scanActions = new JPanel();
		scanActions.setLayout(new FormLayout(
				"default, $lcgap, default",
				"default"));
		scanActions.add(btnScan, CC.xy(1, 1));
		scanActions.add(btnStop, CC.xy(3, 1));
		add(scanActions, CC.xy(1, 7));

		JPanel inputActions = new JPanel();
		inputActions.setLayout(new FormLayout(
				"default, $lcgap, default",
				"default"));
		inputActions.add(btnPrevious, CC.xy(1, 1));
		inputActions.add(btnExport, CC.xy(3, 1));
		add(inputActions, CC.xy(5, 7));
	}
}
