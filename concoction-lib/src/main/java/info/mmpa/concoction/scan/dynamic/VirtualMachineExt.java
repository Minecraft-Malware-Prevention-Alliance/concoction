package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.filesystem.FileManager;
import dev.xdark.ssvm.mirror.type.JavaClass;

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
}
