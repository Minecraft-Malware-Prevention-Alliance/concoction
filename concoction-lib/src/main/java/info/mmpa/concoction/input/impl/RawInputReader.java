package info.mmpa.concoction.input.impl;

import info.mmpa.concoction.input.ClassesAndFiles;
import info.mmpa.concoction.input.InputReader;
import info.mmpa.concoction.input.archive.ArchiveLoadContext;

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
