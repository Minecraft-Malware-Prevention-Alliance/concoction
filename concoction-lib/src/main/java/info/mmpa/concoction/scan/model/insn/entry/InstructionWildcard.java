package info.mmpa.concoction.scan.model.insn.entry;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;

/**
 * Wildcard match for any single instruction.
 */
public final class InstructionWildcard implements InstructionMatchEntry {
	/**
	 * Shared instance for single wildcard matching.
	 */
	public static final InstructionWildcard INSTANCE = new InstructionWildcard();

	private InstructionWildcard() {
		// deny construction
	}

	@Override
	public boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn) {
		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof InstructionWildcard;
	}
}
