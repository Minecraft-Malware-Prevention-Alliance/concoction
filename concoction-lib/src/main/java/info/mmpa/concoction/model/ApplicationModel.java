package info.mmpa.concoction.model;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

/**
 * Full application model, comprised of one or more units.
 * A primary unit defines the code to analyze, and additional supporting units can be added to enhance
 * the accuracy of the scan if needed.
 *
 * @see ModelSource Single component type that builds up the model.
 * @see ModelBuilder For creating new {@link ApplicationModel} instances.
 */
public interface ApplicationModel {
	/**
	 * The primary source is the main component of the full application model.
	 * This main component contains the code that we want to analyze.
	 *
	 * @return The primary logic to analyze in an application.
	 */
	@Nonnull
	ModelSource primarySource();

	/**
	 * The supporting sources are external components that do not outline application logic we want to analyze.
	 * These sources contain all the libraries and dependencies of the {@link #primarySource() primary source}.
	 *
	 * @return Supporting models used to supplement analysis.
	 */
	@Nonnull
	Collection<ModelSource> supportingSources();

	/**
	 * @return A collection of all the sources in this model, including the {@link #primarySource() primary source}
	 * and all {@link #supportingSources() supporting sources}.
	 */
	@Nonnull
	default Collection<ModelSource> allSources() {
		return concat(of(primarySource()), supportingSources().stream())
				.collect(Collectors.toList());
	}

	/**
	 * @return Flat representation of classes and resources from the {@link #primarySource() primary source} and
	 * {@link #supportingSources() all supporting sources}.
	 */
	@Nonnull
	ModelSource flatView();
}
