package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import info.mmpa.concoction.scan.model.TextMatchMode;

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
		TextMatchMode classMatchMode = entry.getClassMatchMode();
		TextMatchMode methodNameMatchMode = entry.getMethodNameMatchMode();
		TextMatchMode methodDescMatchMode = entry.getMethodDescMatchMode();
		if (classMatchMode != TextMatchMode.ANYTHING)
			jgen.writeStringField("class", classMatchMode.name() + " " + entry.getClassName());
		if (methodNameMatchMode != TextMatchMode.ANYTHING)
			jgen.writeStringField("mname", methodNameMatchMode.name() + " " + entry.getMethodName());
		if (methodDescMatchMode != TextMatchMode.ANYTHING)
			jgen.writeStringField("mdesc", methodDescMatchMode.name() + " " + entry.getMethodDesc());
		jgen.writeEndObject();
	}
}
