package info.mmpa.concoction.scan.model.dynamic.entry;

import info.mmpa.concoction.scan.dynamic.CallStackFrame;

import javax.annotation.Nonnull;

/**
 * A dynamic matcher that matches a {@link Condition} in a given {@link MethodLocation}.
 */
public class SingleConditionCheckingDynamic implements DynamicMatchEntry {
	private final MethodLocation location;
	private final Condition condition;
	private final When when;

	/**
	 * @param location
	 * 		Where this match applies to.
	 * @param condition
	 * 		Condition to match against.
	 * @param when
	 * 		When this condition should be checked.
	 */
	public SingleConditionCheckingDynamic(@Nonnull MethodLocation location, @Nonnull Condition condition,
										  @Nonnull When when) {
		this.location = location;
		this.condition = condition;
		this.when = when;
	}

	@Override
	public boolean matchOnEnter(@Nonnull CallStackFrame frame) {
		if (when == When.ENTRY) {
			if (!location.match(frame)) return false;
			return condition.match(frame);
		}
		return false;
	}

	@Override
	public boolean matchOnExit(@Nonnull CallStackFrame frame) {
		if (when == When.RETURN) {
			if (!location.match(frame)) return false;
			return condition.match(frame);
		}
		return false;
	}

	/**
	 * @return Where this match applies to.
	 */
	@Nonnull
	public MethodLocation getLocation() {
		return location;
	}

	/**
	 * @return Condition to match against.
	 */
	@Nonnull
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @return When this condition should be checked.
	 */
	@Nonnull
	public When getWhen() {
		return when;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SingleConditionCheckingDynamic that = (SingleConditionCheckingDynamic) o;

		if (!location.equals(that.location)) return false;
		if (!condition.equals(that.condition)) return false;
		return when == that.when;
	}

	@Override
	public int hashCode() {
		int result = location.hashCode();
		result = 31 * result + condition.hashCode();
		result = 31 * result + when.hashCode();
		return result;
	}
}
