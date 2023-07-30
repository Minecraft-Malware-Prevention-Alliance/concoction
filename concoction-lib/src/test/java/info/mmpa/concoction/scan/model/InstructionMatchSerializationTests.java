package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.SusLevel;
import info.mmpa.concoction.scan.model.behavior.BehaviorMatchingModel;
import info.mmpa.concoction.scan.model.insn.*;
import org.junit.jupiter.api.Test;
import software.coley.collections.Maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static info.mmpa.concoction.scan.model.TextMatchMode.EQUALS;
import static info.mmpa.concoction.util.TestSerialization.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link InstructionMatchEntry} serialization.
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
		Instruction instruction = new Instruction("NOP", null, TextMatchMode.EQUALS, null);
		String serialized = serialize(instruction);
		assertEquals("{\"op\":\"EQUALS NOP\"}", serialized);

		InstructionMatchEntry entry = deserializeInsnEntry("{\"op\":\"EQUALS NOP\"}");
		assertEquals(instruction, entry);
	}

	@Test
	void insnOpcodeAndArgs() {
		Instruction instruction = new Instruction("INVOKESTATIC", "java/lang/Runtime", TextMatchMode.EQUALS, TextMatchMode.STARTS_WITH);
		String serialized = serialize(instruction);
		assertEquals("{\"op\":\"EQUALS INVOKESTATIC\",\"args\":\"STARTS_WITH java/lang/Runtime\"}", serialized);

		InstructionMatchEntry entry = deserializeInsnEntry("{\"op\":\"EQUALS INVOKESTATIC\",\"args\":\"STARTS_WITH java/lang/Runtime\"}");
		assertEquals(instruction, entry);
	}

	@Test
	void multiInsn() {
		InstructionWildcard instruction1 = InstructionWildcard.INSTANCE;
		Instruction instruction2 = new Instruction("NOP", null, TextMatchMode.EQUALS, null);
		Instruction instruction3 = new Instruction("INVOKESTATIC", "java/lang/Runtime", TextMatchMode.EQUALS, TextMatchMode.STARTS_WITH);
		MultiInstruction multiInstruction = new AnyMultiInstruction(Arrays.asList(instruction1, instruction2, instruction3));

		String serialized = serialize(multiInstruction);
		assertEquals("{\"ANY\":[\"*\",{\"op\":\"EQUALS NOP\"},{\"op\":\"EQUALS INVOKESTATIC\",\"args\":\"STARTS_WITH java/lang/Runtime\"}]}", serialized);

		InstructionMatchEntry entry = deserializeInsnEntry("{\n" +
				"  \"ANY\": [\n" +
				"    \"*\",\n" +
				"    { \"op\": \"EQUALS NOP\" },\n" +
				"    { \"op\": \"EQUALS INVOKESTATIC\", \"args\": \"STARTS_WITH java/lang/Runtime\" }\n" +
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
				new Instruction("INVOKESTATIC", "getRuntime()Ljava/lang/Runtime;", EQUALS, EQUALS),
				new Instruction("LDC", null, EQUALS, null),
				new Instruction("INVOKEVIRTUAL", "exec(Ljava/lang/String;)Ljava/lang/Process;", EQUALS, EQUALS)
		);
		InstructionsMatchingModel insnModel = new InstructionsMatchingModel(Maps.of("key", entries));
		BehaviorMatchingModel behaviorModel = new BehaviorMatchingModel(Collections.emptyMap()); // TODO: Provide a value here
		ScanModel model = new ScanModel(archetype, insnModel, behaviorModel);

		// Serialize, deserialize, and compare equality
		String serialized = serialize(model);
		ScanModel modelDeserialized = deserializeModel(serialized);
		assertEquals(model, modelDeserialized);
	}
}
