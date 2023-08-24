package info.mmpa.concoction.input.model.path;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.input.model.ApplicationModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Outline of a modular path to point to different locations in a {@link ApplicationModel}.
 */
@JsonSerialize(using = PathSerializer.class)
public interface PathElement extends Comparable<PathElement> {
	/**
	 * @return Parent path element. Will be {@code null} for the root.
	 */
	@Nullable
	PathElement parent();

	/**
	 * @return Collection of child paths.
	 */
	@Nonnull
	Collection<? extends PathElement> children();

	/**
	 * @return Full path display, which includes all display parts of parents.
	 */
	@Nonnull
	String fullDisplay();

	/**
	 * @return Local display of the current element.
	 */
	@Nonnull
	String localDisplay();
}
