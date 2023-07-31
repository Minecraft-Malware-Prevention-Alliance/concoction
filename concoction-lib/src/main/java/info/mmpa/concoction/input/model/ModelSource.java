package info.mmpa.concoction.input.model;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Basic model unit of classes and files. Typically representing a JAR or directory.
 *
 * @see ApplicationModel Full application model.
 */
public interface ModelSource {
	/**
	 * The identifier of the source, usually based on the name of the file the source was derived from.
	 *
	 * @return Identifier of the source.
	 */
	@Nonnull
	String identifier();

	/**
	 * The classes available in this source. Keys are the internal name of classes.
	 * <ul>
	 *     <li>{@code java/lang/Object} - Package splits are with {@code /}</li>
	 *     <li>{@code java/util/Map$Entry} - Inner classes use {@code $} as separators</li>
	 * </ul>
	 *
	 * @return Map of internal class names to class bytecode.
	 */
	@Nonnull
	Map<String, byte[]> classes();

	/**
	 * The files available in this source. Keys are file paths, relative to the root of the source input.
	 * <br>
	 * For instance if this source models a JAR file with a manifest the key would be {@code META-INF/MANIFEST.MF}.
	 * <br>
	 * Similarly, if the source models a directory, the same path would be resolved from the root directory.
	 *
	 * @return Map of file paths to raw file contents.
	 */
	@Nonnull
	Map<String, byte[]> files();
}
