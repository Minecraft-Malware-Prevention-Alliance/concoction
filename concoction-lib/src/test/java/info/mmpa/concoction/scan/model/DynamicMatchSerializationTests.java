package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.scan.model.dynamic.entry.*;
import org.junit.jupiter.api.Test;

import static info.mmpa.concoction.scan.model.dynamic.entry.NumericParameterCondition.*;
import static info.mmpa.concoction.util.TestSerialization.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for {@link DynamicMatchEntry} and {@link Condition} serialization.
 */
public class DynamicMatchSerializationTests {
	@Test
	void any() {
		AnyCondition wildcard = AnyCondition.INSTANCE;
		String serialized = serialize(wildcard);
		assertEquals("\"ANY\"", serialized, "The any-condition should serialize to 'ANY'");

		Condition deserializedCondition = deserializeCondition("\"ANY\"");
		assertTrue(deserializedCondition instanceof AnyCondition, "'ANY' should be deserialized to any-condition");
	}

	@Test
	void none() {
		NoneCondition wildcard = NoneCondition.INSTANCE;
		String serialized = serialize(wildcard);
		assertEquals("\"NONE\"", serialized, "The none-condition should serialize to 'NONE'");

		Condition deserializedCondition = deserializeCondition("\"NONE\"");
		assertTrue(deserializedCondition instanceof NoneCondition, "'NONE' should be deserialized to none-condition");
	}

	@Test
	void nullParam() {
		NullParameterCondition condition = new NullParameterCondition(1, true);
		String serialized = serialize(condition);
		assertEquals("{\"index\":1,\"null\":true}", serialized);

		Condition deserializedCondition = deserializeCondition(serialized);
		assertEquals(condition, deserializedCondition);
	}

	@Test
	void numericParam() {
		int parameter = 1;
		int cmpVal = 0;
		IntComparison equalZero = intComparison(NumericParameterCondition.OP_EQUAL, cmpVal);
		ComparisonWithOp equalZeroWithOp = fromString("== 0");
		NumericParameterCondition condition = new NumericParameterCondition(parameter, equalZeroWithOp);
		assertTrue(equalZero.compare(0));
		assertFalse(equalZero.compare(1));
		assertFalse(equalZero.compare(-1));
		assertEquals("==", condition.getComparisonOperation());
		assertEquals("0", condition.getComparisonValue());

		String serialized = serialize(condition);
		assertEquals("{\"index\":" + parameter + ",\"match\":\"== " + cmpVal + "\"}", serialized);

		Condition deserializedCondition = deserializeCondition(serialized);
		assertEquals(condition, deserializedCondition);
	}

	@Test
	void stringParam() {
		int parameter = 1;
		TextMatchMode matchMode = TextMatchMode.EQUALS;
		String match = "hello world";
		StringParameterCondition.StringExtractionMode extractionMode =
				StringParameterCondition.StringExtractionMode.KNOWN_STRING_TYPES;
		StringParameterCondition condition = new StringParameterCondition(extractionMode, matchMode, match, parameter);

		String serialized = serialize(condition);
		assertEquals("{\"index\":" + parameter + ",\"match\":\"" + matchMode.name() + " " + match + "\"}", serialized);

		Condition deserializedCondition = deserializeCondition(serialized);
		assertEquals(condition, deserializedCondition);
	}

	@Test
	void entry() {
		MethodLocation location = new MethodLocation("java/lang/", "foo", "(",
				TextMatchMode.STARTS_WITH, TextMatchMode.STARTS_WITH, TextMatchMode.STARTS_WITH);
		Condition condition = AnyCondition.INSTANCE;
		When when = When.ENTRY;
		DynamicMatchEntry entry = new SingleConditionCheckingDynamic(location, condition, when);

		String serialized = serialize(entry);
		assertEquals("{\"location\":{" +
				"\"class\":\"STARTS_WITH java/lang/\"," +
				"\"mname\":\"STARTS_WITH foo\"," +
				"\"mdesc\":\"STARTS_WITH (\"}," +
				"\"condition\":\"ANY\"}", serialized);

		DynamicMatchEntry deserializedEntry = deserializeDynamicEntry(serialized);
		assertEquals(entry, deserializedEntry);
	}
}
