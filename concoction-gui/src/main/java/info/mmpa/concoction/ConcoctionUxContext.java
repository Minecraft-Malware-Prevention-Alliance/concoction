package info.mmpa.concoction;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * Outline of UX process.
 */
public interface ConcoctionUxContext {
	/**
	 * Go to next panel.
	 */
	void gotoNext();

	/**
	 * Goto previous panel.
	 */
	void gotoPrevious();

	/**
	 * @return Main window reference.
	 */
	@Nonnull
	JFrame getFrame();
}
