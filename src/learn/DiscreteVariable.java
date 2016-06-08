package learn;

import java.io.Serializable;
import java.util.Vector;

/**
 * The <code>DiscreteVariable</code> class provides the support
 * necessary for variables that can take on a predefined set of
 * numeric or symbolic values.
 * 
 * @author Tran Xuan Hoang
 */
public class DiscreteVariable extends Variable implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	protected int min;
	protected int max;

	/**
	 * Creates a <code>DiscreteVariable</code> with the given name.
	 * @param name the String name given to the variable.
	 */
	public DiscreteVariable(String name) {
		super(name);
		labels = new Vector<>();
	}

	/**
	 * Sets the minimum value for the variable.
	 * @param min the double minimum value for the variable.
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * Sets the maximum value for the variable.
	 * @param max the double maximum value for the variable.
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * Used within a <code>DataSet</code> to compute the minimum
	 * and maximum value for the variable.
	 * @param inValue the String that contains the value used to
	 * determine minimum or maximum value for the variable.
	 */
	@Override
	public void computeStatistics(String inValue) {
		if (!labels.contains(inValue)) {
			labels.addElement(inValue);
		}
	}

	/**
	 * Converts a symbol to a one-of-N code.
	 * @param inValue he String symbol to be converted.
	 * @param outArray the double array where the one-of-N code
	 * will be stored.
	 * @param inx the starting index where the one-of-N code should
	 * be stored in the output array.
	 */
	@Override
	public int normalize(String inValue, double[] outArray, int inx) {
		int index = getIndex(inValue);
		double code[] = new double[labels.size()];

		if (index < code.length) {
			code[index] = 1.0;
		}

		// copy one of N code to outArray, increase inx
		for (int i = 0; i < code.length; i++) {
			outArray[inx++] = code[i];
		}

		return inx; // return output index
	}

	/**
	 * Retrieves the number of discrete values the variable can take.
	 * @return the size of the one-of-N code when the variable is normalized.
	 */
	@Override
	public int getNormalizedSize() {
		return labels.size();
	}

	/**
	 * Retrieves the value of the given activation in a format
	 * that can be displayed. This method is used to transform the
	 * output of a neural network back into a String value for
	 * display.
	 * @param act the array that contains the activation.
	 * @param start the starting index for the activation within
	 * the array.
	 * @return the value of the activation in a format that can be
	 * displayed.
	 */
	public String getDecodedValue(double[] act, int start) {
		int len = labels.size();
		String value;
		double max = -1.0;

		value = String.valueOf(0);
		for (int i = 0; i < len; i++) {
			if (act[start + i] > max) {
				max = act[start + i];
				value = getLabel(i);
			}
		}

		return value;
	}
} // end class DiscreteVariable