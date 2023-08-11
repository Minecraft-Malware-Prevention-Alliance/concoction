package info.mmpa.concoction.scan.model.insn.entry;

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
 * Deserializes {@link InstructionMatchEntry} shorthand JSON into instances.
 *
 * @see InstructionMatchEntrySerializer
 */
public class InstructionMatchEntryDeserializer extends StdDeserializer<InstructionMatchEntry> {
	/**
	 * New deserializer instance.
	 */
	public InstructionMatchEntryDeserializer() {
		super(InstructionMatchEntry.class);
	}

	@Override
	public InstructionMatchEntry deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
		JsonNode node = jp.getCodec().readTree(jp);
		return deserializeNode(jp, node);
	}

	@Nonnull
	private InstructionMatchEntry deserializeNode(JsonParser jp, JsonNode node) throws JacksonException {
		if (node.isTextual()) {
			// Must be wildcard
			String text = node.asText();
			if ("*".equals(text)) return InstructionWildcard.INSTANCE;
			if ("**".equals(text)) return InstructionWildcardMulti.ANY_INSTANCE;
			int times = Integer.parseInt(text.substring(1));
			return InstructionWildcardMulti.get(times);
		} else if (node.isObject()) {
			// Can be single or multi instruction match
			JsonNode opNode = node.get("op");
			if (opNode != null) {
				String[] opInputs = breakByFirstSpace(jp, opNode.asText());
				// single instruction, in format of:
				//  { "op": "<mode> <text>", "args": "<mode> <text>" }
				JsonNode argsNode = node.get("args");
				if (argsNode == null) {
					// No arguments given
					return new Instruction(opInputs[1], null,
							EnumUtil.insensitiveValueOf(TextMatchMode.class, opInputs[0]), null);
				} else {
					// Arguments given
					String[] argsInputs = breakByFirstSpace(jp, argsNode.asText());
					return new Instruction(opInputs[1], argsInputs[1],
							EnumUtil.insensitiveValueOf(TextMatchMode.class, opInputs[0]),
							EnumUtil.insensitiveValueOf(TextMatchMode.class, argsInputs[0]));
				}
			} else {
				// Should be a multi-instruction if no other case applies.
				// Determine which mode by its name.
				for (MultiMatchMode mode : MultiMatchMode.values()) {
					JsonNode modeNode = node.get(mode.name());
					if (modeNode != null && modeNode.isArray()) {
						// Mode found, now extract the entries and create the multi-matcher.
						List<InstructionMatchEntry> entries = new ArrayList<>(modeNode.size());
						for (JsonNode arrayItem : modeNode) {
							entries.add(deserializeNode(jp, arrayItem));
						}
						return mode.createMultiInsn(entries);
					}
				}
			}
		}
		throw new JsonMappingException(jp, "Instruction match entry expects a JSON object, or '*' literal for wildcards");
	}
}
