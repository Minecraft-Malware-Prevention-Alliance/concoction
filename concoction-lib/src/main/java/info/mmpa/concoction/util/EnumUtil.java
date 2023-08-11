package info.mmpa.concoction.util;

/**
 * Basic enum utils.
 */
public class EnumUtil {
	/**
	 * @param cls
	 * 		Class of enum type.
	 * @param name
	 * 		Enum constant name to resolve to value.
	 * @param <T>
	 * 		Enum type.
	 *
	 * @return Enum constant by name <i>(case insensitive)</i>.
	 */
	public static <T extends Enum<?>> T insensitiveValueOf(Class<T> cls, String name) {
		for (T constant : cls.getEnumConstants()) {
			if (constant.name().equalsIgnoreCase(name))
				return constant;
		}
		throw new IllegalStateException("No match for '" + name + "' for enum type '" + cls.getSimpleName() + "'");
	}
}
