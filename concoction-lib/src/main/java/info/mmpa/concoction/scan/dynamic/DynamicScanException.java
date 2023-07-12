package info.mmpa.concoction.scan.dynamic;

/**
 * Exception wrapper for problems in {@link DynamicScan}.
 */
public class DynamicScanException extends Exception {
	/**
	 * @param message
	 * 		Detail message.
	 * @param cause
	 * 		Cause exception.
	 */
	public DynamicScanException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 * 		Cause exception.
	 */
	public DynamicScanException(Throwable cause) {
		this(null, cause);
	}
}
