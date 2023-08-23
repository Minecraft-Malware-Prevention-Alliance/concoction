package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.MultiMatchMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A condition that defines multiple sub-condition.
 * Any single one of the sub-condition must match the given input.
 */
public class AnyMultiCondition extends MultiCondition {
	/**
	 * @param conditions
	 * 		Sub-conditions where any single input must match in order to pass.
	 */
	public AnyMultiCondition(@Nonnull List<Condition> conditions) {
		super(MultiMatchMode.ANY, conditions);
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		for (Condition condition : getConditions())
			if (condition.match(frame))
				return true;
		return false;
	}
}
