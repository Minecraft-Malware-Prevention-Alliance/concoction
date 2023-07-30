package info.mmpa.concoction.scan.model.behavior;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Collections;
import java.util.Map;

/**
 * Converts {@link Map} <i>(From the JSON layout)</i> to {@link BehaviorMatchingModel}.
 *
 * @see BehaviorMatchingModelSerializingConverter The other way around.
 */
public class BehaviorMatchingModelDeserializingConverter implements Converter<Map<String, BehaviorMatchEntry>, BehaviorMatchingModel> {
	@Override
	public BehaviorMatchingModel convert(Map<String, BehaviorMatchEntry> value) {
		if (value == null) value = Collections.emptyMap();
		return new BehaviorMatchingModel(value);
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		JavaType keyType = typeFactory.constructType(String.class);
		JavaType valueType = typeFactory.constructType(BehaviorMatchEntry.class);
		return typeFactory.constructMapType(Map.class, keyType, valueType);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		return typeFactory.constructType(BehaviorMatchingModel.class);
	}
}
