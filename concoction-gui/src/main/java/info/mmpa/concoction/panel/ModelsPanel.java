package info.mmpa.concoction.panel;

import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.output.DetectionArchetype;
import info.mmpa.concoction.scan.model.ScanModel;
import info.mmpa.concoction.util.UiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Panel for model selection.
 */
public class ModelsPanel extends TableModelPanel<ModelsPanel.ScanModelWithPath> {
	private static final Logger logger = LoggerFactory.getLogger(ModelsPanel.class);
	private static final int COL_NAME = 0;
	private static final int COL_DESC = 1;

	/**
	 * New models panel.
	 *
	 * @param context
	 * 		UI context.
	 */
	public ModelsPanel(@Nonnull ConcoctionUxContext context) {
		super(context);
		initComponents();
	}

	@Nonnull
	@Override
	protected String[] getTableColumnNames() {
		ResourceBundle bundle = UiUtils.getBundle();
		return new String[]{bundle.getString("column.model-id"), bundle.getString("column.model-description")};
	}

	@Nonnull
	@Override
	protected String elementToText(ScanModelWithPath modelWithPath, int columnIndex) {
		DetectionArchetype archetype = modelWithPath.model.getDetectionArchetype();
		switch (columnIndex) {
			case COL_NAME:
				return archetype.getIdentifier();
			case COL_DESC:
				return archetype.getDescription();
		}
		return "";
	}

	@Nonnull
	@Override
	protected String getTitleKey() {
		return "model-panel.title";
	}

	@Nonnull
	@Override
	protected List<ScanModelWithPath> adaptPathsToElement(@Nonnull List<Path> selectedPaths) {
		return selectedPaths.stream().map(ScanModelWithPath::readOrNull).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	protected void configureFileDialog(@Nonnull FileDialog dialog) {
		UiUtils.setFileDialogExtensions(dialog, Collections.singletonList(".json"));
	}

	/**
	 * Wrapper of {@link ScanModel} with the path it originates from.
	 */
	public static class ScanModelWithPath {
		private final ScanModel model;
		private final Path source;

		private ScanModelWithPath(@Nonnull ScanModel model, @Nonnull Path source) {
			this.model = model;
			this.source = source;
		}

		@Nullable
		public static ScanModelWithPath readOrNull(@Nonnull Path path) {
			try {
				return new ScanModelWithPath(ScanModel.fromJson(path), path);
			} catch (Throwable t) {
				logger.warn("Failed to read model from '{}' dropping", path.getFileName(), t);
				return null;
			}
		}
	}
}
