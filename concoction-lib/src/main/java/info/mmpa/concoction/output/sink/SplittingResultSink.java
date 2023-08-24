package info.mmpa.concoction.output.sink;

import info.mmpa.concoction.input.model.path.PathElement;
import info.mmpa.concoction.output.Detection;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.output.Results;
import info.mmpa.concoction.util.Unchecked;

import javax.annotation.Nonnull;

/**
 * Splitting sink which feeds results into a given delegate sink, but keeps a local copy of the results as well.
 * <p>
 * This can be used to manage different scopes of results.
 * <pre>
 * {@code
 * ResultsSink aggregate = new BasicResultsSink();
 * ResultsSink sub1 = new SplittingResultSink(aggregate);
 * ResultsSink sub2 = new SplittingResultSink(aggregate);
 *
 * scanWith(sub1);
 * scanWith(sub2);
 *
 * aggregate.buildResults(); // Has results of 'sub1' + 'sub2'
 * sub1.buildResults(); // Only has its own results, none of 'sub2'
 * sub2.buildResults(); // Only has its own results, none of 'sub1'
 * }</pre>
 */
public class SplittingResultSink extends DelegatingResultsSink {
	private final ResultsSink localSink = new BasicResultsSink();

	/**
	 * @param delegate
	 * 		Sink to delegate operations to.
	 */
	public SplittingResultSink(@Nonnull ResultsSink delegate) {
		super(delegate);
	}

	@Override
	public void onDetection(@Nonnull PathElement path, @Nonnull DetectionArchetype type, @Nonnull Detection detection) {
		super.onDetection(path, type, detection);

		// Copy detections to our own local sink so that we can later report only results specific to our instance
		// rather than whatever the delegate sink given by the constructor has.
		Unchecked.runSafe("split-sink-feed", () -> localSink.onDetection(path, type, detection));
	}

	/**
	 * @return Local results seen only by this instance.
	 */
	@Nonnull
	@Override
	public Results buildResults() {
		return localSink.buildResults();
	}
}
