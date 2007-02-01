package com.graphbuilder.math.func;

/**
The max function.
*/
public class MaxFunction implements Function {

	public MaxFunction() {}

	/**
	Returns the maximum value of the specified inputs.  Double.MAX_VALUE is returned for 0 parameters.
	*/
	public double of(double[] d, int numParam) {
		if (numParam == 0)
			return Double.MAX_VALUE;

		double max = -Double.MAX_VALUE;
		for (int i = 0; i < numParam; i++)
			if (d[i] > max)
				max = d[i];
		return max;
	}

	/**
	Returns true for 0 or more parameters, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam >= 0;
	}

	public String toString() {
		return "max(x1, x2, ..., xn)";
	}
}