package info.mmpa.concoction.input.io.impl;

import info.mmpa.concoction.input.io.ClassesAndFiles;
import info.mmpa.concoction.input.io.InputReader;
import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;

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
