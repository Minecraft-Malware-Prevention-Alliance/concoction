package info.mmpa.concoction.scan.model.insn.entry;

import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.TextMatchMode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An instruction matcher that defines multiple sub-matchers.
 * All the sub-matchers must match the given input all at once.
 * <p>
 * This is mostly useful for when {@link TextMatchMode#REGEX_FULL_MATCH} and
 * {@link TextMatchMode#REGEX_PARTIAL_MATCH} are used in a {@link Instruction instruction matcher}.
 */
public class AllMultiInstruction extends MultiInstruction {
	/**
	 * @param entries
	 * 		Sub-matchers which must all match inputs in order to pass.
	 */
	public AllMultiInstruction(@Nonnull List<InstructionMatchEntry> entries) {
		super(MultiMatchMode.ALL, entries);
	}

	@Override
	public boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn) {
		return entries.stream().allMatch(e -> e.match(method, insn));
	}
}
