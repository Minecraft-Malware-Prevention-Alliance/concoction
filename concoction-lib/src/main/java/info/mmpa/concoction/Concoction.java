package info.mmpa.concoction;

import info.mmpa.concoction.input.io.archive.ArchiveLoadContext;
import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.InvalidModelException;
import info.mmpa.concoction.input.model.ModelBuilder;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.scan.dynamic.CoverageEntryPointSupplier;
import info.mmpa.concoction.scan.dynamic.DynamicScan;
import info.mmpa.concoction.scan.dynamic.DynamicScanException;
import info.mmpa.concoction.scan.dynamic.EntryPointDiscovery;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.scan.standard.StandardScan;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * API for common usage of Concoction.
 */
public class Concoction {
	private final Map<Path, ApplicationModel> inputModels = new HashMap<>();
	private final Map<Path, ScanModel> scanModels = new HashMap<>();
	private ArchiveLoadContext supportingPathLoadContext = ArchiveLoadContext.RANDOM_ACCESS_JAR;
	private int inputDepth = 3;

	private Concoction() {
	}

	/**
	 * The initialization point of the builder
	 *
	 * @return A new Concoction builder.
	 */
	@Nonnull
	public static Concoction builder() {
		return new Concoction();
	}

	/**
	 * Sets directory scanning depth limit.
	 *
	 * @param inputDepth
	 * 		Directory depth to limit when handling
	 *        {@link #addInputDirectory(ArchiveLoadContext, Path, Predicate, Path...)} and
	 *        {@link #addScanModelDirectory(Path)}.
	 *
	 * @return Self.
	 */
	@Nonnull
	public Concoction withMaxInputDirectoryDepth(int inputDepth) {
		this.inputDepth = inputDepth;
		return this;
	}

	/**
	 * Sets supporting path/input archive loading context.
	 *
	 * @param supportingPathLoadContext
	 * 		Load context to use for supporting paths provided to:
	 * 		<ul>
	 * 		<li>{@link #addInput(ArchiveLoadContext, Path, Path...)}</li>
	 * 		<li>{@link #addInputDirectory(ArchiveLoadContext, Path, Predicate, Path...)}</li>
	 * 		</ul>
	 *
	 * @return Self.
	 */
	@Nonnull
	public Concoction withSupportingPathLoadContext(@Nonnull ArchiveLoadContext supportingPathLoadContext) {
		this.supportingPathLoadContext = supportingPathLoadContext;
		return this;
	}

	/**
	 * Adds a single input to scan. One input is made from the primary path plus its supporting paths.
	 *
	 * @param context
	 * 		Context determining how to read from the input.
	 * @param primaryPath
	 * 		Path to input to scan.
	 * @param supportingPaths
	 * 		Supporting paths to assist dynamic scanning of the primary input's contents.
	 * 		The supporting paths use the current supporting path
	 *        {@link #withSupportingPathLoadContext(ArchiveLoadContext) load context} which defaults to
	 *        {@link ArchiveLoadContext#RANDOM_ACCESS_JAR}.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When any of the provided paths do not point to files, or symbolic links to regular files.
	 * 		For reading many inputs from a directory see {@link #addInputDirectory(ArchiveLoadContext, Path, Predicate, Path...)}.
	 */
	@Nonnull
	public Concoction addInput(@Nonnull ArchiveLoadContext context, @Nonnull Path primaryPath,
							   @Nullable Path... supportingPaths) throws IOException {
		return addInput(context, primaryPath, listOptionalPaths(supportingPaths));
	}

	/**
	 * Adds a single input to scan. One input is made from the primary path plus its supporting paths.
	 *
	 * @param context
	 * 		Context determining how to read from the input.
	 * @param primaryPath
	 * 		Path to input to scan.
	 * @param supportingPaths
	 * 		Supporting paths to assist dynamic scanning of the primary input's contents.
	 * 		The supporting paths use the current supporting path
	 *        {@link #withSupportingPathLoadContext(ArchiveLoadContext) load context} which defaults to
	 *        {@link ArchiveLoadContext#RANDOM_ACCESS_JAR}.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When any of the provided paths do not point to files, or symbolic links to regular files.
	 * 		For reading many inputs from a directory see {@link #addInputDirectory(ArchiveLoadContext, Path, Predicate, Path...)}.
	 */
	@Nonnull
	private Concoction addInput(@Nonnull ArchiveLoadContext context, @Nonnull Path primaryPath,
								@Nonnull List<Path> supportingPaths) throws IOException {
		// Create the input model, primary is first, anything after is supporting
		ModelBuilder builder = ModelBuilder.create()
				.addSource(context, validatePath(primaryPath));
		for (Path supportingPath : supportingPaths)
			builder.addSource(supportingPathLoadContext, validatePath(supportingPath));
		try {
			inputModels.put(primaryPath, builder.build());
		} catch (InvalidModelException ex) {
			// Should never be thrown since the primary-path is non-null here.
			throw new IOException("No input provided", ex);
		}
		return this;
	}

