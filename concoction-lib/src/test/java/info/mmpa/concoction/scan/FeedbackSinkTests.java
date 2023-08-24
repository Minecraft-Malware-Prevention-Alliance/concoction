package info.mmpa.concoction.scan;

import example.FileStream;
import example.HelloWorld;
import example.RuntimeExec;
import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.path.ClassPathElement;
import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.output.sink.FeedbackSink;
import info.mmpa.concoction.scan.dynamic.*;
import info.mmpa.concoction.scan.insn.InstructionScanner;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackSinkTests {
	@Test
	void testInstructionFeedback() {
		try {
			// When scanning N classes, we should have N per-class feedback sinks allocated.
			Map<ClassPathElement, TestInstructionFeedbackItemSink> classSinks = new IdentityHashMap<>();
			FeedbackSink feedbackSink = new FeedbackSink() {
				@Override
				public InstructionFeedbackItemSink openClassFeedbackSink(@Nonnull ClassPathElement classPath) {
					return classSinks.computeIfAbsent(classPath, c -> new TestInstructionFeedbackItemSink());
				}

				@Nullable
				@Override
				public DynamicFeedbackItemSink openDynamicFeedbackSink() {
					return null;
				}
			};
			Class<?>[] scanTargetClasses = {
					RuntimeExec.class,
					FileStream.class
			};
			instructionScan("Runtime_exec.json", scanTargetClasses, feedbackSink);

			// Assert N classes were allocated N class sinks
			assertEquals(scanTargetClasses.length, classSinks.size());

			// Assert they all were successful in getting their respective data
			for (TestInstructionFeedbackItemSink sink : classSinks.values()) {
				assertTrue(sink.hasParsedClass());

				Results results = sink.getResults();
				assertNotNull(results);
				if (sink.isOfClass(RuntimeExec.class))
					assertEquals(4, results.size());
				else if (sink.isOfClass(FileStream.class))
					assertEquals(0, results.size());
				else
					fail("Sink was given unknown class: " + sink.className);
			}
		} catch (IOException ex) {
			fail(ex);
		}
	}

	@Test
	void testDynamicFeedback() {
		try {
			// Dynamic scanning shares one sink reference.
			List<TestDynamicFeedbackItemSink> dynamicSinks = new ArrayList<>(1);
			FeedbackSink feedbackSink = new FeedbackSink() {
				@Nullable
				@Override
				public InstructionFeedbackItemSink openClassFeedbackSink(@Nonnull ClassPathElement classPath) {
					return null;
				}

				@Override
				public DynamicFeedbackItemSink openDynamicFeedbackSink() {
					TestDynamicFeedbackItemSink sink = new TestDynamicFeedbackItemSink();
					dynamicSinks.add(sink);
					return sink;
				}
			};
			Class<?>[] scanTargetClasses = {
					HelloWorld.class,
			};
			dynamicScan("HelloWorld.json", scanTargetClasses, feedbackSink);

			// Assert 1 sink was made.
			assertEquals(1, dynamicSinks.size());

			// Assert the sink received stack information and the final results.
			TestDynamicFeedbackItemSink sink = dynamicSinks.get(0);
			Set<String> visitedMethods = sink.getVisited();
			Results results = sink.getResults();
			assertNotEquals(0, visitedMethods.size());
			assertTrue(visitedMethods.contains("example/HelloWorld.start()V"));
			assertTrue(visitedMethods.contains("example/HelloWorld.getMessage()Ljava/lang/String;"));
			assertTrue(visitedMethods.contains("example/HelloWorld.print(Ljava/lang/String;)V"));
			assertTrue(visitedMethods.contains("java/io/PrintStream.println(Ljava/lang/String;)V"));
			assertNotNull(results);
			assertTrue(results.size() >= 1); // Different JVM's may delegate calls differently, resulting in 1 or 2 results
		} catch (IOException | DynamicScanException ex) {
			fail(ex);
		}
	}

	private static void instructionScan(@Nonnull String modelName, @Nonnull Class<?>[] types, @Nonnull FeedbackSink feedbackSink) throws IOException {
		Path path = Paths.get("src/test/resources/models/" + modelName);
		ScanModel model = ScanModel.fromJson(path);
		InstructionScanner scan = new InstructionScanner(Collections.singletonList(model), feedbackSink);
		scan.accept(TestUtils.appModel(types));
	}

	private static void dynamicScan(@Nonnull String modelName, @Nonnull Class<?>[] types, @Nonnull FeedbackSink feedbackSink) throws IOException, DynamicScanException {
		Path path = Paths.get("src/test/resources/models/" + modelName);
		ScanModel model = ScanModel.fromJson(path);
		EntryPointDiscovery discovery = new EntryPointDiscovery() {
			@Nonnull
			@Override
			public List<EntryPoint> createEntryPoints(@Nonnull ApplicationModel model, @Nonnull SsvmContext context) {
				List<EntryPoint> entries = new ArrayList<>();
				for (Class<?> type : types)
					entries.addAll(TestUtils.getPublicStaticNoArgEntryPoints(type));
				return entries;
			}
		};
		DynamicScanner scan = new DynamicScanner(discovery, CoverageEntryPointSupplier.NO_COVERAGE, Collections.singletonList(model), feedbackSink);
		scan.accept(TestUtils.appModel(types));
	}

	private static class TestInstructionFeedbackItemSink implements FeedbackSink.InstructionFeedbackItemSink {
		private String className;
		private Results results;
		private boolean parsedClass;

		@Override
		public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
			// no-op
		}

		@Override
		public void onPreScan(@Nonnull ClassNode classNode) {
			className = classNode.name;
			parsedClass = true;
		}

		@Override
		public void onScanError(@Nonnull Throwable t) {
			parsedClass = false;
		}

		@Override
		public void onCompletion(@Nonnull Results results) {
			this.results = results;
		}

		public boolean hasParsedClass() {
			return parsedClass;
		}

		@Nullable
		public Results getResults() {
			return results;
		}

		public boolean isOfClass(@Nonnull Class<?> cls) {
			return className != null && cls.getName().endsWith(className.replace('/', '.'));
		}
	}

	private static class TestDynamicFeedbackItemSink implements FeedbackSink.DynamicFeedbackItemSink {
		private final Set<String> visited = new TreeSet<>();
		private Results results;

		@Override
		public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
			// no-op
		}

		@Override
		public void onMethodEnter(@Nonnull List<CallStackFrame> stack, @Nonnull CallStackFrame enteredMethodFrame) {
			visited.add(enteredMethodFrame.getOwnerName() + "." +
					enteredMethodFrame.getMethodName() + enteredMethodFrame.getMethodDesc());
		}

		@Override
		public void onMethodExit(@Nonnull List<CallStackFrame> stack, @Nonnull CallStackFrame exitedMethodFrame) {
			// no-op
		}

		@Override
		public void onCompletion(@Nonnull Results results) {
			this.results = results;
		}

		@Nonnull
		public Set<String> getVisited() {
			return visited;
		}

		@Nullable
		public Results getResults() {
			return results;
		}
	}
}
