package info.mmpa.concoction.panel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.util.UiUtils;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Panel representing detections for a given {@link ApplicationModel} in the {@link ScanPanel}.
 * There will be one panel per file.
 */
public class DetectionPanel extends JPanel {
	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, r -> {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		return thread;
	});
	private final Map<PathElement, List<Detection>> detectionsByPath = new IdentityHashMap<>();
	private final Map<DetectionArchetype, Counter> detectionCountByType = new IdentityHashMap<>();
	private final Path associatedInput;
	private JLabel lblDisplay;
	private String nextDisplay = "";

	public DetectionPanel(Path associatedInput) {
		this.associatedInput = associatedInput;
		initComponents();
		executorService.scheduleAtFixedRate(this::updateDisplay, 500, 100, TimeUnit.MILLISECONDS);
	}

	private void updateDisplay() {
		if (!lblDisplay.getText().equals(nextDisplay))
			lblDisplay.setText(nextDisplay);
	}

	public void updateWithDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
		detectionsByPath.computeIfAbsent(path, p -> new ArrayList<>()).add(detection);
		Counter counter = detectionCountByType.computeIfAbsent(type, t -> new Counter());
		counter.count++;
		nextDisplay = associatedInput.getFileName() + "  -> " + (type.getIdentifier() + " : " + counter.count);
	}

	private void initComponents() {
		JPanel wrapper = new JPanel();
		lblDisplay = new JLabel();

		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new FormLayout(
				"default:grow, 3*($lcgap, default)",
				"min"));

		wrapper.setBorder(new LineBorder(Color.gray, 1, true));
		wrapper.setLayout(new BorderLayout());

		lblDisplay.setBackground(new Color(0xcccccc));
		lblDisplay.setBorder(new EmptyBorder(3, 3, 3, 3));
		wrapper.add(lblDisplay, BorderLayout.CENTER);
		add(wrapper, CC.xywh(1, 1, 7, 1));
	}

	/**
	 * Exists as an optimization. Counting from collections is really slow.
	 */
	private static class Counter {
		private int count;
	}
}
