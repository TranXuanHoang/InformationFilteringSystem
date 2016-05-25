package infofilter;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.Customizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ciagent.CIAgentEvent;
import ciagent.CIAgentEventListener;

public class InfoFilterFrame extends JFrame implements CIAgentEventListener {
	/** Serial version. */
	private static final long serialVersionUID = 1L;
	
	JMenuBar menuBar1;
	JMenu menuFile;
	JMenu jMenu1;
	JMenu jMenu2;
	JMenu jMenu3;
	JMenu jMenu5;
	
	JMenuItem resetMenuItem;
	JMenuItem downloadURLMenuItem;
	JMenuItem saveArticleMenuItem;
	JMenuItem loadArticleMenuItem;
	JMenuItem exitMenuItem;
	
	JMenuItem keywordsMenuItem;
	JMenuItem addArticleMenuItem;
	JMenuItem addAllMenuItem;

	JMenuItem cutMenuItem;
	
	JMenuItem aboutMenuItem;
	
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JLabel jLabel1;
	JLabel filterAgentStatusLabel;
	JScrollPane jScrollPane3;
	JScrollPane jScrollPane1;
	JTextArea articleTextArea = new JTextArea();
	ButtonGroup useButtonGroup;
	FileDialog openFileDialog;
	FileDialog saveFileDialog;
	JTable articleTable;
	private TableModel articleTableModel = null; 
	JCheckBoxMenuItem useKeywordsCheckBoxMenuItem;
	JCheckBoxMenuItem useClustersCheckBoxMenuItem;
	JCheckBoxMenuItem useFeedbackCheckBoxMenuItem;
	
	String titleBarText = "CIAgent InfoFilter Application";
	
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
	protected FilterAgent filterAgent;
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
		menuFile = new JMenu("File");
		jMenu1 = new JMenu("Profile");
		jMenu2 = new JMenu("Edit");
		jMenu3 = new JMenu("Filter");
		jMenu5 = new JMenu("Help");
		menuBar1 = new JMenuBar();
		menuBar1.add(menuFile);
		menuBar1.add(jMenu2);
		menuBar1.add(jMenu1);
		menuBar1.add(jMenu3);
		menuBar1.add(jMenu5);
		
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
		
		jMenu1.add(keywordsMenuItem);
	    jMenu1.addSeparator();
	    jMenu1.add(addArticleMenuItem);
	    jMenu1.add(addAllMenuItem);
		
		cutMenuItem = new JMenuItem("Cut");
		cutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cutMenuItem_actionPerformed(e);
			}
		});
		
		jMenu2.add(cutMenuItem);
		
		useKeywordsCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Keywords");
		useKeywordsCheckBoxMenuItem.setSelected(true);
		useKeywordsCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useKeywordsCheckBoxMenuItem_actionPerformed(e);
			}
		});

		useFeedbackCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Feedback");
		useFeedbackCheckBoxMenuItem.setEnabled(
				filterAgent.isRatingNetTrained() ? true : false);
		useFeedbackCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useFeedbackCheckBoxMenuItem_actionPerformed(e);
			}
		});
		
		useClustersCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Clusters");
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
	    useButtonGroup.add(useClustersCheckBoxMenuItem);
	    useButtonGroup.add(useFeedbackCheckBoxMenuItem);
	    
	    jMenu3.add(useKeywordsCheckBoxMenuItem);
	    jMenu3.add(useClustersCheckBoxMenuItem);
	    jMenu3.add(useFeedbackCheckBoxMenuItem);
		
		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutMenuItem_actionPerformed(e);
			}
		});
		
		jMenu5.add(aboutMenuItem);
		
		jLabel1 = new JLabel("Articles");
		filterAgentStatusLabel = new JLabel("FilterAgent status:");
		
		jPanel1.setLayout(new GridLayout());
	    jPanel1.setMinimumSize(new Dimension(500, 200));
	    jPanel1.add(jLabel1);
	    jPanel1.add(filterAgentStatusLabel);
	    
	    jScrollPane1 = new JScrollPane();
	    setUpTheTable();
	    articleTable.setPreferredSize(new Dimension(500, 300));
	    jScrollPane1.getViewport().add(articleTable);
	    jPanel2.setLayout(new GridLayout());
	    jPanel2.setPreferredSize(new Dimension(500, 100));
	    jPanel2.add(jScrollPane1);
	    
	    jScrollPane3 = new JScrollPane();
		jScrollPane3.setPreferredSize(new Dimension(500, 200));
	    jScrollPane3.getViewport().add(articleTextArea);
	    
	    setJMenuBar(menuBar1);
		setLayout(new BorderLayout());
		add(jPanel1, BorderLayout.NORTH);
		add(jPanel2, BorderLayout.CENTER);
		add(jScrollPane3, BorderLayout.SOUTH);
		setSize(500, 395);
		setTitle(titleBarText + " - Using Keywords");
	}


	protected void cutMenuItem_actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	protected void keywordsMenuItem_actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	protected void resetMenuItem_actionPerformed(ActionEvent e) {
		System.out.println("Reset action is requested");
		articles.clear();
		refreshTable();
		articleTextArea.setText("");
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
		openFileDialog = new FileDialog(this, "Open", FileDialog.LOAD);
		openFileDialog.setVisible(true);
		
		String directory = openFileDialog.getDirectory();
		String fileName = openFileDialog.getFile();
		
		if (fileName != null) {
			NewsArticle article = new NewsArticle(directory + fileName);
			
			article.readArticle(fileName, directory);
			articles.addElement(article);
			filterAgent.score(article, filterType);
			
			refreshTable();
			articleTextArea.setText(article.getBody());
			articleTextArea.setCaretPosition(0);
		}
	}

	/**
	 * Saves an article to a personal computer for later uses.
	 * @param e the event generated when the <b>Save Article...</b>
	 * menu item is selected.
	 */
	protected void saveArticleMenuItem_actionPerformed(ActionEvent e) {
		saveFileDialog = new FileDialog(this, "Save", FileDialog.SAVE);
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

	protected void useKeywordsCheckBoxMenuItem_actionPerformed(ActionEvent e) {
		filterType = FilterAgent.USE_KEYWORDS;
		filterArticles();
		this.setTitle(titleBarText + " - Using Keywords");
	}
	
	protected void useFeedbackCheckBoxMenuItem_actionPerformed(ActionEvent e) {
		filterType = FilterAgent.USE_PREDICTED_RATING;
		filterArticles();
		this.setTitle(titleBarText + " - Using Backpropagation Neural Network");
	}

	protected void useClustersCheckBoxMenuItem_actionPerformed(ActionEvent e) {
		filterType = FilterAgent.USE_CLUSTERS;
		filterArticles();
		this.setTitle(titleBarText + " - Using Kohonen Map Neural Network");
	}

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
						articleTextArea.setText(currentArt.body);
						
						// move cursor to the beginning of body
						articleTextArea.setCaretPosition(0);
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
	 * Scores a single article and adds it to the table.
	 * @param art the article to be scored and added.
	 */
	protected void addArticle(NewsArticle art) {
		// add the article to the vector of all downloaded articles
		articles.addElement(art);
		
		// score the article
		filterAgent.score(art, filterType);
		
		// update the GUI table
		refreshTable();
		
		// display article in the text area, set cursor at beginning
		articleTextArea.setText(art.getBody());
		articleTextArea.setCaretPosition(0);
		
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
		articleTextArea.append(msg + "\n");
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