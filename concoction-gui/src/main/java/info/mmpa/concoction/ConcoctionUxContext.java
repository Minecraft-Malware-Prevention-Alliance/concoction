package info.mmpa.concoction;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.nio.file.Path;
import java.util.List;

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
	 * @return List of input paths to scan.
	 */
	@Nonnull
	List<Path> getInputPaths();

	/**
	 * @return List of model paths to match with.
	 */
	@Nonnull
	List<Path> getModelPaths();

	/**
	 * @return Main window reference.
	 */
	@Nonnull
	JFrame getFrame();
}
