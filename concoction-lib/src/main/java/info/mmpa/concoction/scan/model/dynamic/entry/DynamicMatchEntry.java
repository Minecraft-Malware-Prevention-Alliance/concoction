package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * Outline of dynamic matching.
 */
@JsonDeserialize(using = DynamicMatchEntryDeserializer.class)
@JsonSerialize(using = DynamicMatchEntrySerializer.class)
public interface DynamicMatchEntry {
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
