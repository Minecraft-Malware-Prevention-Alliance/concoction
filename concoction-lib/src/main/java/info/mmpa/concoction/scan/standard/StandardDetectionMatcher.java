package info.mmpa.concoction.scan.standard;

import info.mmpa.concoction.model.path.ClassPathElement;
import info.mmpa.concoction.model.path.MethodPathElement;
import info.mmpa.concoction.output.ResultsSink;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 * Base outline of a standard ASM pattern matcher.
 * <p>
 * When iterating over {@link ClassNode#methods a class's methods} child implementations should manage
 * references such that there is only ever one {@link MethodPathElement} per method.
 * <pre>
 * {@code
 * for (MethodNode method : node.methods) {
 * 	 MethodPathElement methodPath = path.child(method); // Use local variables like this
 * 	 if (method.instructions == null) continue;
 * 	 for (AbstractInsnNode instruction : method.instructions) {
 *     // ...
 *   }
 * }
 * }
 * </pre>
 */
public abstract class StandardDetectionMatcher {
	/**
	 * @param sink
	 * 		Sink to pass results into.
	 * @param path
	 * 		Current path of the given class.
	 * @param node
	 * 		Node to scan.
	 */
	public abstract void scan(@Nonnull ResultsSink sink, @Nonnull ClassPathElement path, @Nonnull ClassNode node);
}
