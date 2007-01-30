package com.graphbuilder.math.func;

/**
The floor function.

@see java.lang.Math#floor(double)
*/
public class FloorFunction implements Function {

	public FloorFunction() {}

	/**
	Returns the floor of the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.floor(d[0]);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "floor(x)";
	}
}