package info.mmpa.concoction.input.io.impl;

import info.mmpa.concoction.input.io.ClassesAndFiles;
import info.mmpa.concoction.input.io.InputReader;
import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Source reader from raw bytes.
 */
public class RawInputReader implements InputReader<byte[]> {
	@Nonnull
	@Override
	public ClassesAndFiles from(@Nonnull ArchiveLoadContext context, @Nonnull byte[] input) throws IOException {
		return context.reader().read(input);
	}
}
