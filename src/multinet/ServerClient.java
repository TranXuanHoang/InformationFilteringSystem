package multinet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

/**
 * The <code>ServerClient</code> class provide basic method for
 * exchanging information between server-client application.
 * 
 * @author Tran Xuan Hoang
 */
public class ServerClient extends JPanel {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	private JTextField cPortTextField;
	private JTextField cMaxConnectionsTextField;
	private JButton cRunServerButton;
	private JLabel cConnectStatus;
	private JButton cStopServerButton;

	private JTextField sIPTextField;
	private JTextField sPortTextField;
	private JButton sConnectButton;
	private JLabel sConnectStatus;
	private JButton sDisconnectButton;

	private ImageIcon connectedIcon;
	private ImageIcon disconnectedIcon;

	protected Server server;
	protected Client client;

	/**
	 * Create the panel.
	 */
	public ServerClient() {
		connectedIcon = new ImageIcon(
				getClass().getResource("icons/Connected.png"));
		connectedIcon = resizeIcon(connectedIcon, 25, 25);
		disconnectedIcon = new ImageIcon(
				getClass().getResource("icons/Disconnected.png"));
		disconnectedIcon = resizeIcon(disconnectedIcon, 25, 25);

		setLayout(new BorderLayout(0, 0));

		JPanel southPanel = new JPanel();
		southPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(southPanel, BorderLayout.SOUTH);

		JPanel sendPanel = new JPanel();
		sendPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		sendPanel.setLayout(new BorderLayout(0, 0));

		JPanel receivePanel = new JPanel();
		receivePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		receivePanel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sendPanel, receivePanel);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setBorder(new EmptyBorder(10, 0, 10, 0));
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.5);
		splitPane.setDividerSize(10);
		BasicSplitPaneDivider divider =
				((BasicSplitPaneUI) splitPane.getUI()).getDivider();
		divider.setBorder(null);

		JPanel sendSetupPanel = new JPanel();
		sendPanel.add(sendSetupPanel, BorderLayout.NORTH);
		sendSetupPanel.setLayout(new BorderLayout(0, 0));

		JLabel sendTitleLabel = new JLabel("Exchange Information with Server");
		sendSetupPanel.add(sendTitleLabel, BorderLayout.NORTH);
		sendTitleLabel.setForeground(Color.BLUE);
		sendTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sendTitleLabel.setFont(new Font("Calibri", Font.PLAIN, 16));

		JPanel sendSetupPanel1 = new JPanel();
		sendSetupPanel1.setBorder(new EmptyBorder(5, 10, 5, 10));
		sendSetupPanel.add(sendSetupPanel1, BorderLayout.WEST);
		sendSetupPanel1.setLayout(new GridLayout(2, 1, 5, 10));

		JLabel sIPLabel = new JLabel("IP Address");
		sIPLabel.setHorizontalAlignment(SwingConstants.LEFT);
		sIPLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
		sendSetupPanel1.add(sIPLabel);

		JLabel sPortLabel = new JLabel("Port Number");
		sPortLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
		sendSetupPanel1.add(sPortLabel);

		JPanel sendSetupPanel2 = new JPanel();
		sendSetupPanel2.setBorder(new EmptyBorder(0, 0, 0, 10));
		sendSetupPanel.add(sendSetupPanel2);
		sendSetupPanel2.setLayout(new GridLayout(2, 1, 5, 5));

		sIPTextField = new JTextField();
		sIPTextField.setText("localhost");
		sIPTextField.setToolTipText("IP address of the server to which you want to connect");
		sIPTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
		sIPTextField.setColumns(12);
		sendSetupPanel2.add(sIPTextField);

		sPortTextField = new JTextField();
		sPortTextField.setText("10000");
		sPortTextField.setToolTipText("Port number of the server to which you want to connect");
		sPortTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
		sPortTextField.setColumns(12);
		sendSetupPanel2.add(sPortTextField);

		JPanel sendSetupPanel3 = new JPanel();
		sendSetupPanel3.setBorder(null);
		sendSetupPanel.add(sendSetupPanel3, BorderLayout.SOUTH);
		sendSetupPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		sConnectButton = new JButton("Connect");
		sConnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectClientToServer(e);
			}
		});
		sConnectButton.setToolTipText("Connect to the server whose IP address and port number are as above");
		sConnectButton.setIcon(null);
		sConnectButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		sendSetupPanel3.add(sConnectButton);

		sConnectStatus = new JLabel();
		sConnectStatus.setIcon(disconnectedIcon);
		sendSetupPanel3.add(sConnectStatus);

		sDisconnectButton = new JButton("Disconnect");
		sDisconnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnectClientToServer(e);
			}
		});
		sDisconnectButton.setToolTipText("Disconnect from the sever whose IP address and port number are as above");
		sDisconnectButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		sDisconnectButton.setEnabled(false);
		sendSetupPanel3.add(sDisconnectButton);

		client = new Client();
		sendPanel.add(client, BorderLayout.CENTER);

		JPanel receiveSetupPanel = new JPanel();
		receivePanel.add(receiveSetupPanel, BorderLayout.NORTH);
		receiveSetupPanel.setLayout(new BorderLayout(0, 0));

		JLabel receiveTitleLabel = new JLabel("Exchange Information with Clients");
		receiveSetupPanel.add(receiveTitleLabel, BorderLayout.NORTH);
		receiveTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		receiveTitleLabel.setForeground(Color.BLUE);
		receiveTitleLabel.setFont(new Font("Calibri", Font.PLAIN, 16));

		JPanel receiveSetupPanel1 = new JPanel();
		receiveSetupPanel1.setBorder(new EmptyBorder(5, 10, 5, 10));
		receiveSetupPanel.add(receiveSetupPanel1, BorderLayout.WEST);
		receiveSetupPanel1.setLayout(new GridLayout(2, 1, 5, 10));

		JLabel cPortLabel = new JLabel("Port Number");
		cPortLabel.setHorizontalAlignment(SwingConstants.LEFT);
		receiveSetupPanel1.add(cPortLabel);
		cPortLabel.setFont(new Font("Calibri", Font.PLAIN, 14));

		JLabel cMaxConnections = new JLabel("Max Connections");
		cMaxConnections.setHorizontalAlignment(SwingConstants.LEFT);
		receiveSetupPanel1.add(cMaxConnections);
		cMaxConnections.setFont(new Font("Calibri", Font.PLAIN, 14));

		JPanel receiveSetupPanel2 = new JPanel();
		receiveSetupPanel2.setBorder(new EmptyBorder(0, 0, 0, 10));
		receiveSetupPanel.add(receiveSetupPanel2);
		receiveSetupPanel2.setLayout(new GridLayout(2, 1, 5, 5));

		cPortTextField = new JTextField("10000");
		cPortTextField.setToolTipText("Port number of the server you want to run");
		receiveSetupPanel2.add(cPortTextField);
		cPortTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
		cPortTextField.setColumns(12);

		cMaxConnectionsTextField = new JTextField("10");
		cMaxConnectionsTextField.setToolTipText("Max number of connections that the server you want to run can respond");
		receiveSetupPanel2.add(cMaxConnectionsTextField);
		cMaxConnectionsTextField.setFont(new Font("Calibri", Font.PLAIN, 14));
		cMaxConnectionsTextField.setColumns(10);

		JPanel receiveSetUpPanel3 = new JPanel();
		receiveSetUpPanel3.setBorder(null);
		receiveSetupPanel.add(receiveSetUpPanel3, BorderLayout.SOUTH);
		receiveSetUpPanel3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		cRunServerButton = new JButton("Run Server");
		cRunServerButton.setToolTipText("Run server whose port number max connections are as above");
		cRunServerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runServer(e);
			}
		});
		cRunServerButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		receiveSetUpPanel3.add(cRunServerButton);

		cConnectStatus = new JLabel();
		cConnectStatus.setIcon(disconnectedIcon);
		receiveSetUpPanel3.add(cConnectStatus);

		cStopServerButton = new JButton("Stop Server");
		cStopServerButton.setToolTipText("Stop running server");
		cStopServerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopServer(e);
			}
		});
		cStopServerButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		cStopServerButton.setEnabled(false);
		receiveSetUpPanel3.add(cStopServerButton);

		server = new Server();
		receivePanel.add(server, BorderLayout.CENTER);
	}

	protected void connectClientToServer(ActionEvent e) {
		String serverIP = sIPTextField.getText().trim();
		int serverPort = Integer.parseInt(sPortTextField.getText().trim());

		new Thread() {
			@Override
			public void run() {
				if (client.isClosed()) {
					client.runClient(serverIP, serverPort, ServerClient.this);
				}
			}
		}.start();
	}

	protected void disconnectClientToServer(ActionEvent e) {
		if (client != null && !client.isClosed()) {
			client.closeConnection();

			setDisconnectingClientGUIControls();
		}
	}

	protected void runServer(ActionEvent e) {
		int portNumber = Integer.parseInt(cPortTextField.getText().trim());
		int maxConnections = Integer.parseInt(cMaxConnectionsTextField.getText().trim());

		new Thread() {
			@Override
			public void run() {
				server.runServer(portNumber, maxConnections, ServerClient.this);
			}
		}.start();

	}

	protected void stopServer(ActionEvent e) {
		try {
			server.stopServer();
			setStoppedServerGUIControls();
		} catch (IOException e1) {
			System.out.println("Error: cannot stop server");
			e1.printStackTrace();
		}
	}

	protected void setConnectedClientGUIControls() {
		sIPTextField.setEditable(false);
		sPortTextField.setEditable(false);
		sConnectButton.setEnabled(false);
		sConnectStatus.setIcon(connectedIcon);
		sDisconnectButton.setEnabled(true);
	}

	protected void setDisconnectingClientGUIControls() {
		sIPTextField.setEditable(true);
		sPortTextField.setEditable(true);
		sConnectButton.setEnabled(true);
		sConnectStatus.setIcon(disconnectedIcon);
		sDisconnectButton.setEnabled(false);
	}

	protected void setRunningServerGUIControls() {
		cPortTextField.setEditable(false);
		cMaxConnectionsTextField.setEditable(false);
		cRunServerButton.setEnabled(false);
		cConnectStatus.setIcon(connectedIcon);
		cStopServerButton.setEnabled(true);
	}

	protected void setStoppedServerGUIControls() {
		cPortTextField.setEditable(true);
		cMaxConnectionsTextField.setEditable(true);
		cRunServerButton.setEnabled(true);
		cConnectStatus.setIcon(disconnectedIcon);
		cStopServerButton.setEnabled(false);
	}

	/**
	 * Resizes an icon to the new icon with specified width and height.
	 * @param icon the old icon to be resized.
	 * @param width the width of the new icon.
	 * @param height the height of the new icon.
	 * @return a new icon with the new size.
	 */
	public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
		Image image = icon.getImage();
		Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		return new ImageIcon(newImage);
	}
} // end class ServerClient