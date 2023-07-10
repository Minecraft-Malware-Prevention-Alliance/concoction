package info.mmpa.concoction.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Holds a cache of compiled regex patterns.
 */
public class RegexCache {
	private static final Logger logger = LoggerFactory.getLogger(RegexCache.class);
	private static final Map<String, Pattern> compilationCache = new HashMap<>();

	/**
	 * @param pattern
	 * 		Pattern to compile.
	 *
	 * @return Compiled pattern, or {@code null} if the pattern text was invalid and could not be compiled to a pattern.
	 */
	@Nullable
	public static Pattern pattern(@Nonnull String pattern) {
		return compilationCache.computeIfAbsent(pattern, p -> {
			try {
				return Pattern.compile(pattern);
			} catch (Throwable ex) {
				// Invalid pattern
				logger.error("Invalid regex pattern: {}", pattern, ex);
				return null;
			}
		});
	}
}
