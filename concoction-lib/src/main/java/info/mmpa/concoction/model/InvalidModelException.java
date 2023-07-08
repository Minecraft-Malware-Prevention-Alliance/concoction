package info.mmpa.concoction.model;

/**
 * Exception to outline failures to build in {@link ModelBuilder}.
 */
public class InvalidModelException extends Exception {
	/**
	 * @param message
	 * 		Failure message.
	 */
	public InvalidModelException(String message) {
		super(message);
	}
}
