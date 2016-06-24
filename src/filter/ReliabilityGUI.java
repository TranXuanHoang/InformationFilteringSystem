package filter;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;

/**
 * The <code>ReliabilityGUI</code> class graphically represents
 * information of each object of class <code>Reliability</code>.
 * This class and <code>ReliabilityGUIHeader</code> combine together
 * to create a table showing reliability of all client agents that
 * are connecting to the server agent.
 *  
 * @author Tran Xuan Hoang
 */
public class ReliabilityGUI extends JPanel {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	JLabel agentName;
	JLabel numOfSessions;
	JProgressBar reliability;
	JLabel numOfSelectedArticles;

	/**
	 * Create the panel.
	 * @param name the name of the agent whose reliability information
	 * is graphically displayed.
	 */
	public ReliabilityGUI(String name) {
		setBackground(Color.WHITE);
		setLayout(null);

		agentName = new JLabel(name);
		agentName.setToolTipText("Name of the agent");
		agentName.setHorizontalAlignment(SwingConstants.LEFT);
		agentName.setForeground(Color.BLUE);
		agentName.setFont(new Font("Calibri", Font.PLAIN, 14));
		agentName.setBounds(12, 5, 100, 30);
		add(agentName);

		numOfSessions = new JLabel("0");
		numOfSessions.setHorizontalAlignment(SwingConstants.LEFT);
		numOfSessions.setToolTipText("Number of sending sessions");
		numOfSessions.setFont(new Font("Calibri", Font.PLAIN, 14));
		numOfSessions.setBounds(113, 5, 60, 30);
		add(numOfSessions);

		reliability = new JProgressBar();
		reliability.setStringPainted(true);
		reliability.setToolTipText("Reliability (%)");
		reliability.setFont(new Font("Calibri", Font.PLAIN, 14));
		reliability.setBounds(174, 10, 160, 20);
		add(reliability);

		numOfSelectedArticles = new JLabel("0");
		numOfSelectedArticles.setToolTipText("Cumulative number of selected articles");
		numOfSelectedArticles.setHorizontalAlignment(SwingConstants.LEFT);
		numOfSelectedArticles.setFont(new Font("Calibri", Font.PLAIN, 14));
		numOfSelectedArticles.setBounds(370, 5, 60, 30);
		add(numOfSelectedArticles);
	}

	/**
	 * Updates the interface with the new value of reliability.
	 * @param r the underlying <code>Reliability</code> object
	 * holding reliability information for which this <code>
	 * ReliabilityGUI</code> graphically represents.
	 */
	public void updateReliabilityGUI(Reliability r) {
		agentName.setText(r.getAgentName());
		numOfSessions.setText("" + r.getSession());
		reliability.setValue((int) (r.getReliability() * 100));
		numOfSelectedArticles.setText("" + r.getNumOfSelectedArticles());
	}
} // end class Reliability