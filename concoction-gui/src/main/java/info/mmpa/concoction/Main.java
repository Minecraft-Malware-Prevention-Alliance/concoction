package info.mmpa.concoction;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;

/**
 * GUI entry point.
 */
public class Main {
	/**
	 * Install the UI theme, and open the main window.
	 *
	 * @param args
	 * 		Unused.
	 */
	public static void main(String[] args) {
		LafManager.install(new DarculaTheme());
		new ConcoctionWindow().showInitial();
	}
}
