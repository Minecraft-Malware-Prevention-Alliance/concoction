package info.mmpa.concoction.scan.model.dynamic.entry;

import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.execution.Locals;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Condition implementation for numeric parameter matching.
 */
public class NumericParameterCondition implements Condition {
	private static final Logger logger = LoggerFactory.getLogger(NumericParameterCondition.class);

	private static final NumberFormat NUM_FMT = NumberFormat.getInstance();

	/**
	 * Key for equality comparison.
	 */
	public static final String OP_EQUAL = "==";
	/**
	 * Key for non-equality comparison.
	 */
	public static final String OP_NOT_EQUAL = "!=";
	/**
	 * Key for greater-than comparison.
	 */
	public static final String OP_GREATER = ">";
	/**
	 * Key for greater-than-or-equal comparison.
	 */
	public static final String OP_GREATER_EQUAL = ">=";
	/**
	 * Key for less-than comparison.
	 */
	public static final String OP_LESS = "<";
	/**
	 * Key for less-than-or-equal comparison.
	 */
	public static final String OP_LESS_EQUAL = "<=";
	/**
	 * Key for bitwise and comparison, where a match is any non-zero value.
	 */
	public static final String OP_HAS_MASK = "&";
	/**
	 * Key for modulo comparison, where a match is any zero-remainder value.
	 */
	public static final String OP_IS_DIVISOR = "%";

	/**
	 * Array of all op names.
	 */
	private static final String[] OPS = {
			OP_EQUAL,
			OP_NOT_EQUAL,
			OP_GREATER,
			OP_GREATER_EQUAL,
			OP_LESS,
			OP_LESS_EQUAL,
			OP_HAS_MASK,
			OP_IS_DIVISOR
	};

	private final int index;
	private final ComparisonWithOp match;

	/**
	 * @param index
	 * 		Index to match against. Will be used as the left side of the comparison.
	 * @param match
	 * 		Comparison to make against.
	 */
	public NumericParameterCondition(int index, @Nonnull ComparisonWithOp match) {
		this.index = index;
		this.match = match;
	}

	/**
	 * @return Index to match against. Will be used as the left side of the comparison.
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return Name of comparison operation.
	 */
	@Nonnull
	public String getComparisonOperation() {
		return match.opName;
	}

	/**
	 * @return Value of comparison operation. Represents the right side of the comparison.
	 */
	@Nonnull
	public String getComparisonValue() {
		return match.opValue;
	}

	@Override
	public boolean match(@Nonnull CallStackFrame frame) {
		ExecutionContext<?> ctx = frame.getCtx();
		return match.compare(index, ctx.getLocals());
	}


	/**
	 * @param text
	 * 		Text to check.
	 *
	 * @return {@code true} when it starts with a numeric op.
	 */
	public static boolean startsWithOp(@Nonnull String text) {
		for (String op : OPS)
			if (text.startsWith(op)) return true;
		return false;
	}

	/**
	 * @param text
	 * 		Text format of comparison. Format is {@code operation value}.
	 *
	 * @return Comparison impl of operation for the provided value.
	 */
	@Nonnull
	public static ComparisonWithOp fromString(@Nonnull String text) {
		// 2 args required: <operation> <value>
		String[] args = text.split("\\s+");
		if (args.length < 2) {
			logger.error("Cannot convert '{}' to comparison impl, not in format '<operation> <numeric-value>'", text);
			return new ComparisonWithOp(Comparison.DUMMY, "?", "?");
		}

		// Determine type of value, create operation matcher
		String opStr = args[0];
		String valueStr = args[1];
		return new ComparisonWithOp(fromOpAndValue(opStr, valueStr), opStr, valueStr);
	}

