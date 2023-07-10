package info.mmpa.concoction.util;

import me.coley.cafedude.classfile.instruction.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * String formatting for instructions and their opcodes.
 */
public class InstructionStrings {
	private static final Logger logger = LoggerFactory.getLogger(InstructionStrings.class);
	private static final Map<Integer, String> opcodeToName = new HashMap<>();

	static {
		try {
			for (Field field : Opcodes.class.getDeclaredFields()) {
				opcodeToName.put(field.getInt(null), field.getName());
			}
		} catch (ReflectiveOperationException ex) {
			logger.error("Failed to populate opcode name map", ex);
		}
	}

	/**
	 * @param opcode
	 * 		Opcode value.
	 *
	 * @return Name of opcode.
	 */
	@Nonnull
	public static String opcodeToString(int opcode) {
		String name = opcodeToName.get(opcode);
		if (name == null)
			return "OP-" + opcode;
		return name;
	}

	/**
	 * @param insn
	 * 		Instruction to format.
	 *
	 * @return String representation of instruction.
	 */
	@Nullable
	public static String insnToArgsString(@Nonnull AbstractInsnNode insn) {
		// TODO: Implement sensible toString for different insns
		//  - May want to use a 3rd party formatter like JASM for consistency and easy integration
		//     (like pasting from recaf's assembler, which uses JASM)
		return null;
	}
}
