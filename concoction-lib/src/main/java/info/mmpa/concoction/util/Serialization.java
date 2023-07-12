package info.mmpa.concoction.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.mmpa.concoction.scan.model.insn.InstructionMatchEntry;
import info.mmpa.concoction.scan.model.insn.InstructionMatchEntryDeserializer;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Serialization utils.
 */
public class Serialization {
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		InstructionMatchEntryDeserializer deserializer = new InstructionMatchEntryDeserializer();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(InstructionMatchEntry.class, deserializer);
		mapper.registerModule(module);
	}

	/**
	 * @param value
	 * 		Value to serialize.
	 *
	 * @return JSON string of value.
	 *
	 * @throws JsonProcessingException
	 * 		When the value cannot be serialized.
	 */
	@Nonnull
	public static String serialize(@Nonnull Object value) throws JsonProcessingException {
		return mapper.writeValueAsString(value);
	}

	/**
	 * @param text
	 * 		Text representing an {@link InstructionsMatchingModel}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static InstructionsMatchingModel deserializeModel(@Nonnull String text) throws JsonProcessingException {
		return deserialize(InstructionsMatchingModel.class, text);
	}

	/**
	 * @param text
	 * 		Text representing an {@link InstructionMatchEntry}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static InstructionMatchEntry deserializeEntry(@Nonnull String text) throws JsonProcessingException {
		return deserialize(InstructionMatchEntry.class, text);
	}

	/**
	 * @param type
	 * 		Type to deserialize into.
	 * @param text
	 * 		Text representing the target type.
	 * @param <T>
	 * 		Target type to deserialize into.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static <T> T deserialize(@Nullable Class<T> type, @Nonnull String text) throws JsonProcessingException {
		return mapper.readValue(text, type);
	}
}
