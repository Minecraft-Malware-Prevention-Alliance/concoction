package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

/**
 * Enum for handling {@link DynamicMatchEntry#matchOnEnter(CallStackFrame)} vs
 * {@link DynamicMatchEntry#matchOnExit(CallStackFrame)}.
 */
public enum When {
	/**
	 * The dynamic match is active upon method entry.
	 */
	ENTRY,
	/**
	 * The dynamic match is active upon method returning.
	 */
	RETURN
}
