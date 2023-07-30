package info.mmpa.concoction.scan.model.behavior;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.output.DetectionArchetype;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Model representing pattern matching for a {@link DetectionArchetype detection archetype} against a series of
 * behavioral indicators in method execution.
 * <br>
 * The model may have one or more variants describing different signature techniques.
 */
@JsonDeserialize(converter = BehaviorMatchingModelDeserializingConverter.class)
@JsonSerialize(converter = BehaviorMatchingModelSerializingConverter.class)
public class BehaviorMatchingModel {
	private final Map<String, BehaviorMatchEntry> variants;

	/**
	 * @param variants
	 * 		Map of variants to detect the pattern.
	 */
	public BehaviorMatchingModel(@Nonnull Map<String, BehaviorMatchEntry> variants) {
		this.variants = variants;
	}

	// TODO: Implement scanning

	/**
	 * @return Map of variants to detect the pattern.
	 */
	@Nonnull
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
		return "BehaviorMatchingModel{variants[" + variants.size() + "]}";
	}
}
