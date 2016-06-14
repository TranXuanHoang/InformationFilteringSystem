package infofilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Customizer;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

/**
 * The <code>FilterAgentCustomizer</code> class implements the
 * customizer dialog box allowing user to add, change or remove
 * keywords. The customizer dialog box also allows user to signal
 * filter agent start training neural networks and create the
 * file of profile of training data.
 * 
 * @author Tran Xuan Hoang
 */
public class FilterAgentCustomizer extends JDialog implements Customizer {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	JTextField keywordTextField;
	JList<String> keywordList;

	FilterAgent agent;
	Vector<String> originalKeywords;
	Vector<String> keywords;

	/**
	 * Creates a <code>FilterAgentCustomizer</code> object.
	 */
	public FilterAgentCustomizer() {
		this(null, "Keywords Customizer", true);
	}

	/**
	 * Creates a <code>FilterAgentCustomizer</code> object with the
	 * given frame, title and modality.
	 * @param frame the frame for displaying this customizer.
	 * @param title title of the <code>frame</code>.
	 * @param modal boolean flag that indicates the modality.
	 */
	public FilterAgentCustomizer(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		try {
			createGUI();
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the GUI controls for the customizer dialog box.
	 * @throws Exception if any error occurs during initialization.
	 */
	private void createGUI() throws Exception {
		setBounds(100, 100, 450, 470);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(centerPanel, BorderLayout.CENTER);

		JLabel keywordLabel = new JLabel("Keyword");
		keywordLabel.setForeground(Color.BLUE);
		keywordLabel.setFont(new Font("Calibri", Font.PLAIN, 16));

		keywordTextField = new JTextField();
		keywordTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
		keywordTextField.setColumns(10);

		JLabel listOfKeywordsLabel = new JLabel("List of Keywords for Scoring Articles");
		listOfKeywordsLabel.setForeground(Color.BLUE);
		listOfKeywordsLabel.setFont(new Font("Calibri", Font.PLAIN, 16));

		JScrollPane scrollPane = new JScrollPane();

		JPanel panel = new JPanel();
		GroupLayout gl_centerPanel = new GroupLayout(centerPanel);
		gl_centerPanel.setHorizontalGroup(
				gl_centerPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_centerPanel.createSequentialGroup()
						.addGap(21)
						.addGroup(gl_centerPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_centerPanel.createParallelGroup(Alignment.LEADING, false)
										.addComponent(listOfKeywordsLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(keywordLabel)
										.addComponent(keywordTextField))
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE))
						.addGap(29)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(23, Short.MAX_VALUE))
				);
		gl_centerPanel.setVerticalGroup(
				gl_centerPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_centerPanel.createSequentialGroup()
						.addGap(26)
						.addComponent(keywordLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_centerPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_centerPanel.createSequentialGroup()
										.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addContainerGap())
								.addGroup(gl_centerPanel.createSequentialGroup()
										.addComponent(keywordTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(listOfKeywordsLabel)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))))
				);
		panel.setLayout(new GridLayout(4, 1, 0, 40));

		JButton addButton = new JButton("Add");
		addButton.setToolTipText("Add keyword");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addButtonActionPerformed(e);
			}
		});
		addButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		panel.add(addButton);

		JButton changeButton = new JButton("Change");
		changeButton.setToolTipText("Change keyword");
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeButtonActionPerformed(e);
			}
		});
		changeButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		panel.add(changeButton);

		JButton removeButton = new JButton("Remove");
		removeButton.setToolTipText("Remove keyword");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeButtonActionPerformed(e);
			}
		});
		removeButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		panel.add(removeButton);

		JButton removeAllButton = new JButton("Remove All");
		removeAllButton.setToolTipText("Remove all keywords");
		removeAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAllButtonActionPerformed(e);
			}
		});
		removeAllButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		panel.add(removeAllButton);

		keywordList = new JList<>();
		keywordList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				keywordListMouseClicked(e);
			}
		});
		keywordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		keywordList.setFont(new Font("Calibri", Font.PLAIN, 14));
		scrollPane.setViewportView(keywordList);
		centerPanel.setLayout(gl_centerPanel);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(1, 3, 50, 10));
		southPanel.setBorder(new EmptyBorder(20, 50, 30, 50));
		getContentPane().add(southPanel, BorderLayout.SOUTH);

		JButton saveKeywordsButton = new JButton("Save Keywords");
		saveKeywordsButton.setToolTipText("Create a new list of keywords as above");
		saveKeywordsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveKeywordsButtonActionPerformed(e);
			}
		});
		saveKeywordsButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		southPanel.add(saveKeywordsButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		cancelButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		southPanel.add(cancelButton);
	}

	/**
	 * Sets the object to be customized.
	 * @param obj the object to be customized.
	 */
	@Override
	public void setObject(Object obj) {
		agent = (FilterAgent) obj;
		getDataFromBean();
	}

	/**
	 * Gets data from the bean and sets the GUI controls.
	 */
	public void getDataFromBean() {
		setKeywords(agent.getKeywords());
	}

	/**
	 * Takes the list of keywords given by user from the customize
	 * dialog box, then sets those keywords for the filter agent
	 * and lets the agent creates the data definition <code>.dfn
	 * </code> file.
	 */
	public void setDataOnBean() {
		agent.setKeywords(getKeywords());
		agent.writeProfileDataDefinition();
		agent.deleteKeywordCountsFile();
	}

	/**
	 * Sets the keywords given by user on the <code>JList</code>
	 * GUI component and make a copy of these keywords for later
	 * processing.
	 * @param keys an array of keywords that user provided for the
	 * filter agent.
	 */
	private void setKeywords(String[] keys) {
		keywords = new Vector<>();

		for (int i = 0; i < keys.length; i++) {
			keywords.addElement(keys[i]);
		}

		// make a copy of original data
		originalKeywords = new Vector<>(keywords);

		keywordList.setListData(keywords);
	}

	/**
	 * Retrieves the keywords given by user on the <code>JList</code>
	 * GUI component.
	 * @return an array of keywords that user provided.
	 */
	private String[] getKeywords() {
		String[] keys = new String[keywords.size()];

		for (int i = 0; i < keys.length; i++) {
			keys[i] = keywords.elementAt(i);
		}

		return keys;
	}

	/**
	 * Sets the keyword selected from the list of keywords to the
	 * <i>keyword text field</i>.
	 * @param e the event generated from the mouse click.
	 */
	protected void keywordListMouseClicked(MouseEvent e) {
		int index = keywordList.getSelectedIndex();

		if (index >= 0) {
			keywordTextField.setText(keywords.get(index));
		}
	}

	/**
	 * Adds additional keywords when the <b>Add</b> button is pressed.
	 * User may enter more than one keyword into the <b>keyword text field</b>
	 * by inserting a space in between consecutive keywords.
	 * @param e the event generated when the <b>Add</b> button is pressed.
	 */
	private void addButtonActionPerformed(ActionEvent e) {
		String text = keywordTextField.getText().trim();

		if (text != null && text.length() > 0) {
			String[] newKeywords = text.split(" ");

			for (String newKeyword : newKeywords) {
				addKeyword(newKeyword);
			}

			// update GUI
			keywordList.setListData(keywords);
			keywordTextField.setText("");
		}
	}

	/**
	 * Adds a new non-duplicate keyword to the list of keywords. This
	 * method is called by {@link #addButtonActionPerformed(ActionEvent)}.
	 * @param newKeyword the keyword to be added.
	 */
	private void addKeyword(String newKeyword) {
		// check duplication of added keyword
		for (String key : keywords) {
			if (newKeyword.equalsIgnoreCase(key)) {
				return;
			}
		}

		// add new keyword if no duplication
		keywords.addElement(newKeyword);
	}

	/**
	 * Changes a keyword when the <b>Change</b> button is pressed.
	 * @param e the event generated when the <b>Change</b> button
	 * is pressed.
	 */
	private void changeButtonActionPerformed(ActionEvent e) {
		int index = keywordList.getSelectedIndex();

		if (index >= 0) {
			String value = keywordTextField.getText().trim();

			keywords.setElementAt(value, index);
			keywordList.setListData(keywords);
			keywordTextField.setText("");
		}
	}

	/**
	 * Removes a keyword when the <b>Remove</b> button is pressed.
	 * @param e the event generated when the <b>Remove</b> button
	 * is pressed.
	 */
	private void removeButtonActionPerformed(ActionEvent e) {
		int index = keywordList.getSelectedIndex();

		if (index >= 0) {
			keywords.removeElementAt(index);
			keywordList.setListData(keywords);
			keywordTextField.setText("");
		}
	}

	/**
	 * Removes all the list of keywords when the <b>Remove All</b>
	 * button is pressed.
	 * @param e the event generated when the <b>Remove All</b>
	 * button is pressed.
	 */
	private void removeAllButtonActionPerformed(ActionEvent e) {
		keywords.removeAllElements();
		keywordList.setListData(keywords);
		keywordTextField.setText("");
	}

	/**
	 * Creates a profile when the <b>Save Keywords</b> button
	 * is pressed.
	 * @param e the event generated when the <b>Save Keywords</b>
	 * button is pressed.
	 */
	private void saveKeywordsButtonActionPerformed(ActionEvent e) {
		// confirm user's option to avoid data loss
		int option = JOptionPane.showConfirmDialog(this,
				"This will erase the existing list of keywords and\n" +
						"any network training will be lost.\n" +
						"Are you sure you want to do this?",
						"Create Filter Profire",
						JOptionPane.YES_NO_OPTION);

		// change keywords, clear neural networks,
		// create .dfn file, delete .dat file
		if (option == JOptionPane.YES_OPTION) {
			setDataOnBean();
			dispose();
		}
	}
} // end class FilterAgentCustomizer