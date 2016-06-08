package learn;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.JTextArea;

/**
 * The <code>BackProp</code> class implements the standard backward
 * propagation algorithm with momentum for using in neural networks.
 * 
 * @author Tran Xuan Hoang
 */
public class BackProp implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	// members that are used to manage the data set.
	protected String name;
	private DataSet dataset;
	/** Holds the training data. */
	private Vector<double[]> data;
	/** Current record index. */
	private int recInx = 0;
	/** Total number of records in data. */
	private int numRecs = 0;
	/** Number of fields in each record. */
	private int fieldsPerRec = 0;

	// error measures
	/** The accumulative sum of squared errors. */
	private double sumSquaredError;	// total SSE for an epoch
	private double aveRMSError;		// average root-mean-square error
	private int numPasses;			// number of passes over the data set

	// network architecture parameters
	/** Number of units in the input layer. */
	private int numInputs;
	/** Number of units in the first hidden layer. */
	private int numHid1;
	/** Number of units in the output layer. */
	private int numOutputs;
	/** Total number of units in all layers. */
	private int numUnits;
	/** Total number of weights in the network. */
	private int numWeights;

	// network control parameters
	/** <ul>
	 * <li>mode = 0: the network is in training, the connection
	 * weights are adjusted</li>
	 * <li>mode = 1: the network weights are locked</li>
	 * </ul>
	 */
	private int mode;
	private double learnRate; // learnRate and momentum are used to
	private double momentum;  // control the size of the weight updates
	/**
	 * Specifies how close the predicted output value has to be to
	 * the desired output value before the error is considered to
	 * be 0. For example, if our desired output value is 1.0, with
	 * a <i>tolerance</i> of 0.1, any predicted output value greater
	 * than 0.9 would result in 0 error.
	 */
	private double tolerance;

	// network data
	private double activations[];
	private double weights[];
	/**
	 * Holds the accumulated set of weight changes that should be
	 * made to the <code>weights</code>.
	 */
	private double wDerivs[];
	private double wDeltas[];		// weight changes
	private double thresholds[];
	private double tDerivs[];
	private double tDeltas[];
	private double teach[];         // target output values
	private double error[];
	private double deltas[];        // the error deltas
	transient public JTextArea textArea;

	/**
	 * Creates a back propagation object with the given name.
	 * @param name the name of the back propagation object.
	 */
	public BackProp(String name) {
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
	 * Display the results after a neural network training run.
	 * Displays the relevant information for the network. Default
	 * is to show only the activations array values.
	 */
	public void display_network() {
		show_array("activations", activations);
		// uncomment these lines to see more data on network state
		// show_array("weights",weights);
		// show_array("thresholds", thresholds);
		// show_array("teach",teach);

		String desired = dataset.getClassFieldValue(recInx - 1);
		String actual = dataset.getClassFieldValue(activations,
				numInputs + numHid1);

		if (textArea != null) {
			textArea.append("\n  Desired: " + desired +
					"  Actual: " + actual);
		}
	}

	/**
	 * Implements the activation function. This method computes
	 * the activation value based on the given sum:<br>
	 * f(sum) = 1 / (1 + e^-sum)
	 * @param sum the sum of the <i>threshold</i> and each input
	 * activation multiplied by the corresponding input weight.
	 * @return the computed activation value.
	 */
	public double logistic(double sum) {
		return 1.0 / (1 + Math.exp(-1.0 * sum));
	}

	/**
	 * Reads data from the training or test data set into the
	 * activations of the input units. This method takes a record
	 * from the data set and copies the input values into the
	 * <code>teach</code> array.
	 */
	public void readInputs() {
		recInx = recInx % numRecs; // keep index from 0 to n-1 records
		double[] tempRec = data.elementAt(recInx); // get record
		int inx = 0;

		for (inx = 0; inx < numInputs; inx++) {
			activations[inx] = tempRec[inx];
		}

		for (int i = 0; i < numOutputs; i++) {
			teach[i] = tempRec[inx++];
		}

		recInx++;
	}

	/**
	 * Computes the outputs by doing a single forward pass through
	 * the network. (Note: the implementation of this method needs
	 * to be revised if the neural network's architecture changes,
	 * i.e. has more than one hidden layer)
	 */
	public void computeOutputs() {
		int firstHid1 = numInputs;
		int firstOut = numInputs + numHid1;

		// first layer
		int inx = 0;

		for (int i = firstHid1; i < firstOut; i++) {
			double sum = thresholds[i];

			for (int j = 0; j < numInputs; j++) {
				sum += activations[j] * weights[inx++];
			}

			activations[i] = logistic(sum);
		}

		// second layer
		for (int i = firstOut; i < numUnits; i++) {
			double sum = thresholds[i];

			for (int j = firstHid1; j < firstOut; j++) {
				sum += activations[j] * weights[inx++];
			}

			activations[i] = logistic(sum);
		}
	}

	/**(TODO understand algorithm)
	 * Starting at the output layer and working backward to the
	 * input layer, computes the following:
	 * <ol>
	 * <li>the output layer errors and deltas based on the
	 * difference between the activations and the target values
	 * <li>the squared errors used to calculate the average
	 * root-mean-squared (RMS) error
	 * <li>the hidden layer errors and deltas
	 * <li>the input layer errors
	 * </ol>
	 */
	public void computeError() {
		int firstHid1 = numInputs;
		int firstOut = numInputs + numHid1;

		// clear hidden unit errors
		for (int i = numInputs; i < numUnits; i++) {
			error[i] = 0.0;
		}

		// compute output layer errors and deltas
		for (int i = firstOut; i < numUnits; i++) {
			// compute output errors
			error[i] = teach[i - firstOut] - activations[i];

			// accumulate squared errors
			sumSquaredError += error[i] * error[i];

			if (Math.abs(error[i]) < tolerance) {
				error[i] = 0.0;		// close enough
			}

			deltas[i] = error[i] * activations[i] * (1 - activations[i]);
		}

		// compute hidden layer errors
		int winx = numInputs * numHid1; // offset into weight array

		for (int i = firstOut; i < numUnits; i++) {
			for (int j = firstHid1; j < firstOut; j++) {
				wDerivs[winx] += deltas[i] * activations[j];
				error[j] += weights[winx] * deltas[i];
				winx++;
			}

			tDerivs[i] += deltas[i];
		}

		// compute hidden layer deltas
		for (int i = firstHid1; i < firstOut; i++) {
			deltas[i] = error[i] * activations[i] * (1 - activations[i]);
		}

		// compute input layer errors
		winx = 0; // offset into weight array

		for (int i = firstHid1; i < firstOut; i++) {
			for (int j = 0; j < firstHid1; j++) {
				wDerivs[winx] += deltas[i] * activations[j];
				error[j] += weights[winx] * deltas[i];
				winx++;
			}

			tDerivs[i] += deltas[i];
		}
	}

	/**
	 * Adjusts the weights and thresholds by computing and adding
	 * the delta for each.
	 */
	public void adjustWeights() {
		// first walk through the weights array
		for (int i = 0; i < weights.length; i++) {
			wDeltas[i] = (learnRate * wDerivs[i]) +
					(momentum * wDeltas[i]);

			weights[i] += wDeltas[i]; // modify the weight
			wDerivs[i] = 0.0;
		}

		// then walk through the threshold array
		for (int i = numInputs; i < numUnits; i++) {
			tDeltas[i] = (learnRate * tDerivs[i]) +
					(momentum * tDeltas[i]);

			thresholds[i] += tDeltas[i]; // modify the threshold
			tDerivs[i] = 0.0;
		}

		// if at the end of an epoch, compute average RMS error
		if (recInx == numRecs) {
			numPasses++;			// increase pass counter
			aveRMSError = Math.sqrt(
					sumSquaredError / numRecs * numOutputs);
			sumSquaredError = 0.0;	// clear the accumulator
		}
	}

	/**
	 * For networks with single continuous outputs only, retrieves
	 * the prediction. Do a single forward pass through the
	 * network and return the output value.
	 * @param inputRec the double array of input record.
	 * @return the prediction value.
	 */
	public double getPrediction(double[] inputRec) {
		int firstOut = numInputs + numHid1;

		// set input unit activations
		for (int inx = 0; inx < numInputs; inx++) {
			activations[inx] = inputRec[inx];
		}

		computeOutputs(); // do forward pass through network

		return activations[firstOut];
	}

	/**
	 * Processes the network by reading the input values,
	 * computing the output and error values, and if training,
	 * adjusting the weights.
	 */
	public void process() {
		readInputs();		// set input unit activations
		computeOutputs();	// do forward pass through network
		computeError();		// compute error and deltas

		// only adjust if in training mode
		if (mode == 0) {
			adjustWeights(); // apply changes to weights
		}
	}

	/**
	 * Resets the network by initializing the network arrays.
	 */
	public void reset() {
		for (int i = 0; i < weights.length; i++) {
			// randomize weight between -0.5 and +0.5
			weights[i] = 0.5 - Math.random();
			wDeltas[i] = 0.0;
			wDerivs[i] = 0.0;
		}

		for (int i = 0; i < numUnits; i++) {
			// randomize threshold between -0.5 and +0.5
			thresholds[i] = 0.5 - Math.random();
			tDeltas[i] = 0.0;
			tDerivs[i] = 0.0;
		}
	}

	/**
	 * Defines a back propagation neural network. This method is
	 * provided instead of using the constructor so that the
	 * network's architecture can be changed without creating a
	 * new object.
	 * @param numIn the number of input units.
	 * @param numHidden the number of hidden elements.
	 * @param numOut the number of output units.
	 */
	public void createNetwork(int numIn, int numHidden, int numOut) {
		// set the network architecture
		numInputs = numIn;
		numHid1 = numHidden;
		numOutputs = numOut;
		numUnits = numInputs + numHid1 + numOutputs;
		numWeights = (numInputs * numHid1) + (numHid1 * numOutputs);

		// initialize control parameters
		learnRate = 0.2;
		momentum = 0.7;
		tolerance = 0.1;
		mode = 0;			// 0 = train mode, 1 = run mode
		aveRMSError = 0.0;
		numPasses = 0;

		System.out.println("Network's numUnit: " + numUnits +
				", numIn = " + numIn + ", numHidden = " + numHidden
				+ ", numOut = " + numOut);
		// create weight and error arrays
		activations = new double[numUnits];	// unit activations
		weights = new double[numWeights];
		wDerivs = new double[numWeights];	// accumulated wDeltas
		wDeltas = new double[numWeights];	// weight changes
		thresholds = new double[numUnits];
		tDerivs = new double[numUnits];		// accumulated tDeltas
		tDeltas = new double[numUnits];		// threshold changes
		teach = new double[numOutputs];		// desired outputs
		deltas = new double[numUnits];
		error = new double[numUnits];

		reset(); // reset and initialize the weight arrays
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

	public double getAveRMSError() {
		return aveRMSError;
	}

	public void setAveRMSError(double aveRMSError) {
		this.aveRMSError = aveRMSError;
	}

	public double getLearnRate() {
		return learnRate;
	}

	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}

	public double getMomentum() {
		return momentum;
	}

	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	/**
	 * Return basic information about the back propagation neural network.
	 */
	public String toString() {
		return "Back Propagation Neural Net: " + name +
				"\n\tNum. of units in the input layer: " + numInputs +
				"\n\tNum. of units in the hidden layer: " + numHid1 +
				"\n\tNum. of units in the output layer: " + numOutputs +
				"\n\t" + dataset;
	}
} // end class BackProp