package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.path.ClassPathElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * No-operation feedback sink.
 */
public class NoopFeedbackSink implements FeedbackSink {
	@Nullable
	@Override
	public InstructionFeedbackItemSink openClassFeedbackSink(@Nonnull ClassPathElement classPath) {
		return null;
	}

	@Nullable
	@Override
	public DynamicFeedbackItemSink openDynamicFeedbackSink() {
		return null;
	}
}
