package info.mmpa.concoction.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.annotation.Nonnull;

public class JsonUtil {
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
