package info.mmpa.concoction.panel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.ConcoctionEndStep;
import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.PathResultsPair;
import info.mmpa.concoction.util.Serialization;
import info.mmpa.concoction.util.UiUtils;
import info.mmpa.concoction.util.VerticalFlowLayout;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static info.mmpa.concoction.util.UiUtils.icon;

/**
 * End panel for determining what the user wants to do with the results of the scan.
 */
public class EndPanel extends JPanel implements ConcoctionEndStep, DetectionPanel.SelectionListener {
	private static final Logger logger = LoggerFactory.getLogger(EndPanel.class);
	private static final ResourceBundle bundle = UiUtils.getBundle();
	private final ConcoctionUxContext context;
	private final JPanel contents = new JPanel();
	private final NavigableMap<Path, DetectionPanel> pathToDetectionPanels = new TreeMap<>();
	private NavigableMap<Path, Results> results;
	private boolean exported;
	private boolean actedOn;

	public EndPanel(@Nonnull ConcoctionUxContext context) {
		this.context = context;
		initComponents();
	}

	@Override
	public void onShown() {
		// no-op
	}

	@Override
	public void onHidden() {
		// no-op
	}

	@Override
	public void setResults(@Nonnull NavigableMap<Path, Results> results) {
		this.results = results;
		exported = false;
		actedOn = false;
	}

	@Override
	public void cloneResultDisplays(@Nonnull NavigableMap<Path, DetectionPanel> pathToDetectionPanels) {
		NavigableMap<Path, DetectionPanel> localPanelMap = this.pathToDetectionPanels;

		// Clear old panels
		for (DetectionPanel detectionPanel : pathToDetectionPanels.values())
			detectionPanel.onClear();
		localPanelMap.clear();
		contents.removeAll();

		// Create new panels with selection enabled
		for (DetectionPanel originalPanel : pathToDetectionPanels.values()) {
			DetectionPanel copyWithSelection = originalPanel.copy(true);
			copyWithSelection.setSelectionListener(this);
			localPanelMap.put(copyWithSelection.getAssociatedPath(), copyWithSelection);
			contents.add(copyWithSelection);
		}
	}

	@Override
	public void onSelectionChanged(@Nonnull Path path, boolean selected) {
		// no-op: However later we may want to update the UI to update based on these changes
	}

	/**
	 * Deletes files with detections if they are {@link DetectionPanel#isChecked() selected} in this UI.
	 */
	private void onDeleteSelected() {
		actedOn = true;

		List<String> failedDeletions = new ArrayList<>();
		for (DetectionPanel panel : pathToDetectionPanels.values()) {
			if (panel.isChecked() && Files.exists(panel.getAssociatedPath())) {
				Path path = panel.getAssociatedPath();
				try {
					Files.deleteIfExists(path);

					// Change visuals to indicate deletion
					panel.setBackground(Color.decode("#364230"));
					panel.setBorderWithColor(Color.decode("#68b342"));
				} catch (IOException ex) {
					failedDeletions.add(path.toAbsolutePath().toString());
					logger.warn("Failed to delete path: {}", path, ex);
				}
			}
		}

		// Show dialog with list of files not deleted
		if (!failedDeletions.isEmpty())
			new DeletionFailureDialog(context.getFrame(), failedDeletions).setVisible(true);
	}

	/**
	 * Exports detection results to a file.
	 */
	private void onExportResults() {
		exported = true;

		FileDialog dialog = new FileDialog(context.getFrame(), bundle.getString("scan.export"));
		UiUtils.setFileDialogExtensions(dialog, Collections.singletonList(".zip"));
		dialog.setMode(FileDialog.SAVE);
		dialog.setVisible(true);

		String filePath = dialog.getFile();
		if (filePath != null && !filePath.isEmpty()) {
			try {
				filePath = dialog.getDirectory() + filePath;
				FileOutputStream fos = new FileOutputStream(filePath);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				try (ZipOutputStream zos = new ZipOutputStream(bos)) {
					// Write reports to zip with name pattern:  <file-name>.json
					Set<String> usedFileNames = new HashSet<>();
					for (Map.Entry<Path, Results> entry : results.entrySet()) {
						Path path = entry.getKey();
						String fileAbsolutePath = path.toAbsolutePath().toString();
						String fileName = path.getFileName().toString();

						// Prevent duplicate file paths
						int i = 0;
						String zipPathPrefix = fileName;
						while (!usedFileNames.add(zipPathPrefix))
							zipPathPrefix = fileName + " (" + ++i + ")";

						// Simplify output to representation of:
						// "path": "C:/foo/bar/Example.jar",
						// "detections": {
						//   "detection-type-1": [
						//     "path1", "path2", "path3"
						//   ],
						//   "detection-type-2": [
						//     "path3", "path4", "path5"
						//   ]
						// }
						Map<DetectionArchetype, Set<PathElement>> simplifiedDetectionsMap = new IdentityHashMap<>();
						Results fileResults = entry.getValue();
						for (Detection detection : fileResults) {
							DetectionArchetype archetype = detection.archetype();
							simplifiedDetectionsMap.computeIfAbsent(archetype, a -> new TreeSet<>()).add(detection.path());
						}
						PathResultsPair wrapper = new PathResultsPair(fileAbsolutePath, simplifiedDetectionsMap);

						zos.putNextEntry(new ZipEntry(zipPathPrefix + ".json"));
						zos.write(Serialization.serialize(wrapper).getBytes(StandardCharsets.UTF_8));
						zos.closeEntry();
					}
				}
			} catch (IOException ex) {
				logger.error("Failed to write results to file path '{}'", filePath, ex);
			}
		}
	}

