package info.mmpa.concoction.input.model.impl;

import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.PathAttributed;
import info.mmpa.concoction.input.model.path.SourcePathElement;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * Model source from a {@link Path}.
 */
public class PathModelSource extends DelegatingModelSource implements PathAttributed {
	private final SourcePathElement path = new SourcePathElement(this);
	private final Path sourcePath;

	/**
	 * @param delegate
	 * 		Source to delegate to.
	 * @param sourcePath
	 * 		Path the model is sourced from.
	 */
	public PathModelSource(@Nonnull ModelSource delegate, @Nonnull Path sourcePath) {
		super(delegate);
		this.sourcePath = sourcePath;
	}

	@Nonnull
	@Override
	public SourcePathElement path() {
		// We override this so that when scanning logic calls path, we get the instance pointing to this source wrapper
		// instead of the one this delegates to. This allows sourced path elements to get this class and thus the
		// source path of the content. The other model source implementations do not have this detail.
		return path;
	}

	@Nonnull
	@Override
	public Path getSourcePath() {
		return sourcePath;
	}
}
