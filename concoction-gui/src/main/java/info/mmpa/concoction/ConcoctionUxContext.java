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
	 *
	 * @return The next step. If there is no next step, then the current step.
	 */
	@Nonnull
	ConcoctionStep gotoNext();

	/**
	 * Goto previous panel.
	 *
	 * @return The previous step. If there is no previous step, then the current step.
	 */
	@Nonnull
	ConcoctionStep gotoPrevious();

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
