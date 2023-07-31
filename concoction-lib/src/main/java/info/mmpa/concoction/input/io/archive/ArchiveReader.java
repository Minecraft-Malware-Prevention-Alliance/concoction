package info.mmpa.concoction.input.io.archive;

import info.mmpa.concoction.input.io.ClassesAndFiles;
import info.mmpa.concoction.input.io.NotAClassException;
import software.coley.lljzip.ZipIO;
import software.coley.lljzip.format.compression.ZipCompressions;
import software.coley.lljzip.format.model.LocalFileHeader;
import software.coley.lljzip.format.model.ZipArchive;
import software.coley.lljzip.format.model.ZipPart;
import software.coley.lljzip.format.transform.CentralAdoptingMapper;
import software.coley.lljzip.format.transform.IdentityZipPartMapper;
import software.coley.lljzip.format.transform.JvmClassDirectoryMapper;
import software.coley.lljzip.util.ByteDataUtil;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Outlines archive reading into the intermediate {@link ClassesAndFiles} model.
 * <br>
 * Child types outline how different tools handle reading from archives.
 */
public abstract class ArchiveReader {
	/**
	 * @param path
	 * 		File path to an archive.
	 *
	 * @return Model of classes and files within the archive.
	 *
	 * @throws IOException
	 * 		When the archive cannot be read.
	 */
	@Nonnull
	public abstract ClassesAndFiles read(@Nonnull Path path) throws IOException;

	/**
	 * @param raw
	 * 		Raw archive bytes to read.
	 *
	 * @return Model of classes and files within the archive.
	 *
	 * @throws IOException
	 * 		When the archive cannot be read.
	 */
	@Nonnull
	public abstract ClassesAndFiles read(@Nonnull byte[] raw) throws IOException;

	@Nonnull
	protected ClassesAndFiles fromArchive(@Nonnull ZipArchive archive) throws IOException {
		ClassesAndFiles content = new ClassesAndFiles();

		// It is assumed that prior to being passed into this method the archive
		// has run any manipulations on the inputs necessary such that the local files
		// are updated to hold the correct 'authoritative' values for properties such
		// as file names and content sizes.
		List<LocalFileHeader> localFiles = archive.getLocalFiles();
		for (LocalFileHeader localFile : localFiles) {
			String fileName = localFile.getFileNameAsString();
			long dataLength = localFile.getFileData().length();

			// ZIP has no sense of 'file' vs 'directory' so we can only guess what is a directory vs file.
			// - Files have data associated with them, even if they end in a slash for some bogus reason.
			// - Even if there is no data, empty files should be tracked. We'll limit those to ones not ending in '/'.
			if (dataLength > 0 || !fileName.endsWith("/")) {
				byte[] fileData = ByteDataUtil.toByteArray(ZipCompressions.decompress(localFile));
				if (fileName.endsWith(".class")) {
					try {
						content.addClass(fileName, fileData);
					} catch (NotAClassException e) {
						// If we got here, the class couldn't be read by ASM even after attempting to patch ASM exploits.
						// While it would be stupid, nobody is really prevented from naming random data files 'foo.class'.
						content.addFile(fileName, fileData);
					}
				} else {
					content.addFile(fileName, fileData);
				}
			}
		}

		return content;
	}

	/**
	 * Archive reader modeling how {@link ZipFile} works.
	 */
	public static class RandomAccessArchiveReader extends ArchiveReader {
		@Nonnull
		@Override
		public ClassesAndFiles read(@Nonnull Path path) throws IOException {
			ZipArchive archive = ZipIO.readStandard(path);
			ZipArchive filtered = filter(archive);
			return fromArchive(filtered);
		}

		@Nonnull
		@Override
		public ClassesAndFiles read(@Nonnull byte[] raw) throws IOException {
			ZipArchive archive = ZipIO.readStandard(raw);
			ZipArchive filtered = filter(archive);
			return fromArchive(filtered);
		}

		@Nonnull
		private ZipArchive filter(@Nonnull ZipArchive archive) {
			// Copy all attributes from the central directory header linked entries
			return archive.withMapping(new CentralAdoptingMapper(new IdentityZipPartMapper()));
		}
	}

	/**
	 * Archive reader modeling how the JVM classpath reader works.
	 */
	public static class RunnableArchiveReader extends ArchiveReader {
		@Nonnull
		@Override
		public ClassesAndFiles read(@Nonnull Path path) throws IOException {
			ZipArchive archive = ZipIO.readJvm(path);
			ZipArchive filtered = filter(archive);
			return fromArchive(filtered);
		}

		@Nonnull
		@Override
		public ClassesAndFiles read(@Nonnull byte[] raw) throws IOException {
			ZipArchive archive = ZipIO.readJvm(raw);
			ZipArchive filtered = filter(archive);
			return fromArchive(filtered);
		}

		@Nonnull
		private ZipArchive filter(@Nonnull ZipArchive archive) {
			// The class directory mapper can result in colliding names for some specially crafted malicious inputs.
			// In these cases we will drop later entries since earlier entries have preferential class load order.
			ZipArchive filtered = archive.withMapping(new JvmClassDirectoryMapper(new IdentityZipPartMapper()));
			deduplicate(filtered);
			return filtered;
		}

		private static void deduplicate(@Nonnull ZipArchive filtered) {
			// TODO: Sanity check with a sample that removing the last occurrence of duplicate paths is correct
			//   if not, remove the first and keep the latest index then.
			int i = 0;
			Set<String> names = new HashSet<>();
			List<ZipPart> parts = filtered.getParts();
			while (i < parts.size() - 1) {
				ZipPart partA = parts.get(i);
				if (partA instanceof LocalFileHeader) {
					LocalFileHeader localA = (LocalFileHeader) partA;
					boolean alreadySeen = !names.add(localA.getFileNameAsString());
					if (alreadySeen) {
						filtered.removePart(i);
						i--;
					}
				}
				i++;
			}
		}
	}

	/**
	 * Archive reader modeling how {@link ZipInputStream} works.
	 */
	public static class StreamedArchiveReader extends ArchiveReader {
		@Nonnull
		@Override
		public ClassesAndFiles read(@Nonnull Path path) throws IOException {
			return fromArchive(ZipIO.readNaive(path));
		}

		@Nonnull
		@Override
		public ClassesAndFiles read(@Nonnull byte[] raw) throws IOException {
			return fromArchive(ZipIO.readNaive(raw));
		}
	}
}
