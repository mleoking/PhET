package com.graphbuilder.math;

/**
<p>VarMap maps a name to a value.  A VarMap is used in the eval method of an Expression object.
This class can be used as the default variable-map.

<p>During the evaluation of an expression, if a variable is not supported then a RuntimeException is thrown.
Case sensitivity can only be specified in the constructor (for consistency).  When case sensitivity is false,
the String.equalsIgnoreCase method is used.  When case sensitivity is true, the String.equals method is used.
By default, case sensitivity is true.
*/
public class VarMap {

	private boolean caseSensitive = true;
	private String[] name = new String[2];
	private double[] value = new double[2];
	private int numVars = 0;

	public VarMap() {
		this(true);
	}

	public VarMap(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	Returns the value associated with the specified variable name.

	@throws RuntimeException If a matching variable name cannot be found.
	*/
	public double getValue(String varName) {
		for (int i = 0; i < numVars; i++)
			if (caseSensitive && name[i].equals(varName) || !caseSensitive && name[i].equalsIgnoreCase(varName))
				return value[i];

		throw new RuntimeException("variable value has not been set: " + varName);
	}

	/**
	Assigns the value to the specified variable name.

	@throws IllegalArgumentException If the variable name is null.
	*/	
	public void setValue(String varName, double val) {
		if (varName == null)
			throw new IllegalArgumentException("varName cannot be null");

		for (int i = 0; i < numVars; i++) {
			if (caseSensitive && name[i].equals(varName) || !caseSensitive && name[i].equalsIgnoreCase(varName)) {
				value[i] = val;
				return;
			}
		}

		if (numVars == name.length) {
			String[] tmp1 = new String[2 * numVars];
			double[] tmp2 = new double[tmp1.length];

			for (int i = 0; i < numVars; i++) {
				tmp1[i] = name[i];
				tmp2[i] = value[i];
			}

			name = tmp1;
			value = tmp2;
		}

		name[numVars] = varName;
		value[numVars] = val;
		numVars++;
	}

	/**
	Returns true if the case of the variable names is considered.
	*/
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	Returns an array of exact length of the variable names stored in this map.
	*/
	public String[] getVariableNames() {
		String[] arr = new String[numVars];

		for (int i = 0; i < arr.length; i++)
			arr[i] = name[i];

		return arr;
	}

	/**
	Returns an array of exact length of the values stored in this map.  The returned
	array corresponds to the order of the names returned by getVariableNames.
	*/
	public double[] getValues() {
		double[] arr = new double[numVars];

		for (int i = 0; i < arr.length; i++)
			arr[i] = value[i];

		return arr;
	}

	/**
	Removes the variable-name from the map. Does nothing if the variable-name is not found.
	*/
	public void remove(String varName) {
		for (int i = 0; i < numVars; i++) {
			if (caseSensitive && name[i].equals(varName) || !caseSensitive && name[i].equalsIgnoreCase(varName)) {
				for (int j = i + 1; j < numVars; j++) {
					name[j - 1] = name[j];
					value[j - 1] = value[j];
				}
				numVars--;
				name[numVars] = null;
				value[numVars] = 0;
				break;
			}
		}
	}
}
