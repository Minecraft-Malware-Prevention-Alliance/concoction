package info.mmpa.concoction.scan.model.behavior;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * Outline of behavioral matching.
 */
// TODO: Custom serializers for short-hand
// TODO: Sub-types
//  - Method in class (enter/exit)
//    - with optional parameter matching
//       - Primitive math matching
//       - Object null/not-null
//       - String TextMatchMode matching
//         - Also for char[] and other similar types (StringBuilder, StringBuffer, non-string CharSequence)
//       - byte[] matching helpers
//    - with optional parent calling context predicate
public interface BehaviorMatchEntry {
	/**
	 * @param frame
	 * 		Method call stack reference.
	 *
	 * @return {@code true} on match.
	 */
	boolean matchOnEnter(@Nonnull CallStackFrame frame);

	/**
	 * @param frame
	 * 		Method call stack reference.
	 *
	 * @return {@code true} on match.
	 */
	boolean matchOnExit(@Nonnull CallStackFrame frame);
}
