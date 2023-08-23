package info.mmpa.concoction.panel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.SusLevel;
import info.mmpa.concoction.util.UiUtils;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.carbonicons.CarbonIcons;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Panel representing detections for a given {@link ApplicationModel} in the {@link ScanPanel}.
 * <br>
 * There will be one panel per file.
 */
public class DetectionPanel extends JPanel {
	private static final Color COLOR_HIGHEST = Color.RED;
	private static final Color COLOR_HIGH = Color.decode("#ff6600");
	private static final Color COLOR_MEDIUM = Color.decode("#ffb300");
	private static final Color COLOR_WEAK = Color.decode("#efd349");
	private static final Color COLOR_WEAKEST = Color.decode("#c4b774");
	private static final ResourceBundle bundle = UiUtils.getBundle();
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, r -> {
		Thread thread = new Thread(r);
		thread.setName("Detection-Panel-UI:" + thread.hashCode());
		thread.setDaemon(true);
		return thread;
	});
	private final Map<DetectionArchetype, Counter> detectionCountByType = new IdentityHashMap<>();
	private final Path associatedPath;
	private int lastDetectionTypeCount;
	private SusLevel maxLevel;
	private boolean maxLevelDirty;
	// UI state
	private final JLabel lblThreatIcon = new JLabel();
	private final JLabel lblFileName = new JLabel();
	private final JLabel lblTotalDetections = new JLabel();
	private final JLabel lblModelsMatched = new JLabel();
	private final JLabel lblHighestThreatLevel = new JLabel();
	private JCheckBox chkSelected;
	private Runnable runLabels;
	private Runnable runThreat;

	/**
	 * New detection entry panel.
	 *
	 * @param associatedPath
	 * 		Path of entry.
	 * @param withCheck
	 *        {@code true} to allow user to check this entry. Used via {@link #isChecked()}.
	 */
	public DetectionPanel(@Nonnull Path associatedPath, boolean withCheck) {
		this.associatedPath = associatedPath;

		initComponents(withCheck);
		lblFileName.setText(associatedPath.getFileName().toString());

		// Initial level
		updateThreatLevel(SusLevel.NOTHING_BURGER);

		// Schedule UI updates on interval
		//
		// This keeps our listener delay affect on the scanning process low by making 'to-do' tasks
		// for updating the UI, rather than doing them immediately.
		executorService.scheduleAtFixedRate(this::updateDisplay, 0, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * @param withCheck
	 *        {@code true} to allow user to check this entry. Used via {@link #isChecked()}.
	 *
	 * @return Copy of panel.
	 */
	@Nonnull
	public DetectionPanel copy(boolean withCheck) {
		DetectionPanel panel = new DetectionPanel(associatedPath, withCheck);
		panel.detectionCountByType.putAll(detectionCountByType);
		panel.maxLevel = maxLevel;
		panel.lastDetectionTypeCount = lastDetectionTypeCount;
		if (withCheck)
			panel.chkSelected.setSelected(chkSelected.isSelected());
		panel.lblThreatIcon.setIcon(lblThreatIcon.getIcon());
		panel.lblTotalDetections.setText(lblTotalDetections.getText());
		panel.lblModelsMatched.setText(lblModelsMatched.getText());
		panel.lblHighestThreatLevel.setText(lblHighestThreatLevel.getText());
		return panel;
	}

	/**
	 * Clear UI for closure.
	 */
	public void onClear() {
		detectionCountByType.clear();
		executorService.shutdownNow();
	}

	/**
	 * @return {@code true} when the entry is selected.
	 */
	public boolean isChecked() {
		return chkSelected != null && chkSelected.isSelected();
	}

	/**
	 * Update UI with new stats.
	 *
	 * @param type
	 * 		Type of detection made.
	 */
	public void addDetectionOfType(@Nonnull DetectionArchetype type) {
		Counter counter = detectionCountByType.computeIfAbsent(type, t -> new Counter());
		counter.count++;

		runLabels = () -> {
			// Update total count display
			lblTotalDetections.setText(bundle.getString("scan.entry.total") + " " + counter.count);

			// Update model match count display
			int detectionTypeCount = detectionCountByType.size();
			if (lastDetectionTypeCount != detectionTypeCount) {
				lastDetectionTypeCount = detectionTypeCount;
				lblModelsMatched.setText(bundle.getString("scan.entry.models") + " " + detectionTypeCount);
			}
		};

		// Update threat level display
		updateThreatLevel(type.getLevel());
	}

	/**
	 * Update UI with threat level info.
	 *
	 * @param level
	 * 		New threat level.
	 */
	private void updateThreatLevel(@Nonnull SusLevel level) {
		if (level.isMoreSus(maxLevel)) {
			maxLevelDirty = true;
			maxLevel = level;
		}
	}

	/**
	 * Update UI with threat level info.
	 */
	private void updateThreatLevelUI() {
		String translationKey = "scan.threat." + maxLevel.ordinal();
		Ikon icon;
		Color iconColor;
		switch (maxLevel) {
			case MAXIMUM:
				icon = CarbonIcons.WARNING_ALT_FILLED;
				iconColor = COLOR_HIGHEST;
				break;
			case STRONG:
				icon = CarbonIcons.WARNING_FILLED;
				iconColor = COLOR_HIGH;
				break;
			case MEDIUM:
				icon = CarbonIcons.WARNING;
				iconColor = COLOR_MEDIUM;
				break;
			case WEAK:
				icon = CarbonIcons.INFORMATION_FILLED;
				iconColor = COLOR_WEAK;
				break;
			case NOTHING_BURGER:
				icon = CarbonIcons.INFORMATION;
				iconColor = COLOR_WEAKEST;
				break;
			default:
				// Shouldn't happen
				icon = CarbonIcons.UNKNOWN;
				iconColor = Color.cyan;
				break;
		}

		lblHighestThreatLevel.setText(bundle.getString("scan.entry.threat") + " " + bundle.getString(translationKey));
		lblThreatIcon.setIcon(UiUtils.icon(icon, iconColor));
	}

	/**
	 * Run cached tasks for UI updates.
	 */
	private void updateDisplay() {
		if (runLabels != null) {
			runLabels.run();
			runLabels = null;
		}
		if (maxLevelDirty) {
			maxLevelDirty = false;
			updateThreatLevelUI();
		}
	}

	private void initComponents(boolean withCheck) {
		setBorder(new CompoundBorder(
				new LineBorder(Color.lightGray, 1, true),
				new EmptyBorder(5, 5, 10, 5)
		));
		setLayout(new FormLayout(
				"left:default, $lcgap, left:default, 10px, 3*([120px,default], 35px), default:grow",
				"top:default, 15px, top:default"));

		// Only show checkbox is requested
		if (withCheck) {
			chkSelected = new JCheckBox();
			add(chkSelected, CC.xywh(1, 1, 1, 2, CC.DEFAULT, CC.CENTER));
		}

		lblFileName.setFont(new Font(Font.DIALOG, Font.PLAIN, 22));

		add(lblThreatIcon, CC.xywh(3, 1, 1, 2));
		add(lblFileName, CC.xywh(5, 1, 7, 1));
		add(lblTotalDetections, CC.xy(5, 2));
		add(lblModelsMatched, CC.xy(7, 2));
		add(lblHighestThreatLevel, CC.xy(9, 2));
	}

	/**
	 * Exists as an optimization. Counting from collections is really slow.
	 */
	private static class Counter {
		private int count;
	}
}
