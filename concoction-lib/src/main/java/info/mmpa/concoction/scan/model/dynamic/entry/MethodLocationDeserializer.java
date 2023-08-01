package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import info.mmpa.concoction.scan.model.TextMatchMode;

import javax.annotation.Nonnull;
import java.io.IOException;

import static info.mmpa.concoction.util.JsonUtil.breakByFirstSpace;

/**
 * Deserializes {@link MethodLocation} shorthand JSON into instances.
 *
 * @see MethodLocationSerializer
 */
public class MethodLocationDeserializer extends StdDeserializer<MethodLocation> {
	/**
	 * New deserializer instance.
	 */
	public MethodLocationDeserializer() {
		super(MethodLocation.class);
	}

	@Override
	public MethodLocation deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		return deserializeNode(jp, node);
	}

	@Nonnull
	private MethodLocation deserializeNode(JsonParser jp, JsonNode node) throws JacksonException {
		if (node.isObject()) {
			JsonNode classNode = node.get("class");
			if (classNode == null)
				throw new JsonMappingException(jp, "Dynamic match entry location missing 'class' value");
			JsonNode methodNameNode = node.get("mname");
			if (methodNameNode == null)
				throw new JsonMappingException(jp, "Dynamic match entry location missing 'mname' value");
			JsonNode methodDescNode = node.get("mdesc");
			if (methodDescNode == null)
				throw new JsonMappingException(jp, "Dynamic match entry location missing 'mdesc' value");
			String[] classInputs = breakByFirstSpace(jp, classNode.asText());
			String[] methodNameInputs = breakByFirstSpace(jp, methodNameNode.asText());
			String[] methodDescInputs = breakByFirstSpace(jp, methodDescNode.asText());
			return new MethodLocation(
					classInputs[1],
					methodNameInputs[1],
					methodDescInputs[1],
					TextMatchMode.valueOf(classInputs[0]),
					TextMatchMode.valueOf(methodNameInputs[0]),
					TextMatchMode.valueOf(methodDescInputs[0])
			);
		}
		throw new JsonMappingException(jp, "Dynamic match entry expects a JSON object, or '*' literal for wildcards");
	}
}
