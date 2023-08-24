package info.mmpa.concoction;

import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.panel.DetectionPanel;
import info.mmpa.concoction.panel.ScanPanel;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.NavigableMap;

/**
 * Outline of the final step in {@link ConcoctionUxContext}.
 */
public interface ConcoctionEndStep extends ConcoctionStep {
	/**
	 * Configures this step to handle the given results.
	 *
	 * @param results
	 * 		Results of prior scan to handle.
	 */
	void setResults(@Nonnull NavigableMap<Path, Results> results);

	/**
	 * Configures this step to display copies of the given detection panels.
	 *
	 * @param pathToDetectionPanels
	 * 		Panels from the {@link ScanPanel} to clone.
	 */
	void cloneResultDisplays(@Nonnull NavigableMap<Path, DetectionPanel> pathToDetectionPanels);
}
