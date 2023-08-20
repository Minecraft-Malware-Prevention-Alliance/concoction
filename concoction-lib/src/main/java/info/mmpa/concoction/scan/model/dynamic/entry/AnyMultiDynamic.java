package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.TextMatchMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A dynamic matcher that defines multiple sub-matchers.
 * Any single one of the sub-matchers must match the given input.
 */
public class AnyMultiDynamic extends MultiDynamic {
	/**
	 * @param entries
	 * 		Sub-matchers where any single input must match in order to pass.
	 */
	public AnyMultiDynamic(@Nonnull List<DynamicMatchEntry> entries) {
		super(MultiMatchMode.ANY, entries);
	}

	@Override
	public boolean matchOnEnter(@Nonnull CallStackFrame frame) {
		return getEntries().stream().anyMatch(e -> e.matchOnEnter(frame));
	}

	@Override
	public boolean matchOnExit(@Nonnull CallStackFrame frame) {
		return getEntries().stream().anyMatch(e -> e.matchOnExit(frame));
	}
}
