package com.graphbuilder.math.func;

/**
The hyperbolic cosine function.
*/
public class CoshFunction implements Function {

	public CoshFunction() {}

	/**
	Returns the value of (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2, where x is the value
	at index location 0 and <i>e</i> is the base of natural logarithms.
	*/
	public double of(double[] d, int numParam) {
		return (Math.pow(Math.E, d[0]) + Math.pow(Math.E, -d[0])) / 2;
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "cosh(x)";
	}
}