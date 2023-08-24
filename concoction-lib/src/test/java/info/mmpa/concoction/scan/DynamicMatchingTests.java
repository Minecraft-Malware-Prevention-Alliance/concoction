package info.mmpa.concoction.scan;

import example.*;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.dynamic.*;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.util.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled("TODO: Make test cases and showcase capabilities")
public class DynamicMatchingTests {
	@Test
	void test() {
		try {
			Results results = results("File_read.json", FileStream.class);
			NavigableSet<Detection> detections = results.asNavigableSet();

			// TODO: Create rules to match against and create assertions for them here
			assertEquals(0, detections.size());
		} catch (Exception ex) {
			fail(ex);
		}
	}

	@Nonnull
	private static Results results(@Nonnull String modelName, @Nonnull Class<?> type) throws IOException, DynamicScanException {
		EntryPointDiscovery discovery = TestUtils.getPublicStaticNoArgEntryPointDiscovery(type);
		CoverageEntryPointSupplier coverageSupplier = (model, context) -> null;

		// Read the model
		Path path = Paths.get("src/test/resources/models/" + modelName);
		List<ScanModel> scanModels = Collections.singletonList(ScanModel.fromJson(path));

		// Run the scan.
		DynamicScanner scan = new DynamicScanner(discovery, coverageSupplier, scanModels);
		return scan.accept(TestUtils.appModel(type));
	}
}
