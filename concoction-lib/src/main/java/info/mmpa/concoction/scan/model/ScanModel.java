package info.mmpa.concoction.scan.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.scan.model.dynamic.DynamicMatchingModel;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.util.Serialization;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Top level model layout. Contains optional sub-sections describing elements of different scan components.
 */
public class ScanModel {
	@JsonProperty("archetype")
	private final DetectionArchetype detectionArchetype;
	@JsonProperty("code-patterns")
	private final InstructionsMatchingModel instructionsMatchingModel;
	@JsonProperty("code-behaviors")
	private final DynamicMatchingModel dynamicMatchingModel;

	/**
	 * @param detectionArchetype
	 * 		Information about what the signature is matching.
	 * @param instructionsMatchingModel
	 * 		Instruction based signatures.
	 * @param dynamicMatchingModel
	 * 		Dynamic/runtime based signatures.
	 */
	public ScanModel(@JsonProperty("archetype") @Nonnull DetectionArchetype detectionArchetype,
					 @JsonProperty("code-patterns") @Nonnull InstructionsMatchingModel instructionsMatchingModel,
					 @JsonProperty("code-behaviors") @Nonnull DynamicMatchingModel dynamicMatchingModel) {
		this.detectionArchetype = detectionArchetype;
		this.instructionsMatchingModel = instructionsMatchingModel;
		this.dynamicMatchingModel = dynamicMatchingModel;
	}

	/**
	 * @param path
	 * 		Path to json file representing a {@link ScanModel}.
	 *
	 * @return Parsed model from json.
	 *
	 * @throws IOException
	 * 		When the file cannot be read from,
	 * 		or the json is malformed and cannot deserialize into the model format.
	 */
	@Nonnull
	public static ScanModel fromJson(@Nonnull Path path) throws IOException {
		return fromJson(new String(Files.readAllBytes(path)));
	}

	/**
	 * @param json
	 * 		Json to deserialize.
	 *
	 * @return Parsed model from json.
	 *
	 * @throws JsonProcessingException
	 * 		When the json is malformed and cannot deserialize into the model format.
	 */
	@Nonnull
	public static ScanModel fromJson(@Nonnull String json) throws JsonProcessingException {
		return Serialization.deserializeModel(json);
	}

	/**
	 * @return {@code true} when this model has instruction matching components.
	 */
	public boolean hasInstructionModel() {
		return !instructionsMatchingModel.getVariants().isEmpty();
	}

	/**
	 * @return {@code true} when this model has dynamic/runtime matching components.
	 */
	public boolean hasDynamicModel() {
		return !dynamicMatchingModel.getVariants().isEmpty();
	}

	/**
	 * @return Information about what the signature is matching.
	 */
	@Nonnull
	public DetectionArchetype getDetectionArchetype() {
		return detectionArchetype;
	}

	/**
	 * @return Instruction based signatures.
	 */
	@Nonnull
	public InstructionsMatchingModel getInstructionsMatchingModel() {
		return instructionsMatchingModel;
	}

	/**
	 * @return Dynamic/runtime based signatures.
	 */
	@Nonnull
	public DynamicMatchingModel getDynamicMatchingModel() {
		return dynamicMatchingModel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ScanModel scanModel = (ScanModel) o;

		if (!detectionArchetype.equals(scanModel.detectionArchetype)) return false;
		if (!instructionsMatchingModel.equals(scanModel.instructionsMatchingModel)) return false;
		return dynamicMatchingModel.equals(scanModel.dynamicMatchingModel);
	}

	@Override
	public int hashCode() {
		int result = detectionArchetype.hashCode();
		result = 31 * result + instructionsMatchingModel.hashCode();
		result = 31 * result + dynamicMatchingModel.hashCode();
		return result;
	}
}
