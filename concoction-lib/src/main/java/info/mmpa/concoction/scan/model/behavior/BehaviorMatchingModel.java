package info.mmpa.concoction.scan.model.behavior;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.scan.model.MatchingModel;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Model representing pattern matching for a {@link DetectionArchetype detection archetype} against a series of
 * behavioral indicators in method execution.
 * <br>
 * The model may have one or more variants describing different signature techniques.
 */
public class BehaviorMatchingModel implements MatchingModel<BehaviorMatchEntry> {
	private final DetectionArchetype archetype;
	private final Map<String, BehaviorMatchEntry> variants;

	/**
	 * @param archetype
	 * 		Information about what the signature is matching.
	 * @param variants
	 * 		Map of variants to detect the pattern.
	 */
	public BehaviorMatchingModel(@JsonProperty("archetype") @Nonnull DetectionArchetype archetype,
								 @JsonProperty("variants") @Nonnull Map<String, BehaviorMatchEntry> variants) {
		this.archetype = archetype;
		this.variants = variants;
	}

	// TODO: Implement scanning

	@Nonnull
	@Override
	public DetectionArchetype getArchetype() {
		return archetype;
	}

	@Nonnull
	@Override
	public Map<String, BehaviorMatchEntry> getVariants() {
		return variants;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BehaviorMatchingModel that = (BehaviorMatchingModel) o;

		return variants.equals(that.variants);
	}

	@Override
	public int hashCode() {
		return variants.hashCode();
	}

	@Override
	public String toString() {
		return "BehaviorMatchingModel{" +
				"archetype=" + archetype +
				", variants[" + variants.size() + "]}";
	}
}
