package info.mmpa.concoction.util;

import me.coley.cafedude.classfile.instruction.Opcodes;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
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
		switch (insn.getType()) {
			case AbstractInsnNode.INT_INSN:
				IntInsnNode intNode = (IntInsnNode) insn;
				return String.valueOf(intNode.operand);
			case AbstractInsnNode.VAR_INSN:
				VarInsnNode varNode = (VarInsnNode) insn;
				return String.valueOf(varNode.var);
			case AbstractInsnNode.TYPE_INSN:
				TypeInsnNode typeNode = (TypeInsnNode) insn;
				return typeNode.desc;
			case AbstractInsnNode.FIELD_INSN:
				FieldInsnNode fieldNode = (FieldInsnNode) insn;
				return fieldNode.owner + "." + fieldNode.name + " " + fieldNode.desc;
			case AbstractInsnNode.METHOD_INSN:
				MethodInsnNode methodNode = (MethodInsnNode) insn;
				return methodNode.owner + "." + methodNode.name + methodNode.desc;
			case AbstractInsnNode.INVOKE_DYNAMIC_INSN:
			case AbstractInsnNode.LDC_INSN:
				LdcInsnNode ldcNode = (LdcInsnNode) insn;
				Object cst = ldcNode.cst;
				if (cst instanceof String) {
					return "\"" + cst + "\"";
				} else if (cst instanceof Type) {
					Type cstType = (Type) cst;
					return cstType.getDescriptor();
				} else if (cst instanceof Handle) {
					Handle cstHandle = (Handle) cst;
					if (cstHandle.getDesc().charAt(0) == '(') {
						return cstHandle.getOwner() + "." + cstHandle.getName() + cstHandle.getDesc();
					} else {
						return cstHandle.getOwner() + "." + cstHandle.getName() + " " + cstHandle.getDesc();
					}
				}
				return cst.toString();
			case AbstractInsnNode.IINC_INSN:
				IincInsnNode iincNode = (IincInsnNode) insn;
				return iincNode.var + " " + iincNode.incr;
			case AbstractInsnNode.MULTIANEWARRAY_INSN:
				MultiANewArrayInsnNode multiNode = (MultiANewArrayInsnNode) insn;
				return multiNode.desc + " " + multiNode.dims;
		}
		return null;
	}
}
