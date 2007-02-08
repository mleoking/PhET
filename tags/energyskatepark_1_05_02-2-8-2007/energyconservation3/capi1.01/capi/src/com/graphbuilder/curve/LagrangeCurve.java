package com.graphbuilder.curve;

/**
<p>The Lagrange curve passes through the control-points specified by the group-iterator.
It uses a knot-vector to control when the curve passes through each control-point.  That is,
if there is a knot-value for every control-point, then the curve will pass through point i
when the value of t is knot[i], which is an interesting property.  Figure 1 is an example of
this.

<p><center><img align="center" src="doc-files/lagrange1.gif"/></center>

<p>In addition, when there is a knot-value for every point then the base-index should be 0, and the
base-length should be n-1, where n is the size of the group-iterator.

<p>A knot-vector with size less than n can still be used.  In this case the Lagrange curve is
generated in multiple sections.  This approach works better when the points are roughly equally
spaced.  Figure 2 is an example of this.

<p><center><img align="center" src="doc-files/lagrange2.gif"/></center>

<p>Lagrange curves and also be closed as shown in figures 3 &amp; 4.

<p><center><img align="center" src="doc-files/lagrange3.gif"/></center>

<p><center><img align="center" src="doc-files/lagrange4.gif"/></center>

<p>Notes on the knot-vector, base-index and base-length.  The size of the knot-vector specifies how many
points are used for each section of the curve.  The base-index specifies which point a section starts
at.  The base-index + base-length specify which point the section ends at.  Once a section has been
generated, the next section is generated starting from the end of the last section.
*/
public class LagrangeCurve extends ParametricCurve {

	private ValueVector knotVector = new ValueVector(new double[] { 0.0, 1.0 / 3.0, 2.0 / 3.0, 1.0 }, 4);
	private int baseIndex = 1;
	private int baseLength = 1;
	private boolean interpolateFirst = false;
	private boolean interpolateLast = false;

	private static double[][] pt = new double[0][];

