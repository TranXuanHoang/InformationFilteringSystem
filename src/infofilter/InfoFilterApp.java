package infofilter;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * The <code>InfoFilterApp</code> class implements an <b>information
 * filtering application</b> representing an agent that is able to
 * download articles from the Internet or load articles saved in
 * personal computer then score how much interesting these articles
 * are. Each filter agent created by this class will be connected
 * together to form a network of agents.
 * 
 * @author Tran Xuan Hoang
 */
public class InfoFilterApp {
	/**
	 * Indicates whether frame should be packed.
	 */
	boolean packFrame = false;

	/**
	 * Construct the information filtering application.
	 */
	public InfoFilterApp() {
		InfoFilterFrame frame = new InfoFilterFrame();

		// prevent closing when user clicks close button of the frame
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setExtendedState(JFrame.ICONIFIED);
			}
		});

		// set icon for the application
		ImageIcon icon = new ImageIcon(
				getClass().getResource("icons/AppIcon.png"));
		frame.setIconImage(icon.getImage());

		// pack frames that have useful preferred size information
		// from their layout or validate frames that have preset
		// sizes.
		if (packFrame) {
			frame.pack();
		} else {
			frame.validate();
		}

		// center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();

		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}

		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}

		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Error: unable to set look and feel");
		}

		// create and show the information filtering application
		new InfoFilterApp();
	}
} // end class InfoFilterApp