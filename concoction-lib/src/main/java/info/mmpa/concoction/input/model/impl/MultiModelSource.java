package info.mmpa.concoction.input.model.impl;

import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.util.SplitMap;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * A model source primarily used for {@link ApplicationModel#flatView()} which can represent the combined contents of
 * multiple model sources in a flat representation.
 */
public class MultiModelSource implements ModelSource {
	private final String identifier;
	private final Map<String, byte[]> classes;
	private final Map<String, byte[]> files;

	/**
	 * @param identifier
	 * 		Identifier of the source.
	 * @param sources
	 * 		Model sources to delegate to.
	 */
	public MultiModelSource(@Nonnull String identifier, @Nonnull List<ModelSource> sources) {
		this.identifier = identifier;

		// Create delegate maps for classes/files
		int sourceCount = sources.size();
		if (sourceCount == 0) throw new IllegalArgumentException("Must have at least 1 source");
		else if (sourceCount == 1) {
			// Single item, just use directly
			ModelSource source = sources.get(0);
			classes = source.classes();
			files = source.files();
		} else {
			// Multiple items, wrap in layered split-maps.
			// Items first in the list are assumed to be higher preference.
			ModelSource source = sources.get(0);
			Map<String, byte[]> tempClasses = source.classes();
			Map<String, byte[]> tempFiles = source.files();
			for (int i = 1; i < sourceCount; i++) {
				ModelSource nextSource = sources.get(i);
				tempClasses = new SplitMap<>(tempClasses, nextSource.classes());
				tempFiles = new SplitMap<>(tempFiles, nextSource.files());
			}
			classes = tempClasses;
			files = tempFiles;
		}
	}

	@Nonnull
	@Override
	public String identifier() {
		return identifier;
	}

	@Nonnull
	@Override
	public Map<String, byte[]> classes() {
		return classes;
	}

	@Nonnull
	@Override
	public Map<String, byte[]> files() {
		return files;
	}
}
