package info.mmpa.concoction.util;

import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.impl.BasicApplicationModel;
import info.mmpa.concoction.input.model.impl.BasicModelSource;
import org.apache.commons.io.IOUtils;
import software.coley.collections.Maps;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public class TestUtils {
	@Nonnull
	public static ApplicationModel appModel(@Nonnull Class<?> type) throws IOException {
		String internalName = type.getName().replace('.', '/');
		ModelSource model = new BasicModelSource("test", Maps.of(internalName, code(type)), Collections.emptyMap());
		return new BasicApplicationModel(model, Collections.emptyList());
	}

	@Nonnull
	public static byte[] code(@Nonnull Class<?> type) throws IOException {
		String className = type.getName();
		InputStream is = ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
		if (is == null) throw new IOException(className + " not found");
		return IOUtils.toByteArray(is);
	}
}
