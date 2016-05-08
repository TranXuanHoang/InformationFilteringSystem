package learn;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTextArea;

/**
 * The <code>DecisionTree</code> class implements a decision tree.
 * 
 * @author Tran Xuan Hoang
 */
public class DecisionTree implements Serializable {
	/**
	 * The serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	protected String name;
	protected DataSet ds;
	protected Variable classVar; // the Variable represent the classification field

	/**
	 * A list of all the <code>Variable</code> definitions for
	 * the data set.
	 */
	protected Hashtable<String, Variable> variableList;

	/**
	 * Holds the set of training records, which is the result of
	 * partitioning of the original training set, based on an
	 * attribute test.
	 */
	protected Vector<String[]> examples;

	protected JTextArea textArea;

	/**
	 * Holds one record of train/test data in string format.
	 */
	protected String record[];

	/**
	 * Creates a <code>DecisionTree</code> with the given name.
	 * @param name the name of the decision tree.
	 */
	public DecisionTree(String name) {
		this.name = name;
	}

	/**
	 * Determines whether each record in the <code>examples</code>
	 * vector matches the given variable value. This method takes
	 * the data set and a single variable as input and returns a
	 * boolean value indicating whether all of the training records
	 * contain the same value for the specified variable.
	 * @param examples the records of training data set.
	 * @param variable the <code>Variable</code> object that
	 * contains the value to be matched.
	 * @return <code>true</code> if the records match and <code>
	 * false</code> if they do not.
	 */
	public boolean identical(
			Vector<String[]> examples, Variable variable) {
		int index = variable.column; // see which column to check
		Enumeration<String[]> examps = examples.elements();
		boolean same = true;
		String value = examples.firstElement()[index];

		while (examps.hasMoreElements()) {
			if (value.equals(examps.nextElement()[index])) {
				continue;
			} else {
				same = false;
				break;
			}
		}

		return same;
	}

	/**
	 * Returns the classification field's value which occurs most
	 * often in the given vector of examples. In other words, this
	 * method examines a data set and returns the label which has
	 * the most examples for the specified variable.
	 * @param examples the <code>Vector</code> which is examined.
	 * @return the String that occurs most often.
	 */
	public String majority(Vector<String[]> examples) {
		int index = classVar.column;
		Enumeration<String[]> examps = examples.elements();
		int counts[] = new int[classVar.labels.size()];

		while (examps.hasMoreElements()) {
			String value = examps.nextElement()[index];
			int inx = classVar.getIndex(value);

			counts[inx]++;
		}

		int maxIndex = 0;
		int maxVal = counts[maxIndex];

		for (int i = 1; i < classVar.labels.size(); i++) {
			if (counts[i] > maxVal) {
				maxVal = counts[i];
				maxIndex = i;
			}
		}

		return classVar.getLabel(maxIndex);
	}

	/**
	 * Returns an integer array containing the number of occurrences
	 * of each discrete value in the given set of examples.
	 * @param examples the set of discrete values.
	 * @return an array that contains the number of occurrences of
	 * each discrete value.
	 */
	public int[] getCounts(Vector<String[]> examples) {
		int index = classVar.column; // look at class column only
		Enumeration<String[]> examps = examples.elements();
		int counts[] = new int[classVar.labels.size()];

		while (examps.hasMoreElements()) {
			String value = examps.nextElement()[index];
			int inx = classVar.getIndex(value);

			counts[inx]++;
		}

		return counts;
	}

	/**
	 * Computes the information content, given the number of
	 * positive and negative examples.
	 * @param p the number of positive values.
	 * @param n the number of negative values.
	 * @return the information content.
	 */
	double computeInfo(int p, int n) {
		double total = p + n;
		double pos = p / total;
		double neg = n / total;
		double temp;

		if (p == 0 || n == 0) {
			temp = 0.0;
		} else {
			temp = 0.0 - pos * Math.log(pos) / Math.log(2) -
					neg * Math.log(neg) / Math.log(2);
		}

		return temp;
	}

	/**
	 * Computes the remainder value for the given variable and a
	 * data set.
	 * @param variable the <code>Variable</code> for which the
	 * remainder is computed.
	 * @param examples the data set of the records.
	 * @return the remainder value.
	 */
	double computeRemainder(Variable variable, Vector<String[]> examples) {
		int positive[] = new int[variable.labels.size()];
		int negative[] = new int[variable.labels.size()];
		int index = variable.column;
		int classIndex = classVar.column;
		double sum = 0;
		double numValues = variable.labels.size();
		double numRecs = examples.size();

		for (int i = 0; i < numValues; i++) {
			// get discrete value
			String value = variable.getLabel(i);
			Enumeration<String[]> dataset = examples.elements();

			while (dataset.hasMoreElements()) {
				String[] record = dataset.nextElement();

				if (record[index].equals(value)) {
					if (record[classIndex].equals("yes")) {
						positive[i]++;
					} else {
						negative[i]++;
					}
				}
			} // end while

			double weight = (positive[i] + negative[i]) / numRecs;
			double rem = weight *
					computeInfo(positive[i], negative[i]);
			sum += rem;
		} // end for

		return sum;
	}

