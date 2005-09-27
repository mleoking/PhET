package com.graphbuilder.math.func;

/**
The hyperbolic arc cosine function.
*/
public class AcoshFunction implements Function {

	public AcoshFunction() {}

	/**
	Returns the value of 2 * ln(sqrt((x+1)/2) + sqrt((x-1)/2)), where x is the
	value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		double a = Math.sqrt((d[0] + 1) / 2);
		double b = Math.sqrt((d[0] - 1) / 2);
		return 2 * Math.log(a + b);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "acosh(x)";
	}
}