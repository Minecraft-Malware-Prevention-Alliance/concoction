package info.mmpa.concoction.scan.model.insn;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Converts {@link Map} <i>(From the JSON layout)</i> to {@link InstructionsMatchingModel}.
 *
 * @see InstructionsMatchingModelSerializingConverter The other way around.
 */
public class InstructionsMatchingModelDeserializingConverter implements Converter<Map<String, List<InstructionMatchEntry>>, InstructionsMatchingModel> {
	@Override
	public InstructionsMatchingModel convert(Map<String, List<InstructionMatchEntry>> value) {
		if (value == null) value = Collections.emptyMap();
		return new InstructionsMatchingModel(value);
	}

	@Override
	public JavaType getInputType(TypeFactory typeFactory) {
		JavaType keyType = typeFactory.constructType(String.class);
		CollectionLikeType valueType = typeFactory.constructCollectionLikeType(List.class, InstructionMatchEntry.class);
		return typeFactory.constructMapType(Map.class, keyType, valueType);
	}

	@Override
	public JavaType getOutputType(TypeFactory typeFactory) {
		return typeFactory.constructType(InstructionsMatchingModel.class);
	}
}
