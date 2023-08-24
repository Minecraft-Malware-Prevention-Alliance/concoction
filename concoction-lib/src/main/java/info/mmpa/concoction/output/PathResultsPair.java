package info.mmpa.concoction.output;

import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.input.model.path.PathSerializer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A pair of the file path of an input, and the associated detections.
 */
public class PathResultsPair {
	private final String path;
	private final Map<String, SortedSet<String>> detections;

	/**
	 * @param path
	 * 		File path to input.
	 * @param detections
	 * 		Detections of input contents.
	 */
	public PathResultsPair(@Nonnull String path, @Nonnull Map<DetectionArchetype, Set<PathElement>> detections) {
		this.path = path;
		this.detections = detections.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().getIdentifier(), e -> e.getValue().stream()
						.map(PathSerializer::toString)
						.collect(Collectors.toCollection(TreeSet::new))));
	}

	/**
	 * @return File path to input.
	 */
	@Nonnull
	public String getPath() {
		return path;
	}

	/**
	 * @return Detections of input contents.
	 */
	@Nonnull
	public Map<String, SortedSet<String>> getDetections() {
		return detections;
	}
}
