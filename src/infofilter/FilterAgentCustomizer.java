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
	
	Vector<String> keywords;
	Vector<String> originalKeywords;
	FilterAgent agent;
	
	/**
	 * Creates a <code>FilterAgentCustomizer</code> object.
	 */
	public FilterAgentCustomizer() {
		this(null, "FilterAgent Customizer", false);
	}
	
	/**
	 * Creates a <code>FilterAgentCustomizer</code> object with
	 * the given frame, title and modality.
	 * @param frame the frame for displaying this customizer.
	 * @param title title of the <code>frame</code>.
	 * @param modal boolean flag that indicates the modality.
	 */
	public FilterAgentCustomizer(
			Frame frame, String title, boolean modal) {
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
		JTextField keywordTextField = new JTextField();
		keywordTextField.setBounds(new Rectangle(37, 57, 208, 21));
		
		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setBounds(new Rectangle(38, 91, 207, 228));
		JList<String> keywordList = new JList<>();
		keywordList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//TODO
			}
		});
		
		JButton addButton = new JButton("Add");
		addButton.setBounds(new Rectangle(277, 91, 88, 27));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JButton changeButton = new JButton("Change");
		changeButton.setBounds(new Rectangle(277, 147, 84, 27));
		changeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JButton removeButton = new JButton("Remove");
		removeButton.setBounds(new Rectangle(277, 211, 87, 27));
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JButton createProfileButton = new JButton("Create Profile");
		createProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JButton trainNNButton = new JButton("Train NNs");
		trainNNButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.setPreferredSize(new Dimension(400, 400));
		
		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(null);
		
		JPanel jPanel2 = new JPanel();
		FlowLayout flowLayout1 = new FlowLayout();
		flowLayout1.setHgap(15);
		jPanel2.setLayout(flowLayout1);
		jPanel2.setAlignmentX((float) 0.2);
	    jPanel2.setPreferredSize(new Dimension(573, 37));
	    
		jPanel1.add(jLabel1);
		jPanel1.add(keywordTextField);
		jScrollPane1.getViewport().add(keywordList);
		jPanel1.add(jScrollPane1);
		jPanel1.add(addButton);
		jPanel1.add(changeButton);
		jPanel1.add(removeButton);

		jPanel2.add(createProfileButton);
		jPanel2.add(trainNNButton);
		jPanel2.add(cancelButton);

		panel1.add(jPanel1, BorderLayout.CENTER);
		panel1.add(jPanel2, BorderLayout.SOUTH);
		getContentPane().add(panel1);
	}

	@Override
	public void setObject(Object arg0) {
		// TODO Auto-generated method stub

	}

} // end class FilterAgentCustomizer