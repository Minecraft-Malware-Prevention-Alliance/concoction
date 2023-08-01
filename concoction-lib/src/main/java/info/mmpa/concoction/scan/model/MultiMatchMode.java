package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.scan.model.dynamic.entry.*;
import info.mmpa.concoction.scan.model.insn.entry.*;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Collections.unmodifiableList;

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
	public MultiInstruction createMultiInsn(@Nonnull List<InstructionMatchEntry> entries) {
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

	/**
	 * @param entries
	 * 		Entries to wrap into a multi-dynamic matcher subtype.
	 *
	 * @return Instance of multi-dynamic matcher subtype.
	 */
	@Nonnull
	public MultiDynamic createMultiDynamic(@Nonnull List<DynamicMatchEntry> entries) {
		switch (this) {
			case NONE:
				return new NoneMultiDynamic(unmodifiableList(entries));
			case ALL:
				return new AllMultiDynamic(unmodifiableList(entries));
			case ANY:
			default:
				return new AnyMultiDynamic(unmodifiableList(entries));
		}
	}
}
