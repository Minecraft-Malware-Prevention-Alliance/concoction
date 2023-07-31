package info.mmpa.concoction.scan.model.dynamic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Collections;
import java.util.Map;

/**
 * Converts {@link Map} <i>(From the JSON layout)</i> to {@link DynamicMatchingModel}.
 *
 * @see DynamicMatchingModelSerializingConverter The other way around.
 */
public class DynamicMatchingModelDeserializingConverter implements Converter<Map<String, DynamicMatchEntry>, DynamicMatchingModel> {
	@Override
	public DynamicMatchingModel convert(Map<String, DynamicMatchEntry> value) {
		if (value == null) value = Collections.emptyMap();
		return new DynamicMatchingModel(value);
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		JavaType keyType = typeFactory.constructType(String.class);
		JavaType valueType = typeFactory.constructType(DynamicMatchEntry.class);
		return typeFactory.constructMapType(Map.class, keyType, valueType);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		return typeFactory.constructType(DynamicMatchingModel.class);
	}
}
