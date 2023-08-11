package info.mmpa.concoction.scan.model.dynamic.entry;

import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.value.ObjectValue;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * Condition implementation for object null/not-null parameter matching.
 */
public class NullParameterCondition implements Condition {
	private final int index;
	private final boolean isNull;

	/**
	 * @param index
	 * 		Index to check.
	 * @param isNull
	 * 		Flag to check for null, or not-null.
	 */
	public NullParameterCondition(int index, boolean isNull) {
		this.index = index;
		this.isNull = isNull;
	}

	/**
	 * @return Index to check.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return Flag to check for null, or not-null.
	 */
	public boolean isNull() {
		return isNull;
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		ExecutionContext<?> ctx = frame.getCtx();
		ObjectValue value = ctx.getLocals().loadReference(index);
		return value.isNull() == isNull;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NullParameterCondition that = (NullParameterCondition) o;

		if (index != that.index) return false;
		return isNull == that.isNull;
	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + (isNull ? 1 : 0);
		return result;
	}
}
