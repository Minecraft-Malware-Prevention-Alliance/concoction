package info.mmpa.concoction.scan.model.dynamic.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.scan.model.MultiMatchMode;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Base multi-condition outline.
 *
 * @see AllMultiCondition
 * @see AnyMultiCondition
 * @see NoneMultiCondition
 */
public abstract class MultiCondition implements Condition {
	@JsonSerialize(contentUsing = ConditionSerializer.class)
	@JsonDeserialize(contentUsing = ConditionDeserializer.class)
	private final List<Condition> conditions;
	private final MultiMatchMode mode;

	/**
	 * @param mode
	 * 		Match mode of the condition implementation.
	 * 		Concrete implementations should pass a constant value.
	 * @param conditions
	 * 		Conditions wrapped to match against.
	 */
	public MultiCondition(@Nonnull MultiMatchMode mode, @Nonnull List<Condition> conditions) {
		this.mode = mode;
		this.conditions = conditions;
	}

	/**
	 * @return Conditions to wrap. Behavior changes based on the {@link #getMode() mode}.
	 */
	@Nonnull
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * @return Mode which determines the subtype.
	 */
	@Nonnull
	public MultiMatchMode getMode() {
		return mode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MultiCondition that = (MultiCondition) o;

		if (!conditions.equals(that.conditions)) return false;
		return mode == that.mode;
	}

	@Override
	public int hashCode() {
		int result = conditions.hashCode();
		result = 31 * result + mode.hashCode();
		return result;
	}
}
