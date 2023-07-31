package info.mmpa.concoction.output;

import info.mmpa.concoction.input.model.path.PathElement;

import javax.annotation.Nonnull;

/**
 * Outlines a single detection found by a scanner.
 */
public class Detection implements Comparable<Detection> {
	private final DetectionArchetype archetype;
	private final PathElement path;

	/**
	 * @param archetype
	 * 		Detection archetype.
	 * @param path
	 * 		Path to detection.
	 */
	public Detection(@Nonnull DetectionArchetype archetype, @Nonnull PathElement path) {
		this.archetype = archetype;
		this.path = path;
	}

	/**
	 * @return Detection archetype.
	 */
	@Nonnull
	public DetectionArchetype archetype() {
		return archetype;
	}

	/**
	 * @return Path to detection.
	 */
	@Nonnull
	public PathElement path() {
		return path;
	}

	@Override
	public int compareTo(Detection o) {
		int cmp = path.compareTo(o.path);
		if (cmp == 0)
			cmp = archetype.compareTo(o.archetype);
		return cmp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Detection detection = (Detection) o;

		if (!archetype.equals(detection.archetype)) return false;
		return path.equals(detection.path);
	}

	@Override
	public int hashCode() {
		int result = archetype.hashCode();
		result = 31 * result + path.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Detection{" +
				"archetype=" + archetype +
				", path=" + path +
				'}';
	}
}
