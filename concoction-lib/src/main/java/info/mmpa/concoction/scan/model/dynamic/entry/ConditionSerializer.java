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
	public void serialize(Condition entry, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		// TODO: Implement when condition sub-types are fleshed out
	}
}
