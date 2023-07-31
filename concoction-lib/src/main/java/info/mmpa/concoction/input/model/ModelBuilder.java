package info.mmpa.concoction.input.model;

import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.input.model.impl.BasicModelBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Builder assistant for creating {@link ApplicationModel} instances.
 */
public interface ModelBuilder {
	/**
	 * Adds a source from a file path to an archive.
	 *
	 * @param context
	 * 		Archive load context.
	 * @param path
	 * 		The path to read content from.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When file contents cannot be read.
	 */
	@Nonnull
	ModelBuilder addSource(@Nonnull ArchiveLoadContext context, @Nonnull Path path) throws IOException;

	/**
	 * Adds a source from raw bytes, representing an archive.
	 *
	 * @param context
	 * 		Archive load context.
	 * @param identifier
	 * 		Identifier to associate with the raw content.
	 * @param raw
	 * 		Raw bytes of some input.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When raw contents cannot be read.
	 */
	@Nonnull
	ModelBuilder addSource(@Nonnull ArchiveLoadContext context, @Nonnull String identifier, @Nonnull byte[] raw) throws IOException;

	/**
	 * Creates the application model from inputs provided to the builder.
	 *
	 * @return Final model built from provided sources.
	 *
	 * @throws InvalidModelException
	 * 		When the model could not be built.
	 */
	@Nonnull
	ApplicationModel build() throws InvalidModelException;

	/**
	 * @return New builder instance.
	 */
	@Nonnull
	static ModelBuilder create() {
		return new BasicModelBuilder();
	}
}
