package info.mmpa.concoction.util;

import software.coley.collections.Lists;
import software.coley.collections.Sets;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Read-only map that delegates to two sub-maps.
 *
 * @param <K>
 * 		Key type.
 * @param <V>
 * 		Value type.
 */
public class SplitMap<K, V> implements Map<K, V> {
	private final Map<K, V> primary;
	private final Map<K, V> secondary;

	/**
	 * @param primary
	 * 		Map to prefer.
	 * @param secondary
	 * 		Fallback map.
	 */
	public SplitMap(@Nonnull Map<K, V> primary, @Nonnull Map<K, V> secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	@Override
	public int size() {
		return primary.size() + secondary.size();
	}

	@Override
	public boolean isEmpty() {
		return primary.isEmpty() && secondary.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return primary.containsKey(key) || secondary.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return primary.containsValue(value) || secondary.containsValue(value);
	}

	@Override
	public V get(Object key) {
		V v = primary.get(key);
		if (v == null)
			v = secondary.get(key);
		return v;
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException("SplitMap is read-only");
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException("SplitMap is read-only");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException("SplitMap is read-only");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("SplitMap is read-only");
	}

	@Nonnull
	@Override
	public Set<K> keySet() {
		// Second arg contents take precedent
		return Sets.combine(secondary.keySet(), primary.keySet());
	}

	@Nonnull
	@Override
	public Collection<V> values() {
		// Second arg contents take precedent
		return Lists.combine(new ArrayList<>(secondary.values()), new ArrayList<>(primary.values()));
	}

	@Nonnull
	@Override
	public Set<Entry<K, V>> entrySet() {
		// Second arg contents take precedent
		return Sets.combine(secondary.entrySet(), primary.entrySet());
	}
}
