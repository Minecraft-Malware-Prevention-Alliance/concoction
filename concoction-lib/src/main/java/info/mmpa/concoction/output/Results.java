package info.mmpa.concoction.output;

import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.util.MultiIterator;
import software.coley.collections.Sets;
import software.coley.collections.delegate.DelegatingNavigableMap;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Type alias for the extended class, which has a long generic signature.
 * <br>
 * We can also provide some additional custom utility methods.
 */
public class Results extends DelegatingNavigableMap<PathElement, Map<DetectionArchetype, Set<Detection>>>
		implements Iterable<Detection> {
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

	/**
	 * @return Flat set representation of all found detections.
	 */
	@Nonnull
	public NavigableSet<Detection> asNavigableSet() {
		Spliterator<Detection> spliterator = Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
		NavigableSet<Detection> set = StreamSupport.stream(spliterator, false)
				.collect(Collectors.toCollection(TreeSet::new));
		return Collections.unmodifiableNavigableSet(set);
	}

	@Nonnull
	@Override
	public Iterator<Detection> iterator() {
		MultiIterator<Detection> it = new MultiIterator<>();
		for (Map<DetectionArchetype, Set<Detection>> value : values()) {
			for (Set<Detection> detections : value.values()) {
				it.add(detections.iterator());
			}
		}
		return it;
	}
}
