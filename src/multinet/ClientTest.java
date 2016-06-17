package multinet;

import javax.swing.JFrame;

public class ClientTest 
{
	public static void main( String[] args )
	{
		JFrame application = new JFrame("Client");
		Client client = new Client();

		application.add(client);
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.setSize(400, 350);
		application.setVisible(true);

		client.runClient("127.0.0.1", 12345); // run client application
	} // end main
} // end class ClientTest