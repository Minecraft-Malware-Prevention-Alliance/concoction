package info.mmpa.concoction;

import info.mmpa.concoction.panel.EndPanel;
import info.mmpa.concoction.panel.InputsPanel;
import info.mmpa.concoction.panel.ModelsPanel;
import info.mmpa.concoction.panel.ScanPanel;
import info.mmpa.concoction.util.UiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Main window that allows navigation between panels.
 *
 * @see InputsPanel For input selection.
 * @see ModelsPanel For model selection.
 * @see ScanPanel For scanning the inputs with the models.
 */
public class ConcoctionWindow extends JFrame implements ConcoctionUxContext {
	private static final Logger logger = LoggerFactory.getLogger(ConcoctionWindow.class);
	private static final String CARD_INPUTS = "inputs";
	private static final String CARD_MODELS = "models";
	private static final String CARD_SCAN = "scan";
	private static final String CARD_END = "end";
	private final CardLayout layout = new CardLayout();
	private final InputsPanel inputsPanel = new InputsPanel(this);
	private final ModelsPanel modelsPanel = new ModelsPanel(this);
	private final ScanPanel scanPanel = new ScanPanel(this);
	private final EndPanel endPanel = new EndPanel(this);
	private String currentCard;

	/**
	 * New main window.
	 */
	public ConcoctionWindow() {
		initComponents();
	}

	/**
	 * Shows the window.
	 */
	public void showInitial() {
		setVisible(true);
		showInput();
	}

	private void initComponents() {
		ResourceBundle bundle = UiUtils.getBundle();

		// Window properties
		setTitle(bundle.getString("main.title"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(900, 600);
		try {
			InputStream stream = ConcoctionWindow.class.getResourceAsStream("/logo-32.png");
			if (stream != null)
				setIconImage(ImageIO.read(stream));
		} catch (IOException ex) {
			logger.error("Failed to read logo", ex);
		}

		// Layout
		Container contentPane = getContentPane();
		contentPane.setLayout(layout);
		contentPane.add(inputsPanel, CARD_INPUTS);
		contentPane.add(modelsPanel, CARD_MODELS);
		contentPane.add(scanPanel, CARD_SCAN);
		contentPane.add(endPanel, CARD_END);

		// Centers window on screen when shown
		setLocationRelativeTo(getOwner());
	}

	private void showInput() {
		layout.show(getContentPane(), currentCard = CARD_INPUTS);
		inputsPanel.onShown();
	}

	private void showModels() {
		layout.show(getContentPane(), currentCard = CARD_MODELS);
		modelsPanel.onShown();
	}

	private void showScan() {
		layout.show(getContentPane(), currentCard = CARD_SCAN);
		scanPanel.onShown();
	}

	private void showEnd() {
		layout.show(getContentPane(), currentCard = CARD_END);
		endPanel.onShown();
	}

	@Nonnull
	private ConcoctionStep getCurrentStep() {
		for (Component child : getLayeredPane().getComponents())
			if (child.isVisible())
				if (child instanceof ConcoctionStep)
					return (ConcoctionStep) child;
		throw new IllegalStateException("No current concoction step displayed");
	}

	@Nonnull
	@Override
	public ConcoctionStep gotoNext() {
		switch (currentCard) {
			case CARD_INPUTS:
				inputsPanel.onHidden();
				showModels();
				return modelsPanel;
			case CARD_MODELS:
				modelsPanel.onHidden();
				showScan();
				return scanPanel;
			case CARD_SCAN:
				scanPanel.onHidden();
				showEnd();
				return endPanel;
			case CARD_END:
				// no next
				return endPanel;
		}
		return getCurrentStep();
	}

	@Nonnull
	@Override
	public ConcoctionStep gotoPrevious() {
		switch (currentCard) {
			case CARD_INPUTS:
				// no previous
				return inputsPanel;
			case CARD_MODELS:
				modelsPanel.onHidden();
				showInput();
				return inputsPanel;
			case CARD_SCAN:
				scanPanel.onHidden();
				showModels();
				return modelsPanel;
			case CARD_END:
				endPanel.onHidden();
				showScan();
				return scanPanel;
		}
		return getCurrentStep();
	}

	@Nonnull
	@Override
	public List<Path> getInputPaths() {
		return Collections.unmodifiableList(inputsPanel.getModel());
	}

	@Nonnull
	@Override
	public List<Path> getModelPaths() {
		return modelsPanel.getModel().stream()
				.map(ModelsPanel.ScanModelWithPath::getSource)
				.collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public JFrame getFrame() {
		return this;
	}
}
