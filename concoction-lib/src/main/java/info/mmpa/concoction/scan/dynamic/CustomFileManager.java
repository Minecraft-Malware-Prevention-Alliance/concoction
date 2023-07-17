package info.mmpa.concoction.scan.dynamic;

import dev.xdark.ssvm.filesystem.SimpleFileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * New JDK's require a bit more IO than prior ones in class-loading internals.
 * Giving access to some simple name/attribute look-ups allows usage on more modern JDK's.
 * <p>
 * Reading and writing to files is still unsupported in this implementation, so the VM is
 * in a very limited read-only state using this.
 */
public class CustomFileManager extends SimpleFileManager {
	@Override
	public <A extends BasicFileAttributes> A getAttributes(String path, Class<A> attrType, LinkOption... options) throws IOException {
		Path p = Paths.get(path);
		if (!Files.exists(p))
			return null;
		return Files.readAttributes(p, attrType, options);
	}

	@Override
	public String[] list(String path) {
		return new File(path).list();
	}
}
