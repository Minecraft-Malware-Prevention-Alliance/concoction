package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.scan.model.MultiMatchMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Base multi-dynamic outline.
 *
 * @see AllMultiDynamic
 * @see AnyMultiDynamic
 * @see NoneMultiDynamic
 */
public abstract class MultiDynamic implements DynamicMatchEntry {
	@JsonDeserialize(contentUsing = DynamicMatchEntryDeserializer.class)
	@JsonSerialize(contentUsing = DynamicMatchEntrySerializer.class)
	protected final List<DynamicMatchEntry> entries;
	protected final MultiMatchMode mode;

	/**
	 * @param mode
	 * 		Mode which determines the subtype.
	 * @param entries
	 * 		Dynamic matchers to wrap. Behavior changes based on the {@link #getMode() mode}.
	 */
	protected MultiDynamic(@Nonnull MultiMatchMode mode, @Nonnull List<DynamicMatchEntry> entries) {
		this.mode = mode;
		this.entries = entries;
	}

	/**
	 * @return Dynamic matchers to wrap. Behavior changes based on the {@link #getMode() mode}.
	 */
	@Nonnull
	public List<DynamicMatchEntry> getEntries() {
		return entries;
	}

	/**
	 * @return Mode which determines the subtype.
	 */
	@Nonnull
	public MultiMatchMode getMode() {
		return mode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MultiDynamic that = (MultiDynamic) o;

		if (!entries.equals(that.entries)) return false;
		return mode == that.mode;
	}

	@Override
	public int hashCode() {
		int result = entries.hashCode();
		result = 31 * result + mode.hashCode();
		return result;
	}
}
