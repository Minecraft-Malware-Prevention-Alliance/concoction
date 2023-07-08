package info.mmpa.concoction.input.impl;

import info.mmpa.concoction.input.ClassesAndFiles;
import info.mmpa.concoction.input.InputReader;
import info.mmpa.concoction.input.archive.ArchiveLoadContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Source reader from an NIO path.
 */
public class PathInputReader implements InputReader<Path> {
	@Nonnull
	@Override
	public ClassesAndFiles from(@Nonnull ArchiveLoadContext context, @Nonnull Path input) throws IOException {
		return context.reader().read(input);
	}
}
