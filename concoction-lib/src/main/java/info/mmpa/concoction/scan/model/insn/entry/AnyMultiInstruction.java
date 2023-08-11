package info.mmpa.concoction.scan.model.insn.entry;

import info.mmpa.concoction.scan.model.MultiMatchMode;
import info.mmpa.concoction.scan.model.TextMatchMode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An instruction matcher that defines multiple sub-matchers.
 * Any single one of the sub-matchers must match the given input.
 * <p>
 * This is useful for when there are multiple variants of an API call that could be used interchangeably,
 * and using {@link TextMatchMode#REGEX_FULL_MATCH} or {@link TextMatchMode#REGEX_PARTIAL_MATCH} is overkill.
 */
public class AnyMultiInstruction extends MultiInstruction {
	/**
	 * @param entries
	 * 		Sub-matchers where any single input must match in order to pass.
	 */
	public AnyMultiInstruction(@Nonnull List<InstructionMatchEntry> entries) {
		super(MultiMatchMode.ANY, entries);
	}

	@Override
	public boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn) {
		return entries.stream().anyMatch(e -> e.match(method, insn));
	}
}
