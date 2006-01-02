package com.graphbuilder.curve;

/**
<p>The natural-cubic-spline is constructed using piecewise third order polynomials which pass through all the
control-points specified by the group-iterator.  The curve can be open or closed.  Figure 1 shows an open
curve and figure 2 shows a closed curve.

<p><center><img align="center" src="doc-files/natcubic1.gif"/></center>

<p><center><img align="center" src="doc-files/natcubic2.gif"/></center>

*/
public class NaturalCubicSpline extends ParametricCurve {

/*
The pt array stores the points of the control-path.
The data array is used to store the result of the many calculations.

d[0] = w1  For each dimension, 4 arrays are required to store the
d[1] = x1  results of the calculations.
d[2] = y1  The length of each array is >= to the number of points.
d[3] = z1
d[4] = w2
d[5] = x2
d[6] = y2
d[7] = z2
d[8] = a   // a, b & c are used (by both open and closed) to store
d[9] = b   // the results of the calculations.
d[10] = c
d[11] = d // only used for closed cubic curves
*/

	private static double[][] pt = new double[0][];
	private static double[][] data = new double[0][];
	private static int ci = 0;

	private boolean closed = false;

	public NaturalCubicSpline(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	protected void eval(double[] p) {
		int n = p.length - 1; // dimension

		double t = p[n];
		double t2 = t * t;
		double t3 = t2 * t;

		int j = 0;
		for (int i = 0; i < n; i++)
			p[i] = data[j++][ci] + data[j++][ci] * t + data[j++][ci] * t2 + data[j++][ci] * t3;
	}

	// n is the # of points
	// dim is the dimension
	private static void precalc(int n, int dim, boolean closed) {
		n--;

		double[] a = data[4 * dim];
		double[] b = data[4 * dim + 1];
		double[] c = data[4 * dim + 2];
		int k = 0;

		if (closed) {
			double[] d = data[4 * dim + 3];
			double e, f, g, h;

			for (int j = 0; j < dim; j++) {
				d[1] = a[1] = e = 0.25;
				b[0] = e * 3 * (pt[1][j] - pt[n][j]);
				h = 4;
				f = 3 * (pt[0][j] - pt[n-1][j]);
				g = 1;
				for (int i = 1; i < n; i++) {
					a[i+1] = e = 1.0 / (4.0 - a[i]);
					d[i+1] = -e * d[i];
					b[i] = e * (3.0 * (pt[i+1][j] - pt[i-1][j]) - b[i-1]);
					h = h - g * d[i];
					f = f - g * b[i-1];
					g = -a[i] * g;
				}
				h = h - (g + 1) * (a[n] + d[n]);
				b[n] = f - (g + 1) * b[n-1];

				c[n] = b[n] / h;
				c[n-1] = b[n-1] - (a[n] + d[n]) * c[n];
				for (int i = n-2; i >= 0; i--) {
					c[i] = b[i] - a[i+1] * c[i+1] - d[i+1] * c[n];
				}

				double[] w = data[k++];
				double[] x = data[k++];
				double[] y = data[k++];
				double[] z = data[k++];

				for (int i = 0; i < n; i++) {
					w[i] = pt[i][j];
					x[i] = c[i];
					y[i] = 3 * (pt[i+1][j] - pt[i][j]) - 2 * c[i] - c[i+1];
					z[i] = 2 * (pt[i][j] - pt[i+1][j]) + c[i] + c[i+1];
				}

				w[n] = pt[n][j];
				x[n] = c[n];
				y[n] = 3 * (pt[0][j] - pt[n][j]) - 2 * c[n] - c[0];
				z[n] = 2 * (pt[n][j] - pt[0][j]) + c[n] + c[0];
			}
		}
		else {
			for (int j = 0; j < dim; j++) {
				a[0] = 0.5;
				for (int i = 1; i < n; i++) {
					a[i] = 1.0 / (4 - a[i-1]);
				}
				a[n] = 1.0 / (2.0 - a[n-1]);

				b[0] = a[0] * (3 * (pt[1][j] - pt[0][j]));
				for (int i = 1; i < n; i++) {
					b[i] = a[i] * (3 * (pt[i+1][j] - pt[i-1][j]) - b[i-1]);
				}
				b[n] = a[n] * (3 * (pt[n][j] - pt[n-1][j]) - b[n-1]);

				c[n] = b[n];
				for (int i = n-1; i >= 0; i--) {
					c[i] = b[i] - a[i] * c[i+1];
				}

				double[] w = data[k++];
				double[] x = data[k++];
				double[] y = data[k++];
				double[] z = data[k++];

				for (int i = 0; i < n; i++) {
					w[i] = pt[i][j];
					x[i] = c[i];
					y[i] = 3 * (pt[i+1][j] - pt[i][j]) - 2 * c[i] - c[i+1];
					z[i] = 2 * (pt[i][j] - pt[i+1][j]) + c[i] + c[i+1];
				}

				w[n] = pt[n][j];
				x[n] = 0;
				y[n] = 0;
				z[n] = 0;
			}
		}
	}


	/**
	The closed attribute determines which tri-diagonal matrix to solve.

	@see #getClosed()
	*/
	public void setClosed(boolean b) {
		closed = b;
	}

	/**
	Returns the value of closed.  The default value is false.

	@see #setClosed(boolean)
	*/
	public boolean getClosed() {
		return closed;
	}

	/**
	Returns a value of 1.
	*/
	public int getSampleLimit() {
		return 1;
	}

	/**
	The requirements for this curve are the group-iterator must be in-range and have a group size of at least 2.
	If these requirements are not met then this method returns quietly.
	*/
	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;

		final int n = gi.getGroupSize();
		if (n < 2) return;

		int dim = mp.getDimension();

		// make sure there is enough room
		//-------------------------------------------------------
		int x = 3 + 4 * dim + 1;

		if (data.length < x) {
			double[][] temp = new double[x][];

			for (int i = 0; i < data.length; i++)
				temp[i] = data[i];

			data = temp;
		}

		if (pt.length < n) {
			int m = 2 * n;

			pt = new double[m][];

			for (int i = 0; i < data.length; i++)
				data[i] = new double[m];
		}
		//-------------------------------------------------------

		gi.set(0, 0);

		for (int i = 0; i < n; i++)
			pt[i] = cp.getPoint(gi.next()).getLocation(); // assign the used points to pt

		precalc(n, dim, closed);

		ci = 0; // do not remove

		double[] p = new double[dim + 1];
		eval(p);

		if (connect)
			mp.lineTo(p);
		else
			mp.moveTo(p);

		// Note: performing a ci++ or ci = ci + 1 results in funny behavior
		for (int i = 0; i < n; i++) {
			ci = i;
			BinaryCurveApproximationAlgorithm.genPts(this, 0.0, 1.0, mp);
		}
	}

	public void resetMemory() {
		if (pt.length > 0)
			pt = new double[0][];

		if (data.length > 0)
			data = new double[0][];
	}
}