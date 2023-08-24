package info.mmpa.concoction.scan.model.insn;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import info.mmpa.concoction.input.model.path.MethodPathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.sink.ResultsSink;
import info.mmpa.concoction.scan.model.insn.entry.*;
import info.mmpa.concoction.util.Unchecked;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Model representing pattern matching for a {@link DetectionArchetype detection archetype} against a series of
 * instructions in method bodies.
 * <br>
 * The model may have one or more variants describing different signature techniques.
 */
@JsonDeserialize(converter = InstructionsMatchingModelDeserializingConverter.class)
@JsonSerialize(converter = InstructionsMatchingModelSerializingConverter.class)
public class InstructionsMatchingModel {
	private final Map<String, InstructionMatchingList> variants;

	/**
	 * @param variants
	 * 		Map of variants to detect the pattern.
	 * 		Map values are lists of instruction matchers forming a single signature.
	 */
	public InstructionsMatchingModel(@Nonnull Map<String, List<InstructionMatchEntry>> variants) {
		this.variants = Collections.unmodifiableMap(variants.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> new InstructionMatchingList(e.getValue()))));
	}

	/**
	 * @param sink
	 * 		Sink to feed match results into.
	 * @param archetype
	 * 		Information about what the signature being matched.
	 * @param methodPath
	 * 		Current method path to pass into the sink.
	 * @param classNode
	 * 		Class defining the method.
	 * @param methodNode
	 * 		The method being scanned.
	 */
	public void match(@Nonnull ResultsSink sink, DetectionArchetype archetype, @Nonnull MethodPathElement methodPath,
					  @Nonnull ClassNode classNode, @Nonnull MethodNode methodNode) {
		// Skip methods without code
		if (methodNode.instructions == null) return;

		// Scan with each variant
		for (List<InstructionMatchEntry> entries : variants.values())
			matchVariant(sink, archetype, methodPath, methodNode, entries);
	}

	private void matchVariant(@Nonnull ResultsSink sink,
							  @Nonnull DetectionArchetype archetype,
							  @Nonnull MethodPathElement methodPath,
							  @Nonnull MethodNode methodNode,
							  @Nonnull List<InstructionMatchEntry> entries) {
		// Iterate over instructions and match against the matcher entries.
		// A match will be reported if all entries successfully match in a row for some range of instructions.
		//
		// If there are 5 entries, and the 3rd entry does not match the chain is reset and we move the current index
		// to where the 1st entry matched, plus one index.
		//
		// Multiple matches can be made in a method.
		int matchStart = -1;
		int matchIndex = 0;
		int matchTargetLength = entries.size();
		int matchWildcardIndex = 0;
		int matchWildcardLength = 0;
		AbstractInsnNode[] array = methodNode.instructions.toArray();
		for (int i = 0; i < array.length; i++) {
			AbstractInsnNode insn = array[i];
			if (insn.getOpcode() < 0) continue; // Skip labels
			InstructionMatchEntry matcher = entries.get(matchIndex);

			// Track where the start of a match begins.
			if (matchIndex == 0) matchStart = i;

			// Track if the current instruction matches the current matcher index.
			if (matcher instanceof Instruction || matcher instanceof MultiInstruction) {
				if (matcher.match(methodNode, insn)) {
					// Match sequence at index is good, move on to next match entry.
					matchIndex++;

					// Check if the match is complete.
					if (matchIndex >= matchTargetLength) {
						// Report the detection
						Unchecked.runSafe("insn-sink-feed", () -> sink.onDetection(methodPath, archetype, new Detection(archetype, methodPath)));

						// Match found, jump back to where the match began plus one index
						// and reset the matcher index. This allows items matched by 'i > 0' to
						// be checked against for 'i -1 > 0' matchers on the next go.
						if (matchStart != i)
							i = matchStart + 1;
						matchIndex = 0;
					}
				} else {
					// Allow wildcard to pick up the slack
					if (matchWildcardIndex++ < matchWildcardLength) {
						// Wildcard usage fills in the gap
					} else {
						// Match broken, jump back to where the match began plus one index
						// and reset the matcher index. This allows items matched by 'i > 0' to
						// be checked against for 'i -1 > 0' matchers on the next go.
						if (matchStart != i)
							i = matchStart + 1;
						matchIndex = 0;
					}
				}
			} else if (matcher instanceof InstructionWildcard) {
				// Reset wildcard tracking to allow one match
				matchWildcardIndex = 0;
				matchWildcardLength = 1;
				matchIndex++;
			} else if (matcher instanceof InstructionWildcardMulti) {
				// Reset wildcard tracking to allow multiple matches
				InstructionWildcardMulti wildcardMultiMatcher = InstructionWildcardMulti.get(matchWildcardLength);
				matchWildcardIndex = 0;
				matchWildcardLength = wildcardMultiMatcher.isAnyCount() ?
						Integer.MAX_VALUE :
						wildcardMultiMatcher.getCount();
				matchIndex++;
			}
		}
	}

	/**
	 * @return Map of variants to detect the pattern.
	 */
	@Nonnull
	public Map<String, InstructionMatchingList> getVariants() {
		return variants;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InstructionsMatchingModel that = (InstructionsMatchingModel) o;

		return variants.equals(that.variants);
	}

	@Override
	public int hashCode() {
		return variants.hashCode();
	}

	@Override
	public String toString() {
		return "InstructionsMatchingModel{variants[" + variants.size() + "]}";
	}
}
