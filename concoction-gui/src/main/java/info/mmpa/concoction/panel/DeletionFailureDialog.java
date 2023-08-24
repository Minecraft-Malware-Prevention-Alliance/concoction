package info.mmpa.concoction.panel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import info.mmpa.concoction.util.UiUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Dialog showing the user which files couldn't be deleted from {@link EndPanel}.
 */
public class DeletionFailureDialog extends JDialog {
	private static final ResourceBundle bundle = UiUtils.getBundle();

	/**
	 * New dialog for telling user some files could not be deleted.
	 *
	 * @param owner
	 * 		Parent window, if available.
	 * @param failedDeletions
	 * 		List of file paths failed to be deleted.
	 */
	public DeletionFailureDialog(@Nullable Window owner, @Nonnull List<String> failedDeletions) {
		super(owner);
		initComponents(failedDeletions);
	}

	private void initComponents(@Nonnull List<String> failedDeletions) {
		setTitle(bundle.getString("scan.delete.failed-files.title"));

		// Labels
		JLabel lblMessage = new JLabel();
		JLabel lblTryAgain = new JLabel();
		lblMessage.setText(bundle.getString("scan.delete.failed-files"));
		lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblMessage.setFont(new Font(Font.DIALOG, Font.PLAIN, 18));
		lblTryAgain.setText(bundle.getString("scan.delete.failed-try-again"));
		lblTryAgain.setHorizontalAlignment(SwingConstants.RIGHT);

		// Laying it all out
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new FormLayout(
				"default:grow",
				"2*(default, $lgap), default"));
		contentPanel.add(lblMessage, CC.xy(1, 1));
		JList<String> list = new JList<>(failedDeletions.toArray(String[]::new));
		JScrollPane scrollWrapper = new JScrollPane(list);
		scrollWrapper.setBorder(new LineBorder(Color.decode("#1c1c1c"), 1));
		list.setBackground(new Color(0x333333));

		contentPanel.add(scrollWrapper, CC.xy(1, 3));
		contentPanel.add(lblTryAgain, CC.xy(1, 5));

		JPanel buttonBar = new JPanel();
		buttonBar.setBorder(new EmptyBorder(8, 8, 8, 8));
		buttonBar.setLayout(new FormLayout(
				"$glue, $button",
				"pref"));
		JButton okButton = new JButton();
		okButton.addActionListener(e -> setVisible(false));
		okButton.setText("OK");
		buttonBar.add(okButton, CC.xy(2, 1));

		JPanel dialogPane = new JPanel();
		dialogPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		dialogPane.setLayout(new BorderLayout());
		dialogPane.add(contentPanel, BorderLayout.CENTER);
		dialogPane.add(buttonBar, BorderLayout.SOUTH);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
	}

}