	/**
	 * Takes a data set, a variable, a value for that variable and
	 * returns the subset of data which contains that value in
	 * every record.
	 * @param examples the data set containing training records.
	 * @param variable the <code>Variable</code> representing a
	 * column of the data set.
	 * @param value the value of the <code>variable</code>.
	 * @return a <code>Vector</code> that contains records that
	 * have the same <code>value</code> at column represented by
	 * <code>variable</code>.
	 */
	Vector<String[]> subset(Vector<String[]> examples,
			Variable variable, String value) {
		int index = variable.column;
		Enumeration<String[]> examps = examples.elements();
		Vector<String[]> matchingExamples = new Vector<>();

		while (examps.hasMoreElements()) {
			String[] record = (String[]) examps.nextElement();

			if (value.equals(record[index])) {
				matchingExamples.addElement(record);
			}
		}

		textArea.append("\n Subset - there are " +
				matchingExamples.size() + " records with " +
				variable.name + " = " + value);

		return matchingExamples;
	}

	/**
	 * Chooses the variable with the greatest gain.
	 * @param variables the list of variable to consider.
	 * @param examples a subset of training data.
	 * @return the <code>Variable</code> with the greatest gain.
	 */
	Variable chooseVariable(Hashtable<String, Variable> variables,
			Vector<String[]> examples) {
		Enumeration<Variable> vars = variables.elements();
		double gain = 0.0, bestGain = -1.0;
		Variable best = null;

		int counts[] = getCounts(examples);
		int pos = counts[0];
		int neg = counts[1];
		double info = computeInfo(pos, neg);

		textArea.append("\nInfo = " + info);

		while (vars.hasMoreElements()) {
			Variable var = (Variable) vars.nextElement();

			gain = info - computeRemainder(var, examples);
			textArea.append("\n" + var.name + " gain = " + gain);

			if (gain > bestGain) {
				bestGain = gain;
				best = var;
			}
		}

		textArea.append("\nChoosing best variable: " + best.name);
		return best;
	}

	/**
	 * Constructs a decision tree with the given vector of example
	 * data records, splitting on variables and values with the
	 * most information content.
	 * @param examples a vector of example records (the data set).
	 * @param variables a hash table that contains all variables
	 * representing fields of records.
	 * @param defaultValue the <code>Node</code> object that
	 * contains the default value if the tree cannot be built
	 * from the examples.
	 * @return the tree's root <code>Node</code>.
	 */
	public Node buildDecisionTree(Vector<String[]> examples,
			Hashtable<String, Variable> variables, Node defaultValue) {
		Node tree = new Node();

		if (examples.size() == 0) {
			return defaultValue;
		} else if (identical(examples, classVar)) {
			return new Node(examples.firstElement()[classVar.column]);
		} else if (variables.size() == 0) {
			return new Node(majority(examples));
		} else {
			Variable best = chooseVariable(variables, examples);

			// root node with best gain
			tree = new Node(best.name);
			int numValues = best.labels.size();

			for (int i = 0; i < numValues; i++) {
				Vector<String[]> examples1 =
						subset(examples, best, best.getLabel(i));

				@SuppressWarnings("unchecked")
				Hashtable<String, Variable> variables1 =
				(Hashtable<String, Variable>) variables.clone();

				variables1.remove(best.getName());
				Node subTree = buildDecisionTree(examples1,
						variables1,
						new Node(majority(examples1)));

				tree.addChild(subTree, best.name + "=" + best.getLabel(i));
			}
		}

		return tree;
	}

	/**
	 * Displays the tree, starting with the given root node.
	 * @param root the root node of the tree to be displayed.
	 * @param offset delimiter string (i.e. spaces) to clarify
	 * between nodes when they are displayed.
	 */
	public void displayTree(Node root, String offset) {
		if (root.children.size() == 0) {
			textArea.append("\n" + offset + "    THEN (" +
					root.label + ")  (Leaf node)");
			return;
		} else {
			Enumeration<Node> children = root.children.elements();
			Enumeration<String> links = root.linkLabels.elements();

			textArea.append("\n" + offset + "   " +
					root.label + " (Interior node)");

			while (children.hasMoreElements()) {
				textArea.append("\n" + offset + "   IF (" +
						links.nextElement() + ")");
				displayTree(children.nextElement(), offset + "   ");
			}
		}
	}
} // end class DecisionTree