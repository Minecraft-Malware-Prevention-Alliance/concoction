package info.mmpa.concoction.input.archive;

import javax.annotation.Nonnull;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Types of situations that describe how inputs are executed.
 * <p>
 * Different types of loadable input may need to be read with different strategies in order
 * to mirror their exact intended behavior. This is due to the fact that the ZIP spec is very loose on requirements
 * and the JVM implements the spec in different ways across various circumstances.
 * Some inputs may abuse these specific differences, which is why we need to be specific with how we handle loading
 * content from ZIP archives.
 */
public enum ArchiveLoadContext {
	/**
	 * Used when an input is loaded dynamically via {@link ZipFile} or {@link JarFile}.
	 */
	RANDOM_ACCESS_JAR(new ArchiveReader.RandomAccessArchiveReader()),
	/**
	 * Used when an input is treated as a program run via:
	 * <ul>
	 *     <li>{@code java -jar JAR_NAME}</li>
	 *     <li>{@code java -cp JAR_NAME main-class}</li>
	 * </ul>
	 */
	RUNNABLE_JAR(new ArchiveReader.RunnableArchiveReader()),
	/**
	 * Used when an input is loaded dynamically via streaming such as with {@link ZipInputStream} or {@link JarInputStream}.
	 */
	STREAMED_JAR(new ArchiveReader.StreamedArchiveReader());

	private final ArchiveReader reader;

	/**
	 * @param reader
	 * 		A reader to support the given archive context.
	 */
	ArchiveLoadContext(@Nonnull ArchiveReader reader) {
		this.reader = reader;
	}

	/**
	 * @return A reader to support the given archive context.
	 */
	@Nonnull
	public ArchiveReader reader() {
		return reader;
	}
}
