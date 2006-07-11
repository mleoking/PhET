package com.graphbuilder.math.func;

/**
The square root function.

@see java.lang.Math#sqrt(double)
*/
public class SqrtFunction implements Function {

	public SqrtFunction() {}

	/**
	Returns the square root of the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.sqrt(d[0]);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "sqrt(x)";
	}
}