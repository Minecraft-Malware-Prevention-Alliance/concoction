package info.mmpa.concoction.output;

import info.mmpa.concoction.model.path.PathElement;
import software.coley.collections.Sets;
import software.coley.collections.delegate.DelegatingNavigableMap;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Type alias for the extended class, which has a long generic signature.
 * <br>
 * We can also provide some additional custom utility methods.
 */
public class Results extends DelegatingNavigableMap<PathElement, Map<DetectionArchetype, Set<Detection>>> {
	/**
	 * @param map
	 * 		Backing results map.
	 */
	public Results(@Nonnull NavigableMap<PathElement, Map<DetectionArchetype, Set<Detection>>> map) {
		super(map);
	}

	/**
	 * @param other
	 * 		Other results to merge.
	 *
	 * @return Merged results.
	 */
	@Nonnull
	public Results merge(@Nonnull Results other) {
		NavigableMap<PathElement, Map<DetectionArchetype, Set<Detection>>> merged = new TreeMap<>(other);
		for (Entry<PathElement, Map<DetectionArchetype, Set<Detection>>> entry : entrySet()) {
			PathElement pathKey = entry.getKey();
			Map<DetectionArchetype, Set<Detection>> archetypeMapValue = entry.getValue();
			merged.merge(pathKey, archetypeMapValue, (a, b) -> {
				Map<DetectionArchetype, Set<Detection>> archetypeSetMap = new IdentityHashMap<>(a);
				for (Entry<DetectionArchetype, Set<Detection>> otherArchetypeEntries : b.entrySet()) {
					DetectionArchetype archetypeKey = otherArchetypeEntries.getKey();
					Set<Detection> detectionsValue = otherArchetypeEntries.getValue();
					archetypeSetMap.merge(archetypeKey, detectionsValue, Sets::combine);
				}
				return archetypeSetMap;
			});
		}
		return new Results(merged);
	}
}
