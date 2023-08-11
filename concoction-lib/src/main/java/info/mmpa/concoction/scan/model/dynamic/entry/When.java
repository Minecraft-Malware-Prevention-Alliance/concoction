package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

/**
 * Enum for handling {@link DynamicMatchEntry#matchOnEnter(CallStackFrame)} vs
 * {@link DynamicMatchEntry#matchOnExit(CallStackFrame)}.
 */
public enum When {
	ENTRY,
	RETURN
}
