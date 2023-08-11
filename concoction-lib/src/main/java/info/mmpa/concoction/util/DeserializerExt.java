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
	protected DeserializerExt(@Nonnull Class<?> vc) {
		super(vc);
	}

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
