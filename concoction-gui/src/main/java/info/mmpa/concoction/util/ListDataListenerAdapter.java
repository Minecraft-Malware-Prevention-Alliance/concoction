package info.mmpa.concoction.util;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Adapter for {@link ListDataListener} to allow usage as a lambda.
 */
public interface ListDataListenerAdapter extends ListDataListener {
	/**
	 * Handles all events.
	 *
	 * @param e Event handled.
	 */
	void onEvent(ListDataEvent e);

	@Override
	default void intervalAdded(ListDataEvent e) {
		onEvent(e);
	}

	@Override
	default void intervalRemoved(ListDataEvent e) {
		onEvent(e);
	}

	@Override
	default void contentsChanged(ListDataEvent e) {
		onEvent(e);
	}
}
