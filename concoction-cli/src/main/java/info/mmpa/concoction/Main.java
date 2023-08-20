package info.mmpa.concoction;

import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.dynamic.DynamicScanException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NavigableMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * CLI entry point.
 * <p>
 * Possible application exit codes:
 * <ul>
 *     <li><b>-1:</b> Failure reading scan models</li>
 *     <li><b>-2:</b> Failure reading inputs</li>
 *     <li><b>-3:</b> Failure with dynamic scanning</li>
 *     <li><b>0:</b> Scan results clear</li>
 *     <li><b>N > 0:</b> Number of files scanned where matches were found</li>
 * </ul>
 */
@Command(name = "Concoction", mixinStandardHelpOptions = true,
		version = ConcoctionBuildConfig.VERSION,
		description = "Dynamic Shared Malware Scanner")
public class Main implements Callable<Integer> {
	// Model options
	@Option(names = {"-md", "--modelDir"}, description = "Directory containing concoction scan model files (json)")
	private Path modelDir;
	@Option(names = {"-m", "--model"}, description = "Path to single concoction scan model file (json)")
	private Path model;

	// Input options
	@Option(names = {"-id", "--inputDir"}, description = "Directory containing files to scan (jar)")
	private Path inputDir;
	@Option(names = {"-i", "--input"}, description = "Path to single file to scan (jar)")
	private Path input;
	@Option(names = {"-m", "--mode"}, description = "Mode to use when parsing jar/zip files.\n" +
			" - RANDOM_ACCESS_JAR: Used when inputs are loaded dynamically via 'java.util.zip.ZipFile' or 'java.util.zip.JarFile'\n" +
			" - RUNNABLE_JAR:      Used when inputs are treated as a program run via 'java -jar' or 'java -cp'\n" +
			" - STREAMED_JAR:      Used when inputs are loaded dynamically via streaming such as with 'java.util.zip.ZipInputStream' or 'java.util.zip.JarInputStream'\n")
	private ArchiveLoadContext archiveMode = ArchiveLoadContext.RANDOM_ACCESS_JAR;

	// Output options
	@Option(names = {"-v", "--verbose"}, description = "Enables more verbose logging and error details")
	private boolean verbose;
	@Option(names = {"-rcm", "--resultsConsoleMode"}, description = "Console output display mode for results")
	private ConsoleOutputMode consoleOutputMode = ConsoleOutputMode.CSV;

	// Common / misc options
	@Option(names = {"-dd", "--dirDepth"}, description = "Directory depth to scan for with --modelDir and --inputDir")
	private int directoryDepth = -1;

	/**
	 * Entry point, populates an instance of this class's parameters from the command line args passed.
	 *
	 * @param args
	 * 		Arguments to parse.
	 */
	public static void main(String[] args) {
		final int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		Concoction builder = Concoction.builder();

		// Read models
		try {
			addModels(builder);
		} catch (IOException e) {
			return handleError(-1, e, "Failed reading scan models");
		}

		// Read inputs
		try {
			addInputs(builder);
		} catch (IOException e) {
			return handleError(-2, e, "Failed reading input files");
		}

		// Run scan, exiting with number of matched paths
		try {
			return handleScan(builder);
		} catch (DynamicScanException e) {
			return handleError(-3, e, "Failed during dynamic scan");
		}
	}

	/**
	 * @param builder
	 * 		Builder to update with scan model content to use.
	 *
	 * @throws IOException
	 * 		When an model cannot be read, or no models were provided.
	 */
	private void addModels(@Nonnull Concoction builder) throws IOException {
		if (directoryDepth > 0)
			builder.withMaxInputDirectoryDepth(directoryDepth);

		if (model != null) {
			if (Files.isRegularFile(model))
				builder.addScanModel(model);
			else
				System.err.printf("Given model file '%s' is not a file or found\n", model.getFileName());
		}

		if (modelDir != null) {
			if (Files.isDirectory(modelDir))
				builder.addScanModelDirectory(modelDir);
			else
				System.err.printf("Given model directory '%s' is not a directory or found\n", model.getFileName());
		}

		if (builder.getScanModels().isEmpty())
			throw new IOException("You must provide at least one scan model via '--model <file>' or '--modelDir <directory>'");
	}

	/**
	 * @param builder
	 * 		Builder to update with input content to scan.
	 *
	 * @throws IOException
	 * 		When an input cannot be read, or no inputs were provided.
	 */
	private void addInputs(@Nonnull Concoction builder) throws IOException {
		// TODO: When adding options for dynamic scanning:
		//  - support usage of 'withSupportingPathLoadContext'
		//  - passing support paths to input loading

		if (input != null) {
			if (Files.isRegularFile(input))
				builder.addInput(archiveMode, input);
			else
				System.err.printf("Given input file '%s' is not a file or found\n", input.getFileName());
		}

		if (inputDir != null) {
			if (Files.isDirectory(inputDir))
				builder.addInputDirectory(archiveMode, modelDir);
			else
				System.err.printf("Given input directory '%s' is not a directory or found\n", inputDir.getFileName());
		}

		if (builder.getInputModels().isEmpty())
			throw new IOException("You must provide at least one input via '--input <file>' or '--inputDir <directory>'");
	}

	/**
	 * Scans the inputs provided to {@link Concoction} and prints out the results.
	 *
	 * @param builder
	 * 		Builder with model to scan for in provided inputs.
	 *
	 * @return Number of paths with detections.
	 *
	 * @throws DynamicScanException
	 * 		When dynamic scanning encounters an error.
	 */
	private int handleScan(@Nonnull Concoction builder) throws DynamicScanException {
		NavigableMap<Path, Results> scanResults = builder.scan();
		scanResults.forEach((path, detections) -> {
			switch (consoleOutputMode) {
				case HUMAN:
					// TODO: Implement cleaner human output
				case CSV:
					// Path,Detections
					String detectionIds = detections.asNavigableSet().stream()
							.map(d -> d.archetype().getIdentifier())
							.collect(Collectors.joining(":"));
					System.out.println(path.toAbsolutePath() + "," + detectionIds);
					break;
			}
		});
		return scanResults.size();
	}

	/**
	 * @param ret
	 * 		Error code.
	 * @param t
	 * 		Exception to print.
	 * @param formatPrefix
	 * 		Message to print prefix.
	 *
	 * @return Error code.
	 */
	private int handleError(int ret, @Nonnull Throwable t, @Nonnull String formatPrefix) {
		String message = t.getMessage();
		System.err.printf(formatPrefix + ": %s\n", message == null ? "(no details given)" : message);
		if (verbose)
			t.printStackTrace();
		return ret;
	}
}
