package info.mmpa.concoction.panel;

import java.awt.*;
import java.util.*;
import javax.annotation.Nonnull;
import javax.swing.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import info.mmpa.concoction.ConcoctionUxContext;
import info.mmpa.concoction.ConcoctionWindow;

/**
 * Panel for executing scans.
 */
public class ScanPanel extends JPanel {
	public ScanPanel(@Nonnull ConcoctionUxContext context) {
		initComponents();
	}

	private void initComponents() {
		// TODO: A progress reporting API in concoction would be very useful here

		JLabel tempLabel = new JLabel();
		setLayout(new FormLayout(
			"default, $lcgap, default:grow",
			"default, $lgap, default:grow, $lgap, default"));
		tempLabel.setText("Scanning UI Next :)");
		tempLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tempLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
		add(tempLabel, CC.xywh(1, 1, 3, 5));
	}
}
