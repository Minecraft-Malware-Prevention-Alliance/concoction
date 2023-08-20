package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.execution.ExecutionContext;
import dev.xdark.ssvm.filesystem.FileManager;
import dev.xdark.ssvm.mirror.type.JavaClass;
import dev.xdark.ssvm.value.NullValue;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;

/**
 * Basic extension of the VM class, tweaking some default manager implementations
 * and providing access to additional data.
 */
public class VirtualMachineExt extends VirtualMachine {
	private JavaClass charSequence;

	@Override
	public void bootstrap() {
		super.bootstrap();
		charSequence = findBootstrapClass("java/lang/CharSequence");
		getInterface().setLinkageErrorHandler(VirtualMachineExt::handleLinkageError);
	}

	@Override
	protected FileManager createFileManager() {
		return new CustomFileManager();
	}

	/**
	 * @return {@link CharSequence} type.
	 */
	@Nonnull
	public JavaClass getCharSequence() {
		return charSequence;
	}


	/**
	 * Fills the result
	 * @param ctx Execution context which encountered an unlinked native method.
	 */
	private static void handleLinkageError(@Nonnull ExecutionContext<?> ctx) {
		String retDesc = ctx.getMethod().getReturnType().getDescriptor();
		Type retType = Type.getType(retDesc);
		switch (retType.getSort()) {
			case Type.VOID:
				break;
			case Type.BOOLEAN:
			case Type.CHAR:
			case Type.BYTE:
			case Type.SHORT:
			case Type.INT:
				ctx.setResult(0);
				break;
			case Type.FLOAT:
				ctx.setResult(0F);
				break;
			case Type.LONG:
				ctx.setResult(0L);
				break;
			case Type.DOUBLE:
				ctx.setResult(0D);
				break;
			case Type.ARRAY:
			case Type.OBJECT:
				ctx.setResult(ctx.getVM().getMemoryManager().nullValue());
				break;
			default:
				throw new IllegalStateException("Unsupported method return type: " + retDesc);
		}
	}
}
