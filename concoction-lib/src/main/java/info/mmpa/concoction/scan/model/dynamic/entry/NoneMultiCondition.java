package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.MultiMatchMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A condition that defines multiple sub-conditions.
 * None the sub-conditions must match the given input.
 * <p>
 * This is mostly useful as a blacklist modifier.
 */
public class NoneMultiCondition extends MultiCondition {
	/**
	 * @param conditions
	 * 		Sub-conditions which must all not match inputs in order to pass.
	 */
	public NoneMultiCondition(@Nonnull List<Condition> conditions) {
		super(MultiMatchMode.NONE, conditions);
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		for (Condition condition : getConditions())
			if (condition.match(frame))
				return false;
		return true;
	}
}