	/**
	Creates a LagrangeCurve with knot vector [0, 1/3, 2/3, 1], baseIndex == 1, baseLength == 1,
	interpolateFirst and interpolateLast are both false.  The knot vector, baseIndex and baseLength
	along with the control points define the shape of curve.  See the appendTo method for more information.

	@see #appendTo(MultiPath)
	*/
	public LagrangeCurve(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	/**
	Returns the base-index.  The default value is 1.

	@see #setBaseIndex(int)
	*/
	public int getBaseIndex() {
		return baseIndex;
	}

	/**
	The base-index is an index location into the knot vector such that, for each section, the curve is
	evaluated between [knot[baseIndex], knot[baseIndex + baseLength]].

	@throws IllegalArgumentException If base-index < 0.
	@see #getBaseIndex()
	*/
	public void setBaseIndex(int b) {
		if (b < 0) throw new IllegalArgumentException("base index >= 0 required.");
		baseIndex = b;
	}

	/**
	Returns the base-length.  The default value is 1.

	@see #setBaseLength(int)
	*/
	public int getBaseLength() {
		return baseLength;
	}

	/**
	The base-length along with the base-index specify the interval to evaluate each section.

	@throws IllegalArgumentException If base-length <= 0.
	@see #getBaseLength()
	*/
	public void setBaseLength(int b) {
		if (b <= 0) throw new IllegalArgumentException("base length > 0 required.");
		baseLength = b;
	}

	/**
	If baseIndex > 0 then the first control-points will only be interpolated if interpolate-first
	is set to true.

	@see #setInterpolateFirst(boolean)
	*/
	public boolean getInterpolateFirst() {
		return interpolateFirst;
	}

	/**
	If baseIndex + baseLength < numKnots - 1 then the last control-points will only be interpolated if
	interpolate-last is set to true.

	@see #setInterpolateLast(boolean)
	*/
	public boolean getInterpolateLast() {
		return interpolateLast;
	}

	/**
	Sets the value of the interpolateFirst flag.

	@see #getInterpolateFirst()
	*/
	public void setInterpolateFirst(boolean b) {
		interpolateFirst = b;
	}

	/**
	Sets the value of the interpolateLast flag.

	@see #getInterpolateLast()
	*/
	public void setInterpolateLast(boolean b) {
		interpolateLast = b;
	}

	/**
	Returns the knot-vector for this curve.

	@see #setKnotVector(ValueVector)
	*/
	public ValueVector getKnotVector() {
		return knotVector;
	}

	/**
	Sets the knot-vector for this curve.

	@see #getKnotVector()
	@throws IllegalArgumentException If the value-vector is null.
	*/
	public void setKnotVector(ValueVector v) {
		if (v == null)
			throw new IllegalArgumentException("Knot-vector cannot be null.");
		knotVector = v;
	}

	/**
	Returns a value of 1.
	*/
	public int getSampleLimit() {
		return 1;
	}

	protected void eval(double[] p) {
		double t = p[p.length - 1];

		int n = knotVector.size();

		for (int i = 0; i < n; i++) {
			double[] q = pt[i];
			double L = L(t, i);
			for (int j = 0; j < p.length - 1; j++)
				p[j] += q[j] * L;
		}
	}

	private double L(double t, int i) {
		double d = 1.0;

		int n = knotVector.size();

		for (int j = 0; j < n; j++) {
			double e = knotVector.get(i) - knotVector.get(j);
			if (e != 0)
				d = d * ((t - knotVector.get(j)) / e);
		}

		return d;
	}

	/**
	For the control-points to be interpolated in order, the knot-vector values should be strictly
	increasing, however that is not required.  The requirements are the group-iterator must be in
	range and baseIndex + baseLength < numKnots. As well, the number of points defined by the
	group-iterator must be >= numKnots, otherwise the curve does not have enough control-points
	to define itself.  If any of these requirements are not met, then this method returns quietly.
	*/
	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;
		if (baseIndex + baseLength >= knotVector.size()) return;

		if (pt.length < knotVector.size())
			pt = new double[2 * knotVector.size()][];

		gi.set(0, 0);

		boolean b = false;

		if (baseIndex != 0 && interpolateFirst) {
			for (int i = 0; i < knotVector.size(); i++) {
				if (!gi.hasNext()) return;
				pt[i] = cp.getPoint(gi.next()).getLocation();
			}

			b = doBCAA(mp, knotVector.get(0), knotVector.get(baseIndex), b);
		}

		gi.set(0, 0);

		int last_i = 0;
		int last_j = 0;

		while (true) {
			int temp_i = gi.index_i();
			int temp_j = gi.count_j();

			int index_i = 0;
			int count_j = 0;
			int i = 0;

			int j = 0;
			for (; j < knotVector.size(); j++) {
				if (i == baseLength) {
					index_i = gi.index_i();
					count_j = gi.count_j();
				}

				if (!gi.hasNext()) break;
				pt[j] = cp.getPoint(gi.next()).getLocation();
				i++;
			}

			if (j < knotVector.size()) {
				break;
			}
			else {
				gi.set(index_i, count_j);
				last_i = temp_i;
				last_j = temp_j;
			}

			b = doBCAA(mp, knotVector.get(baseIndex), knotVector.get(baseIndex + baseLength), b);
		}

		if (baseIndex + baseLength < knotVector.size() - 1 && interpolateLast) {
			gi.set(last_i, last_j);

			for (int i = 0; i < knotVector.size(); i++) {
				if (!gi.hasNext()) {
					System.out.println("not enough points to interpolate last");
					return;
				}
				pt[i] = cp.getPoint(gi.next()).getLocation();
			}

			doBCAA(mp, knotVector.get(baseIndex + baseLength), knotVector.get(knotVector.size() - 1), b);
		}
	}

	private boolean doBCAA(MultiPath mp, double t1, double t2, boolean b) {
		if (t2 < t1) {
			double temp = t1;
			t1 = t2;
			t2 = temp;
		}

		if (!b) {
			b = true;
			double[] d = new double[mp.getDimension() + 1];
			d[mp.getDimension()] = t1;
			eval(d);

			if (connect)
				mp.lineTo(d);
			else
				mp.moveTo(d);
		}

		BinaryCurveApproximationAlgorithm.genPts(this, t1, t2, mp);

		return b;
	}

	public void resetMemory() {
		if (pt.length > 0)
			pt = new double[0][];
	}
}