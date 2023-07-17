package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.execution.ExecutionContext;

import javax.annotation.Nonnull;

/**
 * Simple representation of a call-stack frame, more commonly seen through {@link StackTraceElement}.
 */
public class CallStackFrame {
	private final String ownerName;
	private final String methodName;
	private final String methodDesc;
	private transient final ExecutionContext<?> ctx;
	private transient String toString;

	public CallStackFrame(@Nonnull ExecutionContext<?> ctx) {
		this.ctx = ctx;
		this.ownerName = ctx.getOwner().getInternalName();
		this.methodName = ctx.getMethod().getName();
		this.methodDesc = ctx.getMethod().getDesc();
	}

	/**
	 * @return Internal class name holding the method.
	 */
	@Nonnull
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return Name of method executed.
	 */
	@Nonnull
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return Descriptor of method executed.
	 */
	@Nonnull
	public String getMethodDesc() {
		return methodDesc;
	}

	/**
	 * @return VM execution context associated with method.
	 */
	@Nonnull
	public ExecutionContext<?> getCtx() {
		return ctx;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CallStackFrame frame = (CallStackFrame) o;

		if (!ownerName.equals(frame.ownerName)) return false;
		if (!methodName.equals(frame.methodName)) return false;
		return methodDesc.equals(frame.methodDesc);
	}

	@Override
	public int hashCode() {
		int result = ownerName.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + methodDesc.hashCode();
		return result;
	}

	@Override
	public String toString() {
		if (toString == null)
			toString = ownerName + '.' + methodName + methodDesc;
		return toString;
	}
}
