package info.mmpa.concoction.scan.model.insn;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.List;
import java.util.Map;

/**
 * Converts {@link InstructionsMatchingModel} to {@link Map} for JSON representation.
 *
 * @see InstructionsMatchingModelDeserializingConverter The other way around.
 */
public class InstructionsMatchingModelSerializingConverter implements Converter<InstructionsMatchingModel, Map<String, ? extends List<InstructionMatchEntry>>> {
	@Override
	public Map<String, ? extends List<InstructionMatchEntry>> convert(InstructionsMatchingModel value) {
		return value.getVariants();
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		return typeFactory.constructType(InstructionsMatchingModel.class);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		JavaType keyType = typeFactory.constructType(String.class);
		CollectionLikeType valueType = typeFactory.constructCollectionLikeType(List.class, InstructionMatchEntry.class);
		return typeFactory.constructMapType(Map.class, keyType, valueType);
	}
}
