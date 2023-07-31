package info.mmpa.concoction.input.io;

import javax.annotation.Nonnull;

/**
 * Exception to outline an input to {@link ClassesAndFiles#addClass(String, byte[])} was not actually a class.
 */
public class NotAClassException extends Exception {
	private final String name;

	/**
	 * @param name
	 * 		File path that is not a class.
	 * @param cause
	 * 		Parent exception thrown by class validation.
	 */
	public NotAClassException(@Nonnull String name, @Nonnull Throwable cause) {
		super(cause);
		this.name = name;
	}

	/**
	 * @return File path that is not a class.
	 */
	@Nonnull
	public String getName() {
		return name;
	}
}
