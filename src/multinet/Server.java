package multinet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import infofilter.Article;
import infofilter.InfoFilterFrame;
import infofilter.Reliability;

/**
 * Server portion of a client/server stream-socket connection.
 * 
 * @author Tran Xuan Hoang
 */
public class Server extends JPanel implements Serializable {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** Displays information to user. */
	transient private JTextArea displayArea;

	/** Inputs message from user. */
	transient private JTextField enterField;

	/** Server socket. */
	protected ServerSocket serverSocket;

	/** List of client connections. */
	public List<ClientConnection> clients;

	/** To run separate threads for responding clients. */
	transient private ExecutorService runClients;

	/** Holds all articles received from client. */
	public Hashtable<ClientConnection, List<List<Article>>> receivedArticles;

	/** Save reliability of each client agent. */
	public Hashtable<ClientConnection, Reliability> reliabilities;

	/** The GUI of the information filtering application that uses this
	 * server as its core server. */
	private InfoFilterFrame infoFilterFrame;

	/**
	 * Creates GUI and initializes basic foundation data for server.
	 */
	public Server() {
		setLayout(new BorderLayout());

		clients = new ArrayList<>();
		runClients = Executors.newCachedThreadPool();

		displayArea = new JTextArea();
		displayArea.setFont(new Font("Calibri", Font.PLAIN, 14));
		displayArea.setEditable(false);
		add(new JScrollPane(displayArea), BorderLayout.CENTER);

		enterField = new JTextField();
		enterField.setFont(new Font("Calibri", Font.PLAIN, 14));
		enterField.setEditable(false);
		enterField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// send message to all clients
				for (ClientConnection client : clients) {
					client.sendData(event.getActionCommand());
				}

				// display message on the GUI of server
				String message = enterField.getText();
				displayMessage("\nSERVER>>> " + message);
				enterField.setText("");
			}
		}); // end call to addActionListener
		add(enterField, BorderLayout.SOUTH);
	} // end Server constructor

	/**
	 * Sets up and runs server.
	 * @param port the  local port number.
	 * @param maxConnections requested maximum length of the queue
	 * of incoming connections.
	 */
	public void runServer(int port, int maxConnections) {
		try {
			// set up server to receive connections
			serverSocket = new ServerSocket(port, maxConnections);

			while (true) {
				ClientConnection client = waitForConnection(); // wait for a connection
				clients.add(client);

				runClients.execute(client);
			} // end while
		} catch (IOException ioException) {
			if (serverSocket != null && serverSocket.isClosed()) {
				System.out.println("Server closed, not accepting connections");
			} else {
				System.out.println("Error: cannot initialize server socket or accepting connection.");
				ioException.printStackTrace();
			}
		}
	} // end method runServer

	/**
	 * Sets up and runs server.
	 * @param port the  local port number.
	 * @param maxConnections requested maximum length of the queue
	 * of incoming connections.
	 * @param gui the GUI of the class that uses this <code>
	 * ServerClient</code> class as the core server.
	 * @param gui the GUI of the <code>ServerClient</code> class that uses
	 * this class as the core server.
	 * @param infoFilterFrame the GUI of the information filtering
	 * application that this server is initialized as core server of
	 * its <code>ServerClient</code>.
	 */
	public void runServer(int port, int maxConnections,
			ServerClient gui, InfoFilterFrame infoFilterFrame) {
		this.infoFilterFrame = infoFilterFrame;
		receivedArticles = new Hashtable<>();
		reliabilities = new Hashtable<>();

		try {
			// set up server to receive connections
			serverSocket = new ServerSocket(port, maxConnections);

			// set up GUI controls
			gui.setRunningServerGUIControls();

			while (true) {
				ClientConnection client = waitForConnection(); // wait for a connection
				clients.add(client);
				receivedArticles.put(client, new ArrayList<List<Article>>());
				reliabilities.put(client, new Reliability(client.name));

				runClients.execute(client);
			} // end while
		} catch (IOException ioException) {
			if (serverSocket != null && serverSocket.isClosed()) {
				gui.setStoppedServerGUIControls();
				displayMessage("\nServer closed, not accepting connections\n");
			} else {
				System.out.println("Error: cannot initialize server socket or accepting connection.");
				ioException.printStackTrace();
			}
		}
	} // end method runServer

	/**
	 * Waits for a connection to arrive, then displays the connection
	 * information.
	 * @return the <code>ClientConnection</code> object that contains
	 * all utilities for managing I/O and connection with the new
	 * connected client.
	 * @throws IOException if the server cannot accept the connection.
	 */
	private ClientConnection waitForConnection() throws IOException {
		if (clients.size() == 0) {
			displayMessage("Waiting for connection\n");
		} else {
			displayMessage("Still wating for other connections\n");
		}

		// allow server to accept connection
		Socket server = serverSocket.accept();

		displayMessage("\nConnection received from: " + serverSocket.getInetAddress());

		ClientConnection client = new ClientConnection(
				server,
				"Client Agent " + clients.size());

		return client;
	} // end method waitForConnection

	/** Manipulates displayArea in the event-dispatch thread. */
	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayArea.append(messageToDisplay);
				displayArea.setCaretPosition(displayArea.getText().length());
			} // end method run
		}); // end call to SwingUtilities.invokeLater
	} // end method displayMessage

	/**
	 * Temporarily stops the server.
	 * @throws IOException if any error occurs while stopping server.
	 */
	protected void stopServer() throws IOException{
		IOException lastEx=null;

		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			lastEx=ex;
		}

		for (int i = clients.size() - 1; i >= 0; i--) {
			ClientConnection connection = clients.get(i);
			connection.closeConnection();
		}

		if (lastEx!=null){
			throw lastEx;
		}
	}

	/**
	 * The <code>ClientConnection</code> class provides a mechanism
	 * for managing connections between the server and other clients
	 * in multiple threads.
	 * 
	 * @author Tran Xuan Hoang
	 */
	protected class ClientConnection implements Runnable {
		private Socket connection; // connection to client
		private ObjectInputStream input; // input from client
		private ObjectOutputStream output; // output to client
		private String name;

		/**
		 * Creates a <code>ClientConnection</code> object connecting
		 * between the server and the new client. The created object
		 * contains I/O streams allowing exchanging information
		 * between server and client.
		 * @param socket the socket from which server communicates
		 * with the new requesting client.
		 * @param id the identification number of the connection.
		 * @param name the name of the connection (typically, the
		 * name is same as the client's name.
		 */
		public ClientConnection(Socket socket, String name) {
			this.name = name;
			connection = socket;

			try {
				getStreams();
			} catch (IOException ioException) {
				displayMessage("Error: cannot exchange information with " + name);
				ioException.printStackTrace();
				System.exit(1);
			}
		} // end ClientConnection constructor

		@Override
		public void run() {
			try {
				processConnection();
			} catch (IOException e) {
				System.out.println("Error occurs while exchanging data with " + this);
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		}

		/** Gets streams to send and receive data. */
		private void getStreams() throws IOException {
			// set up output stream for objects
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush(); // flush output buffer to send header information

			// set up input stream for objects
			input = new ObjectInputStream(connection.getInputStream());

			displayMessage("\nGot I/O streams with " + this + "\n");
		} // end method getStreams

		/** Exchanges information with client. */
		@SuppressWarnings("unchecked")
		private void processConnection() throws IOException {
			// send connection successful message
			Object message = "Connection successful";
			sendData(message);

			// enable enterField so server user can send messages
			if (!enterField.isEditable()) {
				setTextFieldEditable(true);
			}

			// process messages sent from client
			do { 
				try {
					// read message and display it
					message = input.readObject();

					//TODO receive articles from client
					if (message instanceof ArrayList<?> &&
							((ArrayList<?>) message).get(0) instanceof Article) {
						List<List<Article>> receivedArticle = receivedArticles.get(this);

						receivedArticle.add((ArrayList<Article>) message);
						receivedArticles.put(this, receivedArticle);

						infoFilterFrame.receiveArticlesFromOtherAgent(
								(ArrayList<Article>) message, reliabilities.get(this));
					}

					// end connection with client when client user
					// types TERMINATE
					if (message.equals("TERMINATE")) {
						displayMessage("\n\n" + name + ">>> TERMINATE");
						break;
					}

					if (message instanceof String) {
						displayMessage("\n" + name + ">>> " + message);
					}
				} catch (ClassNotFoundException classNotFoundException) {
					displayMessage("\nUnknown object type received from " + name + "\n");
				} catch (IOException ioException) {
					// end connection with client when client user
					// closes window without typing TERMINATE
					displayMessage("\n\n" + name + ">>> TERMINATE");
					break;
				}
			} while (true);
		} // end method processConnection

		/** Close streams and socket. */
		private void closeConnection() {
			displayMessage("\nTerminating connection with " + this);

			try {
				output.close(); // close output stream
				input.close(); // close input stream
				connection.close(); // close socket
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// remove this connection when its corresponding client ends
			clients.remove(this);
			displayMessage("\n" + clients.size() +
					" Remaining client(s): " + clients + "\n");

			receivedArticles.remove(this);
			reliabilities.remove(this);

			if (clients.isEmpty()) {
				setTextFieldEditable(false); // disable enterField
			}
		} // end method closeConnection

		/** Manipulates enterField in the event-dispatch thread. */
		private void setTextFieldEditable(final boolean editable) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					enterField.setEditable(editable);
				}
			});
		} // end method setTextFieldEditable

		/** Sends message to client. */
		public void sendData(Object message) {
			try {
				// send object to client
				output.reset();
				output.writeObject(message);
				output.flush();
			} catch (IOException ioException) {
				displayArea.append("\nError writing object");
			}
		} // end method sendData

		/**
		 * Retrieves basic information of the connection.
		 */
		public String toString() {
			return name + " <" + connection.getInetAddress().getHostName() + ">";
		}
	} // end class ClientConnection
} // end class Server