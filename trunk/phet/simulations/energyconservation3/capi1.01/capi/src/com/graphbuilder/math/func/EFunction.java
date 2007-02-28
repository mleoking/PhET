package com.graphbuilder.math.func;

/**
Euler's number, <i>e</i>, also called the base of natural logarithms.

@see java.lang.Math#E
*/
public class EFunction implements Function {

	public EFunction() {}

	/**
	Returns the constant <i>e</i> regardless of the input.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.E;
	}

	/**
	Returns true only for 0 parameters, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 0;
	}

	public String toString() {
		return "e()";
	}
}