package infofilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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

	private final JPanel contentPanel = new JPanel();
	private JTextField urlTextField;

	URLReaderAgent agent; // the agent bean we are customizing

	/**
	 * Creates a <code>URLReaderAgentCustomizer</code> object.
	 */
	public URLReaderAgentCustomizer() {
		this(null, "Download Article", true);
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
		setBounds(100, 100, 460, 200);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		JLabel downloadArticleLabel = new JLabel("Download Article from the Internet");
		downloadArticleLabel.setFont(new Font("MS UI Gothic", Font.BOLD, 22));
		downloadArticleLabel.setForeground(new Color(0, 0, 205));
		downloadArticleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		downloadArticleLabel.setBorder(new EmptyBorder(10, 10, 20, 10));
		contentPanel.add(downloadArticleLabel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel urlLabel = new JLabel("URL");
		urlLabel.setHorizontalAlignment(SwingConstants.CENTER);
		urlLabel.setFont(new Font("MS UI Gothic", Font.BOLD, 14));
		centerPanel.add(urlLabel);

		urlTextField = new JTextField();
		urlTextField.setToolTipText("Enter the URL of the article here");
		urlTextField.setHorizontalAlignment(SwingConstants.LEFT);
		urlTextField.setFont(new Font("Calibri", Font.PLAIN, 16));
		centerPanel.add(urlTextField);
		urlTextField.setColumns(25);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 0, 15, 0));
		contentPanel.add(panel, BorderLayout.SOUTH);

		JButton downloadButton = new JButton("Download");
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// first get user data from the customizer dialog box and
				// set values for properties of agent
				try {
					agent.setURL(new URL(urlTextField.getText()));
				} catch (MalformedURLException ex) {
					System.out.println(
							"Error: The URL is not correctly specified.");
				}

				CIAgentEvent event = new CIAgentEvent(this, "getURLText", null);
				agent.postCIAgentEvent(event); // ask agent to get url

				dispose();
			}
		});
		downloadButton.setFont(new Font("Calibri", Font.PLAIN, 16));
		panel.add(downloadButton);
		getRootPane().setDefaultButton(downloadButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		cancelButton.setFont(new Font("Calibri", Font.PLAIN, 16));
		panel.add(cancelButton);
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
		agent.addCIAgentEventListener(this);
	}
} // end class URLReaderAgentCustomizer