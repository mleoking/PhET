package com.graphbuilder.math.func;

/**
The hyperbolic tangent sine function.
*/
public class AtanhFunction implements Function {

	public AtanhFunction() {}

	/**
	Returns the value of (ln(1+x) - ln(1-x)) / 2, where x is the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return (Math.log(1 + d[0]) - Math.log(1 - d[0])) / 2;
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "atanh(x)";
	}
}