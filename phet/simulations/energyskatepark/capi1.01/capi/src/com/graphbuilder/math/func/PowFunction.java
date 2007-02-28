package com.graphbuilder.math.func;

/**
The power function.

@see java.lang.Math#pow(double,double)
*/
public class PowFunction implements Function {

	public PowFunction() {}

	/**
	Returns the value at index location 0 to the exponent of the value
	at index location 1.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.pow(d[0], d[1]);
	}

	/**
	Returns true only for 2 parameters, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 2;
	}

	public String toString() {
		return "pow(x, y)";
	}
}