package info.mmpa.concoction.input.model.path;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Serializes {@link PathElement} into JSON strings.
 */
public class PathSerializer extends StdSerializer<PathElement> {
	public PathSerializer() {
		super(PathElement.class);
	}

	@Override
	public void serialize(PathElement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(toString(value));
	}

	/**
	 * @param value
	 * 		Path to serialize.
	 *
	 * @return String representation.
	 */
	@Nonnull
	public static String toString(@Nullable PathElement value) {
		if (value instanceof SourcePathElement) {
			SourcePathElement sourcePathElement = (SourcePathElement) value;
			return sourcePathElement.getSource().identifier();
		} else if (value instanceof ClassPathElement) {
			ClassPathElement classPathElement = (ClassPathElement) value;
			return classPathElement.getClassName();
		} else if (value instanceof MethodPathElement) {
			MethodPathElement methodPathElement = (MethodPathElement) value;
			return methodPathElement.getClassName() + "." +
					methodPathElement.getMethodName() + methodPathElement.getMethodDesc();
		} else if (value != null) {
			throw new IllegalStateException("Unknown path element type, cannot serialize: " + value.getClass().getSimpleName());
		} else {
			throw new IllegalArgumentException("Cannot serialize null path");
		}
	}
}
