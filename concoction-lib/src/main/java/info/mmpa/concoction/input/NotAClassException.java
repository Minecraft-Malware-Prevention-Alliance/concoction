package info.mmpa.concoction.input;

/**
 * Exception to outline an input to {@link ClassesAndFiles#addClass(String, byte[])} was not actually a class.
 */
public class NotAClassException extends Exception {
	/**
	 * @param cause
	 * 		Parent exception thrown by class validation.
	 */
	public NotAClassException(Throwable cause) {
		super(cause);
	}
}
