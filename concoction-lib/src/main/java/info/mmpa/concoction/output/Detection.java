package info.mmpa.concoction.output;

import javax.annotation.Nullable;

/**
 * Detection instance of a {@link DetectionArchetype}.
 */
public interface Detection {
	/**
	 * @return Description of the local detection details, if any.
	 */
	@Nullable
	String describe();
}
