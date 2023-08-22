package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.path.ClassPathElement;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.dynamic.CallStackFrame;
import info.mmpa.concoction.scan.dynamic.DynamicScanner;
import info.mmpa.concoction.scan.insn.InstructionScanner;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Outline of sink capabilities intended for status updates / user feedback.
 */
public interface FeedbackSink {
	/**
	 * Occasionally called by the scanning processes to check if the scan should be aborted.
	 * <p>
	 * When a sink is implemented by a user, if they wish to cancel a scan all they need to do is make this
	 * return {@code true}. By default, it will be consistently {@code false}.
	 *
	 * @return {@code true} to signal the current scanner to abort its work.
	 */
	default boolean isCancelRequested() {
		return false;
	}

	/**
	 * Called when a new class is being scanned.
	 *
	 * @param classPath
	 * 		Path to class in the containing {@link ApplicationModel}.
	 *
	 * @return Sub-sink for results specific to the provided class.
	 * Can be {@code null} to ignore feedback.
	 */
	@Nullable
	InstructionFeedbackItemSink openClassFeedbackSink(@Nonnull ClassPathElement classPath);

	/**
	 * @return Sub-sink for dynamic analysis feedback. Associated with a single VM execution.
	 */
	@Nullable
	DynamicFeedbackItemSink openDynamicFeedbackSink();

	/**
	 * Sink for {@link DynamicScanner}, not tied to any specific class.
	 */
	interface DynamicFeedbackItemSink extends CommonSink {
		/**
		 * Called when entering a method.
		 *
		 * @param stack
		 * 		Current method call stack, with entered method on the top.
		 * @param enteredMethodFrame
		 * 		Frame of entered method.
		 */
		void onMethodEnter(@Nonnull List<CallStackFrame> stack, @Nonnull CallStackFrame enteredMethodFrame);

		/**
		 * Called when exiting a method.
		 *
		 * @param stack
		 * 		Current method call stack, with exited method removed.
		 * @param exitedMethodFrame
		 * 		Frame of exited method.
		 */
		void onMethodExit(@Nonnull List<CallStackFrame> stack, @Nonnull CallStackFrame exitedMethodFrame);

		/**
		 * Called when scanning for the current VM context is completed.
		 *
		 * @param results
		 * 		Results for all items within the VM execution.
		 */
		void onCompletion(@Nonnull Results results);
	}

	/**
	 * Sink per class for {@link InstructionScanner} feedback.
	 */
	interface InstructionFeedbackItemSink extends CommonSink {
		/**
		 * Called before scan operations start for this class.
		 *
		 * @param classNode
		 * 		Class model.
		 */
		void onPreScan(@Nonnull ClassNode classNode);

		/**
		 * Called when the class could not be scanned.
		 *
		 * @param t
		 * 		Reason for scan failure.
		 */
		void onScanError(@Nonnull Throwable t);

		/**
		 * Called when scanning for the current class is completed.
		 *
		 * @param results
		 * 		Results for all items within the class.
		 */
		void onCompletion(@Nonnull Results results);
	}
}
