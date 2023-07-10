package info.mmpa.concoction.scan.model.method;

import info.mmpa.concoction.model.path.MethodPathElement;
import info.mmpa.concoction.output.ResultsSink;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a signature compriesed of one or more instruction matchers.
 */
public class MethodMatchingModel {
	private final List<InstructionMatchEntry> entries;

	/**
	 * @param entries
	 * 		List of instruction matchers forming a single signature.
	 */
	public MethodMatchingModel(@Nonnull List<InstructionMatchEntry> entries) {
		this.entries = Collections.unmodifiableList(entries);
	}

	/**
	 * @param sink
	 * 		Sink to feed match results into.
	 * @param path
	 * 		Current method path to pass into the sink.
	 * @param classNode
	 * 		Class defining the method.
	 * @param methodNode
	 * 		The method being scanned.
	 */
	public void match(@Nonnull ResultsSink sink, @Nonnull MethodPathElement path,
					  @Nonnull ClassNode classNode, @Nonnull MethodNode methodNode) {
		// Skip methods without code
		if (methodNode.instructions == null) return;

		// Iterate over instructions and match against the matcher entries.
		// A match will be reported if all entries successfully match in a row for some range of instructions.
		//
		// If there are 5 entries, and the 3rd entry does not match the chain is reset and we move the current index
		// to where the 1st entry matched, plus one index.
		//
		// Multiple matches can be made in a method.
		AbstractInsnNode[] array = methodNode.instructions.toArray();
		for (int i = 0; i < array.length; i++) {
			AbstractInsnNode insn = array[i];

			// TODO: Implement what the comment above says
		}
	}

	/**
	 * @return List of instruction matchers forming a single signature.
	 */
	@Nonnull
	public List<InstructionMatchEntry> getEntries() {
		return entries;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MethodMatchingModel that = (MethodMatchingModel) o;

		return entries.equals(that.entries);
	}

	@Override
	public int hashCode() {
		return entries.hashCode();
	}
}
