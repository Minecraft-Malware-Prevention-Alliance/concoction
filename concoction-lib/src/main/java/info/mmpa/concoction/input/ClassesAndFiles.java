package info.mmpa.concoction.input;

import me.coley.cafedude.classfile.ClassFile;
import me.coley.cafedude.io.ClassFileReader;
import me.coley.cafedude.io.ClassFileWriter;
import me.coley.cafedude.transform.IllegalStrippingTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic input model.
 */
public class ClassesAndFiles {
	private final Map<String, byte[]> classes = new HashMap<>();
	private final Map<String, byte[]> files = new HashMap<>();

	/**
	 * @param path
	 * 		Class file path name.
	 * @param raw
	 * 		Class file contents.
	 *
	 * @throws NotAClassException
	 * 		When the file contents do not contain a passable Java class file.
	 */
	public void addClass(@Nonnull String path, @Nonnull byte[] raw) throws NotAClassException {
		try {
			verifyAndAddClass(raw);
		} catch (Throwable t) {
			// Class was not parsed fully by ASM.
			// Maybe its using anti-asm exploits? Attempt to fix with cafedue.
			try {
				ClassFileReader reader = new ClassFileReader();
				ClassFile file = reader.read(raw);
				new IllegalStrippingTransformer(file).transform();
				ClassFileWriter writer = new ClassFileWriter();
				byte[] patched = writer.write(file);

				// Should be good to go now.
				verifyAndAddClass(patched);
			} catch (Throwable t2) {
				// Still not valid. Either this is not a class,
				// or we have a new anti-asm exploit to fix upstream in cafedude.
				throw new NotAClassException(path, t2);
			}
		}
	}

	private void verifyAndAddClass(@Nonnull byte[] raw) {
		// Our classes must be parsed with ASM for later SSVM integration.
		ClassReader reader = new ClassReader(raw);
		String className = reader.getClassName();
		reader.accept(new ClassWriter(0), ClassReader.SKIP_FRAMES);

		// If we get here, it means the class can be fully parsed and written back
		// so, it must be valid enough for our purposes. Otherwise, an exception would
		// have been thrown by ASM.
		classes.put(className, raw);
	}

	/**
	 * @param path
	 * 		File name.
	 * @param raw
	 * 		File contents.
	 */
	public void addFile(@Nonnull String path, @Nonnull byte[] raw) {
		files.put(path, raw);
	}

	/**
	 * @return The classes read.
	 */
	@Nonnull
	public Map<String, byte[]> getClasses() {
		return Collections.unmodifiableMap(classes);
	}

	/**
	 * @return The files read.
	 */
	@Nonnull
	public Map<String, byte[]> getFiles() {
		return Collections.unmodifiableMap(files);
	}
}
