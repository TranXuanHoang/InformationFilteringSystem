package infofilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * The <code>AboutDialog</code> class implements the About dialog
 * for the information filtering system application.
 * 
 * @author Tran Xuan Hoang
 */
public class AboutDialog extends JDialog {
	/**
	 * The serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an <code>AboutDialog</code> object with the given
	 * parent frame, title and modal setting.
	 * @param parent the parent frame object from which this dialog
	 * will be shown.
	 * @param title the title of the dialog.
	 * @param modal flag setting.
	 */
	public AboutDialog(JFrame parent, String title, boolean modal) {
		super(parent, title, modal);

		try {
			createGUI();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Creates the GUI.
	 * @throws Exception if any errors occur.
	 */
	void createGUI() throws Exception {
		JPanel contentPanel = new JPanel();
		setBounds(100, 100, 350, 220);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel label1 = new JLabel("Information Filtering System");
		label1.setFont(new Font("Calibri", Font.BOLD, 14));
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setBounds(9, 21, 316, 18);

		JLabel label2 = new JLabel("Japan Advanced Institute of Science and Technology");
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setBounds(17, 49, 300, 18);

		JLabel label3 = new JLabel("Email: hoangtx@jaist.ac.jp");
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		label3.setForeground(Color.BLUE);
		label3.setBounds(87, 72, 160, 18);


		JLabel label4 = new JLabel("(c) Copyright Hoang T. X. 2016");
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		label4.setBounds(67, 93, 200, 18);

		JButton okButton = new JButton("OK");
		okButton.setBounds(132, 133, 70, 22);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); // close the dialog
			}
		});

		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		panel2.add(okButton);
		panel2.add(label1);
		panel2.add(label2);
		panel2.add(label3);
		panel2.add(label4);

		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setMinimumSize(new Dimension(450, 170));
		panel1.setPreferredSize(new Dimension(450, 170));
		panel1.add(panel2, BorderLayout.CENTER);
		getContentPane().add(panel1);
	}
} // end class AboutDialog