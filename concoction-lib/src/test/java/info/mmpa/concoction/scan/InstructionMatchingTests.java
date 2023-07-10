package info.mmpa.concoction.scan;

import dev.xdark.ssvm.util.IOUtil;
import example.RuntimeExec;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;
import info.mmpa.concoction.model.impl.BasicApplicationModel;
import info.mmpa.concoction.model.impl.BasicModelSource;
import info.mmpa.concoction.model.path.MethodPathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.model.method.InstructionsMatchingModel;
import info.mmpa.concoction.scan.standard.StandardScan;
import org.junit.jupiter.api.Test;
import software.coley.collections.Maps;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.NavigableSet;

import static info.mmpa.concoction.util.Casting.cast;
import static info.mmpa.concoction.util.Serialization.deserializeModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InstructionMatchingTests {
	// TODO: Create more test cases
	//  - Should first properly implement the 'InstructionStrings' handling

	@Test
	void test() {
		try {
			Results results = results("Runtime_exec.json", RuntimeExec.class);
			NavigableSet<Detection> detections = results.detections();

			// Should be one result
			assertEquals(1, detections.size());

			// Should be in the example class's calc method
			Detection detection = detections.iterator().next();
			MethodPathElement path = cast(detection.path());
			assertEquals("example/RuntimeExec", path.getClassName());
			assertEquals("calc()V", path.localDisplay());
		} catch (IOException ex) {
			fail(ex);
		}
	}

	@Nonnull
	private static Results results(@Nonnull String modelName, @Nonnull Class<?> type) throws IOException {
		String json = new String(Files.readAllBytes(Paths.get("src/test/resources/models/" + modelName)));
		InstructionsMatchingModel model = deserializeModel(json);
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
		return IOUtil.readAll(is);
	}
}
