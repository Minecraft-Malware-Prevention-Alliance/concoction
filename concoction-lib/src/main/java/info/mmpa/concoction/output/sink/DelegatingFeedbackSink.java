package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.path.ClassPathElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Delegating feedback sink.
 */
public class DelegatingFeedbackSink implements FeedbackSink {
	private final FeedbackSink delegate;

	/**
	 * @param delegate
	 * 		Sink to delegate to.
	 */
	public DelegatingFeedbackSink(@Nonnull FeedbackSink delegate) {
		this.delegate = delegate;
	}

	@Override
	public boolean isCancelRequested() {
		return delegate.isCancelRequested();
	}

	@Nullable
	@Override
	public InstructionFeedbackItemSink openClassFeedbackSink(@Nonnull ClassPathElement classPath) {
		return delegate.openClassFeedbackSink(classPath);
	}

	@Nullable
	@Override
	public DynamicFeedbackItemSink openDynamicFeedbackSink() {
		return delegate.openDynamicFeedbackSink();
	}
}
