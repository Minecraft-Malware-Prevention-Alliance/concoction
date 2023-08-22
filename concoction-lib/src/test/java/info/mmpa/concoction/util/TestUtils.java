package info.mmpa.concoction.util;

import dev.xdark.ssvm.invoke.Argument;
import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.impl.BasicApplicationModel;
import info.mmpa.concoction.input.model.impl.BasicModelSource;
import info.mmpa.concoction.scan.dynamic.EntryPoint;
import info.mmpa.concoction.scan.dynamic.EntryPointDiscovery;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.Type;
import software.coley.collections.Maps;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TestUtils {
	/**
	 * @param types
	 * 		Classes to fetch.
	 *
	 * @return Model of the classes.
	 *
	 * @throws IOException
	 * 		When the classes cannot be fetched.
	 */
	@Nonnull
	public static ApplicationModel appModel(@Nonnull Class<?>... types) throws IOException {
		Map<String, byte[]> classes = new HashMap<>();
		for (Class<?> type : types) {
			String internalName = type.getName().replace('.', '/');
			classes.put(internalName, code(type));
		}
		ModelSource model = new BasicModelSource("test", classes, Collections.emptyMap());
		return new BasicApplicationModel(model, Collections.emptyList());
	}

	/**
	 * @param type
	 * 		Class to fetch.
	 *
	 * @return Model of just the one class.
	 *
	 * @throws IOException
	 * 		When the class cannot be fetched.
	 */
	@Nonnull
	public static ApplicationModel appModel(@Nonnull Class<?> type) throws IOException {
		String internalName = type.getName().replace('.', '/');
		ModelSource model = new BasicModelSource("test", Maps.of(internalName, code(type)), Collections.emptyMap());
		return new BasicApplicationModel(model, Collections.emptyList());
	}

	/**
	 * @param type
	 * 		Class to fetch.
	 *
	 * @return Bytecode of class.
	 *
	 * @throws IOException
	 * 		When the class cannot be fetched.
	 */
	@Nonnull
	public static byte[] code(@Nonnull Class<?> type) throws IOException {
		String className = type.getName();
		InputStream is = ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
		if (is == null) throw new IOException(className + " not found");
		return IOUtils.toByteArray(is);
	}

	/**
	 * @param declaring
	 * 		Class to start with.
	 * @param method
	 * 		Method to start with. Assumed to have no arguments.
	 *
	 * @return Entry point for method.
	 */
	@Nonnull
	public static EntryPoint toEntryPoint(@Nonnull Class<?> declaring, @Nonnull Method method) {
		String className = declaring.getName().replace('.', '/');
		String methodName = method.getName();
		String methodDescriptor = Type.getMethodDescriptor(method);
		if (!methodDescriptor.contains("()"))
			throw new IllegalArgumentException("Method should have no arguments: " + methodDescriptor);
		Supplier<Argument[]> argumentSupplier = () -> new Argument[0];
		return new EntryPoint(className, methodName, methodDescriptor, argumentSupplier);
	}

	/**
	 * @param type
	 * 		Type to make entry point discovery for.
	 *
	 * @return Entry point discovery for any {@code public static} method with no arguments.
	 */
	@Nonnull
	public static EntryPointDiscovery getPublicStaticNoArgEntryPointDiscovery(@Nonnull Class<?> type) {
		return (model, context) -> getPublicStaticNoArgEntryPoints(type);
	}

	/**
	 * @param type
	 * 		Type to make entry point discovery for.
	 *
	 * @return Entry points for any {@code public static} method with no arguments.
	 */
	@Nonnull
	public static List<EntryPoint> getPublicStaticNoArgEntryPoints(@Nonnull Class<?> type) {
		int mask = Modifier.STATIC | Modifier.PUBLIC;
		return Arrays.stream(type.getDeclaredMethods())
				.filter(m -> (m.getModifiers() & mask) == mask && m.getParameterCount() == 0)
				.map(m -> toEntryPoint(type, m))
				.collect(Collectors.toList());
	}
}
