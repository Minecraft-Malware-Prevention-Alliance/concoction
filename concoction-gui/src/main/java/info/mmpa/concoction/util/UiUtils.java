package info.mmpa.concoction.util;

import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Various string utils for UI display.
 */
public class UiUtils {
	private static final Logger logger = LoggerFactory.getLogger(UiUtils.class);
	private static final String[] SIZE_UNITS = new String[]{"B", "kB", "MB", "GB", "TB"};
	private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("#,##0.#");
	private static final ResourceBundle bundle = ResourceBundle.getBundle("strings");

	/**
	 * @return Resource bundle for text translations.
	 */
	@Nonnull
	public static ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * @param path
	 * 		File path to get size of.
	 *
	 * @return Human legible representation of size.
	 */
	@Nonnull
	public static String fileSize(@Nonnull Path path) {
		if (Files.isRegularFile(path)) {
			try {
				return fileSize(Files.size(path));
			} catch (IOException ex) {
				logger.warn("Failed to read size of file '{}'", path.getFileName(), ex);
			}
		}
		return fileSize(-1);
	}

	/**
	 * @param size
	 * 		Size in bytes.
	 *
	 * @return Human legible representation of size.
	 */
	@Nonnull
	public static String fileSize(long size) {
		if (size <= 0)
			return "?";
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return SIZE_FORMAT.format(size / Math.pow(1024, digitGroups)) + " " + SIZE_UNITS[digitGroups];
	}

	/**
	 * @param dialog
	 * 		Dialog to update.
	 * @param extensions
	 * 		Extensions to filter with.
	 */
	public static void setFileDialogExtensions(@Nonnull FileDialog dialog, @Nonnull List<String> extensions) {
		dialog.setFile(extensions.stream().map(ext -> "*" + ext).collect(Collectors.joining(";")));
		dialog.setFilenameFilter((dir, name) -> {
			String lower = name.toLowerCase();
			return extensions.stream().anyMatch(lower::endsWith);
		});
	}

	/**
	 * @param icon
	 * 		Icon pack item.
	 *
	 * @return Swing icon of item.
	 */
	public static Icon icon(Ikon icon) {
		return FontIcon.of(icon, 24, Color.LIGHT_GRAY);
	}
}
