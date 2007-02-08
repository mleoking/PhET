package com.graphbuilder.math.func;

/**
The min function.
*/
public class MinFunction implements Function {

	public MinFunction() {}

	/**
	Returns the minimum value of the specified inputs.  Double.MIN_VALUE is returned for 0 parameters.
	*/
	public double of(double[] d, int numParam) {
		if (numParam == 0)
			return Double.MIN_VALUE;

		double min = Double.MAX_VALUE;

		for (int i = 0; i < numParam; i++)
			if (d[i] < min)
				min = d[i];
		return min;
	}

	/**
	Returns true for 0 or more parameters, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam >= 0;
	}

	public String toString() {
		return "min(x1, x2, ..., xn)";
	}
}