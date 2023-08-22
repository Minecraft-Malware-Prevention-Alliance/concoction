package info.mmpa.concoction.scan.insn;

import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.path.ClassPathElement;
import info.mmpa.concoction.input.model.path.MethodPathElement;
import info.mmpa.concoction.input.model.path.SourcePathElement;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.sink.*;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.util.AsmUtil;
import info.mmpa.concoction.util.ScanCancelException;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Basic ASM pattern matching based scanning.
 */
public class InstructionScanner {
	private static final Logger logger = LoggerFactory.getLogger(InstructionScanner.class);
	private final Collection<ScanModel> scanModels;
	private final FeedbackSink feedbackSink;

	/**
	 * @param scanModels
	 * 		List of detection models to scan for.
	 */
	public InstructionScanner(@Nonnull Collection<ScanModel> scanModels) {
		this(scanModels, null);
	}

	/**
	 * @param scanModels
	 * 		List of detection models to scan for.
	 * @param feedbackSink
	 * 		Optional sink for completion status / feedback.
	 */
	public InstructionScanner(@Nonnull Collection<ScanModel> scanModels, @Nullable FeedbackSink feedbackSink) {
		this.scanModels = scanModels;
		this.feedbackSink = feedbackSink == null ? new NoopFeedbackSink() : feedbackSink;
	}

	/**
	 * @param model
	 * 		Model to scan.
	 *
	 * @return Detection results found in the model's primary source.
	 */
	@Nonnull
	public Results accept(@Nonnull ApplicationModel model) {
		ResultsSink sink = new BasicResultsSink();
		ModelSource source = model.primarySource();
		SourcePathElement sourcePath = source.path();

		// Iterate over classes and pass along to configured detection finders.
		for (Map.Entry<String, byte[]> classEntry : source.classes().entrySet()) {
			String className = classEntry.getKey();
			ClassPathElement classPath = sourcePath.child(className);
			FeedbackSink.InstructionFeedbackItemSink classFeedbackSink = feedbackSink.openClassFeedbackSink(classPath);
			try {
				ClassNode classNode = AsmUtil.node(classEntry.getValue());
				scanClass(sink, classPath, classNode, classFeedbackSink);
			} catch (ScanCancelException cancel) {
				// Stop and yield current results.
				return sink.buildResults();
			} catch (Throwable t) {
				logger.error("Error occurred handling scanning at {}", classPath, t);

				// Pipe errors to sink.
				//
				// This shouldn't be necessary as ASM crashes should be patched by this point, but we're going to
				// take all the precautions we can.
				if (classFeedbackSink != null)
					classFeedbackSink.onScanError(t);
			}
		}

		// Build results from what we found
		return sink.buildResults();
	}

	private void scanClass(@Nonnull ResultsSink aggregateSink, @Nonnull ClassPathElement classPath,
						   @Nonnull ClassNode classNode, @Nullable FeedbackSink.InstructionFeedbackItemSink classFeedbackSink) throws ScanCancelException {
		// If given, tell the feedback listener the class has been parsed.
		if (classFeedbackSink != null)
			classFeedbackSink.onPreScan(classNode);

		// If we have a feedback listener, split off the results so things found within the class
		// can be collected into a unique results map, while also being added to the aggregate.
		ResultsSink sink = (classFeedbackSink == null) ? aggregateSink : new SplittingResultSink(aggregateSink);

		// TODO #4: Class structure matchers, then refactor this class name to 'StaticScanner'

		// Run per-method matchers (instruction matching models)
		for (MethodNode methodNode : classNode.methods) {
			MethodPathElement methodPath = classPath.child(methodNode);
			if (methodNode.instructions == null) continue;
			for (ScanModel scanModel : scanModels) {
				if (feedbackSink != null && feedbackSink.isCancelRequested())
					throw new ScanCancelException();
				InstructionsMatchingModel matchingModel = scanModel.getInstructionsMatchingModel();
				matchingModel.match(sink, scanModel.getDetectionArchetype(), methodPath, classNode, methodNode);
			}
		}

		// The sink from before will be the split sink, so we can tell our feedback sink
		// about the detection results specific to this class.
		if (classFeedbackSink != null)
			classFeedbackSink.onCompletion(sink.buildResults());
	}
}
