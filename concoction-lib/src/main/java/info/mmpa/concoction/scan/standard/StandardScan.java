package info.mmpa.concoction.scan.standard;

import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;
import info.mmpa.concoction.model.path.ClassPathElement;
import info.mmpa.concoction.model.path.MethodPathElement;
import info.mmpa.concoction.model.path.SourcePathElement;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.ResultsSink;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.util.AsmUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Basic ASM pattern matching based scanning.
 */
public class StandardScan {
	private final Collection<ScanModel> scanModels;

	/**
	 * @param scanModels
	 * 		List of detection models to scan for.
	 */
	public StandardScan(@Nonnull Collection<ScanModel> scanModels) {
		this.scanModels = scanModels;
	}

	/**
	 * @param model
	 * 		Model to scan.
	 *
	 * @return Detection results found in the model's primary source.
	 */
	@Nonnull
	public Results accept(@Nonnull ApplicationModel model) {
		ResultsSink sink = new ResultsSink();
		ModelSource source = model.primarySource();
		SourcePathElement sourcePath = new SourcePathElement(source);

		// Iterate over classes and pass along to configured detection finders.
		for (Map.Entry<String, byte[]> classEntry : source.classes().entrySet()) {
			String className = classEntry.getKey();
			ClassPathElement classPath = sourcePath.child(className);
			try {
				ClassNode classNode = AsmUtil.node(classEntry.getValue());

				// TODO #4: Class structure matchers

				// Run per-method matchers (instruction matching models)
				for (MethodNode methodNode : classNode.methods) {
					MethodPathElement methodPath = classPath.child(methodNode);
					if (methodNode.instructions == null) continue;
					for (ScanModel scanModel : scanModels) {
						InstructionsMatchingModel matchingModel = scanModel.getInstructionsMatchingModel();
						matchingModel.match(sink, scanModel.getDetectionArchetype(), methodPath, classNode, methodNode);
					}
				}
			} catch (Throwable ex) {
				// Pipe errors to sink.
				//
				// This shouldn't be necessary as ASM crashes should be patched by this point, but we're going to
				// take all the precautions we can.
				sink.error(classPath, ex);
			}
		}

		// Build results from what we found
		return sink.buildResults();
	}
}