	private void initComponents() {

		// Base layout
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new FormLayout(
				"default:grow",
				"top:default, $lgap, fill:default:grow, $lgap, bottom:default"));

		// Configure labels and buttons
		JLabel lblTitle = new JLabel();
		JButton btnExport = new JButton();
		JButton btnDelete = new JButton();
		JButton btnPrevious = new JButton();
		JButton btnDone = new JButton();
		lblTitle.setText(bundle.getString("results-panel.title"));
		lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 22));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		btnExport.setText(bundle.getString("scan.export"));
		btnDelete.setText(bundle.getString("scan.delete"));
		btnPrevious.setText(bundle.getString("input.previous"));
		btnDone.setText(bundle.getString("scan.done"));
		btnExport.setIcon(icon(CarbonIcons.DOCUMENT_EXPORT));
		btnDelete.setIcon(icon(CarbonIcons.CLEAN));
		btnPrevious.setIcon(icon(CarbonIcons.PREVIOUS_FILLED));
		btnDone.setIcon(icon(CarbonIcons.CLOSE));
		btnPrevious.addActionListener(e -> context.gotoPrevious());
		btnDone.addActionListener(e -> {
			// Warn user if they're exiting before doing exporting/removal
			if (!actedOn && !exported) {
				if (JOptionPane.showConfirmDialog(context.getFrame(),
						bundle.getString("scan.exit.warn-no-act-no-export"),
						bundle.getString("scan.exit.warn-no-act-no-export.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
					return;
			} else if (!actedOn) {
				if (JOptionPane.showConfirmDialog(context.getFrame(),
						bundle.getString("scan.exit.warn-no-act"),
						bundle.getString("scan.exit.warn-no-act.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
					return;
			} else if (!exported) {
				if (JOptionPane.showConfirmDialog(context.getFrame(),
						bundle.getString("scan.exit.warn-no-export"),
						bundle.getString("scan.exit.warn-no-export.title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
					return;
			}
			System.exit(0);
		});
		btnExport.addActionListener(e -> CompletableFuture.runAsync(this::onExportResults));
		btnDelete.addActionListener(e -> CompletableFuture.runAsync(this::onDeleteSelected));

		// Lay everything out
		JScrollPane scrollWrapper = new JScrollPane(contents);
		VerticalFlowLayout contentLayout = new VerticalFlowLayout(VerticalFlowLayout.TOP);
		scrollWrapper.setBorder(new LineBorder(Color.decode("#1c1c1c"), 1));
		contents.setBorder(new EmptyBorder(15, 15, 15, 15));
		contents.setBackground(new Color(0x333333));
		contents.setLayout(contentLayout);
		JPanel actionActions = new JPanel();
		actionActions.setLayout(new BoxLayout(actionActions, BoxLayout.X_AXIS));
		actionActions.add(btnExport);
		actionActions.add(btnDelete);
		JPanel navActions = new JPanel();
		navActions.setLayout(new BoxLayout(navActions, BoxLayout.X_AXIS));
		navActions.add(btnPrevious);
		navActions.add(btnDone);
		JPanel bottomBar = new JPanel();
		bottomBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		bottomBar.setLayout(new FormLayout(
				"default, $lcgap, default:grow:fill, $lcgap, default",
				"default"));
		bottomBar.add(actionActions, CC.xy(1, 1));
		bottomBar.add(navActions, CC.xy(5, 1));
		add(lblTitle, CC.xy(1, 1));
		add(scrollWrapper, CC.xy(1, 3));
		add(bottomBar, CC.xy(1, 5));
	}
}
