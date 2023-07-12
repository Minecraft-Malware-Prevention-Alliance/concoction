package info.mmpa.concoction.scan;

import example.RuntimeExec;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;
import info.mmpa.concoction.model.impl.BasicApplicationModel;
import info.mmpa.concoction.model.impl.BasicModelSource;
import info.mmpa.concoction.model.path.MethodPathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.scan.standard.StandardScan;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import software.coley.collections.Maps;
import software.coley.collections.Sets;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.NavigableSet;
import java.util.Set;

import static info.mmpa.concoction.util.Unchecked.cast;
import static org.junit.jupiter.api.Assertions.*;

public class InstructionMatchingTests {
	@Test
	void test() {
		try {
			Results results = results("Runtime_exec.json", RuntimeExec.class);
			NavigableSet<Detection> detections = results.detections();

			// There are four sample methods which exhibit match-worthy behavior.
			assertEquals(4, detections.size());

			// We should have one match in each of these methods.
			Set<String> remainingMatches = Sets.ofVar("calc1", "calc2", "calc3", "calc4");
			for (Detection detection : detections) {
				MethodPathElement path = cast(detection.path());
				String detectionMethodName = path.getMethodName();
				if (!remainingMatches.remove(detectionMethodName))
					fail("Match in unexpected method: " + detectionMethodName);
			}
			assertTrue(remainingMatches.isEmpty());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	@Nonnull
	private static Results results(@Nonnull String modelName, @Nonnull Class<?> type) throws IOException {
		Path path = Paths.get("src/test/resources/models/" + modelName);
		InstructionsMatchingModel model = InstructionsMatchingModel.fromJson(path);
		StandardScan scan = new StandardScan(Collections.singletonList(model));
		return scan.accept(appModel(type));
	}

	@Nonnull
	private static ApplicationModel appModel(@Nonnull Class<?> type) throws IOException {
		String internalName = type.getName().replace('.', '/');
		ModelSource model = new BasicModelSource("test", Maps.of(internalName, code(type)), Collections.emptyMap());
		return new BasicApplicationModel(model, Collections.emptyList());
	}

	@Nonnull
	private static byte[] code(@Nonnull Class<?> type) throws IOException {
		String className = type.getName();
		InputStream is = ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
		if (is == null) throw new IOException(className + " not found");
		return IOUtils.toByteArray(is);
	}
}
