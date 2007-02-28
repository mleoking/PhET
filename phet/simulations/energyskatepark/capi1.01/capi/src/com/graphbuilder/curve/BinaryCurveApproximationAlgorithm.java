package com.graphbuilder.curve;

import com.graphbuilder.geom.Geom;

/**
<p>The binary curve approximation algorithm is an algorithm designed to approximate a ParametricCurve using as few
points as possible but keeping the overall visual appearance of the curve smooth.  The number of points used and the
visual appearance are not guaranteed, but the results are usually very good.

<p>Users that do not plan on implementing their own ParametricCurves do not need to use this class.

<p>There is one static method called genPts that takes a ParametricCurve, an interval [t_min, t_max], and a MultiPath
as parameters.  The algorithm uses the ParametricCurve to generate points in the range [t_min, t_max] and append the
points in order to the MultiPath.  Figure 1 shows the basic idea of how the algorithm works.

<p><center><img align="center" src="doc-files/basic_idea.gif"/></center>

<p>The algorithm works by evaluating the parametric midpoint (m) of two points and computing the distance (dist)
from the parametric midpoint to line segment formed by the two points.  That distance is then compared to the
threshold distance, otherwise called the "flatness" of the curve.  The algorithm continues to subdivide the curve
until dist < flatness is reached.  However, that is not all there is to say.  Figure 2 illustrates the problem of
this approach.

<p><center><img align="center" src="doc-files/problem_case.gif"/></center>

<p>In some cases, the evaluated midpoint is on or near the line segment.  To solve this, the idea of a sample limit
is used.  The sample limit specifies how many additional subdivisions to perform once the condition dist < flatness
is reached.  If any of the additional subdivisions does not meet the dist < flatness condition, then the algorithm
continues from the point that did not meet the condition.  The closer the flatness value to 0 and the higher the
sample limit both increase the number of points appended to the multi-path.

<p>The idea of a sample limit is adequate for curves that generate points on the curve using a fixed number of
control-points in sections.  Curves that have this property have a fixed number of inflection points per section.
For example, the CubicBSpline generates itself in sections (considering 4 points at a time) but the BSpline with
degree 3 only has one section.  For curves that do not have this property, the sample-limit can be specified.
Note: As the sample limit is increased, it becomes more difficult to produce a point arrangement that causes a
problem.

<p>Note: The algorithm is designed so that the first point appended to the multi-path will be eval(t_min) and the
last point appended to the multi-path will be eval(t_max).  If the result of computing the distance from the line
segment to the evaluated point is NaN or Infinity then the algorithm aborts by throwing a RuntimeException.

@see com.graphbuilder.curve.Curve
@see com.graphbuilder.curve.ParametricCurve
@see com.graphbuilder.curve.MultiPath
*/
public final class BinaryCurveApproximationAlgorithm {

	private BinaryCurveApproximationAlgorithm() {}

	/**
	Appends a sequence of points to the multi-path using the lineTo method exclusively.  The flatness and the
	dimension of the curve are determined by the multi-path's flatness and dimension.  The sample limit is
	determined by the parametric curve's sample limit.

	@throws IllegalArgumentException If t_min > t_max.
	*/
	public static void genPts(ParametricCurve pc, double t_min, double t_max, MultiPath mp) {
		if (t_min > t_max)
			throw new IllegalArgumentException("t_min <= t_max required.");

		int n = mp.getDimension();

		double t1 = t_min;
		double t2 = t_max;
		double[][] stack = new double[10][];
		int count = 0;

		double[] rdy = new double[n + 1];
		rdy[n] = t1;
		pc.eval(rdy);

		double[] p = new double[n + 1];
		p[n] = t2;
		pc.eval(p);

		stack[count++] = p;

		double[][] limit = new double[pc.getSampleLimit()][];
		double flatSq = mp.getFlatness() * mp.getFlatness();

		double[] d = new double[n + 1]; // result array for the Geom.ptLineSegDistSq method

		while (true) {
			double m = (t1 + t2) / 2;

			double[] pt = new double[n + 1];
			pt[n] = m;
			pc.eval(pt);

			double dist = Geom.ptSegDistSq(rdy, stack[count-1], pt, d, n);

			// an infinite loop will happen if the following is not checked
			if (Double.isNaN(dist) || Double.isInfinite(dist)) {
				String msg = "NaN or infinity resulted from calling the eval method of the " +
				pc.getClass().getName() + " class.";
				throw new RuntimeException(msg);
			}

			boolean flag = false;

			if (dist < flatSq) {
				int i = 0;
				double mm = 0;

				for (; i < limit.length; i++) {
					mm = (t1 + m) / 2;

					double[] q = new double[n + 1];
					limit[i] = q;
					q[n] = mm;
					pc.eval(q);

					if (Geom.ptSegDistSq(rdy, pt, q, d, n) >= flatSq)
						break;
					else
						m = mm;
				}

				if (i == limit.length)
					flag = true;
				else {
					stack = checkSpace(stack, count);
					stack[count++] = pt;

					for (int j = 0; j <= i; j++) {
						stack = checkSpace(stack, count);
						stack[count++] = limit[j];
					}
					t2 = mm;
				}
			}

			if (flag) {
				mp.lineTo(rdy);
				mp.lineTo(pt);
				rdy = stack[--count];

				if (count == 0) break;

				pt = stack[count - 1];
				t1 = t2;
				t2 = pt[n];
			}
			else if (t2 > m) { // case: dist >= flatSq
				stack = checkSpace(stack, count);
				stack[count++] = pt;
				t2 = m;
			}
		}

		mp.lineTo(rdy);
	}

	private static double[][] checkSpace(double[][] stack, int size) {
		if (size == stack.length) {
			double[][] arr = new double[2 * size][];
			for (int i = 0; i < size; i++)
				arr[i] = stack[i];
			return arr;
		}
		return stack;
	}
}

