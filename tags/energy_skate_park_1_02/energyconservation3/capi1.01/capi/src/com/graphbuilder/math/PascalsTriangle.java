package com.graphbuilder.math;

/**
PascalsTriangle can be used for O(1) lookup of the nCr function.
*/
public final class PascalsTriangle {

	private PascalsTriangle() {};

	private static double[][] pt = new double[][] { {1} };

	/**
	The nCr function returns the number of ways r things can be chosen from a set of size n.
	Mathematically, it is defined as: n! / (r! * (n - r)!)
	Although the result is always a whole number, double precision is used because
	the maximum value a double can represent is larger than long.  Thus, large returned
	values will only be an approximation of the actual value.  If the result exceeds the
	capabilities of double precision then the result can be checked using Double.isInfinite(...).
	For example: System.out.println(PascalsTriangle.nCr(1030, 515)); // outputs: Infinity
	If the value of n or r is less than 0 or the value of r is greater than n then 0 is
	returned.
	*/
	public synchronized static double nCr(int n, int r) {
		if (n < 0 || r < 0 || r > n) return 0;

		if (n >= pt.length) {
			int d = 2 * pt.length;
			double[][] pt2 = null;
			if (n > d)
				pt2 = new double[n + 1][];
			else
				pt2 = new double[d + 1][];

			for (int i = 0; i < pt.length; i++)
				pt2[i] = pt[i];

			for (int i = pt.length; i < pt2.length; i++) {
				pt2[i] = new double[(i / 2) + 1];

				pt2[i][0] = 1;

				for (int j = 1; j < pt2[i].length; j++) {
					double x = pt2[i-1][j-1];
					if (j < pt2[i-1].length)
						x = x + pt2[i-1][j];
					else
						x = 2 * x;

					pt2[i][j] = x;
				}
			}
			pt = pt2;
		}

		if (2 * r > n)
			r = n - r;

		return pt[n][r];
	}

	/**
	Resets the internal array to the initial state to free up memory.
	*/
	public synchronized static void reset() {
		pt = new double[][] { {1} };
	}
}