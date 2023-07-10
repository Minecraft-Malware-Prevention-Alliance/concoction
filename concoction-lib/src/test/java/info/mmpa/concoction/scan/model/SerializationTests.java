package info.mmpa.concoction.scan.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.mmpa.concoction.scan.model.method.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link InstructionMatchEntry} serialization.
 */
public class SerializationTests {
	private static final ObjectMapper mapper = new ObjectMapper();

	@BeforeAll
	static void setup() {
		InstructionMatchEntryDeserializer deserializer = new InstructionMatchEntryDeserializer();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(InstructionMatchEntry.class, deserializer);
		mapper.registerModule(module);
	}

	@Test
	void wildcard() {
		InstructionWildcard wildcard = InstructionWildcard.INSTANCE;
		String serialized = serialize(wildcard);
		assertEquals("\"*\"", serialized, "Wildcard should serialize to '*'");

		InstructionMatchEntry entry = deserialize("\"*\"");
		assertTrue(entry instanceof InstructionWildcard, "'*' should be deserialized to wildcard");
	}

	@Test
	void wildcardMultiAny() {
		InstructionWildcardMulti wildcard = InstructionWildcardMulti.ANY_INSTANCE;
		String serialized = serialize(wildcard);
		assertEquals("\"**\"", serialized, "Wildcard-multi-any should serialize to '**'");

		InstructionMatchEntry entry = deserialize("\"**\"");
		assertSame(entry, wildcard, "'**' should be deserialized to wildcard-multi-any");

		// Edge cases
		entry = deserialize("\"*0\"");
		assertSame(entry, wildcard, "'*0' should be deserialized to wildcard-multi-any");
		entry = deserialize("\"*-1\"");
		assertSame(entry, wildcard, "'*-1' should be deserialized to wildcard-multi-any");
	}

	@Test
	void wildcardMultiCount() {
		InstructionWildcardMulti wildcard = InstructionWildcardMulti.get(7);
		String serialized = serialize(wildcard);
		assertEquals("\"*7\"", serialized, "Wildcard-multi-7 should serialize to '*7'");

		InstructionMatchEntry entry = deserialize("\"*7\"");
		assertSame(entry, wildcard, "'*7' should be deserialized to wildcard-multi-7");
	}

	@Test
	void insnOpcodeOnly() {
		Instruction instruction = new Instruction("NOP", null, TextMatchMode.EQUALS, null);
		String serialized = serialize(instruction);
		assertEquals("{\"op\":\"EQUALS NOP\"}", serialized);

		InstructionMatchEntry entry = deserialize("{\"op\":\"EQUALS NOP\"}");
		assertEquals(instruction, entry);
	}

	@Test
	void insnOpcodeAndArgs() {
		Instruction instruction = new Instruction("INVOKESTATIC", "java/lang/Runtime", TextMatchMode.EQUALS,  TextMatchMode.STARTS_WITH);
		String serialized = serialize(instruction);
		assertEquals("{\"op\":\"EQUALS INVOKESTATIC\",\"args\":\"STARTS_WITH java/lang/Runtime\"}", serialized);

		InstructionMatchEntry entry = deserialize("{\"op\":\"EQUALS INVOKESTATIC\",\"args\":\"STARTS_WITH java/lang/Runtime\"}");
		assertEquals(instruction, entry);
	}

	@Test
	void multiInsn() {
		InstructionWildcard instruction1 = InstructionWildcard.INSTANCE;
		Instruction instruction2 = new Instruction("NOP", null, TextMatchMode.EQUALS, null);
		Instruction instruction3 = new Instruction("INVOKESTATIC", "java/lang/Runtime", TextMatchMode.EQUALS,  TextMatchMode.STARTS_WITH);
		MultiInstruction multiInstruction = new AnyMultiInstruction(Arrays.asList(instruction1, instruction2, instruction3));

		String serialized = serialize(multiInstruction);
		assertEquals("{\"ANY\":[\"*\",{\"op\":\"EQUALS NOP\"},{\"op\":\"EQUALS INVOKESTATIC\",\"args\":\"STARTS_WITH java/lang/Runtime\"}]}", serialized);

		InstructionMatchEntry entry = deserialize("{\n" +
				"  \"ANY\": [\n" +
				"    \"*\",\n" +
				"    { \"op\": \"EQUALS NOP\" },\n" +
				"    { \"op\": \"EQUALS INVOKESTATIC\", \"args\": \"STARTS_WITH java/lang/Runtime\" }\n" +
				"  ]\n" +
				"}");
		assertEquals(multiInstruction, entry);
	}

	@Nonnull
	private static String serialize(@Nonnull InstructionMatchEntry entry) {
		try {
			return mapper.writeValueAsString(entry);
		} catch (JsonProcessingException ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}

	@Nonnull
	private static InstructionMatchEntry deserialize(@Nonnull String text) {
		try {
			return mapper.readValue(text, InstructionMatchEntry.class);
		} catch (JsonProcessingException ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}
}
