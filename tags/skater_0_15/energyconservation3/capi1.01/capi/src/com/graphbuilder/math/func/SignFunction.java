package com.graphbuilder.math.func;

/**
The sign function.
*/
public class SignFunction implements Function {

	public SignFunction() {}

	/**
	The sign function returns 1 if the d[0] > 0, -1 if d[0] < 0, else 0.
	*/
	public double of(double[] d, int numParam) {
		if (d[0] > 0) return 1;
		if (d[0] < 0) return -1;
		return 0;
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "sign(x)";
	}
}