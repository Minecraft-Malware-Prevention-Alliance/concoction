package info.mmpa.concoction.input.model.impl;

import info.mmpa.concoction.input.model.ModelSource;
import info.mmpa.concoction.input.model.path.SourcePathElement;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Delegating implementation of {@link ModelSource}.
 */
public class DelegatingModelSource implements ModelSource {
	private final ModelSource delegate;

	/**
	 * @param delegate
	 * 		Source to delegate to.
	 */
	public DelegatingModelSource(@Nonnull ModelSource delegate) {
		this.delegate = delegate;
	}

	@Nonnull
	@Override
	public String identifier() {
		return delegate.identifier();
	}

	@Nonnull
	@Override
	public Map<String, byte[]> classes() {
		return delegate.classes();
	}

	@Nonnull
	@Override
	public Map<String, byte[]> files() {
		return delegate.files();
	}

	@Nonnull
	@Override
	public SourcePathElement path() {
		return delegate.path();
	}

	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public String toString() {
		return delegate.toString();
	}
}
