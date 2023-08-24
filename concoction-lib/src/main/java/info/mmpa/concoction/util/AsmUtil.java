package info.mmpa.concoction.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;

/**
 * Various utils for ASM.
 */
public class AsmUtil {
	/**
	 * @param value
	 * 		Class bytes.
	 *
	 * @return Node representation of class.
	 */
	@Nonnull
	public static ClassNode node(@Nonnull byte[] value) {
		return node(value, 0);
	}

	/**
	 * @param value
	 * 		Class bytes.
	 * @param flags
	 * 		Additional flags to pass.
	 * 		See {@link ClassReader} for constants.
	 *        {@link ClassReader#SKIP_FRAMES} is always applied.
	 *
	 * @return Node representation of class.
	 */
	@Nonnull
	public static ClassNode node(@Nonnull byte[] value, int flags) {
		// Frames are useless to us, and we can save performance by skipping them too.
		ClassNode node = new ClassNode();
		new ClassReader(value).accept(node, ClassReader.SKIP_FRAMES | flags);
		return node;
	}
}
