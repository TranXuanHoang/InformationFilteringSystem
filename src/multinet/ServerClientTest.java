package multinet;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class ServerClientTest {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			System.err.println("Error: unable to set look and feel");
		}
		
		ServerClient panel = new ServerClient();
		JFrame app = new JFrame();
		app.add(panel);
		app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		app.setSize(700, 500);
		app.setVisible(true);
	}

}
