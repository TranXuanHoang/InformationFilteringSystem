package learn;

import java.io.Serializable;

/**
 * The <code>ContinuousVariable</code> provides the support
 * necessary for variables that can take on real, continuous values
 * within a defined range.
 * 
 * @author Tran Xuan Hoang
 */
public class ContinuousVariable extends Variable implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	protected double min = 0.0;
	protected double max = 0.0;

	/**
	 * Creates a <code>ContinuousVariable</code> with the given name.
	 * @param name the name that identifies the variable.
	 */
	public ContinuousVariable(String name) {
		super(name);
	}

	/**
	 * Sets the minimum value for the variable.
	 * @param min the double minimum value for the variable.
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * Sets the maximum value for the variable.
	 * @param max the double maximum value for the variable.
	 */
	public void setMax(double max) {
		this.max = max;
	}

	/**
	 * Used within a <code>DataSet</code> to compute the minimum
	 * and maximum value for the variable, based on the given value.
	 * @param inValue the String that contains the value used to
	 * determine the minimum or maximum value for the variable.
	 */
	@Override
	public void computeStatistics(String inValue) {
		double val = new Double(inValue).doubleValue();

		if (val < min) {
			min = val;
		}

		if (val > max) {
			max = val;
		}
	}

	/**
	 * Linearly scales a value to be in the range from 0.0 to 1.0.
	 * @param inStrValue the String representation of the value
	 * to be scaled.
	 * @param outArray the double array of scaled values.
	 * @param inx the index of the array element where the scaled
	 * value is stored.
	 */
	@Override
	public int normalize(String inStrValue, double[] outArray, int inx) {
		double outValue;
		double inValue = Double.valueOf(inStrValue).doubleValue();

		if (inValue <= min) {
			outValue = min;
		} else if (inValue >= max) {
			outValue = max;
		} else {
			double factor = max - min;
			outValue = inValue / factor;
		}

		outArray[inx] = outValue;
		return inx + 1;
	}
} // end class ContinuousVariable