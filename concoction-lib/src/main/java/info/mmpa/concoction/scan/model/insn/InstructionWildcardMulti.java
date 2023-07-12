package info.mmpa.concoction.scan.model.insn;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Wildcard match for any instruction, multiple times.
 */
public final class InstructionWildcardMulti implements InstructionMatchEntry {
	/**
	 * Shared instance for wildcard matching where the number of matches can be any amount.
	 */
	public static final InstructionWildcardMulti ANY_INSTANCE = new InstructionWildcardMulti(-1);
	private static final Map<Integer, InstructionWildcardMulti> CACHE = new HashMap<>();
	private final int count;

	private InstructionWildcardMulti(int count) {
		// deny construction
		this.count = count;
	}

	/**
	 * @return {@code true} when any number of instructions can be matched by this matcher.
	 */
	public boolean isAnyCount() {
		return count <= 0;
	}

	/**
	 * @return Number of times the wildcard can match in a row, or {@code <= 0} for any number of matches.
	 *
	 * @see #isAnyCount()
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 * 		Number of allowed consecutive instructions to match.
	 *
	 * @return Instance for that number of matches.
	 */
	@Nonnull
	public static InstructionWildcardMulti get(int count) {
		if (count <= 0) return ANY_INSTANCE;
		return CACHE.computeIfAbsent(count, InstructionWildcardMulti::new);
	}

	@Override
	public boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn) {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InstructionWildcardMulti that = (InstructionWildcardMulti) o;

		return count == that.count;
	}

	@Override
	public int hashCode() {
		return count;
	}
}
