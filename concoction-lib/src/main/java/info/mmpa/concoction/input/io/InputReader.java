package info.mmpa.concoction.input.io;

import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.input.model.ModelSource;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Mapper of some given input form to our model representation.
 *
 * @param <S>
 * 		Source input.
 */
public interface InputReader<S> {
	/**
	 * Reads class and file content from the given input and wraps it into a {@link ModelSource}.
	 *
	 * @param context
	 * 		Archive load context.
	 * @param input
	 * 		Input to read from.
	 *
	 * @return Parsed model source from the input.
	 *
	 * @throws IOException
	 * 		When the input cannot be parsed.
	 */
	@Nonnull
	ClassesAndFiles from(@Nonnull ArchiveLoadContext context, @Nonnull S input) throws IOException;
}
