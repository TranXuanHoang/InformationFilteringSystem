package infofilter;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.Customizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;

import agent.AgentEvent;
import agent.AgentEventListener;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import multinet.ServerClient;

public class InfoFilterFrame extends JFrame implements AgentEventListener {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	JMenuBar menuBar;
	JMenu menuFile;
	JMenu menuEdit;
	JMenu menuKeywords;
	JMenu menuFilter;
	JMenu menuExchange;
	JMenu menuHelp;

	JMenuItem resetMenuItem;
	JMenuItem downloadArticleMenuItem;
	JMenuItem loadArticleMenuItem;
	JMenuItem saveArticleMenuItem;
	JMenuItem exitMenuItem;

	JMenuItem deleteMenuItem;

	JMenuItem keywordsMenuItem;
	JMenuItem addArticleMenuItem;
	JMenuItem addAllMenuItem;

	JMenuItem trainNeuralNetworksMenuItem;
	JCheckBoxMenuItem useKeywordsCheckBoxMenuItem;
	JCheckBoxMenuItem useClustersCheckBoxMenuItem;
	JCheckBoxMenuItem useFeedbackCheckBoxMenuItem;
	ButtonGroup useButtonGroup;

	JMenuItem connections;
	JMenuItem agentsNetwork;
	JMenuItem agentReliability;
	JMenuItem sendArticles;
	JMenuItem sendFeedback;

	JMenuItem aboutMenuItem;

	JSplitPane splitPane;
	JScrollPane jScrollPane1;
	JScrollPane jScrollPane2;
	private TableModel articleTableModel;
	JTable articleTable;
	JEditorPane articleEditorPane;

	JPanel southPanel;
	JLabel filterAgentStatusLabel;
	JLabel taskProgessLabel;
	//JProgressBar taskProgessBar;

	String titleBarText = "Information Filtering Application";

	protected static final int COL_SUBJECT_ID = 0;
	protected static final int COL_APPROVE_ID = 1;
	protected static final int COL_SCORE_ID = 2;
	protected static final int COL_RATING_ID = 3;
	private static final String COL_SUBJECT = "Subject";
	private static final String COL_APPROVE = "Approve";
	private static final String COL_SCORE = "Score";
	private static final String COL_RATING = "Rating";
	protected static String[] columnNameList =
		{COL_SUBJECT, COL_APPROVE, COL_SCORE, COL_RATING};
	protected static final int NUM_COLS = columnNameList.length;

	protected Object[][] data;

	/** List of downloaded articles. */
	protected Vector<Article> articles;

	/** The agent that filters articles. */
	protected FilterAgent filterAgent;

	/** The agent that allows user to download article. */
	protected URLReaderAgent urlReaderAgent;

	/** Currently selected article. */
	Article currentArt;
	boolean scored = false; // true if articles were scored

	/**
	 * Type of filtering:<br>
	 * <ul>
	 * <li>{@link FilterAgent#USE_KEYWORDS}
	 * <li>{@link FilterAgent#USE_CLUSTERS}
	 * <li>{@link FilterAgent#USE_PREDICTED_RATING}
	 * </ul>
	 */
	int filterType = 0;
	
	/** Contains methods for exchanging information with other
	 * agents over the Internet. */
	protected ServerClient network;

