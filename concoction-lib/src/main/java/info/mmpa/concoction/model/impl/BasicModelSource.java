package info.mmpa.concoction.model.impl;

import info.mmpa.concoction.model.ModelSource;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

/**
 * Basic model source implementation.
 */
public class BasicModelSource implements ModelSource {
	private final String identifier;
	private final Map<String, byte[]> classes;
	private final Map<String, byte[]> files;

	/**
	 * @param identifier
	 * 		Identifier of the source.
	 * @param classes
	 * 		Map of internal class names to class bytecode.
	 * @param files
	 * 		Map of file paths to raw file contents.
	 */
	public BasicModelSource(@Nonnull String identifier,
							@Nonnull Map<String, byte[]> classes,
							@Nonnull Map<String, byte[]> files) {
		this.identifier = identifier;
		this.classes = Collections.unmodifiableMap(classes);
		this.files = Collections.unmodifiableMap(files);
	}

	@Nonnull
	@Override
	public String identifier() {
		return identifier;
	}

	@Nonnull
	@Override
	public Map<String, byte[]> classes() {
		return classes;
	}

	@Nonnull
	@Override
	public Map<String, byte[]> files() {
		return files;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BasicModelSource that = (BasicModelSource) o;

		if (!identifier.equals(that.identifier)) return false;
		if (!classes.equals(that.classes)) return false;
		return files.equals(that.files);
	}

	@Override
	public int hashCode() {
		int result = identifier.hashCode();
		result = 31 * result + classes.hashCode();
		result = 31 * result + files.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "BasicModelSource{" +
				"identifier='" + identifier + '\'' +
				", classes=" + classes.size() +
				", files=" + files.size() +
				'}';
	}
}