	/**
	 * @param op
	 * 		Argument portion extracted from text.
	 * @param value
	 * 		Value portion extracted from text.
	 *
	 * @return Comparison impl of operation for the provided value.
	 */
	@Nonnull
	public static Comparison fromOpAndValue(@Nonnull String op, @Nonnull String value) {
		try {
			Number parsed = NUM_FMT.parse(value);

			if (parsed instanceof Integer) {
				return intComparison(op, parsed.intValue());
			} else if (parsed instanceof Float) {
				return floatComparison(op, parsed.floatValue());
			} else if (parsed instanceof Double) {
				return doubleComparison(op, parsed.doubleValue());
			} else if (parsed instanceof Long) {
				return longComparison(op, parsed.longValue());
			}

			logger.error("Cannot convert '{}' to comparison impl, unsupported numeric type: {}", value, parsed.getClass().getName());
			return Comparison.DUMMY;
		} catch (ParseException ex) {
			logger.error("Cannot convert '{}' to comparison impl, failed to parse value argument", value, ex);
			return Comparison.DUMMY;
		}
	}

	/**
	 * @param op
	 * 		Operation to use.
	 * @param value
	 * 		Value of right side of the comparison.
	 *
	 * @return Comparison impl of operation for the given value.
	 */
	@Nonnull
	public static FloatComparison floatComparison(@Nonnull String op, float value) {
		switch (op) {
			case OP_EQUAL:
				return v -> v == value;
			case OP_NOT_EQUAL:
				return v -> v != value;
			case OP_GREATER:
				return v -> v > value;
			case OP_GREATER_EQUAL:
				return v -> v >= value;
			case OP_LESS:
				return v -> v < value;
			case OP_LESS_EQUAL:
				return v -> v <= value;
			case OP_IS_DIVISOR:
				return v -> (v % value) == 0;
		}
		logger.error("Unknown float comparison operation '{}'", op);
		return FloatComparison.DUMMY;
	}

	/**
	 * @param op
	 * 		Operation to use.
	 * @param value
	 * 		Value of right side of the comparison.
	 *
	 * @return Comparison impl of operation for the given value.
	 */
	@Nonnull
	public static DoubleComparison doubleComparison(@Nonnull String op, double value) {
		switch (op) {
			case OP_EQUAL:
				return v -> v == value;
			case OP_NOT_EQUAL:
				return v -> v != value;
			case OP_GREATER:
				return v -> v > value;
			case OP_GREATER_EQUAL:
				return v -> v >= value;
			case OP_LESS:
				return v -> v < value;
			case OP_LESS_EQUAL:
				return v -> v <= value;
			case OP_IS_DIVISOR:
				return v -> (v % value) == 0;
		}
		logger.error("Unknown double comparison operation '{}'", op);
		return DoubleComparison.DUMMY;
	}

	/**
	 * @param op
	 * 		Operation to use.
	 * @param value
	 * 		Value of right side of the comparison.
	 *
	 * @return Comparison impl of operation for the given value.
	 */
	@Nonnull
	public static LongComparison longComparison(@Nonnull String op, long value) {
		switch (op) {
			case OP_EQUAL:
				return v -> v == value;
			case OP_NOT_EQUAL:
				return v -> v != value;
			case OP_GREATER:
				return v -> v > value;
			case OP_GREATER_EQUAL:
				return v -> v >= value;
			case OP_LESS:
				return v -> v < value;
			case OP_LESS_EQUAL:
				return v -> v <= value;
			case OP_HAS_MASK:
				return v -> (v & value) != 0;
			case OP_IS_DIVISOR:
				return v -> (v % value) == 0;
		}
		logger.error("Unknown long comparison operation '{}'", op);
		return LongComparison.DUMMY;
	}

	/**
	 * @param op
	 * 		Operation to use.
	 * @param value
	 * 		Value of right side of the comparison.
	 *
	 * @return Comparison impl of operation for the given value.
	 */
	@Nonnull
	public static IntComparison intComparison(@Nonnull String op, int value) {
		switch (op) {
			case OP_EQUAL:
				return v -> v == value;
			case OP_NOT_EQUAL:
				return v -> v != value;
			case OP_GREATER:
				return v -> v > value;
			case OP_GREATER_EQUAL:
				return v -> v >= value;
			case OP_LESS:
				return v -> v < value;
			case OP_LESS_EQUAL:
				return v -> v <= value;
			case OP_HAS_MASK:
				return v -> (v & value) != 0;
			case OP_IS_DIVISOR:
				return v -> (v % value) == 0;
		}
		logger.error("Unknown int comparison operation '{}'", op);
		return IntComparison.DUMMY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NumericParameterCondition that = (NumericParameterCondition) o;

		if (index != that.index) return false;
		return match.equals(that.match);
	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + match.hashCode();
		return result;
	}

