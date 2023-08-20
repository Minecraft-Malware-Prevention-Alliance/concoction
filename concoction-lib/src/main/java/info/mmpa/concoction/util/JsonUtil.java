package info.mmpa.concoction.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.annotation.Nonnull;

/**
 * Common JSON utilities.
 */
public class JsonUtil {
	/**
	 * @param jp
	 * 		Parser context, used for positioning when reporting errors.
	 * @param input
	 * 		String to break into section.
	 *
	 * @return {@code new String[2]} where {@code array[0]} is the first word,
	 * and {@code array[1]} is the rest of the text after, excluding the space splitter.
	 *
	 * @throws JsonProcessingException
	 * 		When no space was found in the given input string.
	 */
	@Nonnull
	public static String[] breakByFirstSpace(@Nonnull JsonParser jp, @Nonnull String input) throws JsonProcessingException {
		String[] split = new String[2];
		int splitIndex = input.indexOf(' ');
		if (splitIndex <= 0)
			throw new JsonMappingException(jp, "opcode or argument was not in expected format of: '<mode> <input>");
		split[0] = input.substring(0, splitIndex); // text match mode
		split[1] = input.substring(splitIndex + 1); // text input
		return split;
	}
}
