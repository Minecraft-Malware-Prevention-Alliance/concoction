package info.mmpa.concoction.panel;

import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.adapter.SingleListSelectionAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.common.collect.ObservableList;
import com.jgoodies.common.collect.ObservableList2;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.util.ListDataListenerAdapter;
import info.mmpa.concoction.util.UiUtils;
import org.kordamp.ikonli.carbonicons.CarbonIcons;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static info.mmpa.concoction.util.UiUtils.icon;

/**
 * Common layout / modeling for inputs with a table display.
 *
 * @param <T>
 * 		Table row content type.
 */
public abstract class TableModelPanel<T> extends JPanel {
	private final ObservableList2<T> model = new ArrayListModel<>();
	private final SelectionInList<T> selectionModel = new SelectionInList<>((List<T>) model);
	private final ConcoctionUxContext context;
	private final JButton btnNext = new JButton();
	private final JButton btnRemoveSelected = new JButton();
	private JTable inputTable;
	private boolean isFirst;
	private boolean isLast;

	protected TableModelPanel(@Nonnull ConcoctionUxContext context) {
		this.context = context;

		// Map 'next' availability to there being at least one item in the model.
		model.addListDataListener((ListDataListenerAdapter) e -> btnNext.setEnabled(!model.isEmpty()));

		// Map 'remove' availability to there being a selection.
		selectionModel.addValueChangeListener(e -> btnRemoveSelected.setEnabled(selectionModel.hasSelection()));
	}

	@SuppressWarnings("deprecation")
	protected void initComponents() {
		ResourceBundle bundle = UiUtils.getBundle();

		// Base layout
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new FormLayout(
				"left:default:grow, $lcgap, right:default",
				"default, $lgap, fill:default:grow, $lgap, default"));

		// Title
		JLabel lblTitle = new JLabel();
		lblTitle.setText(bundle.getString(getTitleKey()));
		lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 22));

		// Table for visualizing inputs
		String[] columnNames = getTableColumnNames();
		AbstractTableAdapter<T> tableAdapter = new AbstractTableAdapter<>(model, columnNames) {
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				T element = model.get(rowIndex);
				if (element == null)
					return "";
				else
					return elementToText(element, columnIndex);
			}
		};
		inputTable = BasicComponentFactory.createTable(selectionModel, tableAdapter);
		inputTable.setSelectionModel(new SingleListSelectionAdapter(selectionModel.getSelectionIndexHolder()));
		JScrollPane tableWrapper = new JScrollPane();
		tableWrapper.setViewportView(inputTable);

		// Button actions for adding/removing content
		FileDialog dialog = new FileDialog(context.getFrame(), bundle.getString("input.select-files"));
		dialog.setMultipleMode(true);
		dialog.setMode(FileDialog.LOAD);
		dialog.setDirectory("C:\\Code\\Java\\concoction\\concoction-cli\\build\\libs");
		JButton btnAdd = new JButton();
		btnAdd.addActionListener(e -> {
			// We configure per-show because things like the file filter (at least the way we have to do it
			// to bypass an implementation problem on windows) 'forgets' itself after you accept one input.
			configureFileDialog(dialog);
			dialog.setVisible(true);

			// Handle selected files and update model/table
			File[] selected = dialog.getFiles();
			if (selected != null && selected.length > 0) {
				List<Path> selectedPaths = Arrays.stream(selected)
						.map(File::toPath)
						.filter(Files::isRegularFile)
						.collect(Collectors.toList());
				List<T> adapted = adaptPathsToElement(selectedPaths);
				if (!adapted.isEmpty())
					model.addAll(adapted);
			}
		});
		btnAdd.setIcon(icon(CarbonIcons.ADD_FILLED));
		btnAdd.setText(bundle.getString("input.add"));
		btnRemoveSelected.addActionListener(e -> model.remove(selectionModel.getSelection()));
		btnRemoveSelected.setIcon(icon(CarbonIcons.TRASH_CAN));
		btnRemoveSelected.setText(bundle.getString("input.remove-selected"));
		btnRemoveSelected.setEnabled(false);

		// Going to the next panel
		btnNext.addActionListener(e -> context.gotoNext());
		btnNext.setIcon(icon(CarbonIcons.NEXT_FILLED));
		btnNext.setText(bundle.getString("input.next"));
		btnNext.setEnabled(false);

		// Going to the previous panel
		JButton btnPrevious = new JButton();
		btnPrevious.addActionListener(e -> context.gotoPrevious());
		btnPrevious.setIcon(icon(CarbonIcons.PREVIOUS_FILLED));
		btnPrevious.setText(bundle.getString("input.previous"));

		// Lay everything out
		JPanel inputActions = new JPanel();
		inputActions.setLayout(new FlowLayout(FlowLayout.LEFT));
		inputActions.add(btnAdd);
		inputActions.add(btnRemoveSelected);
		JPanel navigation = new JPanel();
		navigation.setLayout(new FlowLayout(FlowLayout.RIGHT));
		if (!isFirst) navigation.add(btnPrevious);
		if (!isLast) navigation.add(btnNext);
		add(lblTitle, CC.xywh(1, 1, 3, 1, CC.CENTER, CC.DEFAULT));
		add(tableWrapper, CC.xywh(1, 3, 3, 1));
		add(inputActions, CC.xy(1, 5));
		add(navigation, CC.xy(3, 5));
	}

	/**
	 * @return Table model.
	 */
	@Nonnull
	public ObservableList<T> getModel() {
		return model;
	}

	/**
	 * @return Selection in table model.
	 */
	@Nonnull
	public SelectionInList<T> getSelectionModel() {
		return selectionModel;
	}

	@Nonnull
	protected JTable getInputTable() {
		return inputTable;
	}

	protected void markIsFirstCard() {
		isFirst = true;
	}

	protected void markIsLastCard() {
		isLast = true;
	}

	@Nonnull
	protected abstract String[] getTableColumnNames();

	@Nonnull
	protected abstract String elementToText(T element, int columnIndex);

	@Nonnull
	protected abstract String getTitleKey();

	@Nonnull
	protected abstract List<T> adaptPathsToElement(@Nonnull List<Path> selectedPaths);

	protected abstract void configureFileDialog(@Nonnull FileDialog dialog);
}
