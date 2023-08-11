package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.model.TextMatchMode;

import javax.annotation.Nonnull;

/**
 * Outline of a method location.
 */
@JsonDeserialize(using = MethodLocationDeserializer.class)
@JsonSerialize(using = MethodLocationSerializer.class)
public class MethodLocation {
	private final String className;
	private final String methodName;
	private final String methodDesc;
	private final TextMatchMode classMatchMode;
	private final TextMatchMode methodNameMatchMode;
	private final TextMatchMode methodDescMatchMode;

	/**
	 * @param className
	 * 		Declaring class of method.
	 * @param methodName
	 * 		Method name.
	 * @param methodDesc
	 * 		Method descriptor.
	 * @param classMatchMode
	 * 		Class name text matching technique.
	 * @param methodNameMatchMode
	 * 		Method name text matching technique.
	 * @param methodDescMatchMode
	 * 		Method descriptor text matching technique.
	 */
	public MethodLocation(@Nonnull String className,
						  @Nonnull String methodName,
						  @Nonnull String methodDesc,
						  @Nonnull TextMatchMode classMatchMode,
						  @Nonnull TextMatchMode methodNameMatchMode,
						  @Nonnull TextMatchMode methodDescMatchMode) {
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.classMatchMode = classMatchMode;
		this.methodNameMatchMode = methodNameMatchMode;
		this.methodDescMatchMode = methodDescMatchMode;
	}

	/**
	 * @param frame
	 * 		Frame to check.
	 *
	 * @return {@code true} when this location matches the given frame's location.
	 */
	public boolean match(@Nonnull CallStackFrame frame) {
		if (!classMatchMode.matches(className, frame.getOwnerName())) return false;
		if (!methodNameMatchMode.matches(methodName, frame.getMethodName())) return false;
		return methodDescMatchMode.matches(methodDesc, frame.getMethodDesc());
	}

	/**
	 * @return Declaring class of method.
	 */
	@Nonnull
	public String getClassName() {
		return className;
	}

	/**
	 * @return Method name.
	 */
	@Nonnull
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return Method descriptor.
	 */
	@Nonnull
	public String getMethodDesc() {
		return methodDesc;
	}

	/**
	 * @return Class name text matching technique.
	 */
	@Nonnull
	public TextMatchMode getClassMatchMode() {
		return classMatchMode;
	}

	/**
	 * @return Method name text matching technique.
	 */
	@Nonnull
	public TextMatchMode getMethodNameMatchMode() {
		return methodNameMatchMode;
	}

	/**
	 * @return Method descriptor text matching technique.
	 */
	@Nonnull
	public TextMatchMode getMethodDescMatchMode() {
		return methodDescMatchMode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MethodLocation that = (MethodLocation) o;

		if (!className.equals(that.className)) return false;
		if (!methodName.equals(that.methodName)) return false;
		if (!methodDesc.equals(that.methodDesc)) return false;
		if (classMatchMode != that.classMatchMode) return false;
		if (methodNameMatchMode != that.methodNameMatchMode) return false;
		return methodDescMatchMode == that.methodDescMatchMode;
	}

	@Override
	public int hashCode() {
		int result = className.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + methodDesc.hashCode();
		result = 31 * result + (classMatchMode != null ? classMatchMode.hashCode() : 0);
		result = 31 * result + (methodNameMatchMode != null ? methodNameMatchMode.hashCode() : 0);
		result = 31 * result + (methodDescMatchMode != null ? methodDescMatchMode.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return className + "." + methodName + methodDesc;
	}
}
