package learn;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.JTextArea;

/**
 * The <code>KMapNet</code> class provides the support necessary
 * for the <b>Kohonen Map</b> neural network.
 * 
 * @author Tran Xuan Hoang
 */
public class KMapNet implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	// data parameters
	protected String name;
	private DataSet dataset;
	private Vector<double[]> data;
	private int recInx = 0;         // current record index
	private int numRecs = 0;        // number of records in data
	private int fieldsPerRec;
	private int numPasses = 0;

	// network architecture parameters
	private int numInputs;
	private int numRows;
	private int numCols;
	private int numOutputs;
	private int numUnits;

	// network control parameters
	private int mode;				// 0 = train, 1 = run
	private double learnRate;		// the current learn rate
	private double initLearnRate = 1.0;
	private double finalLearnRate = 0.05;
	private double sigma; 			// used in the computation of neighborhood function
	private int maxNumPasses = 1000;  // default

	// network data
	private int winner;             // index of the winning unit
	private double activations[];
	private double weights[];
	private int distance[];         // used in neighborhood computation
	transient public  JTextArea textArea;

	/**
	 * Creates a Kohonen map neural network with the given name.
	 * @param name the name of the network.
	 */
	public KMapNet(String name) {
		this.name = name;
		setData(new Vector<>());
	}

	/**
	 * Displays an array of network data.
	 * @param name the name of the information being displayed.
	 * @param arr the array to be displayed.
	 */
	public void show_array(String name, double[] arr) {
		if (textArea != null) {
			textArea.append("\n" + name + " = ");
			for (int i = 0; i < arr.length; i++) {
				textArea.append(arr[i] + "  ");
			}
		}
	}

	/**
	 * Displays the relevant information for the network.
	 */
	public void display_network() {
		// show_array("weights", weights);
		show_array("activations", activations);

		if (textArea != null) {
			textArea.append("\nWinner = " + winner + "\n");
		}
	}

	/**
	 * Reads data from the training or test data set into the
	 * activations of the input units.
	 */
	public void readInputs() {
		recInx = recInx % numRecs; // keep index from 0 to n-1 records
		double[] tempRec = data.elementAt(recInx); // get record
		int inx = 0;

		for (inx = 0; inx < numInputs; inx++) {
			activations[inx] = tempRec[inx];
		}

		if (recInx == 0) {
			numPasses++; // completed another pass through the data
			adjustNeighborhood();
		}

		recInx++;
	}

	/**
	 * Initializes a matrix of distances from each unit to all
	 * other in the network.
	 */
	public void computeDistances() {
		distance = new int[numOutputs * numOutputs];

		for (int i = 0; i < numOutputs; i++) {
			int xi = i % numCols;
			int yi = i / numRows;

			for (int j = 0; j < numOutputs; j++) {
				int xj = j % numCols;
				int yj = j / numCols;

				distance[i * numOutputs + j] =
						(int) Math.pow(xi - xj, 4.0) +
						(int) Math.pow(yi - yj, 4.0);
			}
		}
	}

	/**
	 * Adjusts the learn rate and neighborhood width (sigma) as
	 * training progresses.
	 */
	public void adjustNeighborhood() {
		double ratio = (double) numPasses / maxNumPasses;

		learnRate = initLearnRate *
				Math.pow(finalLearnRate / initLearnRate, ratio);
		sigma = (double) numCols * Math.pow(0.20 / numCols, ratio);
	}

	/**
	 * Computes the outputs by doing a single forward pass through
	 * the network, computing the Euclidean distance from the
	 * input vector to all the output units.
	 */
	public void computeOutputs() {
		int lastOut = numUnits - 1;
		int firstOut = numInputs;

		// first layer
		for (int i = firstOut; i <= lastOut; i++) {
			int index = (i - firstOut) * numInputs;
			activations[i] = 0.0;

			for (int j = 0; j < numInputs; j++) {
				// compute net inputs
				activations[i] +=
						(activations[j] - weights[index + j]) *
						(activations[j] - weights[index + j]);
			}
		}
	}

	/**
	 * Select the winner by finding the unit with the smallest
	 * activation.
	 */
	public void selectWinner() {
		winner = 0;
		double min = activations[numInputs];

		for (int i = 0; i < numOutputs; i++) {
			if (activations[i + numInputs] < min) {
				min = activations[i + numInputs];
				winner = i;
			}
		}
	}

	/**
	 * Adjusts the weights of the units in the neighborhood of the
	 * winner using the distance from the winning unit, and the
	 * learn rate.
	 */
	public void adjustWeights() {
		int numOutputs = numRows * numCols;
		double sigma_squared = sigma * sigma;
		double dist, range;
		int inx, base;

		for (int i = 0; i < numOutputs; i++) {
			dist = Math.exp(
					(distance[winner * numOutputs + i] * -1.0) /
					(2.0 * sigma_squared));
			base = i * numInputs;  // compute the base index
			range = learnRate * dist;

			for (int j = 0; j < numInputs; j++) {
				inx = base + j;
				weights[inx] += range * (activations[j] - weights[inx]);
			}
		}
	}

	/**
	 * Selects a winner from a single input record.
	 * @param inputRec the double array which contains the inputs.
	 * @return the winner (cluster number).
	 */
	public int getCluster(double[] inputRec) {
		// set input unit activations
		for (int inx = 0; inx < numInputs; inx++) {
			activations[inx] = inputRec[inx];
		}

		computeOutputs();
		selectWinner();

		return winner;
	}

	/**
	 * Trains or tests the network by testing the input unit
	 * activations, computing the outputs, selecting the winner,
	 * and if in train mode, adjusting the weights.
	 */
	public void cluster() {
		readInputs();      // set input unit activations
		computeOutputs();  // do forward pass through network
		selectWinner();    // find the winning unit

		// only adjust if in training mode
		if (mode == 0) {
			adjustWeights(); // apply changes to weights
		}
	}

	/**
	 * Resets the network: initialize each element of the
	 * <code>weights</code> array with a value between 0.4 and 0.6.
	 */
	public void reset() {
		for (int i = 0; i < weights.length; i++) {
			// initialize weights that are between 0.4 and 0.6
			weights[i] = 0.6 - (0.2 * Math.random());
		}
	}

	/**
	 * Create a Kohonen map network with the specified architecture.
	 * @param numIn the number of input units.
	 * @param numRows the number of rows of the output layer.
	 * @param numCols the number of columns of the output layer.
	 */
	public void createNetwork(int numIn, int numRows, int numCols) {
		// set the network architecture
		this.numInputs = numIn;
		this.numRows = numRows;
		this.numCols = numCols;
		numOutputs = numRows * numCols;
		numUnits = numInputs + numOutputs;

		// initialize control parameters
		learnRate = 0.1;
		mode = 0; // 0 = train mode, 1 = run mode

		// create arrays
		activations = new double[numUnits];
		weights = new double[numInputs * numOutputs];

		// fill in the distance matrix
		computeDistances();
		adjustNeighborhood(); // set the initial learnRate
		reset(); // reset and initialize weight arrays
	}

	public DataSet getDataSet() {
		return dataset;
	}

	public void setDataSet(DataSet dataset) {
		this.dataset = dataset;
	}

	public int getNumRecs() {
		return numRecs;
	}

	public void setNumRecs(int numRecs) {
		this.numRecs = numRecs;
	}

	public int getFieldsPerRec() {
		return fieldsPerRec;
	}

	public void setFieldsPerRec(int fieldsPerRec) {
		this.fieldsPerRec = fieldsPerRec;
	}

	public Vector<double[]> getData() {
		return data;
	}

	public void setData(Vector<double[]> data) {
		this.data = data;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Returns basic information about the Kohonen map neural network.
	 */
	public String toString() {
		return "Kohonen Map Neural Net: " + name +
				"\n\tNum. of units in the input layer: " + numInputs +
				"\n\tNum. of units in the output layer: " + numRows + "x" + numCols +
				"\n\t" + dataset;
	}
} // end class KMapNet