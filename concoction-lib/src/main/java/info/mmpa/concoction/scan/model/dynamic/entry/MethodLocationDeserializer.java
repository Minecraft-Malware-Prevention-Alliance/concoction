package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import info.mmpa.concoction.scan.model.TextMatchMode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
			JsonNode methodNameNode = node.get("mname");
			JsonNode methodDescNode = node.get("mdesc");
			MatchPair classPair = getPair(jp, classNode);
			MatchPair methodNamePair = getPair(jp, methodNameNode);
			MatchPair methodDescPair = getPair(jp, methodDescNode);
			return new MethodLocation(
					classPair.text,
					methodNamePair.text,
					methodDescPair.text,
					classPair.mode,
					methodNamePair.mode,
					methodDescPair.mode
			);
		}
		throw new JsonMappingException(jp, "Dynamic match entry expects a JSON object, or '*' literal for wildcards");
	}

	@Nonnull
	private MatchPair getPair(@Nonnull JsonParser jp, @Nullable JsonNode node) throws JsonProcessingException {
		// Any pair with no node to pull from is filled in with 'match anything'
		if (node == null) return MatchPair.ANY;
		
		String[] inputs = breakByFirstSpace(jp, node.asText());
		return new MatchPair(TextMatchMode.valueOf(inputs[0]), inputs[1]);
	}

	private static class MatchPair {
		private static final MatchPair ANY = new MatchPair(TextMatchMode.ANYTHING, "");
		private final TextMatchMode mode;
		private final String text;

		public MatchPair(TextMatchMode mode, String text) {
			this.mode = mode;
			this.text = text;
		}
	}
}
