package info.mmpa.concoction.input.model.impl;

import info.mmpa.concoction.input.model.ModelSource;

import javax.annotation.Nonnull;
import java.util.Arrays;
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
		if (!(o instanceof ModelSource)) return false;

		BasicModelSource that = (BasicModelSource) o;

		if (!identifier.equals(that.identifier())) return false;
		if (!mapEquals(classes, that.classes())) return false;
		return mapEquals(files, that.files());
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

	private static boolean mapEquals(@Nonnull Map<String, byte[]> a, @Nonnull Map<String, byte[]> b) {
		// Quick key check so that we don't do a bunch of array comparisons off the bat.
		if (!a.keySet().equals(b.keySet())) return false;

		// We can't do a simple 'a.equals(b)' because of odd interactions between value comparisons of arrays.
		// Don't believe me? Comment out the block below and replace it with:
		//   return a.equals(b);
		for (Map.Entry<String, byte[]> e : a.entrySet()) {
			String name = e.getKey();
			byte[] data = e.getValue();
			byte[] other = b.get(name);
			if (other == null)
				return false;
			if (!Arrays.equals(data, other))
				return false;
		}
		return true;
	}
}
