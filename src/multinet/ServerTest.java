package multinet;

import javax.swing.JFrame;

public class ServerTest
{
	public static void main( String[] args )
	{
		JFrame application = new JFrame("Server Side");
		Server server = new Server();

		application.add(server);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.setSize(400, 350);
		application.setVisible(true);

		server.runServer(12345, 100); // run server
	} // end main
} // end class ServerTest