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
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("strings");
	private static final int MAX_CLASS_LEN = 80;
	private static final int DEFAULT_ICON_SIZE = 20;

	/**
	 * Debug flag. Primarily used to enable extra logging.
	 */
	public static boolean debug;

	/**
	 * @return Resource bundle for text translations.
	 */
	@Nonnull
	public static ResourceBundle getBundle() {
		return RESOURCE_BUNDLE;
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
	 * 		Extensions to filter with. Strings must include the '.' prefix.
	 */
	public static void setFileDialogExtensions(@Nonnull FileDialog dialog, @Nonnull List<String> extensions) {
		// This is a work-around for windows which does not support 'setFilenameFilter'
		// Gotta love ol' fashion swing...
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			dialog.setFile(extensions.stream().map(ext -> "*" + ext).collect(Collectors.joining(";")));

		// For sane operating systems, this works.
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
	@Nonnull
	public static Icon icon(@Nonnull Ikon icon) {
		return FontIcon.of(icon, DEFAULT_ICON_SIZE, Color.LIGHT_GRAY);
	}

	/**
	 * @param icon
	 * 		Icon graphic.
	 * @param color
	 * 		Icon color.
	 *
	 * @return Swing icon of item.
	 */
	@Nonnull
	public static Icon icon(@Nonnull Ikon icon, @Nonnull Color color) {
		return FontIcon.of(icon, DEFAULT_ICON_SIZE, color);
	}

	/**
	 * @param icon
	 * 		Icon graphic.
	 * @param size
	 * 		Icon size.
	 *
	 * @return Swing icon of item.
	 */
	@Nonnull
	public static Icon icon(@Nonnull Ikon icon, int size) {
		return FontIcon.of(icon, size, Color.LIGHT_GRAY);
	}

	/**
	 * @param icon
	 * 		Icon graphic.
	 * @param size
	 * 		Icon size.
	 * @param color
	 * 		Icon color.
	 *
	 * @return Swing icon of item.
	 */
	@Nonnull
	public static Icon icon(@Nonnull Ikon icon, int size, @Nonnull Color color) {
		return FontIcon.of(icon, size, color);
	}

	/**
	 * @param name
	 * 		Class name.
	 *
	 * @return Filtered name.
	 */
	@Nonnull
	public static String filterClassName(@Nonnull String name) {
		if (name.indexOf('<') >= 0)
			return "?";
		if (name.length() > MAX_CLASS_LEN)
			return name.substring(0, MAX_CLASS_LEN) + "...";
		return name;
	}
}
