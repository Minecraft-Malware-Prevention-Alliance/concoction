package info.mmpa.concoction.output;

/**
 * Levels of suspicions for report items.
 */
public enum SusLevel {
	/**
	 * Almost guaranteed to be malicious.
	 */
	MAXIMUM,
	/**
	 * Very likely to be malicious, but in some cases it may just be benign shoddy code.
	 */
	STRONG,
	/**
	 * Typically these are strictly context based. For instance, deleting a file is usually not a concern.
	 * Deleting core operating system files is a concern though.
	 */
	MEDIUM,
	/**
	 * Unlikely to be malicious in most circumstances.
	 */
	WEAK,
	/**
	 * Incredibly unlikely to be malicious in any circumstance.
	 */
	NOTHING_BURGER
}
