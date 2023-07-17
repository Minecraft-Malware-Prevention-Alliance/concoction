package info.mmpa.concoction.scan.model;

import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.scan.model.behavior.BehaviorMatchingModel;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Outline of matching models.
 *
 * @param <T>
 * 		Content type to match against.
 *
 * @see InstructionsMatchingModel
 * @see BehaviorMatchingModel
 */
public interface MatchingModel<T> {
	/**
	 * @return Information about what the signature is matching.
	 */
	@Nonnull
	DetectionArchetype getArchetype();

	/**
	 * @return Map of variants to detect the pattern.
	 */
	@Nonnull
	Map<String, T> getVariants();
}
