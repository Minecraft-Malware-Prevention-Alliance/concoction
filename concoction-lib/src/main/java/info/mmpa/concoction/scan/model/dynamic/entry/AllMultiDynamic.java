package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.TextMatchMode;
import info.mmpa.concoction.scan.model.insn.entry.Instruction;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A dynamic matcher that defines multiple sub-matchers.
 * All the sub-matchers must match the given input all at once.
 */
public class AllMultiDynamic extends MultiDynamic {
	/**
	 * @param entries
	 * 		Sub-matchers which must all match inputs in order to pass.
	 */
	public AllMultiDynamic(@Nonnull List<DynamicMatchEntry> entries) {
		super(MultiMatchMode.ALL, entries);
	}

	@Override
	public boolean matchOnEnter(@Nonnull CallStackFrame frame) {
		return entries.stream().allMatch(e -> e.matchOnEnter(frame));
	}

	@Override
	public boolean matchOnExit(@Nonnull CallStackFrame frame) {
		return entries.stream().allMatch(e -> e.matchOnExit(frame));
	}
}
