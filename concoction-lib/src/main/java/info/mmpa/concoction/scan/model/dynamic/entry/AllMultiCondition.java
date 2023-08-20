package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.MultiMatchMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A condition that defines multiple sub-conditions.
 * All the sub-conditions must match the given input all at once.
 */
public class AllMultiCondition extends MultiCondition {
	/**
	 * @param conditions
	 * 		Sub-conditions which must all match inputs in order to pass.
	 */
	public AllMultiCondition(@Nonnull List<Condition> conditions) {
		super(MultiMatchMode.ALL, conditions);
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		return getConditions().stream().allMatch(e -> e.match(frame));
	}
}
