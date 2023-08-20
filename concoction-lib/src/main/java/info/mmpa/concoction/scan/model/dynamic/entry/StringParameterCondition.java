package info.mmpa.concoction.scan.model.dynamic.entry;

import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.mirror.type.JavaClass;
import dev.xdark.ssvm.value.ObjectValue;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.dynamic.VirtualMachineExt;
import info.mmpa.concoction.scan.model.TextMatchMode;

import javax.annotation.Nonnull;

/**
 * Condition implementation for text parameter matching.
 */
public class StringParameterCondition implements Condition {
	private final StringExtractionMode extractionMode;
	private final TextMatchMode matchMode;
	private final String match;
	private final int index;

	/**
	 * @param extractionMode
	 * 		Mode for pulling text from VM objects.
	 * @param matchMode
	 * 		Text match mode for {@link #getMatch()} against parameter values.
	 * @param match
	 * 		Text to match against parameter values.
	 * @param index
	 * 		Parameter index to compare against. Negative for any parameter.
	 */
	public StringParameterCondition(@Nonnull StringExtractionMode extractionMode, @Nonnull TextMatchMode matchMode,
									@Nonnull String match, int index) {
		this.extractionMode = extractionMode;
		this.matchMode = matchMode;
		this.match = match;
		this.index = index;
	}

	/**
	 * @return Mode for pulling text from VM objects.
	 */
	@Nonnull
	public StringExtractionMode getExtractionMode() {
		return extractionMode;
	}

	/**
	 * @return Text match mode for {@link #getMatch()} against parameter values.
	 */
	@Nonnull
	public TextMatchMode getMatchMode() {
		return matchMode;
	}

	/**
	 * @return Text to match against parameter values.
	 */
	@Nonnull
	public String getMatch() {
		return match;
	}

	/**
	 * @return Parameter index to compare against. Negative for any parameter.
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		// Get parameter value
		ExecutionContext<?> ctx = frame.getCtx();
		if (index < 0) {
			// Match against any argument
			int maxArgs = ctx.getMethod().getMaxArgs();
			for (int i = 0; i < maxArgs; i++) {
				ObjectValue value = ctx.getLocals().loadReference(index);
				if (matchValue(ctx, value))
					return true;
			}
		} else {
			// Match against a single argument
			ObjectValue value = ctx.getLocals().loadReference(index);
			return matchValue(ctx, value);
		}

		return false;
	}

	private boolean matchValue(@Nonnull ExecutionContext<?> ctx, @Nonnull ObjectValue value) {
		if (value.isNull()) return false;

		// Extract string to compare to
		VirtualMachineExt vm = (VirtualMachineExt) ctx.getVM();
		String matchTarget = null;
		try {
			switch (extractionMode) {
				case ANY_TYPE_TOSTRING:
					matchTarget = vm.getOperations().toString(value);
					break;
				case KNOWN_STRING_TYPES:
					JavaClass valueType = vm.getMemoryManager().readClass(value);
					if (valueType.isAssignableFrom(vm.getCharSequence()))
						matchTarget = vm.getOperations().toString(value);
					break;
			}
		} catch (Throwable ignored) {
			// If a class defines a 'toString()' that throws an exception, we can't extract the value.
			// For known string types, like String/StringBuilder/etc this is not an issue.
			// It should only be very rare for somebody to implement a CharSequence themselves that does this.
		}

		// Compare
		return matchTarget != null && matchMode.matches(match, matchTarget);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StringParameterCondition that = (StringParameterCondition) o;

		if (index != that.index) return false;
		if (extractionMode != that.extractionMode) return false;
		if (matchMode != that.matchMode) return false;
		return match.equals(that.match);
	}

	@Override
	public int hashCode() {
		int result = extractionMode.hashCode();
		result = 31 * result + matchMode.hashCode();
		result = 31 * result + match.hashCode();
		result = 31 * result + index;
		return result;
	}

	/**
	 * Extraction modes for pulling text from object values.
	 */
	public enum StringExtractionMode {
		/**
		 * Only types of {@link CharSequence} are checked.
		 */
		KNOWN_STRING_TYPES("known-types"),
		/**
		 * All types are checked via their {@link Object#toString()} implementation.
		 */
		ANY_TYPE_TOSTRING("any-type");

		private final String display;

		StringExtractionMode(@Nonnull String display) {
			this.display = display;
		}

		/**
		 * @param text
		 * 		Input form.
		 *
		 * @return Mode matching input form name.
		 */
		@Nonnull
		public static StringExtractionMode get(@Nonnull String text) {
			if (text.equalsIgnoreCase(KNOWN_STRING_TYPES.name()) ||
					text.equalsIgnoreCase(KNOWN_STRING_TYPES.display)) {
				return KNOWN_STRING_TYPES;
			}
			return ANY_TYPE_TOSTRING;
		}

		/**
		 * @return Display name for mode.
		 */
		@Nonnull
		public String getDisplay() {
			return display;
		}

		@Override
		public String toString() {
			return display;
		}
	}
}
