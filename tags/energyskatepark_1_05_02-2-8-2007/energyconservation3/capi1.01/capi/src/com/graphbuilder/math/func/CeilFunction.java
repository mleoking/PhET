package com.graphbuilder.math.func;

/**
The ceiling function.

@see java.lang.Math#ceil(double)
*/
public class CeilFunction implements Function {

	public CeilFunction() {}

	/**
	Returns the ceiling of the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.ceil(d[0]);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "ceil(x)";
	}
}