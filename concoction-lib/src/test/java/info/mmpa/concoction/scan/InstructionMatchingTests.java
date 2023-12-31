package info.mmpa.concoction.scan;

import example.RuntimeExec;
import info.mmpa.concoction.input.model.path.MethodPathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.scan.insn.InstructionScanner;
import info.mmpa.concoction.util.TestUtils;
import org.junit.jupiter.api.Test;
import software.coley.collections.Sets;

import javax.annotation.Nonnull;
import java.io.IOException;
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
			NavigableSet<Detection> detections = results.asNavigableSet();

			// There are four sample methods which exhibit match-worthy behavior.
			assertEquals(4, detections.size());

			// We should have one match in each of these methods.
			Set<String> remainingMatches = Sets.ofVar("calc1", "calc2", "calc3", "calc4");
			for (Detection detection : detections) {
				MethodPathElement methodPath = cast(detection.path());
				String detectionMethodName = methodPath.getMethodName();
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
		ScanModel model = ScanModel.fromJson(path);
		InstructionScanner scan = new InstructionScanner(Collections.singletonList(model));
		return scan.accept(TestUtils.appModel(type));
	}
}
