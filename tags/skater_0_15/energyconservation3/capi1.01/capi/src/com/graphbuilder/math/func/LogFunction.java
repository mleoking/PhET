package com.graphbuilder.math.func;

/**
The log function.
*/
public class LogFunction implements Function {

	public LogFunction() {}

	/**
	If the number of parameters specified is 1, then the log base 10 is taken of the
	value at index location 0.  If the number of parameters specified is 2, then the
	base value is at index location 1.
	*/
	public double of(double[] d, int numParam) {
		if (numParam == 1) {
			return java.lang.Math.log(d[0]) / java.lang.Math.log(10);
		}
		return java.lang.Math.log(d[0]) / java.lang.Math.log(d[1]);
	}

	/**
	Returns true only for 1 or 2 parameters, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1 || numParam == 2;
	}

	public String toString() {
		return "log(x):log(x, y)";
	}
}