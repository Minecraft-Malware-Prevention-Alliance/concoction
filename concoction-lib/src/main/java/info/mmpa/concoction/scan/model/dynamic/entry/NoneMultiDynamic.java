package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.insn.entry.Instruction;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A dynamic matcher that defines multiple sub-matchers.
 * None the sub-matchers must match the given input.
 * <p>
 * This is mostly useful as a blacklist modifier.
 * <br>
 * For example, if there is some API that is used fairly often for malicious purposes, except maybe one or two cases,
 * you can declare the desired cases as {@link SingleConditionCheckingDynamic condition matches} and then invert the
 * matching condition by wrapping those matchers with this type.
 */
public class NoneMultiDynamic extends MultiDynamic {
	/**
	 * @param entries
	 * 		Sub-matchers which must all not match inputs in order to pass.
	 */
	public NoneMultiDynamic(@Nonnull List<DynamicMatchEntry> entries) {
		super(MultiMatchMode.NONE, entries);
	}

	@Override
	public boolean matchOnEnter(@Nonnull CallStackFrame frame) {
		return getEntries().stream().noneMatch(e -> e.matchOnEnter(frame));
	}

	@Override
	public boolean matchOnExit(@Nonnull CallStackFrame frame) {
		return getEntries().stream().noneMatch(e -> e.matchOnExit(frame));
	}
}
