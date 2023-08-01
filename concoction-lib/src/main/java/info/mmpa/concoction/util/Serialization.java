package info.mmpa.concoction.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.scan.model.dynamic.entry.Condition;
import info.mmpa.concoction.scan.model.dynamic.entry.DynamicMatchEntry;
import info.mmpa.concoction.scan.model.dynamic.entry.MethodLocation;
import info.mmpa.concoction.scan.model.insn.entry.InstructionMatchEntry;
import info.mmpa.concoction.scan.model.insn.entry.InstructionMatchEntryDeserializer;

import javax.annotation.Nonnull;

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
	 * 		Text representing an {@link ScanModel}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static ScanModel deserializeModel(@Nonnull String text) throws JsonProcessingException {
		return deserialize(ScanModel.class, text);
	}

	/**
	 * @param text
	 * 		Text representing an {@link InstructionMatchEntry}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static InstructionMatchEntry deserializeInsnEntry(@Nonnull String text) throws JsonProcessingException {
		return deserialize(InstructionMatchEntry.class, text);
	}

	/**
	 * @param text
	 * 		Text representing an {@link DynamicMatchEntry}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static DynamicMatchEntry deserializeDynamicEntry(@Nonnull String text) throws JsonProcessingException {
		return deserialize(DynamicMatchEntry.class, text);
	}

	/**
	 * @param text
	 * 		Text representing an {@link MethodLocation}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static MethodLocation deserializeMethodLocation(@Nonnull String text) throws JsonProcessingException {
		return deserialize(MethodLocation.class, text);
	}

	/**
	 * @param text
	 * 		Text representing an {@link Condition}.
	 *
	 * @return Deserialized value.
	 */
	@Nonnull
	public static Condition deserializeCondition(@Nonnull String text) throws JsonProcessingException {
		return deserialize(Condition.class, text);
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
	public static <T> T deserialize(@Nonnull Class<T> type, @Nonnull String text) throws JsonProcessingException {
		return mapper.readValue(text, type);
	}
}
