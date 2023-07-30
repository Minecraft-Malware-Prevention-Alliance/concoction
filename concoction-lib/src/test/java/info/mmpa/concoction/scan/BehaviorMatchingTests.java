package info.mmpa.concoction.scan;

import dev.xdark.ssvm.invoke.Argument;
import example.FileStream;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.dynamic.*;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BehaviorMatchingTests {
	@Test
	void test() {
		try {
			Results results = results("File_read.json", FileStream.class);
			NavigableSet<Detection> detections = results.detections();

			// TODO: Create rules to match against and create assertions for them here
			assertEquals(0, detections.size());
		} catch (Exception ex) {
			fail(ex);
		}
	}

	@Nonnull
	private static Results results(@Nonnull String modelName, @Nonnull Class<?> type) throws IOException, DynamicScanException {
		// Make entry points for all 'public static' methods in the given class.
		int mask = Modifier.STATIC | Modifier.PUBLIC;
		EntryPointDiscovery discovery = (model, context) -> Arrays.stream(type.getDeclaredMethods())
				.filter(m -> (m.getModifiers() & mask) == mask)
				.map(m -> toEntryPoint(type, m))
				.collect(Collectors.toList());
		CoverageEntryPointSupplier coverageSupplier = (model, context) -> null;

		// Read the model
		Path path = Paths.get("src/test/resources/models/" + modelName);
		List<ScanModel> scanModels = Collections.emptyList();
		// TODO: Deserialize model after creating behavior matching schemes

		// Run the scan.
		DynamicScan scan = new DynamicScan(discovery, coverageSupplier, scanModels);
		return scan.accept(TestUtils.appModel(type));
	}

	@Nonnull
	private static EntryPoint toEntryPoint(@Nonnull Class<?> declaring, @Nonnull Method method) {
		String className = declaring.getName().replace('.', '/');
		String methodName = method.getName();
		String methodDescriptor = Type.getMethodDescriptor(method);
		Supplier<Argument[]> argumentSupplier = () -> new Argument[0];
		return new EntryPoint(className, methodName, methodDescriptor, argumentSupplier);
	}
}
