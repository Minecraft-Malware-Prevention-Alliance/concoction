package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.VirtualMachine;
import dev.xdark.ssvm.api.MethodInvoker;
import dev.xdark.ssvm.api.VMInterface;
import dev.xdark.ssvm.classloading.SupplyingClassLoaderInstaller;
import dev.xdark.ssvm.invoke.InvocationUtil;
import dev.xdark.ssvm.mirror.type.InstanceClass;
import dev.xdark.ssvm.thread.OSThread;
import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.path.MethodPathElement;
import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.input.model.path.SourcePathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.sink.DelegatingResultsSink;
import info.mmpa.concoction.output.sink.FeedbackSink;
import info.mmpa.concoction.output.sink.ResultsSink;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.util.Encapsulation;
import info.mmpa.concoction.util.ScanCancelException;
import info.mmpa.concoction.util.Unchecked;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;

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
	private final FeedbackSink.DynamicFeedbackItemSink dynamicFeedbackSink;

	/**
	 * @param model
	 * 		Model to pass to the VM.
	 * @param aggregateSink
	 * 		Sink to feed match results into.
	 * @param sourcePath
	 * 		Current method path to the containing input source.
	 * 		SSVM will pass along the rest of the path details.
	 * @param scanModel
	 * 		List of detection models to scan for.
	 * @param feedbackSink
	 * 		Sink for completion status / feedback.
	 */
	public SsvmContext(@Nonnull ApplicationModel model,
					   @Nonnull ResultsSink aggregateSink,
					   @Nonnull SourcePathElement sourcePath,
					   @Nonnull Collection<ScanModel> scanModel,
					   @Nonnull FeedbackSink feedbackSink) {
		// Create and initialize the VM.
		VirtualMachine vm = new VirtualMachineExt();
		vm.getProperties().put("java.class.path", ""); // Hide class path of concoction from the VM
		vm.bootstrap();

		// Create shared dynamic sink instance.
		dynamicFeedbackSink = feedbackSink.openDynamicFeedbackSink();
		ResultsSink sink;
		if (dynamicFeedbackSink != null) {
			sink = new DelegatingResultsSink(aggregateSink) {
				@Override
				public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
					super.onDetection(path, type, detection);
					Unchecked.runSafe("dynamic-sink-feed", () -> dynamicFeedbackSink.onDetection(path, type, detection));
				}
			};
		} else {
			sink = aggregateSink;
		}

		// Configure method enter-exit listeners for matching scoped call rules
		VMInterface vmi = vm.getInterface();
		vmi.registerMethodEnterListener(ctx -> {
			if (feedbackSink.isCancelRequested())
				throw new ScanCancelException();

			OSThread thread = vm.currentJavaThread().getOsThread();
			Stack<CallStackFrame> stack = threadFrameMap.computeIfAbsent(thread, t -> new Stack<>());
			CallStackFrame frame = new CallStackFrame(ctx);
			stack.push(frame);

			// TODO: When we match something in a path that does not belong to the application model
			//  we should walk the stack back until we are in-scope of the model and then mark that as
			//  the location of the match. For instance, if we match the string parameter of some method in
			//  'java/net/URL' we want the method path of the match to be our application code that is responsible
			//  for calling that method, not the method itself.
			//   (this applies for method exit too)

			if (dynamicFeedbackSink != null)
				Unchecked.runSafe("dynamic-sink-feed", () -> dynamicFeedbackSink.onMethodEnter(Collections.unmodifiableList(stack), frame));

			MethodPathElement methodPath = sourcePath
					.child(frame.getOwnerName())
					.child(frame.getMethodName(), frame.getMethodDesc());
			for (ScanModel modelEntry : scanModel) {
				DetectionArchetype archetype = modelEntry.getDetectionArchetype();
				modelEntry.getDynamicMatchingModel().matchOnEnter(sink, archetype, methodPath, frame);
			}
		});
		vmi.registerMethodExitListener(ctx -> {
			OSThread thread = vm.currentJavaThread().getOsThread();
			Stack<CallStackFrame> stack = threadFrameMap.get(thread);
			if (stack == null || stack.isEmpty())
				throw new IllegalArgumentException("Cannot pop call stack frame from thread with no prior stack history");
			CallStackFrame frame = stack.pop();

			if (dynamicFeedbackSink != null)
				Unchecked.runSafe("dynamic-sink-feed", () -> dynamicFeedbackSink.onMethodExit(Collections.unmodifiableList(stack), frame));

			MethodPathElement methodPath = sourcePath
					.child(frame.getOwnerName())
					.child(frame.getMethodName(), frame.getMethodDesc());
			for (ScanModel modelEntry : scanModel) {
				DetectionArchetype archetype = modelEntry.getDetectionArchetype();
				modelEntry.getDynamicMatchingModel().matchOnExit(sink, archetype, methodPath, frame);
			}
		});

		// Some patches to circumvent bugs arising from VM implementation changes in later versions
		if (vm.getJvmVersion() > 8) {
			// Bug in SSVM makes it think there are overlapping sleeps, so until that gets fixed we stub out sleeping.
			InstanceClass thread = vm.getSymbols().java_lang_Thread();
			vmi.setInvoker(thread.getMethod("sleep", "(J)V"), MethodInvoker.noop());

			// SSVM manages its own memory, and this conflicts with it. Stubbing it out keeps everyone happy.
			InstanceClass bits = (InstanceClass) vm.findBootstrapClass("java/nio/Bits");
			if (bits != null) vmi.setInvoker(bits.getMethod("reserveMemory", "(JJ)V"), MethodInvoker.noop());
		}

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
	 * Called from {@link DynamicScanner} upon scan completion.
	 *
	 * @param results
	 * 		Managed results for this execution.
	 */
	public void onScanComplete(@Nonnull Results results) {
		if (dynamicFeedbackSink != null)
			Unchecked.runSafe("dynamic-sink-feed", () -> dynamicFeedbackSink.onCompletion(results));
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

	static {
		Encapsulation.unlock();
	}
}
