package info.mmpa.concoction.input.model;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * A model element loaded from a {@link Path}.
 */
public interface PathAttributed {
	/**
	 * @return Attributed path this content was sourced from.
	 */
	@Nonnull
	Path getSourcePath();
}
