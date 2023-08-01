package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializes {@link MethodLocation} subtypes into shorthand JSON.
 *
 * @see MethodLocationDeserializer
 */
public class MethodLocationSerializer extends StdSerializer<MethodLocation> {
	/**
	 * New serializer instance.
	 */
	public MethodLocationSerializer() {
		super(MethodLocation.class);
	}

	@Override
	public void serialize(MethodLocation entry, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeStartObject();
		jgen.writeStringField("class", entry.getClassMatchMode().name() + " " + entry.getClassName());
		jgen.writeStringField("mname", entry.getMethodNameMatchMode().name() + " " + entry.getMethodName());
		jgen.writeStringField("mdesc", entry.getMethodDescMatchMode().name() + " " + entry.getMethodDesc());
		jgen.writeEndObject();
	}
}
