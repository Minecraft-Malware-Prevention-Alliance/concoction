package info.mmpa.concoction.scan.standard;

import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;
import info.mmpa.concoction.model.path.ClassPathElement;
import info.mmpa.concoction.model.path.SourcePathElement;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.ResultsSink;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Basic ASM pattern matching based scanning.
 */
public class StandardScan {
	private final List<StandardDetectionMatcher> matchers;

	/**
	 * @param matchers
	 * 		List of detection matchers to scan with.
	 */
	public StandardScan(@Nonnull List<StandardDetectionMatcher> matchers) {
		this.matchers = matchers;
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
				ClassNode node = node(classEntry.getValue());
				matchers.forEach(matcher -> {
					try {
						matcher.scan(sink, classPath, node);
					} catch (Throwable ex) {
						// Pipe errors to sink.
						//
						// Ideally matchers handle errors themselves properly, but if they don't we can at least
						// fall back here and report the issue at the class-level.
						// Handling it this way allows other classes to be scanned, even if earlier classes in the
						// scan order fail.
						sink.error(classPath, ex);
					}
				});
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
