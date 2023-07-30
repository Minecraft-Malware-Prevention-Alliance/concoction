package info.mmpa.concoction;

import info.mmpa.concoction.input.archive.ArchiveLoadContext;
import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelBuilder;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NavigableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BuilderTests {
	private static final Path projectRoot = Paths.get(System.getProperty("user.dir")).getParent();

	@Test
	void test() {
		Path gradleDir = projectRoot.resolve("gradle");
		Path gradleJar = gradleDir.resolve("wrapper/gradle-wrapper.jar");

		try {
			ApplicationModel modelFromManual = ModelBuilder.create()
					.addSource(ArchiveLoadContext.RUNNABLE_JAR, gradleJar)
					.build();

			// Single input
			NavigableMap<Path, ApplicationModel> models = Concoction.builder()
					.addInput(ArchiveLoadContext.RUNNABLE_JAR, gradleJar)
					.models();
			assertEquals(1, models.size());
			ApplicationModel modelFromBuilder = models.firstEntry().getValue();
			assertEquals(modelFromManual, modelFromBuilder);

			// Potentially N inputs
			models = Concoction.builder()
					.addInputDirectory(ArchiveLoadContext.RUNNABLE_JAR, gradleDir)
					.models();
			assertEquals(1, models.size());
			ApplicationModel modelFromBuilderDir = models.firstEntry().getValue();
			assertEquals(modelFromManual, modelFromBuilderDir);
		} catch (Exception ex) {
			fail(ex);
		}
	}
}
