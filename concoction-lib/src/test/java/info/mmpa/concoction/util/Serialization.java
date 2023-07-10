package info.mmpa.concoction.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.mmpa.concoction.scan.model.method.InstructionMatchEntry;
import info.mmpa.concoction.scan.model.method.InstructionMatchEntryDeserializer;
import info.mmpa.concoction.scan.model.method.InstructionsMatchingModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Serialization utils for testing.
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
	 */
	@Nonnull
	public static String serialize(@Nonnull Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param text
	 * 		Text representing an {@link InstructionsMatchingModel}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static InstructionsMatchingModel deserializeModel(@Nonnull String text) {
		return deserialize(InstructionsMatchingModel.class, text);
	}

	/**
	 * @param text
	 * 		Text representing an {@link InstructionMatchEntry}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static InstructionMatchEntry deserializeEntry(@Nonnull String text) {
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
	public static <T> T deserialize(@Nullable Class<T> type, @Nonnull String text) {
		try {
			return mapper.readValue(text, type);
		} catch (JsonProcessingException ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}
}
