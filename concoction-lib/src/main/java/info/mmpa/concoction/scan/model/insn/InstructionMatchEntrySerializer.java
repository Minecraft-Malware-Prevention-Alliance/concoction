package info.mmpa.concoction.scan.model.insn;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Serializes {@link InstructionMatchEntry} subtypes into shorthand JSON.
 *
 * @see InstructionMatchEntryDeserializer
 */
public class InstructionMatchEntrySerializer extends StdSerializer<InstructionMatchEntry> {
	/**
	 * New serializer instance.
	 */
	public InstructionMatchEntrySerializer() {
		super(InstructionMatchEntry.class);
	}

	@Override
	public void serialize(InstructionMatchEntry entry, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		if (entry instanceof Instruction) {
			// Simple instruction
			Instruction instruction = (Instruction) entry;
			jgen.writeStartObject();
			jgen.writeStringField("op", instruction.getOpcodeMatching().name() + " " + instruction.getOpcode());
			if (instruction.getArgumentsMatching() != null && instruction.getArguments() != null)
				jgen.writeStringField("args", instruction.getArgumentsMatching().name() + " " + instruction.getArguments());
			jgen.writeEndObject();
		} else if (entry instanceof InstructionWildcard) {
			// Wildcard
			jgen.writeString("*");
		} else if (entry instanceof InstructionWildcardMulti) {
			// Wildcard for multiple
			InstructionWildcardMulti wildcardMulti = (InstructionWildcardMulti) entry;
			int count = wildcardMulti.getCount();
			if (count <= 0) jgen.writeString("**"); // any times
			else jgen.writeString("*" + count); // amount of times
		} else if (entry instanceof MultiInstruction) {
			// Multi-match
			MultiInstruction multiInstruction = (MultiInstruction) entry;
			jgen.writeStartObject();
			jgen.writeFieldName(multiInstruction.getMode().name());
			jgen.writeStartArray();
			for (InstructionMatchEntry subEntry : multiInstruction.getEntries()) {
				serialize(subEntry, jgen, provider);
			}
			jgen.writeEndArray();
			jgen.writeEndObject();
		} else {
			throw new IOException("Unknown insn match entry type: " + entry.getClass().getName());
		}
	}
}
