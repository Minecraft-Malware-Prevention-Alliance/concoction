package info.mmpa.concoction.scan.model.method;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Base multi-instruction outline.
 *
 * @see AllMultiInstruction
 * @see AnyMultiInstruction
 * @see NoneMultiInstruction
 */
public abstract class MultiInstruction implements InstructionMatchEntry {
	@JsonDeserialize(contentUsing = InstructionMatchEntryDeserializer.class)
	@JsonSerialize(contentUsing = InstructionMatchEntrySerializer.class)
	protected final List<InstructionMatchEntry> entries;
	protected final MultiMatchMode mode;

	/**
	 * @param mode
	 * 		Mode which determines the subtype.
	 * @param entries
	 * 		Instruction matchers to wrap. Behavior changes based on the {@link #getMode() mode}.
	 */
	protected MultiInstruction(@Nonnull MultiMatchMode mode, @Nonnull List<InstructionMatchEntry> entries) {
		this.mode = mode;
		this.entries = entries;
	}

	/**
	 * @return Instruction matchers to wrap. Behavior changes based on the {@link #getMode() mode}.
	 */
	@Nonnull
	public List<InstructionMatchEntry> getEntries() {
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

		MultiInstruction that = (MultiInstruction) o;

		if (!entries.equals(that.entries)) return false;
		return mode == that.mode;
	}

	@Override
	public int hashCode() {
		int result = entries.hashCode();
		result = 31 * result + mode.hashCode();
		return result;
	}

	/**
	 * Mode for subtypes of multi-instruction matchers.
	 */
	public enum MultiMatchMode {
		/**
		 * @see AllMultiInstruction
		 */
		ALL,
		/**
		 * @see AnyMultiInstruction
		 */
		ANY,
		/**
		 * @see NoneMultiInstruction
		 */
		NONE;

		/**
		 * @param entries
		 * 		Entries to wrap into a multi-instruction matcher subtype.
		 *
		 * @return Instance of multi-instruction matcher subtype.
		 */
		@Nonnull
		public MultiInstruction createMulti(List<InstructionMatchEntry> entries) {
			switch (this) {
				case NONE:
					return new NoneMultiInstruction(unmodifiableList(entries));
				case ALL:
					return new AllMultiInstruction(unmodifiableList(entries));
				case ANY:
				default:
					return new AnyMultiInstruction(unmodifiableList(entries));
			}
		}
	}
}
