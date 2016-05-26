package learn;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * This is the frame for the application which demonstrates three
 * learning algorithms: back propagation neural network, Kohonen
 * map neural network and decision tree.
 * 
 * @author Tran Xuan Hoang
 */
public class LearnFrame extends JFrame implements Runnable {
	/**
	 * The serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenu dataMenu = new JMenu("Data");
	JMenu algorithmMenu = new JMenu("Algorithm");
	JMenu helpMenu = new JMenu("Help");
	JMenuItem startMenuItem = new JMenuItem("Start");
	JMenuItem resetMenuItem = new JMenuItem("Reset");
	JMenuItem exitMenuItem = new JMenuItem("Exit");
	JMenuItem loadDataMenuItem = new JMenuItem("Load...");
	JMenuItem aboutMenuItem = new JMenuItem("About");
	JRadioButtonMenuItem backPropRadioButtonMenuItem =
			new JRadioButtonMenuItem("Back Propagation");
	JRadioButtonMenuItem kohonenRadioButtonMenuItem =
			new JRadioButtonMenuItem("Kohonen Map");
	JRadioButtonMenuItem decisionTreeRadioButtonMenuItem =
			new JRadioButtonMenuItem("Decision Tree");

	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();

	JScrollPane jScrollPane1 = new JScrollPane();
	JScrollPane jScrollPane2 = new JScrollPane();

	JTextArea dataTextArea = new JTextArea();
	JTextArea traceTextArea = new JTextArea();

	//JLabel jLabel1 = new JLabel("Data Set:"); // Data Set label
	JLabel dataSetFileNameLabel = new JLabel();

	private volatile Thread runnit;
	private boolean exitThread = false; // signal to abort training thread
	DataSet dataSet = null; // current data set

	/**
	 * Constructs the frame for the learn application.
	 */
	public LearnFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try {
			initializeGUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the UI components.
	 * @throws Exception if any errors occur.
	 */
	private void initializeGUI() throws Exception {
		this.setTitle("Learn Application - Back Propagation");
		this.setSize(600, 479);
		this.getContentPane().setLayout(new BorderLayout());

		fileMenu.add(startMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(resetMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		startMenuItem.setEnabled(false); // off until data set loaded

		dataMenu.add(loadDataMenuItem);

		algorithmMenu.add(backPropRadioButtonMenuItem);
		algorithmMenu.add(kohonenRadioButtonMenuItem);
		algorithmMenu.add(decisionTreeRadioButtonMenuItem);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(backPropRadioButtonMenuItem);
		buttonGroup.add(kohonenRadioButtonMenuItem);
		buttonGroup.add(decisionTreeRadioButtonMenuItem);
		backPropRadioButtonMenuItem.setSelected(true);

		helpMenu.add(aboutMenuItem);

		menuBar.add(fileMenu);
		menuBar.add(dataMenu);
		menuBar.add(algorithmMenu);
		menuBar.add(helpMenu);
		this.setJMenuBar(menuBar);

		jPanel1.add(new JLabel("Data Set:"));
		jPanel1.add(dataSetFileNameLabel);
		dataSetFileNameLabel.setText("<none>");
		jPanel1.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.getContentPane().add(jPanel1, BorderLayout.NORTH);

		jScrollPane1.getViewport().add(dataTextArea);
		jScrollPane1.setBorder(BorderFactory.createTitledBorder("Data Set"));
		jScrollPane2.getViewport().add(traceTextArea);
		jScrollPane2.setBorder(BorderFactory.createTitledBorder("Training Information"));
		jPanel2.setLayout(new GridLayout(2, 1));
		jPanel2.add(jScrollPane1);
		jPanel2.add(jScrollPane2);
		this.getContentPane().add(jPanel2, BorderLayout.CENTER);

		startMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startMenuItem_actionPerformed(e);
			}
		});
		resetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetMenuItem_actionPerformed(e);
			}
		});
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		loadDataMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDataMenuItem_actionPerformed(e);
			}
		});
		backPropRadioButtonMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backPropRadioButtonMenuItem_actionPerformed(e);
			}
		});
		kohonenRadioButtonMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				kohonenRadioButtonMenuItem_actionPerformed(e);
			}
		});
		decisionTreeRadioButtonMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				decisionTreeRadioButtonMenuItem_actionPerformed(e);
			}
		});
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutMenuItem_actionPerformed(e);
			}
		});
	}

	/**
	 * Processes window events and is overridden to exit when
	 * window closes.
	 * @param e the WindowEvent to be processed.
	 */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		} else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
			e.getWindow().repaint();
		}
	}

	/**
	 * Tests a back prop network using the given dataset and text
	 * area.
	 * Note: this method is run on a separate thread from the GUI.
	 * @param dataset the DataSet used to test the network.
	 * @param bottomText  the JTextArea used to display information.
	 */
	public void testBackProp(DataSet dataset, JTextArea bottomText) {
		BackProp testNet = new BackProp("Test Back Prop Network");

		bottomText.append("Training Back Propagation Network...");
		testNet.textArea = bottomText;
		testNet.setDataSet(dataset);
		testNet.setNumRecs(dataset.numRecords);
		testNet.setFieldsPerRec(dataset.normFieldsPerRec);
		testNet.setData(dataset.normalizedData);

		int numOutputs = dataset.getClassFieldSize();
		int numInputs = testNet.getFieldsPerRec() - numOutputs;

		testNet.createNetwork(numInputs, numInputs, numOutputs);
		bottomText.append("\nNetwork architecture = " + numInputs +
				"-" + numInputs + "-" + numOutputs);
		bottomText.append("\nLearn rate = " + testNet.getLearnRate() +
				",  Momentum = " + testNet.getMomentum());
		bottomText.append("\n\n Each '*' indicates 100 passes "
				+ "over training data\n");

		int maxNumpasses = 2500; // default
		int numRecs = testNet.getNumRecs();
		int numPasses = 0;

		for (numPasses = 0; numPasses < maxNumpasses; numPasses++) {
			for (int j = 0; j < numRecs; j++) {
				testNet.process(); // train
			}

			try {
				Thread.sleep(10); // give up the processor to GUI
			} catch (InterruptedException e) {}

			if ((numPasses % 100) == 0) {
				bottomText.append("*");
			}

			if (exitThread) {
				testNet.textArea.append("\n\nUser pressed Reset"
						+ " ... training halted!\n\n");
				break; // exit the loop
			}
		}

		testNet.textArea.append("\n  Passes Completed: " + numPasses +
				"\tRMS Error = " + testNet.getAveRMSError() + "\n");
		testNet.setMode(1); // lock the network

		// do a final pass and display the results
		for (int i = 0; i < testNet.getNumRecs(); i++) {
			testNet.process();
			testNet.display_network();
		}
	}

	/**
	 * Tests a Kohonen map network using the given dataset and text
	 * area.
	 * Note: this method is run on a separate thread from the GUI.
	 * @param dataset the DataSet used to test the network.
	 * @param bottomText  the JTextArea used to display information.
	 */
	public void testKMapNet(DataSet dataset, JTextArea bottomText) {
		KMapNet testNet = new KMapNet("Test Kohonen Map Network");

		bottomText.append("Training Kohonen Map Network...");
		bottomText.append("\nEach '*' indicates 1 pass over "
				+ "training data.\n");
		testNet.textArea = bottomText;
		testNet.setDataSet(dataset);
		testNet.setNumRecs(dataset.numRecords);
		testNet.setFieldsPerRec(dataset.fieldsPerRec);
		testNet.setData(dataset.normalizedData); // get vector of data

		// create network, all fields are inputs
		testNet.createNetwork(testNet.getFieldsPerRec(), 4, 4); // default net arch
		int maxNumPasses = 20; // default
		int numRecs = testNet.getNumRecs();
		int numPasses = 0;

		// train the network
		for (numPasses = 0; numPasses < maxNumPasses; numPasses++) {
			for (int j = 0; j < numRecs; j++) {
				testNet.cluster(); // train
			}

			try {
				Thread.sleep(10); // give up the processor to GUI
			} catch (InterruptedException e) {}

			bottomText.append("*");

			if (exitThread) {
				testNet.textArea.append("\n\nUser pressed Reset "
						+ "... training halted!\n\n");
				break; // exit the loop
			}
		}

		testNet.textArea.append(
				"\n  Passes Completed: " + numPasses + "\n");
		testNet.setMode(1); // lock the network weights

		for (int i = 0; i < testNet.getNumRecs(); i++) {
			testNet.cluster();
			testNet.display_network();
		}
	}

	/**
	 * Tests a decision tree using the given data set and text area.
	 * Note: this method is run on a separate thread from the GUI.
	 * @param dataset the DataSet used to train the network.
	 * @param dataSet the DataSet.
	 * @param bottomText  the JTextArea used to display information.
	 */
	public void testDecisionTree(DataSet dataSet, JTextArea bottomText) {
		DecisionTree tree = new DecisionTree("Test Decision Tree");

		tree.textArea.append("Starting Decision Tree...");
		tree.textArea = bottomText;
		tree.ds = dataSet;
		tree.examples = dataSet.data; // get vector of data
		tree.variableList = dataSet.variableList;

		// test that data set contains all categorical fields
		boolean allCategorical = true;
		Enumeration<Variable> vars = tree.variableList.elements();

		while (vars.hasMoreElements()) {
			Variable var = (Variable) vars.nextElement();

			if (!var.isCategorical()) {
				allCategorical = false;
				break;
			}
		}

		if (!allCategorical) {
			tree.textArea.append("\nDecision Tree cannot process "
					+ "continuous data\n");
			tree.textArea.append(
					"\nPlease select a different data set\n");
			return;
		}

		tree.classVar = tree.variableList.get("ClassField");
		tree.variableList.remove("ClassField");

		// recursively build tree
		Node root = tree.buildDecisionTree(tree.examples,
				tree.variableList,
				new Node("default"));

		// now display the results
		tree.textArea.append("\n\nDecisionTree -- classVar = " +
				tree.classVar.name);
		tree.displayTree(root, "  ");
		tree.textArea.append("\nStopping DecisionTree - success!");
	}

	/**
	 * Starts a thread when START is selected.
	 * @param e the ActionEvent for the selection.
	 */
	void startMenuItem_actionPerformed(ActionEvent e) {
		runnit = new Thread(this);
		exitThread = false;

		// Causes this thread to begin execution,
		// the JVM calls the run method of this thread.
		runnit.start();
	}

	/**
	 * Clears the text area and sets a boolean flag to exit
	 * training thread.
	 * @param e the ActionEvent that was generated when Reset was
	 * selected.
	 */
	void resetMenuItem_actionPerformed(ActionEvent e) {
		traceTextArea.setText("");
		exitThread = true; // signal training thread to halt
	}

	/**
	 * Gets the data set filename and loads the data set.
	 * @param e the ActionEvent that was generated.
	 */
	void loadDataMenuItem_actionPerformed(ActionEvent e) {
		FileDialog dlg = new FileDialog(this,
				"Load Data Set", FileDialog.LOAD);

		dlg.setFile("*.dfn");
		dlg.setVisible(true);
		String dirName = dlg.getDirectory();
		String fileName = dlg.getFile();

		if (fileName != null) {
			dataTextArea.setText("");

			dataSet = new DataSet("ds", dirName + fileName);
			dataSet.setDisplay(dataTextArea);
			dataSet.loadDataFile(); // load the data set from the data file

			dataSetFileNameLabel.setText(dirName + fileName);
			startMenuItem.setEnabled(true);
		}

		this.repaint();
	}

	/**
	 * Sets the title on the frame when the back prop radio button
	 * is clicked.
	 * @param e the ActionEvent that was generated.
	 */
	void backPropRadioButtonMenuItem_actionPerformed(ActionEvent e) {
		setTitle("Learn Application - Back Propagation");
	}

	/**
	 * Sets the title on the frame when the Kohnen map radio
	 * button is clicked.
	 * @param e the ActionEvent that was generated.
	 */
	void kohonenRadioButtonMenuItem_actionPerformed(ActionEvent e) {
		setTitle("Learn Application - Kohonen Map");
	}

	/**
	 * Sets the title on the frame when the decision tree radio
	 * button is clicked.
	 * @param e the ActionEvent that was generated.
	 */
	void decisionTreeRadioButtonMenuItem_actionPerformed(ActionEvent e) {
		setTitle("Learn Application - Decision Tree");
	}

	/**
	 * Displays the About dialog.
	 * @param e the ActionEvent generated when About was selected.
	 */
	void aboutMenuItem_actionPerformed(ActionEvent e) {
		AboutDialog dlg = new AboutDialog(this,
				"About Learn Application", true);
		Point loc = this.getLocation();

		dlg.setLocation(loc.x + 50, loc.y + 50);
		dlg.setVisible(true);
	}

	/**
	 * Runs the selected algorithm in a separate thread, writing
	 * some trace information into the the text area of the
	 * application window. If debug is set on, additional trace
	 * information will be displayed.
	 */
	@Override
	public void run() {
		traceTextArea.setText("");

		if (backPropRadioButtonMenuItem.isSelected()) {
			testBackProp(dataSet, traceTextArea);
		} else if (kohonenRadioButtonMenuItem.isSelected()) {
			testKMapNet(dataSet, traceTextArea);
		} else if (decisionTreeRadioButtonMenuItem.isSelected()) {
			testDecisionTree(dataSet, traceTextArea);
		}
	}
} // end class LearnFrame