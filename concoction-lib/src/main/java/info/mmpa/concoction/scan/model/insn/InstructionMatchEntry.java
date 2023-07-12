package info.mmpa.concoction.scan.model.insn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;

/**
 * Outline of instruction level matching.
 */
@JsonSerialize(using = InstructionMatchEntrySerializer.class)
@JsonDeserialize(using = InstructionMatchEntryDeserializer.class)
public interface InstructionMatchEntry {
	/**
	 * @param method
	 * 		Method containing the instruction.
	 * @param insn
	 * 		Instruction to match.
	 *
	 * @return {@code true} on match.
	 */
	boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn);
}
