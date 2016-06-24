package filter;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;

/**
 * The <code>ReliabilityGUIHeader</code> is a GUI component that
 * create the header of the reliability table when the
 * <b>Reliability of Agents...</b> menu item is selected.
 * 
 * @author Tran Xuan Hoang
 */
public class ReliabilityGUIHeader extends JPanel {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ReliabilityGUIHeader() {
		setLayout(null);
		setSize(440, 60);

		JLabel name = new JLabel("Name of Agent");
		name.setBounds(12, 3, 100, 54);
		name.setToolTipText("Name of the agent");
		name.setHorizontalAlignment(SwingConstants.LEFT);
		name.setForeground(Color.BLUE);
		name.setFont(new Font("Calibri", Font.PLAIN, 14));
		add(name);

		JLabel session = new JLabel("Session");
		session.setBounds(113, 3, 60, 54);
		session.setToolTipText("Number of sending sessions");
		session.setHorizontalAlignment(SwingConstants.LEFT);
		session.setFont(new Font("Calibri", Font.PLAIN, 14));
		add(session);

		JLabel reliability = new JLabel("Reliability (%)");
		reliability.setHorizontalAlignment(SwingConstants.CENTER);
		reliability.setFont(new Font("Calibri", Font.PLAIN, 14));
		reliability.setBounds(174, 3, 160, 54);
		add(reliability);

		JLabel numOfSelectedArticles = new JLabel("<html>Num of<br>Selected<br>Articles</html>");
		numOfSelectedArticles.setBounds(370, 3, 70, 54);
		numOfSelectedArticles.setToolTipText("Cumulative number of selected articles");
		numOfSelectedArticles.setHorizontalAlignment(SwingConstants.LEFT);
		numOfSelectedArticles.setFont(new Font("Calibri", Font.PLAIN, 14));
		add(numOfSelectedArticles);
	}
} // end class ReliabilityGUIHeader