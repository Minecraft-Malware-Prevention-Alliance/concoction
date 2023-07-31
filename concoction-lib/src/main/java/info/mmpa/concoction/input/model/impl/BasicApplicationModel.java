package info.mmpa.concoction.input.model.impl;

import info.mmpa.concoction.input.model.ApplicationModel;
import info.mmpa.concoction.input.model.ModelSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic application model implementation.
 */
public class BasicApplicationModel implements ApplicationModel {
	private final ModelSource primarySource;
	private final Collection<ModelSource> supportingSources;
	private final ModelSource flatView;

	/**
	 * @param primarySource
	 * 		The primary logic to analyze in an application.
	 * @param supportingSources
	 * 		Supporting models used to supplement analysis.
	 */
	public BasicApplicationModel(@Nonnull ModelSource primarySource,
								 @Nonnull Collection<ModelSource> supportingSources) {
		this.primarySource = primarySource;
		this.supportingSources = supportingSources;

		// Create flat view of all sources
		List<ModelSource> sources = new ArrayList<>(1 + supportingSources.size());
		sources.add(primarySource);
		sources.addAll(supportingSources);
		flatView = new MultiModelSource("flattened", sources);
	}

	@Nonnull
	@Override
	public ModelSource primarySource() {
		return primarySource;
	}

	@Nonnull
	@Override
	public Collection<ModelSource> supportingSources() {
		return supportingSources;
	}

	@Nonnull
	@Override
	public ModelSource flatView() {
		return flatView;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BasicApplicationModel that = (BasicApplicationModel) o;

		if (!primarySource.equals(that.primarySource)) return false;
		return supportingSources.equals(that.supportingSources);
	}

	@Override
	public int hashCode() {
		int result = primarySource.hashCode();
		result = 31 * result + supportingSources.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "BasicApplicationModel{" +
				"primarySource=" + primarySource +
				", supportingSources=" + supportingSources +
				'}';
	}
}
