package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.output.Results;

import javax.annotation.Nonnull;

/**
 * Outline of sink operations for collecting results.
 */
public interface ResultsSink extends CommonSink {
	/**
	 * @return Navigable map of path elements to detections found at those path locations.
	 */
	@Nonnull
	Results buildResults();
}
