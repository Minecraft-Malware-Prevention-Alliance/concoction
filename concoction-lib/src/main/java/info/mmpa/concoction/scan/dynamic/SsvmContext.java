package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller;
import dev.xdark.ssvm.invoke.InvocationUtil;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;

import javax.annotation.Nonnull;
import java.io.IOException;

import static dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller.install;
import static dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller.supplyFromMaps;

/**
 * Wrapper holding the virtual machine instance and supporting helpers.
 */
public class SsvmContext {
	private final VirtualMachine vm;
	private final SupplyingClassLoaderInstaller.Helper loader;
	private final InvocationUtil invoker;

	/**
	 * @param model
	 * 		Model to pass to the VM.
	 */
	public SsvmContext(@Nonnull ApplicationModel model) {
		// Create and initialize the VM.
		VirtualMachine vm = new VirtualMachine() {
			// TODO: Override file manager to create dummy file handles
			//  - Allow tracking of IO operations, without having them fail due to the default impl
			//    giving all file handles a value of '0'
		};
		vm.bootstrap();
		this.vm = vm;

		// Create a loader capable of pulling classes and files from the model.
		try {
			ModelSource flatSourceModel = model.flatView();
			loader = install(vm, supplyFromMaps(flatSourceModel.classes(), flatSourceModel.files()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Create a method invocation helper.
		invoker = InvocationUtil.create(vm);
	}

	/**
	 * @return The VM instance.
	 */
	@Nonnull
	public VirtualMachine getVm() {
		return vm;
	}

	/**
	 * @return The class loader helper which pulls from our {@link ApplicationModel#primarySource()}.
	 */
	@Nonnull
	public SupplyingClassLoaderInstaller.Helper getLoaderHelper() {
		return loader;
	}

	/**
	 * @return Helper to more easily invoke methods in the VM.
	 */
	@Nonnull
	public InvocationUtil getInvoker() {
		return invoker;
	}
}
