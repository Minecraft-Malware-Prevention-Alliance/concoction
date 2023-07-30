package info.mmpa.concoction.scan.model.behavior;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModel;
import info.mmpa.concoction.scan.model.insn.InstructionsMatchingModelDeserializingConverter;

import java.util.Map;

/**
 * Converts {@link BehaviorMatchingModel} to {@link Map} for JSON representation.
 *
 * @see BehaviorMatchingModelDeserializingConverter The other way around.
 */
public class BehaviorMatchingModelSerializingConverter implements Converter<BehaviorMatchingModel, Map<String, BehaviorMatchEntry>> {
	@Override
	public Map<String, BehaviorMatchEntry> convert(BehaviorMatchingModel value) {
		return value.getVariants();
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		return typeFactory.constructType(BehaviorMatchingModel.class);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		JavaType keyType = typeFactory.constructType(String.class);
		JavaType valueType = typeFactory.constructType(BehaviorMatchEntry.class);
		return typeFactory.constructMapType(Map.class, keyType, valueType);
	}
}
