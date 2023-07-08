package info.mmpa.concoction.model.impl;

import info.mmpa.concoction.model.ApplicationModel;
import info.mmpa.concoction.model.ModelSource;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Basic application model implementation.
 */
public class BasicApplicationModel implements ApplicationModel {
	private final ModelSource primarySource;
	private final Collection<ModelSource> supportingSources;

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
