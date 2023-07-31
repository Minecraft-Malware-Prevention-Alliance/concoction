package info.mmpa.concoction.output;

import info.mmpa.concoction.input.model.path.PathElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Result sink output for scan operations.
 */
public class ResultsSink {
	private static final Logger logger = LoggerFactory.getLogger(ResultsSink.class);
	// There should only be one instance of a path element to any given location.
	// With this in mind, we can use an identity hash-map for some performance gains.
	private final Map<PathElement, Map<DetectionArchetype, Set<Detection>>> pathDetections = new IdentityHashMap<>();

	/**
	 * @param path
	 * 		Path to item.
	 * @param type
	 * 		Type of detection.
	 * @param detection
	 * 		The detection instance details.
	 */
	public void add(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
		pathDetections.computeIfAbsent(path, p -> new IdentityHashMap<>())
				.computeIfAbsent(type, t -> Collections.newSetFromMap(new IdentityHashMap<>()))
				.add(detection);
	}

	/**
	 * @param path
	 * 		Path to item.
	 * @param t
	 * 		Error that occurred at the item.
	 */
	public void error(@Nonnull PathElement path, @Nonnull Throwable t) {
		logger.error("Error occurred handling scanning at {}", path, t);
	}

	/**
	 * @return Navigable map of path elements to detections found at those path locations.
	 */
	@Nonnull
	public Results buildResults() {
		// Since path elements are comparable to themselves, we can make a sorted map that
		// yields alphabetic iteration order by wrapping them in a tree-map.
		return new Results(Collections.synchronizedNavigableMap(new TreeMap<>(pathDetections)));
	}
}
