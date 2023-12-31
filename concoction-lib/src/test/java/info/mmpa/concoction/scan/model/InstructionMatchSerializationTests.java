package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.SusLevel;
import info.mmpa.concoction.scan.model.dynamic.DynamicMatchingModel;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.scan.model.insn.entry.*;
import org.junit.jupiter.api.Test;
import software.coley.collections.Maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static info.mmpa.concoction.scan.model.TextMatchMode.EQUALS;
import static info.mmpa.concoction.util.TestSerialization.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link InstructionsMatchingModel} and {@link InstructionMatchEntry} serialization.
 */
public class InstructionMatchSerializationTests {
	@Test
	void wildcard() {
		InstructionWildcard wildcard = InstructionWildcard.INSTANCE;
		String serialized = serialize(wildcard);
		assertEquals("\"*\"", serialized, "Wildcard should serialize to '*'");

		InstructionMatchEntry entry = deserializeInsnEntry("\"*\"");
		assertTrue(entry instanceof InstructionWildcard, "'*' should be deserialized to wildcard");
	}

	@Test
	void wildcardMultiAny() {
		InstructionWildcardMulti wildcard = InstructionWildcardMulti.ANY_INSTANCE;
		String serialized = serialize(wildcard);
		assertEquals("\"**\"", serialized, "Wildcard-multi-any should serialize to '**'");

		InstructionMatchEntry entry = deserializeInsnEntry("\"**\"");
		assertSame(entry, wildcard, "'**' should be deserialized to wildcard-multi-any");

		// Edge cases
		entry = deserializeInsnEntry("\"*0\"");
		assertSame(entry, wildcard, "'*0' should be deserialized to wildcard-multi-any");
		entry = deserializeInsnEntry("\"*-1\"");
		assertSame(entry, wildcard, "'*-1' should be deserialized to wildcard-multi-any");
	}

	@Test
	void wildcardMultiCount() {
		InstructionWildcardMulti wildcard = InstructionWildcardMulti.get(7);
		String serialized = serialize(wildcard);
		assertEquals("\"*7\"", serialized, "Wildcard-multi-7 should serialize to '*7'");

		InstructionMatchEntry entry = deserializeInsnEntry("\"*7\"");
		assertSame(entry, wildcard, "'*7' should be deserialized to wildcard-multi-7");
	}

	@Test
	void insnOpcodeOnly() {
		Instruction instruction = new Instruction("nop", null, TextMatchMode.EQUALS, null);
		String serialized = serialize(instruction);
		assertEquals("{\"op\":\"EQUALS nop\"}", serialized);

		InstructionMatchEntry entry = deserializeInsnEntry("{\"op\":\"EQUALS nop\"}");
		assertEquals(instruction, entry);
	}

	@Test
	void insnOpcodeAndArgs() {
		Instruction instruction = new Instruction("invokestatic", "java/lang/Runtime", TextMatchMode.EQUALS, TextMatchMode.STARTS_WITH);
		String serialized = serialize(instruction);
		assertEquals("{\"op\":\"EQUALS invokestatic\",\"args\":\"STARTS_WITH java/lang/Runtime\"}", serialized);

		InstructionMatchEntry entry = deserializeInsnEntry("{\"op\":\"EQUALS invokestatic\",\"args\":\"STARTS_WITH java/lang/Runtime\"}");
		assertEquals(instruction, entry);
	}

	@Test
	void multiInsn() {
		InstructionWildcard instruction1 = InstructionWildcard.INSTANCE;
		Instruction instruction2 = new Instruction("nop", null, TextMatchMode.EQUALS, null);
		Instruction instruction3 = new Instruction("invokestatic", "java/lang/Runtime", TextMatchMode.EQUALS, TextMatchMode.STARTS_WITH);
		MultiInstruction multiInstruction = new AnyMultiInstruction(Arrays.asList(instruction1, instruction2, instruction3));

		String serialized = serialize(multiInstruction);
		assertEquals("{\"ANY\":[\"*\",{\"op\":\"EQUALS nop\"},{\"op\":\"EQUALS invokestatic\",\"args\":\"STARTS_WITH java/lang/Runtime\"}]}", serialized);

		InstructionMatchEntry entry = deserializeInsnEntry("{\n" +
				"  \"ANY\": [\n" +
				"    \"*\",\n" +
				"    { \"op\": \"EQUALS nop\" },\n" +
				"    { \"op\": \"EQUALS invokestatic\", \"args\": \"STARTS_WITH java/lang/Runtime\" }\n" +
				"  ]\n" +
				"}");
		assertEquals(multiInstruction, entry);
	}

	/**
	 * So long as the other tests pass, equality in the object and the deserialized object should imply
	 * {@link InstructionsMatchingModel} is properly serializable.
	 */
	@Test
	void model() {
		// Process foo = Runtime.getRuntime().exec("foo");
		DetectionArchetype archetype = new DetectionArchetype(SusLevel.MAXIMUM, "id", "description");
		List<InstructionMatchEntry> entries = Arrays.asList(
				new Instruction("invokestatic", "getRuntime()Ljava/lang/Runtime;", EQUALS, EQUALS),
				new Instruction("ldc", null, EQUALS, null),
				new Instruction("invokevirtual", "exec(Ljava/lang/String;)Ljava/lang/Process;", EQUALS, EQUALS)
		);
		InstructionsMatchingModel insnModel = new InstructionsMatchingModel(Maps.of("key", entries));
		DynamicMatchingModel dynamicModel = new DynamicMatchingModel(Collections.emptyMap());
		ScanModel model = new ScanModel(archetype, insnModel, dynamicModel);

		// Serialize, deserialize, and compare equality
		String serialized = serialize(model);
		ScanModel modelDeserialized = deserializeModel(serialized);
		assertEquals(model, modelDeserialized);
	}
}
