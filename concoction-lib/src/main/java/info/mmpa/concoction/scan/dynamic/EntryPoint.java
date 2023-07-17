package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.invoke.Argument;
import dev.xdark.ssvm.invoke.InvocationUtil;
import dev.xdark.ssvm.mirror.member.JavaMethod;
import dev.xdark.ssvm.mirror.type.InstanceClass;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Model of an entry-point.
 */
public class EntryPoint implements Comparable<EntryPoint> {
	private final String className;
	private final String methodName;
	private final String methodDescriptor;
	private final Supplier<Argument[]> argumentSupplier;

	/**
	 * @param className
	 * 		Name of class declaring the entry point method.
	 * @param methodName
	 * 		Entry point method name.
	 * @param methodDescriptor
	 * 		Entry point method descriptor.
	 * @param argumentSupplier
	 * 		Supplier of arguments to pass to the method when invoking with SSVM.
	 */
	public EntryPoint(@Nonnull String className,
					  @Nonnull String methodName,
					  @Nonnull String methodDescriptor,
					  @Nonnull Supplier<Argument[]> argumentSupplier) {
		this.className = className;
		this.methodName = methodName;
		this.methodDescriptor = methodDescriptor;
		this.argumentSupplier = argumentSupplier;
	}

	/**
	 * @param context
	 * 		SSVM context to invoke within.
	 *
	 * @throws ClassNotFoundException
	 * 		When the entry point's declaring class could not be found/loaded.
	 */
	public void invoke(@Nonnull SsvmContext context) throws ClassNotFoundException {
		InstanceClass ownerClass = context.getLoaderHelper().loadClass(className.replace('/', '.'));
		JavaMethod method = ownerClass.getMethod(methodName, methodDescriptor);
		InvocationUtil invoker = context.getInvoker();
		char retType = methodDescriptor.charAt(methodDescriptor.lastIndexOf(')') + 1);
		switch (retType) {
			case 'V':
				invoker.invokeVoid(method, argumentSupplier.get());
				return;
			case 'Z':
			case 'C':
			case 'B':
			case 'S':
			case 'I':
				invoker.invokeInt(method, argumentSupplier.get());
				return;
			case 'F':
				invoker.invokeFloat(method, argumentSupplier.get());
				return;
			case 'J':
				invoker.invokeLong(method, argumentSupplier.get());
				return;
			case 'D':
				invoker.invokeDouble(method, argumentSupplier.get());
				return;
			case '[':
			case 'L':
				invoker.invokeReference(method, argumentSupplier.get());
				return;
			default:
				throw new IllegalStateException("Invalid return type on method: " + methodDescriptor);
		}
	}

	/**
	 * @return Name of class declaring the entry point method.
	 */
	@Nonnull
	public String getClassName() {
		return className;
	}

	/**
	 * @return Entry point method name.
	 */
	@Nonnull
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return Entry point method descriptor.
	 */
	@Nonnull
	public String getMethodDescriptor() {
		return methodDescriptor;
	}

	/**
	 * @return Supplier of arguments to pass to the method when invoking with SSVM.
	 */
	@Nonnull
	public Supplier<Argument[]> getArgumentSupplier() {
		return argumentSupplier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EntryPoint that = (EntryPoint) o;

		if (!className.equals(that.className)) return false;
		if (!methodName.equals(that.methodName)) return false;
		if (!methodDescriptor.equals(that.methodDescriptor)) return false;
		return argumentSupplier.equals(that.argumentSupplier);
	}

	@Override
	public int hashCode() {
		int result = className.hashCode();
		result = 31 * result + methodName.hashCode();
		result = 31 * result + methodDescriptor.hashCode();
		result = 31 * result + argumentSupplier.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return className + "." + methodName + methodDescriptor;
	}

	@Override
	public int compareTo(@Nonnull EntryPoint o) {
		int cmp = className.compareTo(o.className);
		if (cmp != 0){
			cmp = methodName.compareTo(o.methodName);
			if (cmp != 0) cmp = methodDescriptor.compareTo(o.methodDescriptor);
		}
		return cmp;
	}
}
