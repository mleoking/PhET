package com.graphbuilder.curve;

import com.graphbuilder.math.PascalsTriangle;

/**
<p>General n-point Bezier curve implementation.  The Bezier curve defines itself using all the points
from the control-path specified by the group-iterator.  To compute a single point on the curve requires
O(n) multiplications where n is the group-size of the group-iterator.  Thus, the Bezier curve is
considered to be expensive, but it has several mathematical properties (not discussed here) that
make it appealing.  Figure 1 shows an example of a Bezier curve.

<p><center><img align="center" src="doc-files/bezier1.gif"/></center>

<p>The maximum number of points that the Bezier curve can use is 1030 because the evaluation of a point
uses the nCr (n-choose-r) function.  The computation uses double precision, and double precision cannot
represent the result of 1031 choose i, where i = [500, 530].

@see com.graphbuilder.curve.Curve
@see com.graphbuilder.math.PascalsTriangle
*/

public class BezierCurve extends ParametricCurve {

	// a[] is required to compute (1 - t)^n starting from the last index.
	// The idea is that all Bezier curves can share the same array, which
	// is more memory efficient than each Bezier curve having its own array.
	private static double[] a = new double[0];

	private double t_min = 0.0;
	private double t_max = 1.0;
	private int sampleLimit = 1;

	public BezierCurve(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	public void eval(double[] p) {
		double t = p[p.length - 1];

		int numPts = gi.getGroupSize();

		if (numPts > a.length)
			a = new double[2 * numPts];

		a[numPts - 1] = 1;
		double b = 1.0;
		double one_minus_t = 1.0 - t;

		for (int i = numPts - 2; i >= 0; i--)
			a[i] = a[i+1] * one_minus_t;

		gi.set(0, 0);

		int i = 0;

		while (i < numPts) {
			double pt = PascalsTriangle.nCr(numPts - 1, i);

			if (Double.isInfinite(pt) || Double.isNaN(pt)) {
				// are there any techniques that can be used
				// to calculate past 1030 points?
				// 1031 choose 515 == infinity
			}
			else {
				double gravity = a[i] * b * pt;
				double[] d = cp.getPoint(gi.next()).getLocation();

				for (int j = 0; j < p.length - 1; j++)
					p[j] = p[j] + d[j] * gravity;
			}

			b = b * t;
			i++;
		}
	}

	public int getSampleLimit() {
		return sampleLimit;
	}

	/**
	Sets the sample-limit.  For more information on the sample-limit, see the
	BinaryCurveApproximationAlgorithm class.  The default sample-limit is 1.

	@throws IllegalArgumentException If sample-limit < 0.
	@see com.graphbuilder.curve.BinaryCurveApproximationAlgorithm
	@see #getSampleLimit()
	*/
	public void setSampleLimit(int limit) {
		if (limit < 0)
			throw new IllegalArgumentException("Sample-limit >= 0 required.");

		sampleLimit = limit;
	}

	/**
	Specifies the interval that the curve should define itself on.  The default interval is [0.0, 1.0].

	@throws IllegalArgumentException If t_min > t_max.
	@see #t_min()
	@see #t_max()
	*/
	public void setInterval(double t_min, double t_max) {
		if (t_min > t_max)
			throw new IllegalArgumentException("t_min <= t_max required.");

		this.t_min = t_min;
		this.t_max = t_max;
	}

	/**
	Returns the starting interval value.

	@see #setInterval(double, double)
	@see #t_max()
	*/
	public double t_min() {
		return t_min;
	}

	/**
	Returns the finishing interval value.

	@see #setInterval(double, double)
	@see #t_min()
	*/
	public double t_max() {
		return t_max;
	}

	/**
	The only requirement for this curve is the group-iterator must be in range or this method returns quietly.
	*/
	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;

		int n = mp.getDimension();

		double[] d = new double[n + 1];
		d[n] = t_min;
		eval(d);

		if (connect)
			mp.lineTo(d);
		else
			mp.moveTo(d);

		BinaryCurveApproximationAlgorithm.genPts(this, t_min, t_max, mp);
	}

	public void resetMemory() {
		if (a.length > 0)
			a = new double[0];
	}
}