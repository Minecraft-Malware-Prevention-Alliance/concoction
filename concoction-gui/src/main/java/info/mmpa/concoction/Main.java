package info.mmpa.concoction;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;
import info.mmpa.concoction.util.UiUtils;

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
		if (args != null)
			for (String arg : args)
				if (arg.equalsIgnoreCase("debug")) {
					UiUtils.debug = true;
					break;
				}
		LafManager.install(new DarculaTheme());
		new ConcoctionWindow().showInitial();
	}
}
