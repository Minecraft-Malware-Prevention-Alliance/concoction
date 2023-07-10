package info.mmpa.concoction.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Iterator that spans multiple sub-iterators.
 *
 * @param <T>
 * 		Iterated value type.
 */
public class MultiIterator<T> implements Iterator<T> {
	private final List<Iterator<T>> iterators;
	private int current;

	/**
	 * New empty multi-iterator.
	 */
	public MultiIterator() {
		this(new ArrayList<>());
	}

	/**
	 * New multi-iterator wrapping existing iterators.
	 *
	 * @param iterators
	 * 		Iterators to wrap.
	 */
	public MultiIterator(@Nonnull List<Iterator<T>> iterators) {
		this.iterators = iterators;
	}

	/**
	 * @param iterator
	 * 		Iterator to add.
	 *
	 * @return Self.
	 */
	@Nonnull
	public MultiIterator<T> add(@Nonnull Iterator<T> iterator) {
		iterators.add(iterator);
		return this;
	}

	/**
	 * @param index
	 * 		Index to insert into.
	 * @param iterator
	 * 		Iterator to add.
	 *
	 * @return Self.
	 */
	@Nonnull
	public MultiIterator<T> add(int index, @Nonnull Iterator<T> iterator) {
		iterators.add(index, iterator);
		return this;
	}

	@Override
	public boolean hasNext() {
		int max = iterators.size();
		while (current < max && !iterators.get(current).hasNext())
			current++;
		return current < max;
	}

	@Override
	public T next() {
		int max = iterators.size();
		while (current < max && !iterators.get(current).hasNext())
			current++;
		return iterators.get(current).next();
	}
}
