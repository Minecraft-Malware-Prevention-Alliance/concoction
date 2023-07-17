package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.api.VMInterface;
import dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller;
import dev.xdark.ssvm.invoke.InvocationUtil;
import dev.xdark.ssvm.thread.OSThread;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

import static dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller.install;
import static dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller.supplyFromMaps;

/**
 * Wrapper holding the virtual machine instance and supporting helpers.
 */
public class SsvmContext {
	private final Map<OSThread, Stack<CallStackFrame>> threadFrameMap = new IdentityHashMap<>();
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
			//  - Default yields same file handle for all operations, which is not great
		};
		vm.bootstrap();

		// Configure method enter-exit listeners for matching scoped call rules
		VMInterface vmi = vm.getInterface();
		vmi.registerMethodEnterListener(ctx -> {
			OSThread thread = vm.currentJavaThread().getOsThread();
			Stack<CallStackFrame> stack = threadFrameMap.computeIfAbsent(thread, t -> new Stack<>());
			stack.push(new CallStackFrame(ctx));

			// TODO: Only push/pop frames within the scope of our EntryPoint
		});
		vmi.registerMethodExitListener(ctx -> {
			OSThread thread = vm.currentJavaThread().getOsThread();
			Stack<CallStackFrame> stack = threadFrameMap.get(thread);
			if (stack == null || stack.isEmpty())
				throw new IllegalArgumentException("Cannot pop call stack frame from thread with no prior stack history");
			stack.pop();
		});

		// Store VM instance.
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
