package com.graphbuilder.math.func;

/**
The round function.

@see java.lang.Math#round(double)
*/
public class RoundFunction implements Function {

	public RoundFunction() {}

	/**
	Returns the value at d[0] rounded to the nearest integer value.
	If the value exceeds the capabilities of long precision then
	the value itself is returned.
	*/
	public double of(double[] d, int numParam) {
		if (d[0] >= Long.MAX_VALUE || d[0] <= Long.MIN_VALUE)
			return d[0];

		return Math.round(d[0]);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "round(x)";
	}
}