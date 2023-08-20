package info.mmpa.concoction;

import picocli.CommandLine;

/**
 * From: <a href="https://github.com/remkop/picocli/issues/774#issuecomment-516355359">remkop on #774</a>
 * <hr>
 * Disables indentation in options assumed to be grouped.
 * We're not grouping them though so here we are pasting.
 */
public class NoIndentationHelpFactory implements CommandLine.IHelpFactory {
	private static final int COLUMN_REQUIRED_OPTION_MARKER_WIDTH = 2;
	private static final int COLUMN_SHORT_OPTION_NAME_WIDTH = 2;
	private static final int COLUMN_OPTION_NAME_SEPARATOR_WIDTH = 2;
	private static final int COLUMN_LONG_OPTION_NAME_WIDTH = 22;

	private static final int INDEX_REQUIRED_OPTION_MARKER = 0;
	private static final int INDEX_SHORT_OPTION_NAME = 1;
	private static final int INDEX_OPTION_NAME_SEPARATOR = 2;
	private static final int INDEX_LONG_OPTION_NAME = 3;
	private static final int INDEX_OPTION_DESCRIPTION = 4;

	@Override
	public CommandLine.Help create(CommandLine.Model.CommandSpec commandSpec, CommandLine.Help.ColorScheme colorScheme) {
		return new CommandLine.Help(commandSpec, colorScheme) {
			@Override
			public Layout createDefaultLayout() {

				// The default layout creates a TextTable with 5 columns, as follows:
				// 0: empty text or (if configured) the requiredOptionMarker character
				// 1: short option name
				// 2: comma separator (if option has both short and long option)
				// 3: long option name(s)
				// 4: option description
				//
				// The code below creates a TextTable with 3 columns, as follows:
				// 0: empty text or (if configured) the requiredOptionMarker character
				// 1: all option names, comma-separated if necessary
				// 2: option description

				int optionNamesColumnWidth = COLUMN_SHORT_OPTION_NAME_WIDTH +
						COLUMN_OPTION_NAME_SEPARATOR_WIDTH +
						COLUMN_LONG_OPTION_NAME_WIDTH;

				TextTable table = TextTable.forColumnWidths(colorScheme.ansi(),
						COLUMN_REQUIRED_OPTION_MARKER_WIDTH,
						optionNamesColumnWidth,
						commandSpec.usageMessage().width() - (optionNamesColumnWidth + COLUMN_REQUIRED_OPTION_MARKER_WIDTH));
				Layout result = new Layout(colorScheme,
						table,
						createDefaultOptionRenderer(),
						createDefaultParameterRenderer()) {
					public void layout(CommandLine.Model.ArgSpec argSpec, Ansi.Text[][] cellValues) {

						// The default option renderer produces 5 Text values for each option.
						// Below we combine the short option name, comma separator and long option name
						// into a single Text object, and we pass 3 Text values to the TextTable.
						for (Ansi.Text[] original : cellValues) {
							if (original[INDEX_OPTION_NAME_SEPARATOR].getCJKAdjustedLength() > 0) {
								original[INDEX_OPTION_NAME_SEPARATOR] = original[INDEX_OPTION_NAME_SEPARATOR].concat(" ");
							}
							Ansi.Text[] threeColumns = new Ansi.Text[]{
									original[INDEX_REQUIRED_OPTION_MARKER],
									original[INDEX_SHORT_OPTION_NAME]
											.concat(original[INDEX_OPTION_NAME_SEPARATOR])
											.concat(original[INDEX_LONG_OPTION_NAME]),
									original[INDEX_OPTION_DESCRIPTION],
							};
							table.addRowValues(threeColumns);
						}
					}
				};
				return result;
			}
		};
	}
}
