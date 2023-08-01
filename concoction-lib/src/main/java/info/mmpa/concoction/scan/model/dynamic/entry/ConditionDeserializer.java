package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Deserializes {@link Condition} shorthand JSON into instances.
 *
 * @see ConditionSerializer
 */
public class ConditionDeserializer extends StdDeserializer<Condition> {
	/**
	 * New deserializer instance.
	 */
	public ConditionDeserializer() {
		super(Condition.class);
	}

	@Override
	public Condition deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		return deserializeNode(jp, node);
	}

	@Nonnull
	private Condition deserializeNode(JsonParser jp, JsonNode node) throws JacksonException {
		// TODO: Implement when condition sub-types are fleshed out
		throw new UnsupportedOperationException("implement condition deserialization");
	}
}
