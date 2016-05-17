package infofilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ciagent.CIAgentEvent;
import ciagent.CIAgentEventListener;

/**
 * The <code>URLReaderAgentCustomizer</code> class implements the
 * customizer dialog that allows user to enter the URL of the web
 * page from which data will be downloaded.
 * 
 * @author Tran Xuan Hoang
 */
public class URLReaderAgentCustomizer extends JDialog implements Customizer, CIAgentEventListener {
	/** Serial version. */
	private static final long serialVersionUID = 1L;
	
	URLReaderAgent agent; // the agent bean we are customizing
	
	/**
	 * Creates a <code>URLReaderAgentCustomizer</code> object.
	 */
	public URLReaderAgentCustomizer() {
		this(null, "URLReaderAgent Customizer", false);
	}

	/**
	 * Creates a <code>URLReaderAgentCustomizer</code> object with
	 * the given frame, title and modality.
	 * @param frame the frame for displaying this customizer.
	 * @param title title of the <code>frame</code>.
	 * @param modal boolean flag that indicates the modality.
	 */
	public URLReaderAgentCustomizer(Frame frame, String title, boolean modal) {
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
		JLabel nameLabel = new JLabel("Name");
		nameLabel.setBounds(new Rectangle(22, 20, 41, 17));
		JLabel urlLabel = new JLabel("URL:");
		urlLabel.setBounds(new Rectangle(23, 66, 106, 17));
		JLabel paramsLabel = new JLabel("Parameter string:");
		paramsLabel.setBounds(new Rectangle(19, 152, 143, 17));

		JTextField nameTextField = new JTextField();
		nameTextField.setBounds(new Rectangle(110, 18, 139, 21));

		JComboBox<String> urlComboBox = new JComboBox<>();
		urlComboBox.setEditable(true);
		urlComboBox.setBounds(new Rectangle(19, 93, 368, 25));
		urlComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// currently do nothing
			}
		});
		urlComboBox.addItem("http://aima.cs.berkeley.edu/") ;
		urlComboBox.addItem("https://en.wikipedia.org/wiki/Artificial_neural_network") ;
		urlComboBox.addItem("http://www.fipa.org") ;
		urlComboBox.addItem("http://legacy.australianetwork.com/studyenglish/se_series1.htm");

		JComboBox<String> paramsComboBox = new JComboBox<>();
		paramsComboBox.setEditable(true);
		paramsComboBox.setBounds(new Rectangle(18, 190, 371, 24));
		paramsComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// currently do nothing
			}
		});
		String sampleParms = "?" + "&dmon=" + "JUL" + "&dday=" +
				"1" + "&orig=" + "RST" + "&dest=" + "MCO" +
				"&rmon=" + "JUL" + "&rday=" + "8";
		paramsComboBox.addItem(sampleParms);

		JButton queryButton = new JButton("Get URL");
		queryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				queryButtonActionPerformed(e);
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel jPanel1 = new JPanel(null);
		jPanel1.add(nameLabel);
		jPanel1.add(nameTextField);
		jPanel1.add(urlLabel);
		jPanel1.add(urlComboBox);
		jPanel1.add(paramsLabel);
		jPanel1.add(paramsComboBox);

		JPanel jPanel2 = new JPanel();
		jPanel2.add(queryButton);
		jPanel2.add(cancelButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setMinimumSize(new Dimension(400, 300));
		panel.setPreferredSize(new Dimension(400, 300));
		panel.add(jPanel1, BorderLayout.CENTER);
		panel.add(jPanel2, BorderLayout.SOUTH);

		getContentPane().add(panel);
	}

	@Override
	public void processCIAgentEvent(CIAgentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postCIAgentEvent(CIAgentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setObject(Object arg0) {
		// TODO Auto-generated method stub

	}

	public void getDataFromBean() {
		// TODO
	}

	public void setDataOnBean() {
		// TODO
	}

	private void queryButtonActionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
} // end class URLReaderAgentCustomizer