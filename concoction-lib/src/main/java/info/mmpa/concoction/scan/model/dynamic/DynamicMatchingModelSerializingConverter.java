package info.mmpa.concoction.scan.model.dynamic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import info.mmpa.concoction.scan.model.dynamic.entry.DynamicMatchEntry;

import java.util.Map;

/**
 * Converts {@link DynamicMatchingModel} to {@link Map} for JSON representation.
 *
 * @see DynamicMatchingModelDeserializingConverter The other way around.
 */
public class DynamicMatchingModelSerializingConverter implements Converter<DynamicMatchingModel, Map<String, DynamicMatchEntry>> {
	@Override
	public Map<String, DynamicMatchEntry> convert(DynamicMatchingModel value) {
		return value.getVariants();
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		return typeFactory.constructType(DynamicMatchingModel.class);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		JavaType keyType = typeFactory.constructType(String.class);
		JavaType valueType = typeFactory.constructType(DynamicMatchEntry.class);
		return typeFactory.constructMapType(Map.class, keyType, valueType);
	}
}
