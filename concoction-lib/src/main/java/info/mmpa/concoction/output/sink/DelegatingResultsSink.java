package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;

import javax.annotation.Nonnull;

/**
 * Delegating result sink implementation.
 */
public class DelegatingResultsSink implements ResultsSink {
	private final ResultsSink delegate;

	/**
	 * @param delegate
	 * 		Sink to delegate operations to.
	 */
	public DelegatingResultsSink(@Nonnull ResultsSink delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
		delegate.onDetection(path, type, detection);
	}

	@Nonnull
	@Override
	public Results buildResults() {
		return delegate.buildResults();
	}
}
