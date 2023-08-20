package info.mmpa.concoction.scan.model.insn.entry;

import info.mmpa.concoction.scan.model.MultiMatchMode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An instruction matcher that defines multiple sub-matchers.
 * None the sub-matchers must match the given input.
 * <p>
 * This is mostly useful as a blacklist modifier.
 * <br>
 * For example, if there is some API that is used fairly often for malicious purposes, except maybe one or two cases,
 * you can declare the desired cases as {@link Instruction instruction matches} and then invert the matching condition
 * by wrapping those matchers with this type.
 */
public class NoneMultiInstruction extends MultiInstruction {
	/**
	 * @param entries
	 * 		Sub-matchers which must all not match inputs in order to pass.
	 */
	public NoneMultiInstruction(@Nonnull List<InstructionMatchEntry> entries) {
		super(MultiMatchMode.NONE, entries);
	}

	@Override
	public boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn) {
		return getEntries().stream().noneMatch(e -> e.match(method, insn));
	}
}
