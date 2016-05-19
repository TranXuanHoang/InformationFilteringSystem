package infofilter;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
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
	protected Vector<NewsArticle> articles = new Vector<>();
	protected FilterAgent filterAgent = new FilterAgent();
	protected URLReaderAgent urlReaderAgent = new URLReaderAgent();
	
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
			initializeGUI();
			initializeUnderlyingData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initializeUnderlyingData() {
		//TODO
	}
	
	private void initializeGUI() throws Exception {
		menuBar1 = new JMenuBar();
		menuBar1.add(menuFile);
		menuBar1.add(jMenu1);
		menuBar1.add(jMenu2);
		menuBar1.add(jMenu3);
		menuBar1.add(jMenu5);
		menuFile = new JMenu("File");
		jMenu1 = new JMenu("Profile");
		jMenu2 = new JMenu("Edit");
		jMenu3 = new JMenu("Filter");
		jMenu5 = new JMenu("Help");
		
		resetMenuItem = new JMenuItem("Clear All");
		resetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		downloadURLMenuItem = new JMenuItem("Dowload URL...");
		downloadURLMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		saveArticleMenuItem = new JMenuItem("Save Article...");
		saveArticleMenuItem.setEnabled(false);
		saveArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		loadArticleMenuItem = new JMenuItem("Load Article...");
		loadArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		menuFile.add(resetMenuItem);
	    menuFile.addSeparator();
	    menuFile.add(downloadURLMenuItem);
	    menuFile.addSeparator();
	    menuFile.add(saveArticleMenuItem);
	    menuFile.add(loadArticleMenuItem);
	    menuFile.addSeparator();
	    menuFile.add(exitMenuItem);
		
		keywordsMenuItem = new JMenuItem("Customize...");
		keywordsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addArticleMenuItem = new JMenuItem("Add Article");
		addArticleMenuItem.setEnabled(false);
		addArticleMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addAllMenuItem = new JMenuItem("Add All Articles");
		addAllMenuItem.setEnabled(false);
		addAllMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
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
				// TODO Auto-generated method stub
				
			}
		});
		
		jMenu2.add(cutMenuItem);
		
		useKeywordsCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Keywords");
		useKeywordsCheckBoxMenuItem.setSelected(true);
		useKeywordsCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		useFeedbackCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Feedback");
		useFeedbackCheckBoxMenuItem.setEnabled(
				filterAgent.isRatingNetTrained() ? true : false);
		useFeedbackCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		useClustersCheckBoxMenuItem =
				new JCheckBoxMenuItem("Using Clusters");
		useClustersCheckBoxMenuItem.setEnabled(
				filterAgent.isClusterNetTrained() ? true : false);
		useClustersCheckBoxMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
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
				// TODO Auto-generated method stub
				
			}
		});
		
		jMenu5.add(aboutMenuItem);
		
		jLabel1 = new JLabel("Articles");
		filterAgentStatusLabel = new JLabel("FilterAgent status:");
		
		articleTable.setPreferredSize(new Dimension(500, 300));
		
		jPanel1.setLayout(new GridLayout());
	    jPanel1.setMinimumSize(new Dimension(500, 200));
	    jPanel1.add(jLabel1);
	    jPanel1.add(filterAgentStatusLabel);
	    
	    jScrollPane1 = new JScrollPane();
	    setUpTheTable();
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

	@Override
	public void processCIAgentEvent(CIAgentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postCIAgentEvent(CIAgentEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Enables using clusters for filtering after the Kohonen map
	 * neural network was trained.
	 */
	public void clusterNetTrained() {
		//TODO
	}
	
	/**
	 * Enables using feedback for filtering after the back propagation
	 * neural network was trained.
	 */
	public void ratingNetTrained() {
		//TODO
	}
} // end class InfoFilterFrame