package info.mmpa.concoction.input.model.impl;

import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.PathAttributed;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * Model source from a {@link Path}.
 */
public class PathModelSource extends DelegatingModelSource implements PathAttributed {
	private final Path path;

	/**
	 * @param delegate
	 * 		Source to delegate to.
	 * @param path
	 * 		Path the model is sourced from.
	 */
	public PathModelSource(@Nonnull ModelSource delegate, @Nonnull Path path) {
		super(delegate);
		this.path = path;
	}

	@Nonnull
	@Override
	public Path getSourcePath() {
		return path;
	}
}
