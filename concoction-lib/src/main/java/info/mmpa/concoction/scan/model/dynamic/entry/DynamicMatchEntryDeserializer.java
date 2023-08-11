package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.TextMatchMode;
import info.mmpa.concoction.util.EnumUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static info.mmpa.concoction.util.JsonUtil.breakByFirstSpace;

/**
 * Deserializes {@link DynamicMatchEntry} shorthand JSON into instances.
 *
 * @see DynamicMatchEntrySerializer
 */
public class DynamicMatchEntryDeserializer extends StdDeserializer<DynamicMatchEntry> {
	/**
	 * New deserializer instance.
	 */
	public DynamicMatchEntryDeserializer() {
		super(DynamicMatchEntry.class);
	}

	@Override
	public DynamicMatchEntry deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		return deserializeNode(jp, node);
	}

	@Nonnull
	private DynamicMatchEntry deserializeNode(JsonParser jp, JsonNode node) throws JacksonException {
		if (node.isObject()) {
			// If location exists, it's a condition matcher
			JsonNode locationNode = node.get("location");
			if (locationNode != null) {
				// Construct the location
				MethodLocation location = jp.getCodec().treeToValue(locationNode, MethodLocation.class);

				// Construct the condition
				JsonNode conditionNode = node.get("condition");
				if (conditionNode == null)
					throw new JsonMappingException(jp, "Dynamic match entry missing 'condition' value");
				Condition condition = jp.getCodec().treeToValue(conditionNode, Condition.class);

				// Construct the when
				JsonNode whenNode = node.get("when");
				When when = whenNode == null ? When.ENTRY : EnumUtil.insensitiveValueOf(When.class, whenNode.asText());

				return new SingleConditionCheckingDynamic(location, condition, when);
			} else {
				// Should be a multi-dynamic if no other case applies.
				// Determine which mode by its name.
				for (MultiMatchMode mode : MultiMatchMode.values()) {
					JsonNode modeNode = node.get(mode.name());
					if (modeNode != null && modeNode.isArray()) {
						// Mode found, now extract the entries and create the multi-matcher.
						List<DynamicMatchEntry> entries = new ArrayList<>(modeNode.size());
						for (JsonNode arrayItem : modeNode) {
							entries.add(deserializeNode(jp, arrayItem));
						}
						return mode.createMultiDynamic(entries);
					}
				}
			}
		}
		throw new JsonMappingException(jp, "Dynamic match entry expects a JSON object, or '*' literal for wildcards");
	}
}
