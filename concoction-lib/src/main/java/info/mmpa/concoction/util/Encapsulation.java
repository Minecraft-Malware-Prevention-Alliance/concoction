package info.mmpa.concoction.util;

import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

/**
 * Encapsulation bypass.
 */
public class Encapsulation {
	/**
	 * Unlock module restrictions in reflection.
	 */
	public static void unlock() {
		try {
			String version = System.getProperty("java.class.version");
			if (Double.parseDouble(version) >= Opcodes.V9) {
				// Accessed reflectively since this only needs to be done on Java 9+
				// and references to new module classes will fail on Java 8
				Method deencapsulate = Class.forName("dev.xdark.deencapsulation.Deencapsulation")
						.getDeclaredMethod("deencapsulate", Class.class);
				deencapsulate.invoke(null, Encapsulation.class);
			}
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to unlock reflection access", ex);
		}
	}
}
