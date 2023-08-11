package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializes {@link DynamicMatchEntry} subtypes into shorthand JSON.
 *
 * @see DynamicMatchEntryDeserializer
 */
public class DynamicMatchEntrySerializer extends StdSerializer<DynamicMatchEntry> {
	/**
	 * New serializer instance.
	 */
	public DynamicMatchEntrySerializer() {
		super(DynamicMatchEntry.class);
	}

	@Override
	public void serialize(DynamicMatchEntry entry, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		if (entry instanceof SingleConditionCheckingDynamic) {
			SingleConditionCheckingDynamic conditionCheck = (SingleConditionCheckingDynamic) entry;
			jgen.writeStartObject();
			jgen.writeObjectField("location", conditionCheck.getLocation());
			if (conditionCheck.getWhen() != When.ENTRY) {
				// ENTRY is the default value for when, so if that's our value we can omit it.
				jgen.writeStringField("when", conditionCheck.getWhen().name());
			}
			jgen.writeObjectField("condition", conditionCheck.getCondition());
			jgen.writeEndObject();
		} else if (entry instanceof MultiDynamic) {
			// Multi-match
			MultiDynamic multiInstruction = (MultiDynamic) entry;
			jgen.writeStartObject();
			jgen.writeFieldName(multiInstruction.getMode().name());
			jgen.writeStartArray();
			for (DynamicMatchEntry subEntry : multiInstruction.getEntries()) {
				serialize(subEntry, jgen, provider);
			}
			jgen.writeEndArray();
			jgen.writeEndObject();
		} else {
			throw new IOException("Unknown dynamic match entry type: " + entry.getClass().getName());
		}
	}
}
