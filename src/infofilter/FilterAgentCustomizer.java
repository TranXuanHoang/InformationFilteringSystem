package infofilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Customizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
		this(null, "FilterAgent Customizer", false);
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
		JLabel jLabel1 = new JLabel("Keyword");
		jLabel1.setBounds(new Rectangle(38, 34, 120, 17));
		keywordTextField = new JTextField();
		keywordTextField.setBounds(new Rectangle(37, 57, 208, 21));

		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(new Rectangle(38, 91, 207, 228));
		keywordList = new JList<>();
		keywordList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				keywordListMouseClicked(e);
			}
		});

		JButton addButton = new JButton("Add");
		addButton.setBounds(new Rectangle(277, 91, 88, 27));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addButtonActionPerformed(e);
			}
		});

		JButton changeButton = new JButton("Change");
		changeButton.setBounds(new Rectangle(277, 147, 84, 27));
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeButtonActionPerformed(e);
			}
		});

		JButton removeButton = new JButton("Remove");
		removeButton.setBounds(new Rectangle(277, 211, 87, 27));
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeButtonActionPerformed(e);
			}
		});

		JButton createProfileButton = new JButton("Create Profile");
		createProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createProfileButtonActionPerformed(e);
			}
		});

		JButton trainNNButton = new JButton("Train NNs");
		trainNNButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				trainNNButtonActionPerformed(e);
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(null);

		jPanel1.add(jLabel1);
		jPanel1.add(keywordTextField);
		jScrollPane1.getViewport().add(keywordList);
		jPanel1.add(jScrollPane1);
		jPanel1.add(addButton);
		jPanel1.add(changeButton);
		jPanel1.add(removeButton);

		JPanel jPanel2 = new JPanel();
		FlowLayout flowLayout1 = new FlowLayout();
		flowLayout1.setHgap(15);
		jPanel2.setLayout(flowLayout1);
		jPanel2.setAlignmentX((float) 0.2);
		jPanel2.setPreferredSize(new Dimension(573, 37));

		jPanel2.add(createProfileButton);
		jPanel2.add(trainNNButton);
		jPanel2.add(cancelButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(400, 400));
		panel.add(jPanel1, BorderLayout.CENTER);
		panel.add(jPanel2, BorderLayout.SOUTH);
		getContentPane().add(panel);
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
		
		if (index > 0) {
			keywordTextField.setText(keywords.get(index));
		}
	}

	private void addButtonActionPerformed(ActionEvent e) {
		String keyword = keywordTextField.getText().trim();
		keywords.addElement(keyword);
		keywordList.setListData(keywords);
	}

	private void changeButtonActionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	private void removeButtonActionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	private void createProfileButtonActionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	private void trainNNButtonActionPerformed(ActionEvent e) {
		//TODO
	}
} // end class FilterAgentCustomizer