package info.mmpa.concoction.util;

/**
 * Casting utils.
 */
public class Casting {
	/**
	 * @param v
	 * 		Value to cast.
	 * @param <T>
	 * 		Target type to cast to.
	 *
	 * @return Casted value.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object v) {
		return (T) v;
	}
}
