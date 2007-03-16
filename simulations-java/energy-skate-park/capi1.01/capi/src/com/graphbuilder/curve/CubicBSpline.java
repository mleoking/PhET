package com.graphbuilder.curve;

/**
<p>The cubic B-spline is defined by third order polynomial basis functions.  Each point on the curve
is locally controlled by 4 control-points.  In general, the curve does not pass through the control points,
only near.  The exceptions to this are the first and last control-points and if there are duplicate sequential
control-points.

<p>The CubicBSpline is the same as the BSpline with degree 3.  However, the CubicBSpline is much faster
to compute than the BSpline.  The following table represents the approximate speed difference in computation
between the CubicBSpline and BSpline of degree 3.

<p>
<center>
<table border="1">
	<tr><td><b>Number of Points</b></td><td><b>Times Faster</b></td></tr>
	<tr><td>10</td><td>4.6</td></tr>
	<tr><td>20</td><td>6.9</td></tr>
	<tr><td>30</td><td>9.5</td></tr>
	<tr><td>40</td><td>11</td></tr>
</table>
Table 1: Efficiency of CubicBSpline
</center>

<p>As the number of points increases, the BSpline gets slower and slower.  The reason is the CubicBSpline is
built in segments, using 4 points at a time.  However, the BSpline is a single segment, and requires iteration
through all the points to compute a single point on the curve.  Unlike the BSpline, the CubicBSpline does not
have a knot vector, a definable sample limit, range or degree, which means the memory required for the
CubicBSpline is less than the BSpline.

<p>Relative to other curves, the cubic B-spline is computationally inexpensive, and easy to work with.
To create a closed cubic B-spline, use "0:n-1,0:2" as the control-string and set interpolateEndpoints
to false.  Figures 1, 2 & 3 show some examples of cubic B-splines.  See the appendTo method for more
information.

<p><center><img align="center" src="doc-files/cubicb1.gif"/></center>

<p><center><img align="center" src="doc-files/cubicb2.gif"/></center>

<p><center><img align="center" src="doc-files/cubicb3.gif"/></center>

@see BSpline
*/
public class CubicBSpline extends ParametricCurve {

	private static int section = 0;
	private static int numPoints = 0;
	private static double[][] pt = new double[4][];
	private static double[] b = new double[4];

	private boolean interpolateEndpoints = false;

