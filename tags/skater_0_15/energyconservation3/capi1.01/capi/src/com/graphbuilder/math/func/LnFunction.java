package com.graphbuilder.math.func;

/**
The natural logarithm function.

@see java.lang.Math#log(double)
*/
public class LnFunction implements Function {

	public LnFunction() {}

	/**
	Returns the natural logarithm of the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.log(d[0]);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "ln(x)";
	}
}