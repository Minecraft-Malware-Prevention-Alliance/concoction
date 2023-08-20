package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * Condition that always matches.
 */
public class AnyCondition implements Condition {
	/**
	 * Shared instance.
	 */
	public static final AnyCondition INSTANCE = new AnyCondition();

	private AnyCondition() {
		// deny construction
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		return true;
	}
}
