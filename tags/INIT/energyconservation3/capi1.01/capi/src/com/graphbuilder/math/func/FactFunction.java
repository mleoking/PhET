package com.graphbuilder.math.func;

/**
The factorial function.
*/
public class FactFunction implements Function {

	public FactFunction() {}

	/**
	Takes the (int) of the value at index location 0 and computes the factorial
	of that number.
	*/
	public double of(double[] d, int numParam) {
		int n = (int) d[0];

		double result = 1;

		for (int i = n; i > 1; i--)
			result *= i;

		return result;
	}

	/**
	Returns true only for 1 parameter, false otherwise.
	*/
	public boolean acceptNumParam(int numParam) {
		return numParam == 1;
	}

	public String toString() {
		return "fact(n)";
	}
}