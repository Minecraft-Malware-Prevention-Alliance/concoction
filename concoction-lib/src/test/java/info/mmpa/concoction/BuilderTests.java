package info.mmpa.concoction;

import info.mmpa.concoction.input.archive.ArchiveLoadContext;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelBuilder;
import info.mmpa.concoction.scan.model.ScanModel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BuilderTests {
	private static final Path currentDir = Paths.get(System.getProperty("user.dir"));
	private static final Path projectRoot = currentDir.getParent();

	@Test
	void testInputs() {
		Path gradleDir = projectRoot.resolve("gradle");
		Path gradleJar = gradleDir.resolve("wrapper/gradle-wrapper.jar");

		try {
			ApplicationModel modelFromManual = ModelBuilder.create()
					.addSource(ArchiveLoadContext.RUNNABLE_JAR, gradleJar)
					.build();

			// Single input
			NavigableMap<Path, ApplicationModel> models = Concoction.builder()
					.addInput(ArchiveLoadContext.RUNNABLE_JAR, gradleJar)
					.getInputModels();
			assertEquals(1, models.size());
			ApplicationModel modelFromBuilder = models.firstEntry().getValue();
			assertEquals(modelFromManual, modelFromBuilder);

			// Potentially N inputs
			models = Concoction.builder()
					.addInputDirectory(ArchiveLoadContext.RUNNABLE_JAR, gradleDir)
					.getInputModels();
			assertEquals(1, models.size());
			ApplicationModel modelFromBuilderDir = models.firstEntry().getValue();
			assertEquals(modelFromManual, modelFromBuilderDir);
		} catch (Exception ex) {
			fail(ex);
		}
	}

	@Test
	void testScanModels() {
		Path modelsDir = currentDir.resolve("src/test/resources/models");

		try {
			// Manual setup
			NavigableMap<Path, ScanModel> manualModels = new TreeMap<>();
			try (Stream<Path> stream = Files.walk(modelsDir)) {
				for (Path path : stream.collect(Collectors.toList())) {
					if (Files.isRegularFile(path))
						manualModels.put(path, ScanModel.fromJson(path));
				}
			}

			// Setup from builder
			NavigableMap<Path, ScanModel> scanModels = Concoction.builder()
					.addScanModelDirectory(modelsDir)
					.getScanModels();

			assertEquals(manualModels, scanModels);
		} catch (Exception ex) {
			fail(ex);
		}
	}
}