	/**
	 * Base float comparison outline.
	 */
	public interface FloatComparison extends Comparison {
		/**
		 * Dummy that never matches.
		 */
		FloatComparison DUMMY = (v) -> false;

		@Override
		default boolean compare(int index, @Nonnull Locals locals) {
			float value = locals.loadFloat(index);
			return compare(value);
		}

		/**
		 * @param value
		 * 		Left side value of comparison.
		 *
		 * @return {@code true} on match success.
		 */
		boolean compare(float value);
	}

	/**
	 * Base double comparison outline.
	 */
	public interface DoubleComparison extends Comparison {
		/**
		 * Dummy that never matches.
		 */
		DoubleComparison DUMMY = (v) -> false;

		@Override
		default boolean compare(int index, @Nonnull Locals locals) {
			double value = locals.loadDouble(index);
			return compare(value);
		}

		/**
		 * @param value
		 * 		Left side value of comparison.
		 *
		 * @return {@code true} on match success.
		 */
		boolean compare(double value);
	}

	/**
	 * Base long comparison outline.
	 */
	public interface LongComparison extends Comparison {
		/**
		 * Dummy that never matches.
		 */
		LongComparison DUMMY = (v) -> false;

		@Override
		default boolean compare(int index, @Nonnull Locals locals) {
			long value = locals.loadLong(index);
			return compare(value);
		}

		/**
		 * @param value
		 * 		Left side value of comparison.
		 *
		 * @return {@code true} on match success.
		 */
		boolean compare(long value);
	}

	/**
	 * Base int comparison outline.
	 */
	public interface IntComparison extends Comparison {
		/**
		 * Dummy that never matches.
		 */
		IntComparison DUMMY = (v) -> false;

		@Override
		default boolean compare(int index, @Nonnull Locals locals) {
			int value = locals.loadInt(index);
			return compare(value);
		}

		/**
		 * @param value
		 * 		Left side value of comparison.
		 *
		 * @return {@code true} on match success.
		 */
		boolean compare(int value);
	}

	/**
	 * Delegating implementation of {@link Comparison} with an associated name.
	 */
	public static class ComparisonWithOp implements Comparison {
		private final Comparison comparison;
		private final String opName;
		private final String opValue;

		/**
		 * @param comparison
		 * 		Delegate comparison target.
		 * @param opName
		 * 		Name of comparison operation.
		 * @param opValue
		 * 		Value to compare against.
		 */
		public ComparisonWithOp(@Nonnull Comparison comparison, @Nonnull String opName, @Nonnull String opValue) {
			this.comparison = comparison;
			this.opName = opName;
			this.opValue = opValue;
		}

		@Override
		public boolean compare(int index, @Nonnull Locals locals) {
			return comparison.compare(index, locals);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ComparisonWithOp that = (ComparisonWithOp) o;

			if (!opName.equals(that.opName)) return false;
			return opValue.equals(that.opValue);
		}

		@Override
		public int hashCode() {
			int result = opName.hashCode();
			result = 31 * result + opValue.hashCode();
			return result;
		}
	}

	/**
	 * Base comparison outline.
	 */
	public interface Comparison {
		/**
		 * Dummy that never matches.
		 */
		Comparison DUMMY = (index, locals) -> false;

		/**
		 * @param index
		 * 		Index of variable to load.
		 * @param locals
		 * 		Local variable table reference to load from.
		 *
		 * @return {@code true} on match success.
		 */
		boolean compare(int index, @Nonnull Locals locals);
	}
}
