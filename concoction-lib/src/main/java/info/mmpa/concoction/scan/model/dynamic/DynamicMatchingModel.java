package info.mmpa.concoction.scan.model.dynamic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.output.DetectionArchetype;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Model representing pattern matching for a {@link DetectionArchetype detection archetype} against a series of
 * dynamic indicators in method execution.
 * <br>
 * The model may have one or more variants describing different signature techniques.
 */
@JsonDeserialize(converter = DynamicMatchingModelDeserializingConverter.class)
@JsonSerialize(converter = DynamicMatchingModelSerializingConverter.class)
public class DynamicMatchingModel {
	private final Map<String, DynamicMatchEntry> variants;

	/**
	 * @param variants
	 * 		Map of variants to detect the pattern.
	 */
	public DynamicMatchingModel(@Nonnull Map<String, DynamicMatchEntry> variants) {
		this.variants = variants;
	}

	// TODO: Implement scanning

	/**
	 * @return Map of variants to detect the pattern.
	 */
	@Nonnull
	public Map<String, DynamicMatchEntry> getVariants() {
		return variants;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DynamicMatchingModel that = (DynamicMatchingModel) o;

		return variants.equals(that.variants);
	}

	@Override
	public int hashCode() {
		return variants.hashCode();
	}

	@Override
	public String toString() {
		return "DynamicMatchingModel{variants[" + variants.size() + "]}";
	}
}
