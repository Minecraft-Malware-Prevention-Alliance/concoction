package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * Outline for condition matching in a given stack-frame.
 */
@JsonSerialize(using = ConditionSerializer.class)
@JsonDeserialize(using = ConditionDeserializer.class)
public interface Condition {
	/**
	 * @param frame
	 * 		Frame to check for conditions within.
	 *
	 * @return {@code true} when condition matches.
	 */
	boolean match(@Nonnull CallStackFrame frame);
}
