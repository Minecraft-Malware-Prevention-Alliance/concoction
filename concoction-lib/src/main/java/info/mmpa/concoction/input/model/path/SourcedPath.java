package info.mmpa.concoction.input.model.path;

import info.mmpa.concoction.input.model.ModelSource;

import javax.annotation.Nonnull;

/**
 * Path element component that has an associated {@link ModelSource}.
 */
public interface SourcedPath extends PathElement {
	/**
	 * @return Model source path element value.
	 */
	@Nonnull
	ModelSource getSource();
}
