package info.mmpa.concoction.util;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unchecked operation utils.
 */
public class Unchecked {
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

	/**
	 * @param function
	 * 		Throwing function.
	 * @param in
	 * 		Function input.
	 * @param <R>
	 * 		Function output type.
	 * @param <T>
	 * 		Function input type.
	 *
	 * @return Function output.
	 */
	public static <R, T> R map(UncheckedFunction<T, R> function, T in) {
		try {
			return function.apply(in);
		} catch (Throwable ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param supplier
	 * 		Throwing supplier.
	 * @param <T>
	 * 		Supplier output type.
	 *
	 * @return Supplier output.
	 */
	public static <T> T supply(UncheckedSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (Throwable ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * @param runnable
	 * 		Throwing runnable.
	 */
	public static void run(UncheckedRunnable runnable) {
		try {
			runnable.run();
		} catch (Throwable ex) {
			fail(ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Throwing runnable.
	 */
	public interface UncheckedRunnable {
		void run() throws Throwable;
	}

	/**
	 * Throwing supplier.
	 *
	 * @param <T>
	 * 		Supplier output type.
	 */
	public interface UncheckedSupplier<T> {
		T get() throws Throwable;
	}

	/**
	 * Throwing function.
	 *
	 * @param <R>
	 * 		Function output type.
	 * @param <T>
	 * 		Function input type.
	 */
	public interface UncheckedFunction<T, R> {
		R apply(T in) throws Throwable;
	}
}
