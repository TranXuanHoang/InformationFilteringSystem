package infofilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.net.MalformedURLException;
import java.net.URL;

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
public class URLReaderAgentCustomizer extends JDialog
implements Customizer, CIAgentEventListener {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	JTextField nameTextField;
	JComboBox<String> urlComboBox;
	JComboBox<String> paramsComboBox;

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

		nameTextField = new JTextField();
		nameTextField.setBounds(new Rectangle(110, 18, 139, 21));

		urlComboBox = new JComboBox<>();
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

		paramsComboBox = new JComboBox<>();
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

		JButton queryButton = new JButton("Download");
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

	/**
	 * Processes event generated between <code>URLReaderAgent</code>s.
	 * @param event the event to be processed.
	 */
	@Override
	public void processCIAgentEvent(CIAgentEvent event) {
		Object source = event.getSource();
		Object arg = event.getArgObject();
		Object action = event.getAction();

		if (action != null) {
			if (action.equals("trace")) {
				System.out.println(
						"Processing event from " + source +
						", with the argument: " + arg +
						", and the action: " + action);
			} else if (action.equals("addURLText")) {
				System.out.println("URL text read by agent");
			}
		}
	}

	/**
	 * Immediately processes event generated between <code>
	 * URLReaderAgent</code>s but does not add it to the
	 * event queue.
	 */
	@Override
	public void postCIAgentEvent(CIAgentEvent event) {
		processCIAgentEvent(event);
	}

	/**
	 * Sets the object to be customized.
	 * @param obj the object to be customized.
	 */
	@Override
	public void setObject(Object obj) {
		agent = (URLReaderAgent) obj;
		getDataFromBean();
		agent.addCIAgentEventListener(this);

	}

	/**
	 * Gets data from <code>URLReaderAgent</code> bean and sets
	 * <code>URLReaderAgentCustomizer</code> GUI.
	 */
	public void getDataFromBean() {
		nameTextField.setText(agent.getName());
		URL url = agent.getURL();

		if (url == null) {
			urlComboBox.setSelectedIndex(0); // select default
		} else {
			urlComboBox.setSelectedItem(url);
		}

		paramsComboBox.setSelectedItem(agent.getParamString());
	}

	/**
	 * Takes data from <code>URLReaderAgentCustomizer</code> GUI
	 * and sets properties on <code>URLReaderAgent</code> bean.
	 */
	public void setDataOnBean() {
		String name = nameTextField.getText().trim();

		System.out.println("Test " + agent + ", " + name);
		agent.setName(name);

		try {
			String url = (String) urlComboBox.getSelectedItem();
			agent.setURL(new URL(url));
		} catch (MalformedURLException e) {
			System.out.println(
					"Error: The URL is not correctly specified.");
		}

		String paramString = (String) paramsComboBox.getSelectedItem();
		agent.setParamString(paramString);
	}

	/**
	 * Handles the event when the <b>Download</b> button is clicked.
	 * @param e the event generated when the <b>Download</b>
	 * button is pressed.
	 */
	private void queryButtonActionPerformed(ActionEvent e) {
		// first get user data from the customizer dialog box and
		// set values for properties of agent
		setDataOnBean();

		CIAgentEvent event = new CIAgentEvent(this, "getURLText", null);
		agent.postCIAgentEvent(event); // ask agent to get url

		dispose();
	}
} // end class URLReaderAgentCustomizer