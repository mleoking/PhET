package com.graphbuilder.math.func;

/**
The arc sine function.

@see java.lang.Math#asin(double)
*/
public class AsinFunction implements Function {

	public AsinFunction() {}

	/**
	Returns the arc sine of the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.asin(d[0]);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "asin(x)";
	}
}