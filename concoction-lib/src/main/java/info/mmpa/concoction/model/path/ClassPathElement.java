package info.mmpa.concoction.model.path;

import info.mmpa.concoction.model.ModelSource;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A path to a class within a {@link ModelSource}.
 */
public class ClassPathElement extends AbstractPathElement {
	private final Map<String, MethodPathElement> children = new HashMap<>();
	private final SourcePathElement parent;
	private final String className;

	/**
	 * @param parent
	 * 		Parent path element.
	 * @param className
	 * 		Name of the class.
	 */
	public ClassPathElement(@Nonnull SourcePathElement parent, @Nonnull String className) {
		this.parent = parent;
		this.className = className;
	}

	/**
	 * @param methodName
	 * 		Method name.
	 * @param methodDesc
	 * 		Method desc.
	 *
	 * @return New child path to a method within this class.
	 */
	@Nonnull
	public MethodPathElement child(@Nonnull String methodName, @Nonnull String methodDesc) {
		return children.computeIfAbsent(methodName + methodDesc, k -> new MethodPathElement(this, methodName, methodDesc));
	}

	/**
	 * @param method
	 * 		Method declaration.
	 *
	 * @return New child path to a method within this class.
	 */
	@Nonnull
	public MethodPathElement child(@Nonnull MethodNode method) {
		return child(method.name, method.desc);
	}

	/**
	 * @return Class name path element value.
	 */
	@Nonnull
	public String getClassName() {
		return className;
	}

	/**
	 * @return Model source path element value.
	 */
	@Nonnull
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
	public Collection<MethodPathElement> children() {
		return children.values();
	}

	@Nonnull
	@Override
	public String fullDisplay() {
		return parent.fullDisplay() + " : " + localDisplay();
	}

	@Nonnull
	@Override
	public String localDisplay() {
		return className;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ClassPathElement that = (ClassPathElement) o;

		return className.equals(that.className);
	}

	@Override
	public int hashCode() {
		return className.hashCode();
	}

	@Override
	public String toString() {
		return localDisplay();
	}
}
