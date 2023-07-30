package info.mmpa.concoction.util;

import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.scan.model.insn.InstructionMatchEntry;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;

import static info.mmpa.concoction.util.Unchecked.map;

/**
 * Delegates to {@link Serialization} but calls {@link Assertions#fail()} when any errors occur.
 */
public class TestSerialization {
	@Nonnull
	public static InstructionMatchEntry deserializeInsnEntry(@Nonnull String json) {
		return map(Serialization::deserializeInsnEntry, json);
	}

	@Nonnull
	public static ScanModel deserializeModel(@Nonnull String json) {
		return map(Serialization::deserializeModel, json);
	}

	@Nonnull
	public static String serialize(@Nonnull Object value) {
		return map(Serialization::serialize, value);
	}
}
