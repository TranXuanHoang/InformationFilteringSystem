package infofilter;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.Customizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ciagent.CIAgentEvent;
import ciagent.CIAgentEventListener;

public class InfoFilterFrame extends JFrame implements CIAgentEventListener {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	JMenuBar menuBar;
	JMenu menuFile;
	JMenu menuEdit;
	JMenu menuProfile;
	JMenu menuFilter;
	JMenu menuHelp;

	JMenuItem resetMenuItem;
	JMenuItem downloadURLMenuItem;
	JMenuItem loadArticleMenuItem;
	JMenuItem saveArticleMenuItem;
	JMenuItem exitMenuItem;

	JMenuItem deleteMenuItem;

	JMenuItem keywordsMenuItem;
	JMenuItem addArticleMenuItem;
	JMenuItem addAllMenuItem;

	JCheckBoxMenuItem useKeywordsCheckBoxMenuItem;
	JCheckBoxMenuItem useClustersCheckBoxMenuItem;
	JCheckBoxMenuItem useFeedbackCheckBoxMenuItem;
	ButtonGroup useButtonGroup;

	JMenuItem aboutMenuItem;

	JSplitPane splitPane;
	JScrollPane jScrollPane1;
	JScrollPane jScrollPane2;
	private TableModel articleTableModel;
	JTable articleTable;
	JEditorPane articleEditorPane;

	JPanel jPanel1;
	JLabel filterAgentStatusLabel;

	String titleBarText = "Information Filtering Application";

	protected static final int NUM_COLS = 3;
	protected static final int COL_SUBJECT_ID = 0;
	protected static final int COL_SCORE_ID = 1;
	protected static final int COL_RATING_ID = 2;
	private static final String COL_SUBJECT = "Subject";
	private static final String COL_SCORE = "Score";
	private static final String COL_RATING = "Rating";
	protected String[] columnNameList =
		{COL_SUBJECT, COL_SCORE, COL_RATING};

	protected Object[][] data;

	/** List of downloaded articles. */
	protected Vector<NewsArticle> articles;

	/** The agent that filters articles. */
	protected FilterAgent filterAgent;

	/** The agent that allows user to download article. */
	protected URLReaderAgent urlReaderAgent;

	/** Currently selected article. */
	NewsArticle currentArt;
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
		// check if a serialized FilterAgent exists
		try {
			FilterAgent tmpFilterAgent = FilterAgent.restoreFromFile(
					FilterAgent.fileName);

			if (tmpFilterAgent != null) {
				filterAgent = tmpFilterAgent;
			}
		} catch (Exception e) {
			// no error, just catch any exception
		}

		articles = new Vector<>();

		filterAgent = new FilterAgent();
		filterAgent.infoFilter = this;
		filterAgent.addCIAgentEventListener(this); // for trace msgs
		filterAgent.initialize();
		filterAgent.startAgentProcessing(); // start filter agent thread

