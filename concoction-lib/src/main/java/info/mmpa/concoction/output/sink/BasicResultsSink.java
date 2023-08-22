package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Result sink output for scan operations.
 */
public class BasicResultsSink implements ResultsSink {
	// There should only be one instance of a path element to any given location.
	// With this in mind, we can use an identity hash-map for some performance gains.
	private final Map<PathElement, Map<DetectionArchetype, Set<Detection>>> pathDetections = new IdentityHashMap<>();

	@Override
	public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
		pathDetections.computeIfAbsent(path, p -> new IdentityHashMap<>())
				.computeIfAbsent(type, t -> Collections.newSetFromMap(new IdentityHashMap<>()))
				.add(detection);
	}

	@Nonnull
	@Override
	public Results buildResults() {
		// Since path elements are comparable to themselves, we can make a sorted map that
		// yields alphabetic iteration order by wrapping them in a tree-map.
		return new Results(Collections.synchronizedNavigableMap(new TreeMap<>(pathDetections)));
	}
}
