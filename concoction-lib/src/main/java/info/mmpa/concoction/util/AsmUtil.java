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
		// Frames are useless to us, and we can save performance by skipping them too.
		ClassNode node = new ClassNode();
		new ClassReader(value).accept(node, ClassReader.SKIP_FRAMES);
		return node;
	}
}