	/**
	 * Adds a variable number of inputs to scan. Each input is made from a jar/zip file path found in the directory
	 * <i>(and sub-directories)</i> plus the given supporting paths. All inputs share the same common supporting paths.
	 *
	 * @param context
	 * 		Context determining how to read from each input.
	 * @param directory
	 * 		Root directory to scan for files.
	 * @param commonSupportingPaths
	 * 		Supporting paths to assist dynamic scanning of the primary input's contents.
	 * 		The supporting paths use the current supporting path
	 *        {@link #withSupportingPathLoadContext(ArchiveLoadContext) load context} which defaults to
	 *        {@link ArchiveLoadContext#RANDOM_ACCESS_JAR}.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When the provided root directory cannot be read from, or when
	 * 		any of the discovered files in the directory cannot be read from.
	 */
	@Nonnull
	public Concoction addInputDirectory(@Nonnull ArchiveLoadContext context, @Nonnull Path directory,
										@Nullable Path... commonSupportingPaths) throws IOException {
		return addInputDirectory(context, directory, Concoction::isJarPath, listOptionalPaths(commonSupportingPaths));
	}

	/**
	 * Adds a variable number of inputs to scan. Each input is made from a file path found in the directory
	 * <i>(and sub-directories)</i> plus the given supporting paths. All inputs share the same common supporting paths.
	 *
	 * @param context
	 * 		Context determining how to read from each input.
	 * @param directory
	 * 		Root directory to scan for files.
	 * @param fileFilter
	 * 		Filter to limit which files are handled.
	 * @param commonSupportingPaths
	 * 		Supporting paths to assist dynamic scanning of the primary input's contents.
	 * 		The supporting paths use the current supporting path
	 *        {@link #withSupportingPathLoadContext(ArchiveLoadContext) load context} which defaults to
	 *        {@link ArchiveLoadContext#RANDOM_ACCESS_JAR}.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When the provided root directory cannot be read from, or when
	 * 		any of the discovered files in the directory cannot be read from.
	 */
	@Nonnull
	public Concoction addInputDirectory(@Nonnull ArchiveLoadContext context, @Nonnull Path directory,
										@Nullable Predicate<Path> fileFilter,
										@Nullable Path... commonSupportingPaths) throws IOException {
		return addInputDirectory(context, directory, fileFilter, listOptionalPaths(commonSupportingPaths));
	}

	/**
	 * Adds a variable number of inputs to scan. Each input is made from a file path found in the directory
	 * <i>(and sub-directories)</i> plus the given supporting paths. All inputs share the same common supporting paths.
	 *
	 * @param context
	 * 		Context determining how to read from each input.
	 * @param directory
	 * 		Root directory to scan for files.
	 * @param fileFilter
	 * 		Filter to limit which files are handled.
	 * @param commonSupportingPaths
	 * 		Supporting paths to assist dynamic scanning of the primary input's contents.
	 * 		The supporting paths use the current supporting path
	 *        {@link #withSupportingPathLoadContext(ArchiveLoadContext) load context} which defaults to
	 *        {@link ArchiveLoadContext#RANDOM_ACCESS_JAR}.
	 *
	 * @return Self.
	 *
	 * @throws IOException
	 * 		When the provided root directory cannot be read from, or when
	 * 		any of the discovered files in the directory cannot be read from.
	 */
	@Nonnull
	public Concoction addInputDirectory(@Nonnull ArchiveLoadContext context, @Nonnull Path directory,
										@Nullable Predicate<Path> fileFilter,
										@Nonnull List<Path> commonSupportingPaths) throws IOException {
		try (Stream<Path> stream = Files.walk(directory, inputDepth)) {
			for (Path path : stream.collect(Collectors.toList()))
				if (Files.isRegularFile(path) && (fileFilter == null || fileFilter.test(path)))
					addInput(context, path, commonSupportingPaths);
		}
		return this;
	}

