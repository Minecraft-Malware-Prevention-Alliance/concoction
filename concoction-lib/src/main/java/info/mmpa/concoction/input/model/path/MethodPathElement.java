package info.mmpa.concoction.input.model.path;

import info.mmpa.concoction.input.model.ModelSource;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * A path to a method within a class.
 */
public class MethodPathElement extends AbstractPathElement implements SourcedPath {
	private final ClassPathElement parent;
	private final String methodName;
	private final String methodDesc;

	/**
	 * @param parent
	 * 		Parent path element.
	 * @param methodName
	 * 		Name of the method.
	 * @param methodDesc
	 * 		Descriptor of the method.
	 */
	public MethodPathElement(@Nonnull ClassPathElement parent, @Nonnull String methodName, @Nonnull String methodDesc) {
		this.parent = parent;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}

	/**
	 * @return Method element value's name.
	 */
	@Nonnull
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return Method element value's descriptor.
	 */
	@Nonnull
	public String getMethodDesc() {
		return methodDesc;
	}

	/**
	 * @return Class name path element value.
	 */
	@Nonnull
	public String getClassName() {
		return parent.getClassName();
	}

	@Nonnull
	@Override
	public ModelSource getSource() {
		return parent.getSource();
	}

	@Nonnull
	@Override
	public PathElement parent() {
		return parent;
	}

	@Nonnull
	@Override
	public Collection<? extends PathElement> children() {
		return Collections.emptyList();
	}

	@Nonnull
	@Override
	public String fullDisplay() {
		return parent.fullDisplay() + "." + localDisplay();
	}

	@Nonnull
	@Override
	public String localDisplay() {
		return methodName + methodDesc;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MethodPathElement that = (MethodPathElement) o;

		if (!methodName.equals(that.methodName)) return false;
		return methodDesc.equals(that.methodDesc);
	}

	@Override
	public int hashCode() {
		int result = methodName.hashCode();
		result = 31 * result + methodDesc.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return localDisplay();
	}
}
