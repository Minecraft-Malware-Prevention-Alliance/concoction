package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * Condition that never matches.
 */
public class NoneCondition implements Condition {
	public static final NoneCondition INSTANCE = new NoneCondition();

	private NoneCondition() {
		// deny construction
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		return false;
	}
}
