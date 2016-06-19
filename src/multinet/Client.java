package multinet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Client portion of a stream-socket connection between client
 * and server.
 * 
 * @author Tran Xuan Hoang
 */
public class Client extends JPanel implements Serializable {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** Enters information from user. */
	transient private JTextField enterField;

	/** Displays information to user. */
	private JTextArea displayArea;

	/** Output stream for sending formation to server. */
	private ObjectOutputStream output;

	/** Input stream for receiving formation from server. */
	private ObjectInputStream input;

	/** Contains message from server. */
	private String message = "";

	/** IP address of the host server for this socket. */
	private String serverIP;

	/** Port number of the host server to which this client connects to. */
	private int serverPort;

	/** Socket to communicate with server. */
	private Socket socket;

	/**
	 * Initializes serverIP and sets up GUI.
	 * @param host the server's IP to which this client connects.
	 */
	public Client() {
		setLayout(new BorderLayout());

		displayArea = new JTextArea();
		displayArea.setFont(new Font("Calibri", Font.PLAIN, 14));
		displayArea.setEditable(false);
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		enterField = new JTextField();
		enterField.setFont(new Font("Calibri", Font.PLAIN, 14));
		enterField.setEditable(false);
		enterField.addActionListener(new ActionListener() {
			// send message to server
			public void actionPerformed(ActionEvent event) {
				sendData(event.getActionCommand());

				// display message on the GUI of server
				String message = enterField.getText();
				displayMessage("\nCLIENT>>> " + message);
				enterField.setText("");
			}
		}); // end call to addActionListener
		add(enterField, BorderLayout.SOUTH);
	} // end Client constructor

	/**
	 * Allows client to connect to server specified by <code>IP</code>
	 * and <code>port</code>.
	 * @param serverIP the IP address of server to which client will connect.
	 * @param serverPort the port of server to which client will connect.
	 * @throws UnknownHostException if the host server cannot be connected.
	 * @throws IOException if any I/O occurs when creating the socket.
	 */
	public void connectToServer(String serverIP, int serverPort)
			throws IOException {
		try {
			this.serverIP = serverIP;
			this.serverPort = serverPort;

			// create Socket to make connection to server
			socket = new Socket(InetAddress.getByName(serverIP), serverPort);

			// display connection information
			displayMessage("\nConnected to: " +
					socket.getInetAddress().getHostName());
		} catch (IOException e) {
			displayMessage("\nServer to which you connect is not running");
			throw new IOException("Cannot connect to server <" + serverIP +
					", " + serverPort + ">");
		}
	}

	/**
	 * Processes messages with server.
	 * @throws IOException if any I/O error occurs when exchanging
	 * information with server.
	 */
	public void runClient() throws IOException {
		try {
			getStreams();
			processConnection();
		} catch (IOException e) {
			throw new IOException();
		} finally {
			closeConnection();
		}
	}

	/**
	 * Connects to server and processes messages from server.
	 * @param serverIP the IP address of server to which client will connect.
	 * @param serverPort the port of server to which client will connect.
	 */
	public void runClient(String serverIP, int serverPort) {
		try	{
			this.serverIP = serverIP;
			this.serverPort = serverPort;

			connectToServer(); // create a Socket to make connection
			getStreams(); // get the input and output streams
			processConnection(); // process connection
		} catch (EOFException eofException) {
			displayMessage("\nClient terminated connection");
		} catch(SocketException socketException) {
			displayMessage("\nServer to which you connect is not running");
			return;
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeConnection(); // close connection
		}
	} // end method runClient

	/**
	 * Connects to server and processes messages from server.
	 * @param serverIP the IP address of server to which client will connect.
	 * @param serverPort the port of server to which client will connect.
	 * @param gui the GUI of <code>ServerClient</code> class that uses
	 * this class as the core client.
	 */
	public void runClient(String serverIP, int serverPort, ServerClient gui) {
		try	{
			this.serverIP = serverIP;
			this.serverPort = serverPort;

			connectToServer(); // create a Socket to make connection

			// set up GUI controls
			gui.setConnectedClientGUIControls();

			getStreams(); // get the input and output streams
			processConnection(); // process connection
		} catch (EOFException e) {
			closeConnection(); // close connection
			gui.setDisconnectingClientGUIControls();
			displayMessage("\nClient terminated connection because server stopped running");
		} catch (ConnectException e) {
			displayMessage("\nServer to which you want to connect is not running");
		} catch(SocketException e) {
			closeConnection();
			gui.setDisconnectingClientGUIControls();
			displayMessage("\nClient disconnected from server");
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // end method runClient

	/** Connects to server. */
	private void connectToServer() throws IOException {
		displayMessage("\nAttempting connection");

		// create Socket to make connection to server
		socket = new Socket(InetAddress.getByName(serverIP), serverPort);

		// display connection information
		displayMessage("\nConnected to: " +
				socket.getInetAddress().getHostName());
	} // end method connectToServer

	/** Gets streams to send and receive data. */
	private void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream(socket.getOutputStream());      
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream(socket.getInputStream());

		displayMessage("\nGot I/O streams\n");
	} // end method getStreams

	/**
	 * Processes connection with server. Basically, this method
	 * receives information from server.
	 */
	private void processConnection() throws IOException {
		// enable enterField so client user can send messages
		setTextFieldEditable(true);

		// process messages sent from server
		do { 
			try {
				// read message and display it
				message = (String) input.readObject();
				displayMessage("\n" + serverIP + ">>> " + message);
			} catch (ClassNotFoundException classNotFoundException) {
				displayMessage("\nUnknown object type received");
			}
		} while (!message.equals("TERMINATE"));
	} // end method processConnection

	/** Closes streams and socket. */
	protected void closeConnection() {
		setTextFieldEditable(false); // disable enterField

		try {
			if (output != null) {
				output.close(); // close output stream
			}

			if (input != null) {
				input.close(); // close input stream
			}

			if (socket != null) {
				socket.close(); // close socket
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	} // end method closeConnection

	/** Checks whether the client is closed. */
	public boolean isClosed() {
		if (socket == null) {
			return true;
		}

		return socket.isClosed();
	}

	/** Sends message to server. */
	public void sendData(Object message) {
		try {
			// all reset before writing the same object to ensure
			// its updated state is serialized
			output.reset();
			output.writeObject(message);
			output.flush(); // flush data to output
		} catch (IOException ioException) {
			displayArea.append("\nError writing object");
		}
	} // end method sendData

	/** Manipulates displayArea in the event-dispatch thread. */
	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayArea.append(messageToDisplay);
				displayArea.setCaretPosition(displayArea.getText().length());
			}
		});
	} // end method displayMessage

	/** Manipulates enterField in the event-dispatch thread. */
	private void setTextFieldEditable(final boolean editable) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				enterField.setEditable(editable);
			}
		});
	} // end method setTextFieldEditable
} // end class Client