package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.TextMatchMode;
import info.mmpa.concoction.util.DeserializerExt;
import info.mmpa.concoction.util.EnumUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static info.mmpa.concoction.util.JsonUtil.breakByFirstSpace;

/**
 * Deserializes {@link Condition} shorthand JSON into instances.
 *
 * @see ConditionSerializer
 */
public class ConditionDeserializer extends DeserializerExt<Condition> {
	/**
	 * New deserializer instance.
	 */
	public ConditionDeserializer() {
		super(Condition.class);
	}

	@Override
	public Condition deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		if (jp == null)
			throw new IOException("Cannot deserialize null 'JavaParser' instance");
		else if (jp.getCodec() == null)
			throw new IOException("Cannot deserialize 'JavaParser' instance with 'null' codec");
		JsonNode node = jp.getCodec().readTree(jp);
		return deserializeNode(jp, node);
	}

	@Nonnull
	private Condition deserializeNode(JsonParser jp, JsonNode node) throws JacksonException {
		if (node.isTextual()) {
			// Must be 'ANY' or 'NONE'
			String nodeText = node.asText();
			if (nodeText.equalsIgnoreCase("ANY")) return AnyCondition.INSTANCE;
			if (nodeText.equalsIgnoreCase("NONE")) return NoneCondition.INSTANCE;
			throw new JsonMappingException(jp, "String representation of conditions can only be 'ANY' or 'NONE' but was: " + nodeText);
		} else if (node.isObject()) {
			// Most conditions will have an 'index' to compare against.
			JsonNode indexNode = node.get("index");
			int index = indexNode == null ? -1 : indexNode.asInt();

			// If we see a field 'null' we know it's a null-param check condition.
			JsonNode nullNode = node.get("null");
			if (nullNode != null)
				return new NullParameterCondition(index, nullNode.asBoolean());

			// Check for multi-conditions
			JsonNode anyNode = findCaseless(node, "ANY");
			JsonNode allNode = findCaseless(node, "ALL");
			JsonNode noneNode = findCaseless(node, "NONE");
			MultiMatchMode multiMatchMode = null;
			List<Condition> conditions = new ArrayList<>();
			if (anyNode != null && anyNode.isArray()) {
				multiMatchMode = MultiMatchMode.ANY;
				for (JsonNode subNode : anyNode)
					conditions.add(deserializeNode(jp, subNode));
			} else if (allNode != null && allNode.isArray()) {
				multiMatchMode = MultiMatchMode.ALL;
				for (JsonNode subNode : allNode)
					conditions.add(deserializeNode(jp, subNode));
			} else if (noneNode != null && noneNode.isArray()) {
				multiMatchMode = MultiMatchMode.NONE;
				for (JsonNode subNode : noneNode)
					conditions.add(deserializeNode(jp, subNode));
			}
			if (multiMatchMode != null) {
				switch (multiMatchMode) {
					case ALL:
						return new AllMultiCondition(conditions);
					case ANY:
						return new AnyMultiCondition(conditions);
					case NONE:
						return new NoneMultiCondition(conditions);
				}
			}

			// Get the other possible fields for other conditions, and see which makes the most sense here.
			JsonNode extractionNode = node.get("extraction");
			JsonNode matchNode = node.get("match");
			if (matchNode == null)
				throw new JsonMappingException(jp, "Missing expected 'match' field");

			// If the 'match' field value starts with a numeric op, it's a numeric param condition.
			String matchRaw = matchNode.asText();
			if (NumericParameterCondition.startsWithOp(matchRaw)) {
				return new NumericParameterCondition(index, NumericParameterCondition.fromString(matchRaw));
			}

			// The only remaining possibility is a string parameter condition.
			String[] matchInputs = breakByFirstSpace(jp, matchNode.asText());
			TextMatchMode matchMode = EnumUtil.insensitiveValueOf(TextMatchMode.class, matchInputs[0]);
			String match = matchInputs[1];
			StringParameterCondition.StringExtractionMode extractionMode = extractionNode == null ?
					StringParameterCondition.StringExtractionMode.KNOWN_STRING_TYPES :
					StringParameterCondition.StringExtractionMode.get(extractionNode.asText());
			return new StringParameterCondition(extractionMode, matchMode, match, index);
		}

		throw new JsonMappingException(jp, "Comparison was not an object or textual model");
	}
}
