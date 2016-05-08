package learn;

import javax.swing.UIManager;

/**
 * The <code>LearnApp</code> is an application class that contains
 * <code>main</code> method for demonstrating three learning
 * algorithms: back propagation neural network, Kohonen map neural
 * network and decision tree.
 * 
 * @author Tran Xuan Hoang
 */
public class LearnApp {
	/**
	 * Indicates whether frame should be packed.
	 */
	boolean packFrame = false;

	/**
	 * Constructs the learn application.
	 */
	public LearnApp() {
		LearnFrame frame = new LearnFrame();

		// Pack frames that have useful preferred size information
		// from their layout or validate frames that have preset
		// sizes.
		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Error: unable to set look and feel");
		}

		// Create and show the learn application
		new LearnApp();
	} // end main
} // end class LearnApp