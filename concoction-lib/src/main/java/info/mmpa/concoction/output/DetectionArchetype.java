package info.mmpa.concoction.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;

import javax.annotation.Nonnull;

/**
 * Outlines a description of something to detect.
 *
 * @see InstructionsMatchingModel Detection for method instructions.
 */
public class DetectionArchetype implements Comparable<DetectionArchetype> {
	@JsonProperty("level")
	private final SusLevel level;
	@JsonProperty("identifier")
	private final String identifier;
	@JsonProperty("description")
	private final String description;

	/**
	 * @param level
	 * 		Suspicion level of the detection.
	 * @param identifier
	 * 		A unique identifier of the detection.
	 * @param description
	 * 		A description of what the detection means.
	 */
	public DetectionArchetype(@JsonProperty("level") @Nonnull SusLevel level,
							  @JsonProperty("identifier") @Nonnull String identifier,
							  @JsonProperty("description") @Nonnull String description) {
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

		// Description not relevant for equality
		if (level != detection.level) return false;
		return identifier.equals(detection.identifier);
	}

	@Override
	public int hashCode() {
		// Description not relevant for hashing
		int result = level.hashCode();
		result = 31 * result + identifier.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return level.name() + " - " + identifier;
	}
}
