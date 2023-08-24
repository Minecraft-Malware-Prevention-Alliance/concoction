package info.mmpa.concoction;

/**
 * Outline of a step in the {@link ConcoctionUxContext}.
 */
public interface ConcoctionStep {
	/**
	 * Called when this step is shown/navigated to.
	 */
	void onShown();

	/**
	 * Called when this step is hidden/navigated away from.
	 */
	void onHidden();
}
