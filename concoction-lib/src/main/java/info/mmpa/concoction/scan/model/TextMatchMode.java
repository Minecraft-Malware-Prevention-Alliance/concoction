package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.util.RegexCache;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * Text match mode for scan model strings.
 */
public enum TextMatchMode {
	/**
	 * Exact match.
	 */
	EQUALS(String::equals),
	/**
	 * Containment match.
	 */
	CONTAINS((src, input) -> input != null && input.contains(src)),
	/**
	 * Input starts with an exact match.
	 */
	STARTS_WITH((src, input) -> input != null && input.startsWith(src)),
	/**
	 * Input ends with an exact match.
	 */
	ENDS_WITH((src, input) -> input != null && input.endsWith(src)),
	/**
	 * Input fully matches a pattern.
	 */
	REGEX_FULL_MATCH((src, input) -> {
		if (input == null) return false;
		Pattern pattern = RegexCache.pattern(src);
		if (pattern == null) return false;
		return pattern.matcher(input).matches();
	}),
	/**
	 * Input partially matches a pattern.
	 */
	REGEX_PARTIAL_MATCH((src, input) -> {
		if (input == null) return false;
		Pattern pattern = RegexCache.pattern(src);
		if (pattern == null) return false;
		return pattern.matcher(input).find();
	}),
	/**
	 * Input does not matter, always match.
	 */
	ANYTHING((src, input) -> true),
	/**
	 * Input does not matter, never match.
	 */
	NOTHING((src, input) -> false);

	// First arg is source text, second ard is input to check for match against the source.
	private final BiPredicate<String, String> matcher;

	TextMatchMode(BiPredicate<String, String> matcher) {
		this.matcher = matcher;
	}

	/**
	 * @param src
	 * 		Source input, used as comparison base.
	 * @param input
	 * 		Input to check for match against the source.
	 *
	 * @return {@code true} on match.
	 */
	public boolean matches(@Nonnull String src, @Nullable String input) {
		return matcher.test(src, input);
	}
}
