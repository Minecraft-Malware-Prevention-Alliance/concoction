package info.mmpa.concoction.panel;

import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.util.UiUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Panel for file selection.
 */
public class InputsPanel extends TableModelPanel<Path> {
	private static final int COL_NAME = 0;
	private static final int COL_SIZE = 1;

	/**
	 * New input panel.
	 *
	 * @param context
	 * 		UI context.
	 */
	public InputsPanel(@Nonnull ConcoctionUxContext context) {
		super(context);
		markIsFirstCard();
		initComponents();
	}

	@Nonnull
	@Override
	protected String[] getTableColumnNames() {
		ResourceBundle bundle = UiUtils.getBundle();
		return new String[]{bundle.getString("column.path"), bundle.getString("column.size")};
	}

	@Nonnull
	@Override
	protected String elementToText(Path path, int columnIndex) {
		switch (columnIndex) {
			case COL_NAME:
				return path.getFileName().toString();
			case COL_SIZE:
				return UiUtils.fileSize(path);
		}
		return "";
	}

	@Nonnull
	@Override
	protected String getTitleKey() {
		return "input-panel.title";
	}

	@Nonnull
	@Override
	protected List<Path> adaptPathsToElement(@Nonnull List<Path> selectedPaths) {
		// Not conversion needed, though later if we want to do more input validation here we can.
		// Non-zip/jar files can be filtered out for instance.
		return selectedPaths;
	}

	@Override
	protected void configureFileDialog(@Nonnull FileDialog dialog) {
		UiUtils.setFileDialogExtensions(dialog, Arrays.asList(".jar", ".zip"));
	}
}
