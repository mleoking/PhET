package com.graphbuilder.curve;

/**
<p>The Cardinal-spline passes through the points in the control-path specified by the group-iterator.
However, the curve does not pass through the first or the last control-point, it begins at the
second control-point and ends at the second last control-point.

<p>There is a single parameter, alpha, that gives some control over the shape of the curve.  When
the value of alpha is 0.5 the curve becomes the CatmullRomSpline.  Figure 1 shows an example of a
CardinalSpline.

<p><center><img align="center" src="doc-files/cardinal1.gif"/></center>

@see com.graphbuilder.curve.CatmullRomSpline
*/
public class CardinalSpline extends ParametricCurve {

	private static double[][] pt = new double[4][];

	private double alpha = 0.5;

	public CardinalSpline(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	protected void eval(double[] p) {
		double t = p[p.length - 1];
		double t2 = t * t;
		double t3 = t2 * t;

		double a = 2 * t3 - 3 * t2 + 1;
		double b = -2 * t3 + 3 * t2;
		double c = alpha * (t3 - 2 * t2 + t);
		double d = alpha * (t3 - t2);

		for (int i = 0; i < p.length - 1; i++)
			p[i] = a * pt[1][i] + b * pt[2][i] + c * (pt[2][i] - pt[0][i]) + d * (pt[3][i] - pt[1][i]);
	}

	/**
	Returns the value of alpha.  The default value is 0.5.

	@see #setAlpha(double)
	*/
	public double getAlpha() {
		return alpha;
	}

	/**
	Sets the value of alpha.

	@see #getAlpha()
	*/
	public void setAlpha(double a) {
		alpha = a;
	}

	/**
	Returns a value of 1.
	*/
	public int getSampleLimit() {
		return 1;
	}

	/**
	The requirements for this curve are the group-iterator must be in-range and have a group size of at least 4.
	If these requirements are not met then this method returns quietly.
	*/
	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;
		if (gi.getGroupSize() < 4) return;

		gi.set(0, 0);

		for (int i = 0; i < 4; i++)
			pt[i] = cp.getPoint(gi.next()).getLocation();

		double[] d = new double[mp.getDimension() + 1];
		eval(d);

		if (connect)
			mp.lineTo(d);
		else
			mp.moveTo(d);

		gi.set(0, 0);

		while (true) {
			int index_i = gi.index_i();
			int count_j = gi.count_j();

			for (int i = 0; i < 4; i++) {
				if (!gi.hasNext()) return;
				pt[i] = cp.getPoint(gi.next()).getLocation();
			}

			gi.set(index_i, count_j);
			gi.next();

			BinaryCurveApproximationAlgorithm.genPts(this, 0.0, 1.0, mp);
		}
	}
}