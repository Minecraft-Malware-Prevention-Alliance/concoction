package info.mmpa.concoction.scan.standard;

import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;
import info.mmpa.concoction.model.path.ClassPathElement;
import info.mmpa.concoction.model.path.MethodPathElement;
import info.mmpa.concoction.model.path.SourcePathElement;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.ResultsSink;
import info.mmpa.concoction.scan.model.method.MethodMatchingModel;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Basic ASM pattern matching based scanning.
 */
public class StandardScan {
	private final List<MethodMatchingModel> models;

	/**
	 * @param models
	 * 		List of detection models to scan for.
	 */
	public StandardScan(@Nonnull List<MethodMatchingModel> models) {
		this.models = models;
	}

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
				ClassNode classNode = node(classEntry.getValue());
				for (MethodNode methodNode : classNode.methods) {
					MethodPathElement methodPath = classPath.child(methodNode);
					if (methodNode.instructions == null) continue;
					for (MethodMatchingModel matchingModel : models)
						matchingModel.match(sink, methodPath, classNode, methodNode);
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

	@Nonnull
	private static ClassNode node(@Nonnull byte[] value) {
		// Frames are useless to us, and we can save performance by skipping them too.
		ClassNode node = new ClassNode();
		new ClassReader(value).accept(node, ClassReader.SKIP_FRAMES);
		return node;
	}
}