	/**
	 * Adds a single {@link ScanModel} to target for inputs.
	 *
	 * @param path
	 * 		Path to JSON file
	 *
	 * @return Self
	 *
	 * @throws IOException
	 * 		When the JSON file could not be read into the {@link ScanModel} representation.
	 * 		Can be due to IO issues, or a malformed JSON file.
	 */
	@Nonnull
	public Concoction addScanModel(@Nonnull Path path) throws IOException {
		path = validatePath(path);
		ScanModel model = ScanModel.fromJson(path);
		scanModels.put(path, model);
		return this;
	}

	/**
	 * Adds all JSON files in the given directory <i>(and sub-directories)</i> as {@link ScanModel}s to target for inputs.
	 *
	 * @param directory
	 * 		Root directory to scan for files.
	 *
	 * @return Self
	 *
	 * @throws IOException
	 * 		When any of the JSON files could not be read into the {@link ScanModel} representation.
	 * 		Can be due to IO issues, or a malformed JSON file.
	 */
	@Nonnull
	public Concoction addScanModelDirectory(@Nonnull Path directory) throws IOException {
		try (Stream<Path> stream = Files.walk(directory, inputDepth)) {
			for (Path path : stream.collect(Collectors.toList()))
				if (isJsonPath(path))
					addScanModel(path);
		}
		return this;
	}

	/**
	 * @return A map view of all added inputs.
	 */
	@Nonnull
	public NavigableMap<Path, ApplicationModel> getInputModels() {
		return new TreeMap<>(inputModels);
	}

	/**
	 * @return A map view of all added scan models.
	 */
	@Nonnull
	public NavigableMap<Path, ScanModel> getScanModels() {
		return new TreeMap<>(scanModels);
	}

	/**
	 * Scans the inputs with the provided matching models.
	 *
	 * @return Map of input paths to their scan results.
	 *
	 * @throws DynamicScanException
	 * 		When dynamic scanning encountered a problem, if any dynamic scanning is done.
	 */
	@Nonnull
	public NavigableMap<Path, Results> scan() throws DynamicScanException {
		NavigableMap<Path, Results> allResults = new TreeMap<>();

		// Static scanning first
		List<ScanModel> insnModels = scanModels.values().stream()
				.filter(ScanModel::hasInstructionModel)
				.collect(Collectors.toList());
		if (!insnModels.isEmpty()) {
			StandardScan scan = new StandardScan(insnModels);
			for (Map.Entry<Path, ApplicationModel> entry : inputModels.entrySet()) {
				Path scannedFilePath = entry.getKey();
				ApplicationModel inputModel = entry.getValue();
				Results results = scan.accept(inputModel);
				allResults.merge(scannedFilePath, results, Results::merge);
			}
		}

		// Then dynamic scanning
		List<ScanModel> dynamicModels = scanModels.values().stream()
				.filter(ScanModel::hasBehaviorModel)
				.collect(Collectors.toList());
		if (!insnModels.isEmpty()) {
			// TODO: Point these interfaces to proper implementations
			//  - This will currently scan nothing
			EntryPointDiscovery entryPointDiscovery = (model, context) -> Collections.emptyList();
			CoverageEntryPointSupplier coverageEntryPointSupplier = (model, context) -> null;
			DynamicScan scan = new DynamicScan(entryPointDiscovery, coverageEntryPointSupplier, dynamicModels);
			for (Map.Entry<Path, ApplicationModel> entry : inputModels.entrySet()) {
				Path scannedFilePath = entry.getKey();
				ApplicationModel inputModel = entry.getValue();
				Results results = scan.accept(inputModel);
				allResults.merge(scannedFilePath, results, Results::merge);
			}
		}

		return allResults;
	}

	private static boolean isJarPath(@Nonnull Path path) {
		if (!Files.isRegularFile(path)) return false;

		// Naive, but good enough check for most use cases
		String fileName = path.getFileName().toString().toLowerCase();
		return fileName.endsWith(".jar") || fileName.endsWith(".zip");
	}

	private static boolean isJsonPath(@Nonnull Path path) {
		if (!Files.isRegularFile(path)) return false;

		String fileName = path.getFileName().toString().toLowerCase();
		return fileName.endsWith(".json");
	}

	@Nonnull
	private static List<Path> listOptionalPaths(@Nullable Path[] paths) {
		return paths == null ? Collections.emptyList() : Arrays.asList(paths);
	}

	@Nonnull
	private static Path validatePath(@Nonnull Path path) throws IOException {
		// Resolve symbolic links
		if (Files.isSymbolicLink(path))
			path = Files.readSymbolicLink(path);

		// Input must be a regular file
		if (!Files.isRegularFile(path))
			throw new IOException("Input path '" + path + "' does not exist, or is not a file");

		return path;
	}
}
