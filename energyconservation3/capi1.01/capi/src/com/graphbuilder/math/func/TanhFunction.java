package com.graphbuilder.math.func;

/**
The hyperbolic tangent function.
*/
public class TanhFunction implements Function {

	public TanhFunction() {}

	/**
	Returns the value of (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>),
	where x is the value at index location 0 and <i>e</i> is the base of natural logarithms.
	*/
	public double of(double[] d, int numParam) {
		double e = Math.pow(Math.E, 2 * d[0]);
		return (e - 1) / (e + 1);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "tanh(x)";
	}
}