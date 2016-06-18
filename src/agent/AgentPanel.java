package agent;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

/**
 * The <code>CIAgentPanel</code> class is a part of the GUI for
 * applications using <code>CIAgent</code> class.
 * 
 * @author Tran Xuan Hoang
 */
public class AgentPanel extends JPanel {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	JLabel nameLabel = new JLabel();
	JLabel stateLabel = new JLabel();
	JLabel sleepTimeLabel = new JLabel();
	JLabel asyncTimeLabel = new JLabel();
	JLabel currentStateLabel = new JLabel();
	JTextField nameTextField = new JTextField();
	JTextField sleepTimeTextField = new JTextField();
	JTextField asyncTimeTextField = new JTextField();
	JToggleButton startStopToggleButton = new JToggleButton();
	JToggleButton suspendResumeToggleButton = new JToggleButton();

	/**
	 * Create a panel for simulating an agent.
	 */
	public AgentPanel() {
		try {
			createPanel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes a panel of graphical user interface for an agent.
	 * @throws Exception if an error occurs while initializing.
	 */
	private void createPanel() throws Exception {
		this.setLayout(null);
		nameLabel.setText("Name");
		nameLabel.setBounds(new Rectangle(27, 18, 106, 17));
		stateLabel.setText("State");
		stateLabel.setBounds(new Rectangle(26, 111, 41, 17));
		sleepTimeLabel.setText("Sleep time");
		sleepTimeLabel.setBounds(new Rectangle(25, 47, 101, 17));
		asyncTimeLabel.setText("Asynch time");
		asyncTimeLabel.setBounds(new Rectangle(26, 83, 80, 17));
		currentStateLabel.setText("Uninitiated");
		currentStateLabel.setBounds(new Rectangle(184, 115, 140, 17));

		nameTextField.setBounds(new Rectangle(183, 17, 146, 21));
		sleepTimeTextField.setBounds(new Rectangle(184, 50, 146, 21));
		asyncTimeTextField.setBounds(new Rectangle(185, 81, 145, 21));

		startStopToggleButton.setText("Start");
		startStopToggleButton.setBounds(new Rectangle(25, 150, 115, 25));
		startStopToggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// currently, this listener is leaved as do nothing
			}
		});

		suspendResumeToggleButton.setText("Suspend");
		suspendResumeToggleButton.setBounds(new Rectangle(186, 149, 115, 25));
		suspendResumeToggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// currently, this listener is leaved as do nothing
			}
		});

		this.add(nameLabel);
		this.add(stateLabel);
		this.add(sleepTimeLabel);
		this.add(asyncTimeLabel);
		this.add(currentStateLabel);
		this.add(nameTextField);
		this.add(sleepTimeTextField);
		this.add(asyncTimeTextField);
		this.add(startStopToggleButton);
		this.add(suspendResumeToggleButton);
	}

	/**
	 * Uses input text fields to set properties of a specified agent.
	 * @param agent the agent in which properties are set.
	 */
	public void setDataOnBean(Agent agent) {
		agent.setName(nameTextField.getText().trim());
		agent.setSleepTime(Integer.parseInt(
				sleepTimeTextField.getText().trim()));
		agent.setAsyncTime(Integer.parseInt(
				asyncTimeTextField.getText().trim()));
	}

	/**
	 * Sets text fields based on data in a specified agent.
	 * @param agent the agent from which data is retrieved.
	 */
	public void getDataFromBean(Agent agent) {
		nameTextField.setText(agent.getName());
		sleepTimeTextField.setText(String.valueOf(agent.getSleepTime()));
		asyncTimeTextField.setText(String.valueOf(agent.getAsyncTime()));
		currentStateLabel.setText(agent.getState().toString());
	}
} // end class CIAgentPanel