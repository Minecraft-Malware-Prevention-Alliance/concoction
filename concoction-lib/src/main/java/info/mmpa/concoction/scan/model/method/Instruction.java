package info.mmpa.concoction.scan.model.method;

import info.mmpa.concoction.scan.model.TextMatchMode;
import info.mmpa.concoction.util.InstructionStrings;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An instruction matcher for a single instruction.
 * <p>
 * If you only want to match opcodes, the {@link #getArguments() arguments} and
 * {@link #getArgumentsMatching() argument matching mode} parameters are optional.
 * You can omit these if you are matching simple instructions like {@code ICONST_0}
 * or if you don't care about argument values at all. Not providing any argument matching
 * conditions means any argument value will match so long as the opcode also matches.
 * <p>
 * How you match arguments can be quite flexible.
 * <ul>
 *     <li>If you want one specific thing you can use
 *     {@link TextMatchMode#EQUALS} and the exact text as the {@link #getOpcode() opcode} /
 *     {@link #getArguments() arguments}.</li>
 *     <li>If you want something less specific you can use {@link TextMatchMode#CONTAINS},
 *     {@link TextMatchMode#STARTS_WITH}, {@link TextMatchMode#ENDS_WITH},
 *     {@link TextMatchMode#REGEX_FULL_MATCH}, or {@link TextMatchMode#REGEX_PARTIAL_MATCH}</li>
 *     <li>If you want to mix specific matches but allow multiple alternatives, consider bundling specific
 *     instruction matchers with {@link AllMultiInstruction}, {@link AnyMultiInstruction},
 *     or {@link NoneMultiInstruction}. Each multi-instruction matcher has its own unique benefits
 *     for different circumstances.</li>
 * </ul>
 */
public class Instruction implements InstructionMatchEntry {
	private final String opcode;
	private final String arguments;
	private final TextMatchMode opcodeMatching;
	private final TextMatchMode argumentsMatching;

	/**
	 * @param opcode
	 * 		Opcode text to match.
	 * @param arguments
	 * 		Argument text to match. Can be {@code null} to allow any argument content.
	 * @param opcodeMatching
	 * 		Opcode text matching technique.
	 * @param argumentsMatching
	 * 		Argument text matching technique. Can be {@code null} to allow any argument content.
	 */
	public Instruction(@Nonnull String opcode, @Nullable String arguments,
					   @Nonnull TextMatchMode opcodeMatching, @Nullable TextMatchMode argumentsMatching) {
		this.opcode = opcode;
		this.arguments = arguments;
		this.opcodeMatching = opcodeMatching;
		this.argumentsMatching = argumentsMatching;
	}

	@Override
	public boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn) {
		String opcodeName = InstructionStrings.opcodeToString(insn.getOpcode());
		if (!opcodeMatching.matches(opcode, opcodeName)) return false;
		String argumentText = InstructionStrings.insnToArgsString(insn);
		if (argumentText == null || argumentsMatching == null || arguments == null) return true;
		return argumentsMatching.matches(arguments, argumentText);
	}

	/**
	 * @return Opcode text to match.
	 */
	@Nonnull
	public String getOpcode() {
		return opcode;
	}

	/**
	 * @return Argument text to match. Can be {@code null} to allow any argument content.
	 */
	@Nullable
	public String getArguments() {
		return arguments;
	}

	/**
	 * @return Opcode text matching technique.
	 */
	@Nonnull
	public TextMatchMode getOpcodeMatching() {
		return opcodeMatching;
	}

	/**
	 * @return Argument text matching technique. Can be {@code null} to allow any argument content.
	 */
	@Nullable
	public TextMatchMode getArgumentsMatching() {
		return argumentsMatching;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Instruction that = (Instruction) o;

		if (!opcode.equals(that.opcode)) return false;
		if (!Objects.equals(arguments, that.arguments)) return false;
		if (opcodeMatching != that.opcodeMatching) return false;
		return argumentsMatching == that.argumentsMatching;
	}

	@Override
	public int hashCode() {
		int result = opcode.hashCode();
		result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
		result = 31 * result + opcodeMatching.hashCode();
		result = 31 * result + (argumentsMatching != null ? argumentsMatching.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Instruction{" + "opcode='" + opcode + '\'' + ", arguments='" + arguments + '\''
				+ ", opcodeMatching=" + opcodeMatching + ", argumentsMatching=" + argumentsMatching + '}';
	}
}
