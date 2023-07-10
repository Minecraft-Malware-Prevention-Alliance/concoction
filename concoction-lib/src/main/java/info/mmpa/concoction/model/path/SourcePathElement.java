package info.mmpa.concoction.model.path;

import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A path to a {@link ModelSource} in an {@link ApplicationModel}.
 */
public class SourcePathElement extends AbstractPathElement {
	private final Map<String, ClassPathElement> children = new HashMap<>();
	private final ModelSource source;

	/**
	 * @param source
	 * 		A model source to serve as the path root.
	 */
	public SourcePathElement(@Nonnull ModelSource source) {
		this.source = source;
	}

	/**
	 * @param className
	 * 		Name of class in the {@link ModelSource#classes() source classes map}.
	 *
	 * @return New child path to a class within this model source.
	 */
	@Nonnull
	public ClassPathElement child(@Nonnull String className) {
		return children.computeIfAbsent(className, n -> new ClassPathElement(this, n));
	}

	/**
	 * @return Model source path element value.
	 */
	@Nonnull
	public ModelSource getSource() {
		return source;
	}

	@Nullable
	@Override
	public PathElement parent() {
		// No parent
		return null;
	}

	@Nonnull
	@Override
	public Collection<ClassPathElement> children() {
		return children.values();
	}

	@Nonnull
	@Override
	public String fullDisplay() {
		return localDisplay();
	}

	@Nonnull
	@Override
	public String localDisplay() {
		return source.identifier();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SourcePathElement that = (SourcePathElement) o;

		return source == that.source;
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public String toString() {
		return localDisplay();
	}
}
