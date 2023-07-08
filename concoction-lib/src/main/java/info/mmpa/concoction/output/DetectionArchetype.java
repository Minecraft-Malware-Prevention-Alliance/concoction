package info.mmpa.concoction.output;

import javax.annotation.Nonnull;

/**
 * Outlines a 'kind' of detection.
 */
public class DetectionArchetype implements Comparable<DetectionArchetype> {
	// Known detection archetypes
	public static DetectionArchetype EXAMPLE =
			new DetectionArchetype(SusLevel.MAXIMUM, "Example", "If you see this, you're pretty boned");
	// Instance values
	private final SusLevel level;
	private final String identifier;
	private final transient String description;

	/**
	 * @param level
	 * 		Suspicion level of the detection.
	 * @param identifier
	 * 		A unique identifier of the detection.
	 * @param description
	 * 		A description of what the detection means.
	 */
	protected DetectionArchetype(@Nonnull SusLevel level,
							  @Nonnull String identifier,
							  @Nonnull String description) {
		this.level = level;
		this.identifier = identifier;
		this.description = description;
	}

	/**
	 * @return Suspicion level of the detection.
	 */
	@Nonnull
	public SusLevel getLevel() {
		return level;
	}

	/**
	 * @return A unique identifier of the detection.
	 */
	@Nonnull
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return A description of what the detection means.
	 */
	@Nonnull
	public String getDescription() {
		return description;
	}

	@Override
	public int compareTo(DetectionArchetype o) {
		return identifier.compareTo(o.identifier);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DetectionArchetype detection = (DetectionArchetype) o;

		if (level != detection.level) return false;
		return identifier.equals(detection.identifier);
	}

	@Override
	public int hashCode() {
		int result = level.hashCode();
		result = 31 * result + identifier.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return level.name() + " - " + identifier;
	}
}
