package learn;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JTextArea;

/**
 * The <code>DataSet</code> class is used to load data from text
 * files into memory for use in training or testing.
 * 
 * @author Tran Xuan Hoang
 */
public class DataSet implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	/** The name of the data set. */
	protected String name;

	/** The name of the file that contains the data set. */
	protected String fileName;

	/**
	 * Indicates whether there is symbolic data in the file.<br>
	 * If <code>true</code> use <code>double[]</code>, else
	 * <code>String</code>.
	 */
	protected boolean allNumericData;

	/** Stores raw data read from the file. */
	protected Vector<String[]> data;

	/** Stores scaled and translated data. */
	protected Vector<double[]> normalizedData;

	/**
	 * Holds a set of <code>Variable</code>s that define the logical
	 * data types for each field in the file (variable definition).
	 */
	protected Hashtable<String, Variable> variableList;

	/**
	 * Holds references to the same <code>Variable</code>s as the
	 * <code>variableList</code>, but the variables are added to the
	 * <code>Vector</code> in the order they are added to the
	 * <code>DataSet</code>.
	 */
	protected Vector<Variable> fieldList; // field definitions where index = column

	/**
	 * The number of data fields in each record (e.g. each line)
	 * in the data file (e.g. .dat file).
	 */
	protected int fieldsPerRec = 0;

	/** The number of data fields in each record that are normalized. */
	protected int normFieldsPerRec = 0;

	/**
	 * The number of records in the data file (equivalent to the size
	 * of the <code>fieldList</code>)
	 */
	protected int numRecords = 0;

	transient public JTextArea textArea1;

	/**
	 * Creates a <code>DataSet</code> with the given name that will
	 * be populated from the specified file.
	 * @param name the name of the data set.
	 * @param fileName the name of the text file from which the
	 * data set is populated.
	 */
	public DataSet(String name, String fileName) {
		this.name = name;

		if (fileName.endsWith(".dfn") || fileName.endsWith(".dat")) {
			int inx = fileName.indexOf(".");
			this.fileName = fileName.substring(0, inx);
		} else {
			this.fileName = fileName; // text file name only
		}

		fieldsPerRec = 0;			// start with no variables defined
		allNumericData = true;		// assume all numeric data
		data = new Vector<>();		// hold String data
		variableList = new Hashtable<>(); // for named lookup
		fieldList = new Vector<>();	// for ordered lookup
	}

	/**
	 * Reads the data file definition which is a simple text file
	 * that contains a list of the field data types and their names.
	 */
	public void loadDataFileDefinition() {
		trace("\nReading file definition " + fileName + ".dfn");

		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(fileName + ".dfn")))) {
			int recInx = 0;
			int token = 0;
			StringTokenizer input = null;
			String line = null;

			do {
				line = in.readLine();

				if (line != null) {
					input = new StringTokenizer(line);
					String varType = input.nextToken();
					String varName = input.nextToken();

					if (varType.equals("continuous")) {
						addVariable(new ContinuousVariable(varName));
					} else if (varType.equals("discrete")) {
						addVariable(new DiscreteVariable(varName));
					} else if (varType.equals("categorical")) {
						addVariable(new DiscreteVariable(varName));
					}

					trace("\n  Record " + recInx + ": " +
							varType + " " + varName);

					recInx++;
				} else {
					break;
				}
			} while (token != StreamTokenizer.TT_EOF);

			fieldsPerRec = fieldList.size();
			trace("\nCreated " + fieldsPerRec + " variables.\n");
		} catch (FileNotFoundException e) {
			trace("Error: Cannot find definition file " + fileName + ".dfn");
		} catch (IOException e) {
			trace("Error Reading file: " + fileName + ".dfn");
		}
	}

	/**
	 * Adds a variable to the list of fields in the record.
	 * @param var the <code>Variable</code> to be added to the list.
	 */
	public void addVariable(Variable var) {
		variableList.put(var.name, var);
		fieldList.addElement(var); // add in order of arrival
		var.setColumn(fieldsPerRec);
		fieldsPerRec++;
	}

	/**
	 * Reads the data from the <i>data file</i> defined by the
	 * <i>data file definition</i>.
	 * @throws FileNotFoundException if the <i>data file</i> not found.
	 * @throws IOException  if any error occurs while reading the
	 * <i>data file</i>.
	 */
	public void loadDataFile() throws FileNotFoundException, IOException {
		// first read the file definition and create variables
		loadDataFileDefinition();
		fieldsPerRec = fieldList.size(); //TODO redundant

		trace("\nReading file " + fileName + ".dat with " +
				fieldsPerRec + " fields per record");

		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(fileName + ".dat")))) {
			String tempRec[] = null; // used when data is symbolic
			int recInx = 0;
			StringTokenizer input = null;
			String line = null;

			while ((line = in.readLine()) != null) {
				input = new StringTokenizer(line);
				tempRec = new String[fieldsPerRec];
				data.addElement(tempRec); // add record

				trace("\n  Record " + recInx + ": ");
				for (int i = 0; i < fieldsPerRec; i++) {
					tempRec[i] = input.nextToken();
					fieldList.elementAt(i).computeStatistics(tempRec[i]);
					trace(tempRec[i] + " ");
				}

				recInx++;
			}

			numRecords = recInx;
			trace("\nLoaded " + numRecords + " records into memory.\n");

			normalizeData(); // now convert to numeric form
			displayVariables();
			displayNormalizedData();
		} catch (FileNotFoundException e) {
			trace("Error: Cannot find data record file " + fileName + ".dat");
			throw new FileNotFoundException();
		} catch (IOException e) {
			trace("Error reading file: " + fileName + ".dat");
			e.printStackTrace();
			throw new IOException();
		}
	}

	/**
	 * Retrieves the size of the class field.
	 * @return the class field size.
	 */
	public int getClassFieldSize() {
		if (variableList.get("ClassField") == null) {
			trace("DataSet " + name + " does not have a ClassField");
			return 0;
		} else {
			return variableList.get("ClassField").getNormalizedSize();
		}
	}

	/**
	 * Computes the record size after each variable in the record
	 * is normalized.
	 * @return the normalized record size.
	 */
	public int getNormalizedRecordSize() {
		int sum = 0;
		Enumeration <Variable> vars = variableList.elements();

		while (vars.hasMoreElements()) {
			Variable var = (Variable) vars.nextElement();
			sum += var.getNormalizedSize();
		}

		return sum;
	}

	/**
	 * Retrieves the class field value for the given record index.
	 * @param recIndex the index of the record (i.e. each line
	 * in the data file is a record).
	 * @return the class field value.
	 */
	public String getClassFieldValue(int recIndex) {
		Variable classField = variableList.get("ClassField");
		return data.elementAt(recIndex)[classField.column];
	}

	/**
	 * Retrieves the class field value for a given activation.
	 * @param activations the array of activations from which
	 * value is retrieved.
	 * @param index the starting index of the output unit.
	 * @return the class field value.
	 */
	public String getClassFieldValue(double[] activations, int index) {
		String value;
		Variable classField = variableList.get("ClassField");

		if (classField.isCategorical()) {
			value = classField.getDecodedValue(activations, index);
		} else {
			value = String.valueOf(activations[index]);
		}

		return value;
	}

	/**
	 * Normalizes a record by translating discrete data to a
	 * one-of-N vector and by scaling all continuous data to be
	 * in the 0.0 to 1.0 range.
	 */
	public void normalizeData() {
		normalizedData = new Vector<double[]>();
		normFieldsPerRec = getNormalizedRecordSize();
		Enumeration<String[]> rawData = data.elements();

		while (rawData.hasMoreElements()) {
			int inx = 0;
			double normNumRec[] = new double[normFieldsPerRec];
			Enumeration<Variable> fields = fieldList.elements();

			String[] tempRec = (String[]) rawData.nextElement();
			for (int i = 0; i < fieldsPerRec; i++) {
				Variable var = fields.nextElement();
				inx = var.normalize(tempRec[i], normNumRec, inx);
			}

			normalizedData.addElement(normNumRec);
		}
	}

	/**
	 * Displays the normalized data.
	 */
	public void displayNormalizedData() {
		trace("\n\nNormalized data:");

		Enumeration<double[]> rawData = normalizedData.elements();
		int recInx = 0;

		while (rawData.hasMoreElements()) {
			trace("\n  Record " + recInx++ + ": ");
			double[] tempNumRec = (double[]) rawData.nextElement();
			int numFields = tempNumRec.length;

			for (int i = 0; i < numFields; i++) {
				trace(String.valueOf(tempNumRec[i]) + " ");
			}
		}

		trace("\n\n");
	}

	/**
	 * Adds text to the text area for display.
	 * @param text the String to be displayed.
	 */
	public void trace(String text) {
		if (textArea1 != null) {
			textArea1.append(text);
		} else {
			System.out.println(text);
		}
	}

	/**
	 * Sets the text area to be displayed for the data set information.
	 * @param textArea the <code>JTextArea</code> text area to be displayed.
	 */
	public void setDisplay(JTextArea textArea) {
		textArea1 = textArea;
	}

	/**
	 * Displays all variables and their values.
	 */
	public void displayVariables() {
		Enumeration<Variable> vars = variableList.elements();

		trace("\nVariables:");
		while (vars.hasMoreElements()) {
			String values;
			Variable temp = vars.nextElement();

			if (temp.labels != null) {
				values = temp.getLabels();
			} else {
				values = "< real>";
			}

			trace("\n " + temp.name + "( " + values + ") ");
		}
	}

	/**
	 * Retrieves the number of records.
	 * @return the number of records.
	 */
	public int getNumRecords() {
		return numRecords;
	}

	/**
	 * Retrieves the number of fields per each record.
	 * @return the number of fields per each record.
	 */
	public int getFieldsPerRec() {
		return fieldsPerRec;
	}

	/**
	 * Retrieves the number of normalized fields per each record.
	 * @return the number of normalized fields per each record.
	 */
	public int getNormFieldsPerRec() {
		return normFieldsPerRec;
	}

	/**
	 * Retrieves the data that is already normalized.
	 * @return the normalized data (a vector of double arrays).
	 */
	public Vector<double[]> getNormalizedData() {
		return normalizedData;
	}

	/**
	 * Retrieves the file name of the data set.
	 * @return the file name of the data set.
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns basic information about the data set.
	 */
	public String toString() {
		List<String> vars = Collections.list(variableList.keys());

		return "Dataset: extracted from file " + fileName +
				"\n\t" + vars.size() + "Variables: " + vars;
	}
} // end class DataSet