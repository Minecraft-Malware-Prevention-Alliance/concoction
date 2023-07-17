package info.mmpa.concoction.scan.model.insn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import software.coley.collections.delegate.DelegatingList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper type modeling a {@link List} of custom-serialization enabled entries.
 */
@JsonDeserialize(contentUsing = InstructionMatchEntryDeserializer.class)
@JsonSerialize(contentUsing = InstructionMatchEntrySerializer.class)
public class InstructionMatchingList extends DelegatingList<InstructionMatchEntry> {
	/**
	 * Empty matching list.
	 */
	public InstructionMatchingList() {
		this(new ArrayList<>());
	}

	/**
	 * Pre-populated matching list.
	 *
	 * @param delegate
	 * 		Delegate list to wrap.
	 */
	public InstructionMatchingList(@Nonnull List<InstructionMatchEntry> delegate) {
		super(delegate);
	}
}
