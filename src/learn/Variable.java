package learn;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * The <code>Variable</code> abstract class provides the common
 * support necessary for continuous, numeric discrete, and categorical
 * variables.
 * 
 * @author Tran Xuan Hoang
 */
public abstract class Variable implements Serializable {
	/**
	 * The serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	protected String name;
	protected String value;

	/**
	 * Each variable represents a column of a field of the set of
	 * records.
	 */
	protected int column;

	/**
	 *  Used by categorical only, this field contains the set of
	 *  all values that the variable has.
	 */
	protected Vector<String> labels;

	/**
	 * Creates a <code>Variable</code> object with no specified
	 * name, value and labels.
	 */
	public Variable() {
		name = null;
		value = null;
		labels = null;
	}

	/**
	 * Creates a <code>Variable</code> with the given name.
	 * @param name
	 */
	public Variable(String name) {
		this.name = name;
		value = null;
		labels = null;
	}

	/**
	 * Retrieves the name of the variable.
	 * @return the name of the variable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the variable.
	 * @param val the String value of the variable.
	 */
	public void setValue(String val) {
		value = val;
	}

	/**
	 * Retrieves the value of the variable.
	 * @return the value of the variable.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the labels using the given label string.
	 * @param labels the String that contains the labels for a
	 * categorical variable.
	 */
	public void setLabels(String labels) {
		this.labels = new Vector<>();
		StringTokenizer tok = new StringTokenizer(labels, " ");

		while (tok.hasMoreTokens()) {
			this.labels.addElement(new String(tok.nextToken()));
		}
	}

	/**
	 * Retrieves the label with the specified index.
	 * @param index the index of the desired label.
	 * @return the label at the given index.
	 */
	public String getLabel(int index) {
		return (String) labels.elementAt(index);
	}

	/**
	 * Retrieves all the labels from a categorical variable.
	 * @return a String that contains all the labels, separated
	 * by spaces.
	 */
	public String getLabels() {
		String labelList = new String();
		Enumeration<String> labels = this.labels.elements();

		while (labels.hasMoreElements()) {
			labelList += labels.nextElement() + " ";
		}

		return labelList;
	}

	/**
	 * Retrieves the index for the given label.
	 * @param label the label for which its index is retrieved.
	 * @return the index of the given label, -1 if label was not found.
	 */
	public int getIndex(String label) {
		int i = 0;
		int index = -1;
		Enumeration<String> labels = this.labels.elements();

		while (labels.hasMoreElements()) {
			if (label.equals(labels.nextElement())) {
				index = i;
				break;
			}

			i++;
		}

		return index;
	}

	/**
	 * Determines if the variable is categorical.
	 * @return <code>true</code> if the variable is categorical.
	 * Otherwise, return <code>false</code>.
	 */
	public boolean isCategorical() {
		return (labels != null);
	}

	/**
	 * Sets the column.
	 * @param col the integer value of the column.
	 */
	public void setColumn(int col) {
		column = col;
	}

	/**
	 * Computes the minimum and maximum values for this variable
	 * based on the given String, but can also be used to compute
	 * other statistics as well.
	 * @param inValue the String on which the statistics are based.
	 */
	public abstract void computeStatistics(String inValue);

	/**
	 * Converts the given symbol for use in the network.
	 * @param inValue the String to be converted.
	 * @param outArray the double array of converted values.
	 * @param inx the index that indicates where the converted
	 * output is to be stored in the array.
	 * @return the index of the next element in the array.
	 */
	public abstract int normalize(String inValue,
			double[] outArray, int inx);

	/**
	 * Retrieves the normalized size of this variable.
	 * @return the normalized size.
	 */
	public int getNormalizedSize() {
		return 1;
	}

	/**
	 * Retrieves the activation value in a format that can be
	 * displayed.
	 * @param act the double array of activation values.
	 * @param index the index of the activation to be displayed.
	 * @return the value in a format that can be displayed.
	 */
	public String getDecodedValue(double[] act, int index) {
		return String.valueOf(act[index]);
	}
} // end class Variable