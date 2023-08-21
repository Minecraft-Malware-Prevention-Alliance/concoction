package info.mmpa.concoction;

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
import java.util.ResourceBundle;

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
	private final CardLayout layout = new CardLayout();
	private final InputsPanel inputsPanel = new InputsPanel(this);
	private final ModelsPanel modelsPanel = new ModelsPanel(this);
	private final ScanPanel scanPanel = new ScanPanel(this);
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

		// Centers window on screen when shown
		setLocationRelativeTo(getOwner());
	}

	private void showInput() {
		layout.show(getContentPane(), currentCard = CARD_INPUTS);
	}

	private void showModels() {
		layout.show(getContentPane(), currentCard = CARD_MODELS);
	}

	private void showScan() {
		layout.show(getContentPane(), currentCard = CARD_SCAN);
	}

	@Override
	public void gotoNext() {
		switch (currentCard) {
			case CARD_INPUTS:
				showModels();
				break;
			case CARD_MODELS:
				showScan();
				break;
			case CARD_SCAN:
				// no next
				break;
		}
	}

	@Override
	public void gotoPrevious() {
		switch (currentCard) {
			case CARD_INPUTS:
				// no previous
				break;
			case CARD_MODELS:
				showInput();
				break;
			case CARD_SCAN:
				showModels();
				break;
		}
	}

	@Nonnull
	@Override
	public JFrame getFrame() {
		return this;
	}
}
