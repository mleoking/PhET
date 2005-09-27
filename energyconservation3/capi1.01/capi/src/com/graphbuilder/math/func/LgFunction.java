package com.graphbuilder.math.func;

/**
The log base 2 function.
*/
public class LgFunction implements Function {

	public LgFunction() {}

	/**
	Returns the log base 2 of the value at index location 0.
	*/
	public double of(double[] d, int numParam) {
		return java.lang.Math.log(d[0]) / java.lang.Math.log(2);
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "lg(x)";
	}
}