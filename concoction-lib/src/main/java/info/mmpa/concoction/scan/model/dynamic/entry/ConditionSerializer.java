package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializes {@link Condition} subtypes into shorthand JSON.
 *
 * @see ConditionDeserializer
 */
public class ConditionSerializer extends StdSerializer<Condition> {
	/**
	 * New serializer instance.
	 */
	public ConditionSerializer() {
		super(Condition.class);
	}

	@Override
	public void serialize(Condition condition, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		if (condition instanceof AnyCondition) {
			jgen.writeString("ANY");
		} else if (condition instanceof NoneCondition) {
			jgen.writeString("NONE");
		} else if (condition instanceof NullParameterCondition) {
			NullParameterCondition nullParameterCondition = (NullParameterCondition) condition;
			jgen.writeStartObject();
			jgen.writeNumberField("index", nullParameterCondition.getIndex());
			jgen.writeBooleanField("null", nullParameterCondition.isNull());
			jgen.writeEndObject();
		} else if (condition instanceof StringParameterCondition) {
			StringParameterCondition stringParameterCondition = (StringParameterCondition) condition;
			jgen.writeStartObject();
			if (stringParameterCondition.getIndex() >= 0)
				jgen.writeNumberField("index", stringParameterCondition.getIndex());
			if (stringParameterCondition.getExtractionMode() != StringParameterCondition.StringExtractionMode.KNOWN_STRING_TYPES)
				jgen.writeStringField("extraction", stringParameterCondition.getExtractionMode().getDisplay());
			jgen.writeStringField("match", stringParameterCondition.getMatchMode().name() + " " + stringParameterCondition.getMatch());
			jgen.writeEndObject();
		} else if (condition instanceof NumericParameterCondition) {
			NumericParameterCondition numericParameterCondition = (NumericParameterCondition) condition;
			jgen.writeStartObject();
			if (numericParameterCondition.getIndex() >= 0)
				jgen.writeNumberField("index", numericParameterCondition.getIndex());
			jgen.writeStringField("match", numericParameterCondition.getComparisonOperation() + " " + numericParameterCondition.getComparisonValue());
			jgen.writeEndObject();
		} else if (condition instanceof MultiCondition) {
			MultiCondition multiCondition = (MultiCondition) condition;
			jgen.writeStartObject();
			jgen.writeFieldName(multiCondition.getMode().name());
			jgen.writeStartArray();
			for (Condition subCondition : multiCondition.getConditions())
				serialize(subCondition, jgen, provider);
			jgen.writeEndArray();
		}else {
			throw new IllegalStateException("Unsupported condition class: " + condition.getClass().getName());
		}
	}
}
