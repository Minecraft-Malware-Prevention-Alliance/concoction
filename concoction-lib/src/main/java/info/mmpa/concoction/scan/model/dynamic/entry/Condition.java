package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

// TODO: Sub-types
//  - Any (wildcard, useful where any execution of a 'MethodLocation' is flag-worthy)
//  - Value matching for (Parameter N), (Field $NAME/$DESC)
//    - Primitive math matching
//    - Object null/not-null
//    - String TextMatchMode matching
//      - Also for char[] and other similar types (StringBuilder, StringBuffer, non-string CharSequence)
//    - byte[] matching helpers

/**
 * Outline for condition matching in a given stack-frame.
 */
public interface Condition {
	/**
	 * @param frame
	 * 		Frame to check for conditions within.
	 *
	 * @return {@code true} when condition matches.
	 */
	boolean match(@Nonnull CallStackFrame frame);
}