		urlReaderAgent = new URLReaderAgent();
		urlReaderAgent.addCIAgentEventListener(this);
		urlReaderAgent.initialize();
		urlReaderAgent.startAgentProcessing(); // start it running
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
		menuProfile = new JMenu("Profile");
		menuFilter = new JMenu("Filter");
		menuHelp = new JMenu("Help");
		menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuProfile);
		menuBar.add(menuFilter);
		menuBar.add(menuHelp);

		resetMenuItem = new JMenuItem("Clear All");
		resetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMenuItem_actionPerformed(e);
			}
		});

		downloadURLMenuItem = new JMenuItem("Dowload URL...");
		downloadURLMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				downloadURLMenuItem_actionPerformed(e);
			}
		});

		loadArticleMenuItem = new JMenuItem("Load Article...");
		loadArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadArticleMenuItem_actionPerformed(e);
			}
		});

		saveArticleMenuItem = new JMenuItem("Save Article...");
		saveArticleMenuItem.setEnabled(false);
		saveArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveArticleMenuItem_actionPerformed(e);
			}
		});

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menuFile.add(resetMenuItem);
		menuFile.addSeparator();
		menuFile.add(downloadURLMenuItem);
		menuFile.add(loadArticleMenuItem);
		menuFile.add(saveArticleMenuItem);
		menuFile.addSeparator();
		menuFile.add(exitMenuItem);

		deleteMenuItem = new JMenuItem("Delete");
		deleteMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteMenuItem_actionPerformed(e);
			}
		});

		menuEdit.add(deleteMenuItem);

		keywordsMenuItem = new JMenuItem("Customize...");
		keywordsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keywordsMenuItem_actionPerformed(e);
			}
		});

		addArticleMenuItem = new JMenuItem("Add Article");
		addArticleMenuItem.setEnabled(false);
		addArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addArticleMenuItem_actionPerformed(e);
			}
		});

		addAllMenuItem = new JMenuItem("Add All Articles");
		addAllMenuItem.setEnabled(false);
		addAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addAllMenuItem_actionPerformed(e);
			}
		});

		menuProfile.add(keywordsMenuItem);
		menuProfile.addSeparator();
		menuProfile.add(addArticleMenuItem);
		menuProfile.add(addAllMenuItem);

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
				filterAgent.isRatingNetTrained() ? true : false);
		useFeedbackCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useFeedbackCheckBoxMenuItem_actionPerformed(e);
			}
		});

		useClustersCheckBoxMenuItem = new JCheckBoxMenuItem(
				"Using Kohonen Map Neural Network");
		useClustersCheckBoxMenuItem.setEnabled(
				filterAgent.isClusterNetTrained() ? true : false);
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

		menuFilter.add(useKeywordsCheckBoxMenuItem);
		menuFilter.add(useFeedbackCheckBoxMenuItem);
		menuFilter.add(useClustersCheckBoxMenuItem);

		aboutMenuItem = new JMenuItem("About");
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

		filterAgentStatusLabel = new JLabel("FilterAgent status:");
		filterAgentStatusLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
		jPanel1 = new JPanel(new GridLayout());
		jPanel1.setBorder(new EmptyBorder(10, 10, 10, 10));
		jPanel1.add(filterAgentStatusLabel);

		setTitle(titleBarText + " - Using Keywords");
		setJMenuBar(menuBar);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
		add(jPanel1, BorderLayout.SOUTH);
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
		articleEditorPane.setText("<html></html>");

		addArticleMenuItem.setEnabled(false);
		addAllMenuItem.setEnabled(false);
		saveArticleMenuItem.setEnabled(false);
	}

	/**
	 * Opens the <code>URLReaderAgent</code> customizer and allow
	 * the user to download articles from web pages.
	 * @param e the event generated when the <b>Download URL...</b>
	 * menu item is selected.
	 */
	protected void downloadURLMenuItem_actionPerformed(ActionEvent e) {
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
			int type = NewsArticle.typeOfFile(fileName);
			NewsArticle article = new NewsArticle(filePath, type);

			switch (type) {
			case NewsArticle.FROM_TEXT_FILE:
				String text = NewsArticle.readArticle(filePath);
				article.setBody(text);
				article.setSubject(fileName, type);
				break;

			case NewsArticle.FROM_PDF_FILE:
				String pdf = Utilities.getContentsOfPDFFile(filePath);
				article.setBody(pdf);
				article.setSubject(fileName, type);
				break;

			case NewsArticle.FROM_MS_WORD_FILE:
				String msword = Utilities.getContentsOfWordFile(filePath);
				article.setBody(msword);
				article.setSubject(fileName, type);
				break;

			case NewsArticle.FROM_HTML_FILE:
				String html = NewsArticle.readArticle(filePath);
				article.setBody(html);
				article.setSubject(fileName, type);
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
				NewsArticle article = articles.elementAt(index);
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

		articleEditorPane.setText("<html></html>");
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
		// get data from the set of downloaded articles
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
				case COL_SCORE_ID:
					break;
				case COL_RATING_ID:
					String userRating = ((String) value).trim();
					data[row][col] = userRating;

					if (currentArt != null) {
						currentArt.setUserRating(userRating);
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
		articleTable.getColumn(COL_SUBJECT).setPreferredWidth(200);
		articleTable.getColumn(COL_SCORE).setPreferredWidth(30);
		articleTable.getColumn(COL_RATING).setPreferredWidth(30);
		articleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// handle the table's row selection events
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

						articleEditorPane.setText(currentArt.getBody());
						articleEditorPane.setCaretPosition(0);
					} else {
						currentArt = null;
					}
				}
			}
		}); // end handle the table's row selection events

		JComboBox<String> userRatings =
				new JComboBox<>(FilterAgent.RATINGS);
		articleTable.getColumnModel().getColumn(COL_RATING_ID).
		setCellEditor(new DefaultCellEditor(userRatings));
		articleTable.setCellSelectionEnabled(true);
	}

	/**
	 * Retrieves the subjects, scores and user ratings of downloaded
	 * articles.
	 * @return an array of downloaded articles' information
	 * including subjects, scores and ratings.
	 */
	private Object[][] getTableData() {
		Object[][] table;

		if (articles == null) {
			return null;
		}

		if (articles.size() == 0) {
			table = new Object[1][NUM_COLS];

			table[0][0] = "";
			table[0][1] = "";
			table[0][2] = "";

			return table;
		} else {
			table = new Object[articles.size()][NUM_COLS];

			for (int i = 0; i < articles.size(); i++) {
				NewsArticle article = articles.elementAt(i);

				table[i][0] = article.getSubject();
				table[i][1] = String.valueOf(article.getScore(filterType));
				table[i][2] = article.getUserRating();
			}

			return table;
		}
	}

	/**
	 * Changes the contents of the <b>Score</b> column in the table.
	 */
	private void updateTableData() {
		for (int i = 0; i < articles.size(); i++) {
			NewsArticle article = articles.get(i);
			String score = String.valueOf(article.getScore(filterType));

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
	public void processCIAgentEvent(CIAgentEvent e) {
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
				addArticle((NewsArticle) arg);
			} else if (action.equals("status")) {
				filterAgentStatusLabel.setText(
						"FilterAgent Status: " + (String) arg);
			}
		}
	}

	/**
	 * Just calls the {@link #processCIAgentEvent(CIAgentEvent)}
	 * method because the <code>InforFilterFrame</code> does not
	 * process asynchronous events.
	 * @param e the event to be processed.
	 */
	@Override
	public void postCIAgentEvent(CIAgentEvent e) {
		processCIAgentEvent(e);
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
	protected void addArticle(NewsArticle art) {
		// add the article to the vector of all downloaded articles
		articles.addElement(art);

		// score the article
		filterAgent.score(art, filterType);

		// update the GUI table
		refreshTable();

		// display article in editor pane, set cursor at beginning
		articleEditorPane.setText(art.getBody());
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
	Vector<NewsArticle> insertionSort(Vector<NewsArticle> articles) {
		int size = articles.size();
		Vector<NewsArticle> out = new Vector<>(articles);

		for (int i = 1; i < size; i++) {
			NewsArticle ai = out.get(i);

			for (int j = 0; j <= i - 1; j++) {
				NewsArticle aj = out.get(j);

				if (ai.getScore(filterType) > aj.getScore(filterType)) {
					out.remove(i);
					out.insertElementAt(aj, j);
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
	 * Processes window events and is overridden to exit when
	 * window closes.
	 * @param e the WindowEvent to be processed.
	 */
	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		} else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
			e.getWindow().repaint();
		}
	}
} // end class InfoFilterFrame