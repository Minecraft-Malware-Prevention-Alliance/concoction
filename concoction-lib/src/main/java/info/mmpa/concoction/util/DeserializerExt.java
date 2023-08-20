package info.mmpa.concoction.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Extended deserializer with some common utilities.
 *
 * @param <T>
 * 		Target type to deserialize into.
 */
public abstract class DeserializerExt<T> extends StdDeserializer<T> {
	/**
	 * @param valueClass
	 * 		Type to deserialize.
	 */
	protected DeserializerExt(@Nonnull Class<?> valueClass) {
		super(valueClass);
	}

	/**
	 * @param root
	 * 		Root to search in.
	 * @param targetFieldName
	 * 		Field name to search for.
	 *
	 * @return Matching field in the root by name, ignoring case.
	 */
	@Nullable
	protected static JsonNode findCaseless(@Nonnull JsonNode root, @Nonnull String targetFieldName) {
		Iterator<String> itr = root.fieldNames();
		while (itr.hasNext()) {
			String fieldName = itr.next();
			if (targetFieldName.equalsIgnoreCase(fieldName))
				return root.get(fieldName);
		}
		return null;
	}
}