	/**
	 * Constructs a frame for the information filtering agent.
	 */
	public InfoFilterFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try {
			initializeUnderlyingData();
			initializeGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the underlying data including the reference to
	 * the set of articles, the filter agent, the URL reader agent.
	 */
	private void initializeUnderlyingData() {
		articles = new Vector<>();

		// check if a serialized FilterAgent exists
		try {
			FilterAgent tmpFilterAgent = FilterAgent.restoreFromFile(
					FilterAgent.fileName);
			if (tmpFilterAgent != null) {
				filterAgent = tmpFilterAgent;

				System.out.println("\nSerialized " + filterAgent);
			}
		} catch (Exception e) {
			// there is no serialized filter agent, create a new one
			filterAgent = new FilterAgent();

			System.out.println("There is no serialized filter agent. "
					+ "New filter agent was created.");
			System.out.println("New " + filterAgent);
		}

		filterAgent.infoFilter = this;
		filterAgent.addAgentEventListener(this); // for trace msgs
		filterAgent.initialize();
		filterAgent.startAgentProcessing(); // start filter agent thread

		urlReaderAgent = new URLReaderAgent();
		urlReaderAgent.addAgentEventListener(this);
		urlReaderAgent.initialize();
		urlReaderAgent.startAgentProcessing(); // start it running

		// initialize network connections
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					network = new ServerClient(InfoFilterFrame.this);
				}
			});
		} catch (Exception e) {
			System.out.println("Error while creating ServerClient object");
		}
	}

	/**
	 * Initializes the GUI of the information filtering system.
	 * @throws Exception if any errors occur during initialization.
	 */
	private void initializeGUI() throws Exception {
		// set size before to allow set up the split pane's divider
		setSize(700, 500);

		menuFile = new JMenu("File");
		menuEdit = new JMenu("Edit");
		menuKeywords = new JMenu("Keywords");
		menuFilter = new JMenu("Filter");
		menuExchange = new JMenu("Exchange");
		menuHelp = new JMenu("Help");
		menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuKeywords);
		menuBar.add(menuFilter);
		menuBar.add(menuExchange);
		menuBar.add(menuHelp);

		resetMenuItem = new JMenuItem("Clear All");
		resetMenuItem.setIcon(getIcon("icons/File_ClearAll.png"));
		resetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMenuItem_actionPerformed(e);
			}
		});

		downloadArticleMenuItem = new JMenuItem("Dowload Article...");
		downloadArticleMenuItem.setIcon(getIcon("icons/File_DownloadArticle.png"));
		downloadArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadArticleMenuItem_actionPerformed(e);
			}
		});

		loadArticleMenuItem = new JMenuItem("Load Article...");
		loadArticleMenuItem.setIcon(getIcon("icons/File_LoadArticle.png"));
		loadArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadArticleMenuItem_actionPerformed(e);
			}
		});

		saveArticleMenuItem = new JMenuItem("Save Article...");
		saveArticleMenuItem.setIcon(getIcon("icons/File_SaveArticle.png"));
		saveArticleMenuItem.setEnabled(false);
		saveArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveArticleMenuItem_actionPerformed(e);
			}
		});

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setIcon(getIcon("icons/File_Exit.png"));
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menuFile.add(resetMenuItem);
		menuFile.addSeparator();
		menuFile.add(downloadArticleMenuItem);
		menuFile.add(loadArticleMenuItem);
		menuFile.add(saveArticleMenuItem);
		menuFile.addSeparator();
		menuFile.add(exitMenuItem);

		deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.setIcon(getIcon("icons/Edit_Delete.png"));
		deleteMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteMenuItem_actionPerformed(e);
			}
		});

		menuEdit.add(deleteMenuItem);

		keywordsMenuItem = new JMenuItem("Customize...");
		keywordsMenuItem.setIcon(getIcon("icons/Keywords_Customize.png"));
		keywordsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keywordsMenuItem_actionPerformed(e);
			}
		});

		addArticleMenuItem = new JMenuItem("Add Article");
		addArticleMenuItem.setIcon(getIcon("icons/Keywords_AddArticle.png"));
		addArticleMenuItem.setEnabled(true);
		addArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addArticleMenuItem_actionPerformed(e);
			}
		});

		addAllMenuItem = new JMenuItem("Add All Articles");
		addAllMenuItem.setIcon(getIcon("icons/Keywords_AddAllArticles.png"));
		addAllMenuItem.setEnabled(true);
		addAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllMenuItem_actionPerformed(e);
			}
		});

		menuKeywords.add(keywordsMenuItem);
		menuKeywords.addSeparator();
		menuKeywords.add(addArticleMenuItem);
		menuKeywords.add(addAllMenuItem);

		trainNeuralNetworksMenuItem = new JMenuItem("Train Neural Networks");
		trainNeuralNetworksMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				trainNeuralNetworksMenuItem_actionPerformed(e);
			}
		});

		useKeywordsCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Keywords");
		useKeywordsCheckBoxMenuItem.setSelected(true);
		useKeywordsCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useKeywordsCheckBoxMenuItem_actionPerformed(e);
			}
		});

		useFeedbackCheckBoxMenuItem = new JCheckBoxMenuItem(
				"Using Back Propagation Neural Network");
		useFeedbackCheckBoxMenuItem.setEnabled(
				filterAgent.neuralNetworksTrained ? true : false);
		useFeedbackCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useFeedbackCheckBoxMenuItem_actionPerformed(e);
			}
		});

		useClustersCheckBoxMenuItem = new JCheckBoxMenuItem(
				"Using Kohonen Map Neural Network");
		useClustersCheckBoxMenuItem.setEnabled(
				filterAgent.neuralNetworksTrained ? true : false);
		useClustersCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useClustersCheckBoxMenuItem_actionPerformed(e);
			}
		});

		useButtonGroup = new ButtonGroup();
		useButtonGroup.add(useKeywordsCheckBoxMenuItem);
		useButtonGroup.add(useFeedbackCheckBoxMenuItem);
		useButtonGroup.add(useClustersCheckBoxMenuItem);

		menuFilter.add(trainNeuralNetworksMenuItem);
		menuFilter.addSeparator();
		menuFilter.add(useKeywordsCheckBoxMenuItem);
		menuFilter.add(useFeedbackCheckBoxMenuItem);
		menuFilter.add(useClustersCheckBoxMenuItem);
		
		connections = new JMenuItem("Network Connections...");
		connections.setIcon(getIcon("icons/Exchange_NetworkConnections.png"));
		connections.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connections_actionPerformed(e);
			}
		});
		
		agentsNetwork = new JMenuItem("Network of Agents...");
		agentsNetwork.setIcon(getIcon("icons/Exchange_NetworkOfAgents.png"));
		agentsNetwork.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agentsNetwork_actionPerformed(e);
			}
		});
		
		agentReliability = new JMenuItem("Reliability of Agents...");
		agentReliability.setIcon(getIcon("icons/Exchange_ReliabilityOfAgents.png"));
		agentReliability.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agentReliability_actionPerformed(e);
			}
		});
		
		sendArticles = new JMenuItem("Send Article(s)");
		sendArticles.setIcon(getIcon("icons/Exchange_SendArticles.png"));
		sendArticles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendArticles_actionPerformed(e);
			}
		});
		
		sendFeedback = new JMenuItem("Send Feedback");
		sendFeedback.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendFeedback_actionPerformed(e);
			}
		});

		menuExchange.add(connections);
		menuExchange.add(agentsNetwork);
		menuExchange.addSeparator();
		menuExchange.add(agentReliability);
		menuExchange.addSeparator();
		menuExchange.add(sendArticles);
		menuExchange.add(sendFeedback);

		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setIcon(getIcon("icons/Help_About.png"));
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutMenuItem_actionPerformed(e);
			}
		});

		menuHelp.add(aboutMenuItem);

		setUpTheTable();
		articleTable.getTableHeader().setFont(
				new Font("Calibri", Font.BOLD, 14));
		articleTable.getTableHeader().setForeground(new Color(200, 70, 70));
		articleTable.setFont(new Font("Calibri", Font.PLAIN, 14));
		jScrollPane1 = new JScrollPane();
		jScrollPane1.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLUE),
				"Articles",
				TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION ,
				new Font("Calibri", Font.PLAIN, 16), Color.BLUE));
		jScrollPane1.getViewport().add(articleTable);

		articleEditorPane = new JEditorPane();
		articleEditorPane.setContentType("text/html");
		articleEditorPane.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		articleEditorPane.setEditable(false);
		jScrollPane2 = new JScrollPane();
		jScrollPane2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLUE),
				"Contents of the Selected Article",
				TitledBorder.LEFT,
				TitledBorder.DEFAULT_POSITION ,
				new Font("Calibri", Font.PLAIN, 16), Color.BLUE));
		jScrollPane2.getViewport().add(articleEditorPane);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				jScrollPane1, jScrollPane2);
		splitPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation((int) (this.getHeight() * 0.4));
		splitPane.setDividerSize(10);
		BasicSplitPaneDivider divider =
				((BasicSplitPaneUI) splitPane.getUI()).getDivider();
		divider.setBorder(null);
		Dimension minimumSize = new Dimension(this.getWidth(),
				(int) (this.getHeight() * 0.5 / 2));
		jScrollPane1.setMinimumSize(minimumSize);
		jScrollPane2.setMinimumSize(minimumSize);

		filterAgentStatusLabel = new JLabel();
		filterAgentStatusLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
		taskProgessLabel = new JLabel();
		taskProgessLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
		//taskProgessBar = new JProgressBar();
		//taskProgessBar.setStringPainted(true);
		//taskProgessBar.setForeground(Color.GREEN);
		JPanel progressPanel = new JPanel(new GridLayout(1, 0));
		progressPanel.add(taskProgessLabel);
		//progressPanel.add(taskProgessBar);
		
		southPanel = new JPanel(new GridLayout());
		southPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		southPanel.add(filterAgentStatusLabel);
		southPanel.add(progressPanel);

		setTitle(titleBarText + " - Using Keywords");
		setJMenuBar(menuBar);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	/**
	 * Resets the information filtering application when the
	 * <b>Clear All</b> menu item is selected.
	 * @param e the event generated when the <b>Clear All</b>
	 * menu item is selected.
	 */
	protected void resetMenuItem_actionPerformed(ActionEvent e) {
		System.out.println("Reset action is requested");
		articles.clear();
		refreshTable();

		if (articleEditorPane.getContentType().equals("text/html")) {
			articleEditorPane.setText("<html></html>");
		} else {
			articleEditorPane.setText("");
		}

		addArticleMenuItem.setEnabled(false);
		addAllMenuItem.setEnabled(false);
		saveArticleMenuItem.setEnabled(false);
	}

	/**
	 * Opens the <code>URLReaderAgent</code> customizer and allow
	 * the user to download articles from web pages.
	 * @param e the event generated when the <b>Download Article...</b>
	 * menu item is selected.
	 */
	protected void downloadArticleMenuItem_actionPerformed(ActionEvent e) {
		Class<?> customizerClass = urlReaderAgent.getCustomizerClass();

		if (customizerClass == null) {
			trace("Error: cannot find URLReaderAgent customizer class");
			return;
		}

		// found a customizer, now open it
		Customizer customizer = null;

		try {
			customizer = (Customizer) customizerClass.newInstance();
			customizer.setObject(urlReaderAgent);
		} catch (Exception ex) {
			trace("Error: opening URLReaderAgent customizer - " +
					ex.toString());
			return;
		}

		JDialog dlg = (JDialog) customizer;

		// center the dialog
		Dimension dlgSize = dlg.getSize();
		Dimension frameSize = this.getSize();
		Point frameLoc = this.getLocationOnScreen();

		dlg.setLocation(
				frameLoc.x + (frameSize.width - dlgSize.width) / 2,
				frameLoc.y + (frameSize.height - dlgSize.height) / 2);
		dlg.setVisible(true);
	}

	/**
	 * Loads and scores an article from a personal computer.
	 * @param e the event generated when the <b>Load Article...</b>
	 * menu item is selected.
	 */
	protected void loadArticleMenuItem_actionPerformed(ActionEvent e) {
		FileDialog openFileDialog = new FileDialog(
				this, "Open", FileDialog.LOAD);
		openFileDialog.setVisible(true);

		String directory = openFileDialog.getDirectory();
		String fileName = openFileDialog.getFile();
		String filePath = directory + fileName;

		if (fileName != null) {
			int type = Article.typeOfFile(fileName);
			Article article = new Article(filePath, type);

			switch (type) {
			case Article.FROM_TEXT_FILE:
				String text = Article.readArticle(filePath);
				article.setBody(text);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_PDF_FILE:
				String pdf = Utilities.getContentsOfPDFFile(filePath);
				article.setBody(pdf);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_MS_WORD_FILE:
				String msword = Utilities.getContentsOfWordFile(filePath);
				article.setBody(msword);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_PPTX_FILE:
				String pptx = Utilities.getContentsOfPPTXFile(filePath);
				article.setBody(pptx);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/plain");
				break;

			case Article.FROM_HTML_FILE:
				String html = Article.readArticle(filePath);
				article.setBody(html);
				article.setSubject(fileName, type);
				articleEditorPane.setContentType("text/html");
				break;

			default:
				System.out.println("Error: the type of the selected"
						+ " file is not allowed to be read.");
				return;
			}

			articles.addElement(article);
			filterAgent.score(article, filterType);

			refreshTable();
			articleEditorPane.setText(article.getBody());
			articleEditorPane.setCaretPosition(0);
		}
	}

	/**
	 * Saves an article to a personal computer for later uses.
	 * @param e the event generated when the <b>Save Article...</b>
	 * menu item is selected.
	 */
	protected void saveArticleMenuItem_actionPerformed(ActionEvent e) {
		FileDialog saveFileDialog = new FileDialog(
				this, "Save", FileDialog.SAVE);
		saveFileDialog.setVisible(true);

		String directory = saveFileDialog.getDirectory();
		String fileName = saveFileDialog.getFile();

		if (fileName != null) {
			int index = articleTable.getSelectedRow();

			if (index != -1) {
				Article article = articles.elementAt(index);
				article.writeArticle(fileName, directory);
			}
		}
	}

	/**
	 * Deletes the article selected by user from the table of all
	 * loaded and downloaded articles.
	 * @param e the event generated when the <b>Delete</b> button
	 * is selected.
	 */
	protected void deleteMenuItem_actionPerformed(ActionEvent e) {
		int selectedRow = articleTable.getSelectedRow();

		if (selectedRow == -1 || articles.size() == 0) {
			return; // nothing is selected
		}

		articles.removeElementAt(selectedRow);
		currentArt = null;

		if (articleEditorPane.getContentType().equals("text/html")) {
			articleEditorPane.setText("<html></html>");
		} else {
			articleEditorPane.setText("");
		}

		refreshTable(); // update the table model and refresh display

		if (articles.size() == 0) {
			saveArticleMenuItem.setEnabled(false);
			addArticleMenuItem.setEnabled(false);
			addAllMenuItem.setEnabled(false);
		}
	}

	/**
	 * Opens a dialog box allowing user to <b>Add</b>, <b>Change</b>,
	 * <b>Remove</b> keywords that are used to score articles. The
	 * customizer dialog box also allows user to create a <code>
	 * .dfn</code> profile data file that defines all fields of
	 * each input record.
	 * @param e the event generated when the <b>Customize</b> menu
	 * item is selected.
	 */
	protected void keywordsMenuItem_actionPerformed(ActionEvent e) {
		Class<?> customizerClass = filterAgent.getCustomizerClass();

		if (customizerClass == null) {
			trace("Error: cannot find FilterAgent customizer class");
			return;
		}

		// found a customizer, now open it
		Customizer customizer = null;

		try {
			customizer = (Customizer) customizerClass.newInstance();
			customizer.setObject(filterAgent);
		} catch (Exception ex) {
			System.out.println("Error: opening customizer - " +
					ex.toString());
			return;
		}

		JDialog dlg = (JDialog) customizer;

		// center the dialog
		Dimension dlgSize = dlg.getSize();
		Dimension frameSize = this.getSize();
		Point frameLoc = this.getLocationOnScreen();

		dlg.setLocation(
				frameLoc.x + (frameSize.width - dlgSize.width) / 2,
				frameLoc.y + (frameSize.height - dlgSize.height) / 2);
		dlg.setVisible(true);
	}

	protected void addArticleMenuItem_actionPerformed(ActionEvent e) {
		// open the profile data file and append the profile
		// record of the currently selected article
		filterAgent.addArticleToProfile(currentArt);
	}

	protected void addAllMenuItem_actionPerformed(ActionEvent e) {
		// open the profile data file and append the profile and
		// append profile records of all downloaded articles
		filterAgent.addAllArticlesToProfile(articles);
	}

	/**
	 * Trains the back propagation neural network and the Kohonen
	 * map neural network (Signals the filter agent to start training
	 * the neural networks on its own thread).
	 * @param e the event generated when the <b>Train Neural Networks</b>
	 * menu item is selected.
	 */
	protected void trainNeuralNetworksMenuItem_actionPerformed(ActionEvent e) {
		if (filterAgent.neuralNetworksTrained) {
			int option = JOptionPane.showConfirmDialog(this,
					"This will reset the current neural networks and\n" +
							"start training them again.\n" +
							"Are you sure you want to do this?",
							"Traing Neural Networks",
							JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_NO_OPTION) {
				filterAgent.buildNeuralNetworks();
			}
		} else {
			filterAgent.buildNeuralNetworks();
		}
	}

	/**
	 * Scores all loaded/downloaded articles by counting the
	 * frequencies of keywords.
	 * @param e the event generated when the <b>Using Keywords</b>
	 * menu item is selected.
	 */
	protected void useKeywordsCheckBoxMenuItem_actionPerformed(ActionEvent e) {
		filterType = FilterAgent.USE_KEYWORDS;
		filterArticles();
		this.setTitle(titleBarText + " - Using Keywords");
	}

	/**
	 * Scores all loaded/downloaded articles using the back
	 * propagation neural network.
	 * @param e the event generated when the <b>Using Back
	 * Propagation Neural Network</b> menu item is selected.
	 */
	protected void useFeedbackCheckBoxMenuItem_actionPerformed(ActionEvent e) {
		filterType = FilterAgent.USE_PREDICTED_RATING;
		filterArticles();
		this.setTitle(titleBarText + " - Using Backpropagation Neural Network");
	}

	/**
	 * Scores all loaded/downloaded articles using the Kohonen map
	 * neural network.
	 * @param e the event generated when the <b>Using Kohonen Map</b>
	 * menu item is selected.
	 */
	protected void useClustersCheckBoxMenuItem_actionPerformed(ActionEvent e) {
		filterType = FilterAgent.USE_CLUSTERS;
		filterArticles();
		this.setTitle(titleBarText + " - Using Kohonen Map Neural Network");
	}

	/**
	 * Customize network connections between agent over Internet.
	 * @param e the event generated when the <b>Network Connections</b>
	 * menu item is selected.
	 */
	protected void connections_actionPerformed(ActionEvent e) {
		JFrame frame = new JFrame("Network Connections");
		frame.setSize(650, 400);
		frame.setIconImage(getIcon("icons/Exchange_NetworkConnections.png").getImage());
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.add(network);

		// center the dialog
		Dimension frameSize = frame.getSize();
		Dimension appSize = this.getSize();
		Point appLocation = this.getLocationOnScreen();

		frame.setLocation(
				appLocation.x + (appSize.width - frameSize.width) / 2,
				appLocation.y + (appSize.height - frameSize.height) / 2);

		frame.setVisible(true);
	}

	/**
	 * Graphically shows the network of agents that connect to
	 * this agent.
	 * @param e the event generated when the <b>Network of Agent...</b>
	 * menu item is selected.
	 */
	protected void agentsNetwork_actionPerformed(ActionEvent e) {
		if (network.server.reliabilities == null) {
			JOptionPane.showMessageDialog(this,
					"There is no agent connecting to you",
					"", JOptionPane.ERROR_MESSAGE, null);
		} else {
			List<Reliability> reliabilities = new ArrayList<Reliability>(
					network.server.reliabilities.values());

			// create graph of agents
			Graph<String, String> g;

			// Graph<V, E> where V is the type of the vertices and E is the type of the edges
			// Note showing the use of a SparseGraph rather than a SparseMultigraph
			g = new SparseGraph<String, String>();

			// add vertices and edges to the graph
			String serverNode = "You";
			g.addVertex(serverNode);

			for (Reliability r : reliabilities) {
				String clientNode = r.getAgentName();
				String edgeLabel = r.getIPAddress();

				g.addVertex(clientNode);
				g.addEdge(edgeLabel, clientNode, serverNode, EdgeType.DIRECTED);
			}

			// 2. A layout implementation
			CircleLayout<String, String> layout = new CircleLayout<String, String>(g);
			layout.setSize(new Dimension(300, 300));
			BasicVisualizationServer<String, String> vv = new BasicVisualizationServer<>(layout);
			vv.setPreferredSize(new Dimension(350, 350));

			Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {
				@Override
				public Paint transform(String vertexName) {
					if (vertexName.equals("You")) {
						return Color.RED;
					} else {
						return new Color(160, 200, 250);
					}
				}
			};
			
			Transformer<String, String> labelTransformer =
					new ChainedTransformer<String,String>(new Transformer[]{
					new ToStringLabeller<String>(),
					new Transformer<String,String>() {
						public String transform(String input) {
							return "<html><font color=\"blue\">" + input + "</html>";
						}}});


			vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
			vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
			vv.getRenderContext().setEdgeLabelTransformer(labelTransformer);
			vv.getRenderContext().setLabelOffset(20);
			vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
			vv.setBackground(Color.WHITE);

			JDialog dialog = new JDialog(this, "Network of Agents", true);
			dialog.setIconImage(getIcon("icons/Exchange_NetworkOfAgents.png").getImage());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.getContentPane().add(vv);
			dialog.pack();
			dialog.setResizable(false);

			// center dialog
			Dimension dialogSize = dialog.getSize();
			Dimension appSize = this.getSize();
			Point appLocation = this.getLocationOnScreen();

			dialog.setLocation(
					appLocation.x + (appSize.width - dialogSize.width) / 2,
					appLocation.y + (appSize.height - dialogSize.height) / 2);

			dialog.setVisible(true);
		}
	}
	
	/**
	 * Graphically shows the reliability of all agents that have been
	 * connecting and sending articles to this agent.
	 * @param e the event generated when the <b>Reliability of Agent...</b>
	 * menu item is selected.
	 */
	protected void agentReliability_actionPerformed(ActionEvent e) {
		if (network.server.reliabilities == null) {
			JOptionPane.showMessageDialog(this,
					"There is no agent connecting to you",
					"", JOptionPane.ERROR_MESSAGE, null);
		} else {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			ReliabilityGUIHeader header = new ReliabilityGUIHeader();
			panel.add(header);

			List<Reliability> reliabilities = new ArrayList<Reliability>(
					network.server.reliabilities.values());

			for (Reliability r : reliabilities) {
				panel.add(r.gui);
			}

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.getViewport().add(panel);

			JFrame frame = new JFrame("Reliability of Client Agents");
			frame.setIconImage(getIcon("icons/Exchange_ReliabilityOfAgents.png").getImage());
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			frame.add(scrollPane);
			frame.setSize(460, 140 + reliabilities.size() * 60);
			frame.setResizable(false);

			// center frame
			Dimension frameSize = frame.getSize();
			Dimension appSize = this.getSize();
			Point appLocation = this.getLocationOnScreen();

			frame.setLocation(
					appLocation.x + (appSize.width - frameSize.width) / 2,
					appLocation.y + (appSize.height - frameSize.height) / 2);

			frame.setVisible(true);
		}
	}

	/**
	 * Sends selected articles to the other agent that this agent
	 * is connected to.
	 * @param e the event generated when the <b>Send Article(s)</b>
	 * menu item is selected.
	 */
	protected void sendArticles_actionPerformed(ActionEvent e) {
		ArrayList<Article> selectedArticles = new ArrayList<>();

		for (int i = 0; i < articles.size(); i++) {
			Article article = articles.get(i);

			if (article.apporved) {
				selectedArticles.add(article);
			}
		}

		if (network.client.isClosed()) {
			displayTaskERR("Network is disconnected - Cannot send articles");
		} else if (selectedArticles.size() == 0) {
			displayTaskERR("No articles are selected to send");
		} else {
			network.client.sendData(selectedArticles);
			displayTaskMSG("Article" +
					(selectedArticles.size() > 1 ? "s were " : " was ") + "sent");
		}
	}
	
	//TODO check reliability
	public void receiveArticlesFromOtherAgent(
			ArrayList<Article> receivedArticles,
			Reliability reliability) {
		// score received article using the corresponding filter type
		Vector<Article> newArticles = new Vector<>(receivedArticles);
		filterAgent.score(newArticles, filterType);

		// select articles that have score higher than or equal to the
		// threshold of the corresponding filter type
		int selected = 0;
		int eliminated = 0;

		for (Article newArticle : newArticles) {
			if (newArticle.getScore(filterType) >=
					getAcceptableScoreThreshold(filterType)) {
				newArticle.apporved = true;
				selected++;
			} else {
				newArticle.apporved = false;
				eliminated++;
			}
		}

		reliability.updateReliability(selected, eliminated);

		// add received articles to the set of articles and view them
		// in the GUI table of articles
		articles.addAll(newArticles);
		refreshTable();
	}
	
	public double getAcceptableScoreThreshold(int filterType) {
		switch (filterType) {
		case FilterAgent.USE_KEYWORDS:
			return keywordThreshold;
		case FilterAgent.USE_PREDICTED_RATING:
			return backpropThreshold;
		case FilterAgent.USE_CLUSTERS:
			return kmapThreshold;
		default:
			return 0;
		}
	}
	
	public int keywordThreshold = 60;
	public double backpropThreshold = 0.8;
	public double kmapThreshold = 0.85;

	protected void sendFeedback_actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Shows a dialog box that contains general information about
	 * the information filtering application.
	 * @param e the event generated when the <b>About</b> menu
	 * item is selected.
	 */
	protected void aboutMenuItem_actionPerformed(ActionEvent e) {
		AboutDialog about = new AboutDialog(this,
				"About Information Filtering Application", true);
		Dimension aboutSize = about.getSize();
		Dimension frameSize = this.getSize();
		Point frameLoc = this.getLocationOnScreen();

		about.setLocation(
				frameLoc.x + (frameSize.width - aboutSize.width) / 2,
				frameLoc.y + (frameSize.height - aboutSize.height) / 2);
		about.setVisible(true);
	}

	/**
	 * Defines the GUI, model and event handling for the table of
	 * articles.
	 */
	protected void setUpTheTable() {
		// get data from the set of downloaded/loaded articles
		data = getTableData();

		// create a model of the data
		articleTableModel = new AbstractTableModel() {
			/** Serial version. */
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

			@Override
			public void setValueAt(Object value, int row, int col) {
				switch (col) {
				case COL_SUBJECT_ID:
					break;

				case COL_APPROVE_ID:
					Boolean approved = (Boolean) value;
					data[row][col] = approved;

					if (articles.size() > 0) {
						articles.elementAt(row).setApproved(approved);
					}

					break;

				case COL_SCORE_ID:
					break;

				case COL_RATING_ID:
					String userRating = ((String) value).trim();
					data[row][col] = userRating;

					if (articles.size() > 0) {
						articles.get(row).setUserRating(userRating);
					}

					break;
				}
			}

			@Override
			public int getRowCount() {
				return data.length;
			}

			@Override
			public int getColumnCount() {
				return columnNameList.length;
			}

			@Override
			public String getColumnName(int colID) {
				return columnNameList[colID];
			}

			@Override
			public Class<?> getColumnClass(int colID) {
				return getValueAt(0, colID).getClass();
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				switch (col) {
				case COL_SUBJECT_ID:
					return false;
				case COL_APPROVE_ID:
					return true;
				case COL_SCORE_ID:
					return false;
				case COL_RATING_ID:
					return true;
				default:
					return false;
				}
			}
		}; // end create a model of the data

		articleTable = new JTable(articleTableModel);

		// handle the table's row selection events (1-click event)
		ListSelectionModel rowSM = articleTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// ignore extra message
				if (e.getValueIsAdjusting()) {
					return;
				}

				ListSelectionModel m = (ListSelectionModel) e.getSource();

				if (!m.isSelectionEmpty()) {
					int selectedRow = m.getMinSelectionIndex();

					if (articles.size() > 0) {
						currentArt = articles.get(selectedRow);

						if (currentArt.getType() == Article.FROM_WEB_PAGE) {
							articleEditorPane.setContentType("text/html");
							try {
								articleEditorPane.setPage(currentArt.getID());
							} catch (Exception exception) {
								articleEditorPane.setText(currentArt.getBody());
							}
						} else if (currentArt.getType() == Article.FROM_HTML_FILE) {
							articleEditorPane.setContentType("text/html");
							articleEditorPane.setText(currentArt.getBody());
						} else {
							articleEditorPane.setContentType("text/plain");
							articleEditorPane.setText(currentArt.getBody());
						}

						articleEditorPane.setCaretPosition(0);
					} else {
						currentArt = null;
					}
				}
			}
		}); // end handle the table's row selection events

		// handle the table row's double-click event (open file
		// or web page using the system application.
		articleTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = articleTable.rowAtPoint(e.getPoint());
					int col = articleTable.columnAtPoint(e.getPoint());

					if (articles != null && row >= 0 &&
							row < articles.size() && col == COL_SUBJECT_ID) {
						Article art = articles.get(row);

						if (art.type == Article.FROM_WEB_PAGE) {
							Utilities.openWebPageUsingSystemBrowser(art.getID());
						} else {
							Utilities.viewFileUsingSystemApp(art.getID());
						}
					}
				}
			}
		});

		JCheckBox approved = new JCheckBox();
		articleTable.getColumnModel().getColumn(COL_APPROVE_ID).
		setCellEditor(new DefaultCellEditor(approved));

		JComboBox<String> userRatings =
				new JComboBox<>(FilterAgent.RATINGS);
		userRatings.setFont(new Font("Calibri", Font.PLAIN, 14));
		articleTable.getColumnModel().getColumn(COL_RATING_ID).
		setCellEditor(new DefaultCellEditor(userRatings));

		articleTable.getColumn(COL_SUBJECT).setPreferredWidth(300);
		articleTable.getColumn(COL_APPROVE).setMaxWidth(70);
		articleTable.getColumn(COL_APPROVE).setMinWidth(70);
		articleTable.getColumn(COL_SCORE).setPreferredWidth(30);
		articleTable.getColumn(COL_RATING).setPreferredWidth(30);

		articleTable.setRowHeight(20);
		articleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		articleTable.setCellSelectionEnabled(true);
	}

	/**
	 * Retrieves the subjects, approved flags, scores and
	 * user ratings of downloaded/loaded articles.
	 * @return an array of downloaded/loaded articles' information
	 * including subjects, approved flags, scores and ratings.
	 */
	private Object[][] getTableData() {
		Object[][] table;

		if (articles == null) {
			return null;
		}

		if (articles.size() == 0) {
			table = new Object[1][NUM_COLS];

			table[0][0] = "";
			table[0][1] = false;
			table[0][2] = "";
			table[0][3] = "";

			return table;
		} else {
			table = new Object[articles.size()][NUM_COLS];

			for (int i = 0; i < articles.size(); i++) {
				Article article = articles.elementAt(i);

				table[i][0] = article.getSubject();
				table[i][1] = article.isApproved();
				table[i][2] = String.valueOf(article.getScore(filterType));
				table[i][3] = article.getUserRating();
			}

			return table;
		}
	}

	/**
	 * Changes the contents of the <b>Approved</b> and
	 * <b>Score</b> columns in the table.
	 */
	private void updateTableData() {
		for (int i = 0; i < articles.size(); i++) {
			Article article = articles.get(i);
			String score = String.valueOf(article.getScore(filterType));

			data[i][COL_APPROVE_ID] = article.apporved;
			data[i][COL_SCORE_ID] = score;
		}
	}

	/**
	 * Updates the table data and sends an event to refresh the
	 * table's GUI.
	 */
	private void updateTable() {
		updateTableData();

		TableModelEvent e = new TableModelEvent(articleTableModel);
		articleTable.tableChanged(e);
	}

	/** Refresh the table of articles with changed data. */
	private void refreshTable() {
		data = getTableData();
		updateTable();
	}

	/**
	 * Handles three types of action events:
	 * <ul>
	 * <li>trace: display trace message
	 * <li>status: display status of agent
	 * <li>addArticle: score article using current filter type
	 * </ul>
	 * @param e the event to be processed.
	 */
	@Override
	public void processAgentEvent(AgentEvent e) {
		//Object source = e.getSource();
		Object arg = e.getArgObject();
		Object action = e.getAction();

		if (action != null) {
			if (action.equals("trace")) {
				if ((arg != null) && (arg instanceof String)) {
					trace((String) arg);
				}
			} else if (action.equals("addArticle")) {
				// score the article sent by another agent
				addArticle((Article) arg);
			} else if (action.equals("status")) {
				filterAgentStatusLabel.setForeground(Color.BLACK);
				filterAgentStatusLabel.setText((String) arg);
			} else if (action.equals("displayMSG")) {
				filterAgentStatusLabel.setForeground(Color.BLUE);
				filterAgentStatusLabel.setText((String) arg);
			} else if (action.equals("displayERR")) {
				filterAgentStatusLabel.setForeground(Color.RED);
				filterAgentStatusLabel.setText((String) arg);
			}
		}
	}

	/**
	 * Just calls the {@link #processAgentEvent(AgentEvent)}
	 * method because the <code>InforFilterFrame</code> does not
	 * process asynchronous events.
	 * @param e the event to be processed.
	 */
	@Override
	public void postAgentEvent(AgentEvent e) {
		processAgentEvent(e);
	}

	/**
	 * Enables using clusters for filtering after the Kohonen map
	 * neural network was trained.
	 */
	public void clusterNetTrained() {
		useClustersCheckBoxMenuItem.setEnabled(true);
	}

	/**
	 * Enables using feedback for filtering after the back propagation
	 * neural network was trained.
	 */
	public void ratingNetTrained() {
		useFeedbackCheckBoxMenuItem.setEnabled(true);
	}

	/**
	 * Scores a single article downloaded from the Internet and
	 * adds it to the table.
	 * @param art the article downloaded from the Internet which
	 * will be scored and added to the table.
	 */
	protected void addArticle(Article art) {
		// add the article to the vector of all
		// downloaded and loaded articles
		articles.addElement(art);

		// score the article
		filterAgent.score(art, filterType);

		// update the GUI table
		refreshTable();

		// display article in editor pane, set cursor at beginning
		if (art.getType() == Article.FROM_WEB_PAGE) {
			articleEditorPane.setContentType("text/html");
			try {
				articleEditorPane.setPage(art.getID());
			} catch (Exception exception) {
				articleEditorPane.setText(art.getBody());
			}
		} else if (art.getType() == Article.FROM_HTML_FILE) {
			articleEditorPane.setContentType("text/html");
			articleEditorPane.setText(art.getBody());
		} else {
			articleEditorPane.setContentType("text/plain");
			articleEditorPane.setText(art.getBody());
		}

		articleEditorPane.setCaretPosition(0);

		// enable menu items so that user can add the article to
		// the profile if desired
		addArticleMenuItem.setEnabled(true);
		addAllMenuItem.setEnabled(true);
		saveArticleMenuItem.setEnabled(true);
	}

	/**
	 * Filters the set of articles by scoring and sorting them,
	 * then uses {@link #refreshTable()} method to update the
	 * GUI table again.
	 */
	public void filterArticles() {
		filterAgent.score(articles, filterType);

		articles = insertionSort(articles);

		refreshTable();

		if (articles.size() > 0) {
			currentArt = articles.firstElement();
		}
	}

	/**
	 * Sorts a set of articles in descending order with respect to
	 * their scores of the corresponding filter type.
	 * @param articles the set of articles to be sorted<br>
	 * <b>Note:</b> the order of elements of this vector will be
	 * preserved after the invocation of this method.
	 * @return a vector of articles sorted in descending order.
	 */
	Vector<Article> insertionSort(Vector<Article> articles) {
		int size = articles.size();
		Vector<Article> out = new Vector<>(articles);

		for (int i = 1; i < size; i++) {
			Article ai = out.get(i);

			for (int j = 0; j <= i - 1; j++) {
				Article aj = out.get(j);

				if (ai.getScore(filterType) > aj.getScore(filterType)) {
					out.remove(i);
					out.insertElementAt(ai, j);
					break;
				}
			}
		}

		return out;
	}

	/**
	 * Displays a message in the bottom text area.
	 * @param msg the message to be displayed.
	 */
	synchronized void trace(String msg) {
		// RECONSIDER
		// articleTextArea.append(msg + "\n");
		// articleEditorPane.setText(articleEditorPane.getText() + msg + "\n");
	}
	
	/**
	 * Displays message at {@link #taskProgessLabel} while
	 * performing a task.
	 * @param msg the message to be displayed.
	 */
	protected void displayTaskMSG(String msg) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					taskProgessLabel.setForeground(Color.BLACK);
					taskProgessLabel.setText(msg);
					taskProgessLabel.setToolTipText(msg);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Displays error message at {@link #taskProgessLabel} while
	 * performing a task.
	 * @param err the error message to be displayed.
	 */
	protected void displayTaskERR(String err) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					taskProgessLabel.setForeground(Color.RED);
					taskProgessLabel.setText(err);
					taskProgessLabel.setToolTipText(err);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extract icon for menu items in menu bar.
	 * @param fileName the name of the icon image file.
	 * @return <code>ImageIcon</code> for the image file.
	 */
	private ImageIcon getIcon(String fileName) {
		ImageIcon icon = new ImageIcon(
				getClass().getResource(fileName));
		icon = ServerClient.resizeIcon(icon, 16, 16);
		
		return icon;
	}
} // end class InfoFilterFrame