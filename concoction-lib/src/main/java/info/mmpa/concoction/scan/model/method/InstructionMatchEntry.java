package info.mmpa.concoction.scan.model.method;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;

@JsonSerialize(using = InstructionMatchEntrySerializer.class)
@JsonDeserialize(using = InstructionMatchEntryDeserializer.class)
public interface InstructionMatchEntry {
	boolean match(@Nonnull MethodNode method, @Nonnull AbstractInsnNode insn);
}
