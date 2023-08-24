package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;

import javax.annotation.Nonnull;

/**
 * Outline of common sink capabilities.
 *
 * @see ResultsSink Sink for collecting results.
 * @see FeedbackSink Sink for status updates / user feedback.
 */
public interface CommonSink {
	/**
	 * @param path
	 * 		Path to item.
	 * @param type
	 * 		Type of detection.
	 * @param detection
	 * 		The detection instance details.
	 */
	void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection);
}