	public CubicBSpline(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	protected void eval(double[] p) {

		double t = p[p.length - 1];
		double t2 = t * t;
		double t3 = t2 * t;

		double u = 1 - t;
		double u2 = u * u;
		double u3 = u2 * u;

		if (numPoints == 4) {
			b[0] = u2 * u;
			b[1] = 3 * u2 * t;
			b[2] = 3 * u * t2;
			b[3] = t3;
		}
		else if (numPoints == 5) {
			if (section == 0) {
				b[0] = u3;
				b[1] = 7 * t3 / 4 - 9 * t2 / 2 + 3 * t;
				b[2] = -t3 + 3 * t2 / 2;
				b[3] = t3 / 4;
			}
			else {
				b[0] = u3 / 4;
				b[1] = -u3 + 3 * u2 / 2;
				b[2] = 7 * u3 / 4 - 9 * u2 / 2 + 3 * u;
				b[3] = t3;
			}
		}
		else if (numPoints == 6) {
			if (section == 0) {
				b[0] = u3;
				b[1] = 7 * t3 / 4 - 9 * t2 / 2 + 3 * t;
				b[2] = -11 * t3 / 12 + 3 * t2 / 2;
				b[3] = t3 / 6;
			}
			else if (section == 1) {
				b[0] = u3 / 4;
				b[1] = 7 * t3 / 12 - 5 * t2 / 4 + t / 4 + 7.0 / 12;
				b[2] = -7 * t3 / 12 + t2 / 2 + t / 2 + 1.0 / 6;
				b[3] = t3 / 4;
			}
			else {
				b[0] = u3 / 6;
				b[1] = -11 * u3 / 12 + 3 * u2 / 2;
				b[2] = 7 * u3 / 4 - 9 * u2 / 2 + 3 * u;
				b[3] = t3;
			}
		}
		else { // 7 and >= 8 have the same basis functions
			if (section == 0) {
				b[0] = u3;
				b[1] = 7 * t3 / 4 - 9 * t2 / 2 + 3 * t;
				b[2] = -11 * t3 / 12 + 3 * t2 / 2;
				b[3] = t3 / 6;
			}
			else if (section == 1) {
				b[0] = u3 / 4;
				b[1] = 7 * t3 / 12 - 5 * t2 / 4 + t / 4 + 7.0 / 12;
				b[2] = -t3 / 2 + t2 / 2 + t / 2 + 1.0 / 6;
				b[3] = t3 / 6;
			}
			else if (section == 2) { // if numPoints == 7 then section 2 gets skipped
				b[0] = u3 / 6;
				b[1] = t3 / 2 - t2 + 2.0 / 3;
				b[2] = (-t3 + t2 + t) / 2 + 1.0 / 6;
				b[3] = t3 / 6;
			}
			else if (section == 3) {
				b[0] = u3 / 6;
				b[1] = -u3 / 2 + u2 / 2 + u / 2 + 1.0 / 6;
				b[2] = 7 * u3 / 12 - 5 * u2 / 4 + u / 4 + 7.0 / 12;
				b[3] = t3 / 4;
			}
			else {
				b[0] = u3 / 6;
				b[1] = -11 * u3 / 12 + 3 * u2 / 2;
				b[2] = 7 * u3 / 4 - 9 * u2 / 2 + 3 * u;
				b[3] = t3;
			}
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < p.length - 1; j++)
				p[j] = p[j] + pt[i][j] * b[i];
		}
	}

	/**
	Returns a value of 1.
	*/
	public int getSampleLimit() {
		return 1;
	}

	/**
	Sets the curve to start at the first control-point and end at the last control-point specified by
	the group-iterator.

	@see #getInterpolateEndpoints()
	*/
	public void setInterpolateEndpoints(boolean b) {
		interpolateEndpoints = b;
	}

	/**
	Returns the interpolateEndpoints value.  The default value is false.

	@see #setInterpolateEndpoints(boolean)
	*/
	public boolean getInterpolateEndpoints() {
		return interpolateEndpoints;
	}

	/**
	The requirements for this curve are the group-iterator must be in-range and have a group size of at least 4.
	If these requirements are not met then this method returns quietly.
	*/
	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;
		int n = gi.getGroupSize();
		if (n < 4) return;

		if (interpolateEndpoints) {
			numPoints = n;
			section = 0;
		}
		else {
			numPoints = -1; // defaults to numPoints >= 7 in the eval method
			section = 2;	// section doesn't change when interpolateEndpoints == false
		}

		gi.set(0, 0);
		int index_i = 0;
		int count_j = 0;

		for (int i = 0; i < 4; i++)
			pt[i] = cp.getPoint(gi.next()).getLocation();

		double[] d = new double[mp.getDimension() + 1];
		eval(d);

		if (connect)
			mp.lineTo(d);
		else
			mp.moveTo(d);

		int j = 3;

		while (true) {
			BinaryCurveApproximationAlgorithm.genPts(this, 0.0, 1.0, mp);
			j++;
			if (j == n) break;

			gi.set(index_i, count_j);
			gi.next();
			index_i = gi.index_i();
			count_j = gi.count_j();

			for (int i = 0; i < 4; i++)
				pt[i] = cp.getPoint(gi.next()).getLocation();

			if (interpolateEndpoints) {
				if (n < 7) {
					section++;
				}
				else {
					if (section != 2)
						section++;

					if (section == 2 && j == n - 2)
						section++;
				}
			}
		}
	}
}