package com.graphbuilder.curve;

/**
<p>General implementation of the Non-Uniform Rational B-spline or NURB-Spline.  The main advantage
of the NURB-Spline over the B-Spline is the ability to represent conic sections.  To do this, a curve
with degree of 2 is used.  Figure 1 contains examples of conic arcs.

<p><center><img align="center" src="doc-files/nurbs1.gif"/></center>

<p>NURB-Splines can also be used to generate circles as shown in figure 2.

<p><center><img align="center" src="doc-files/nurbs2.gif"/></center>

<p>As seen in the figures, every control-point has an associated weight value.  The weight-values control
how much relative pull each control-point has on the curve.  If the weight-value is 0, then the associated
point will have no affect on the curve.  If a point has an associated weight of 0, but the curve is expected
to pass through that point, then it is likely that the result of evaluation will be the origin.  All weights
must be >= 0.
*/
public class NURBSpline extends BSpline {

	private static double[] nw = new double[0]; // (required length >= numPts)
	private static double[] weight = new double[0]; // (required length >= numPts)

	private ValueVector weightVector = new ValueVector(new double[] { 1, 1, 1, 1 }, 4);
	private boolean useWeightVector = true;

	public NURBSpline(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	protected void eval(double[] p) {
		int dim = p.length - 1;
		double t = p[dim];
		double sum2 = 0;

		int numPts = gi.getGroupSize();

		for (int i = 0; i < numPts; i++) {
			nw[i] = N(t, i) * weight[i];
			sum2 += nw[i];
		}

		if (sum2 == 0) sum2 = 1;

		for (int i = 0; i < dim; i++) {
			double sum1 = 0;
			gi.set(0,0);

			for (int j = 0; j < numPts; j++)
				sum1 += nw[j] * cp.getPoint(gi.next()).getLocation()[i];

			p[i] = sum1 / sum2;
		}
	}

	/**
	Returns the weight-vector.

	@see #setWeightVector(ValueVector)
	*/
	public ValueVector getWeightVector() {
		return weightVector;
	}

	/**
	Sets the weight-vector.

	@see #getWeightVector()
	@throws IllegalArgumentException If the value-vector is null.
	*/
	public void setWeightVector(ValueVector v) {
		if (v == null)
			throw new IllegalArgumentException("Weight-vector cannot be null.");

		weightVector = v;
	}

	/**
	Returns the value of the useWeightVector flag.  The default value is true.

	@see #setUseWeightVector(boolean)
	*/
	public boolean getUseWeightVector() {
		return useWeightVector;
	}

	/**
	Sets the value of the useWeightVector flag.  If the flag is true, then the internal weightVector
	will be used.  If the flag is false, then all weights will be assumed to be 1.

	@see #getUseWeightVector()
	*/
	public void setUseWeightVector(boolean b) {
		useWeightVector = b;
	}

	/**
	The requirements of the appendTo method include the requirements of the BSpline appendTo method, plus
	a couple more.  The additional requirements only apply if the useWeightVector flag is true.  If so, then
	the weight-vector must have size equal to the group-size of the GroupIterator and all weights must have a
	value >= 0.  This method returns quietly if these requirements are not met.

	@see com.graphbuilder.curve.BSpline#appendTo(MultiPath)
	*/
	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;
		int numPts = gi.getGroupSize();

		if (nw.length < numPts) {
			nw = new double[2 * numPts];
			weight = new double[2 * numPts];
		}

		if (useWeightVector) {
			if (weightVector.size() != numPts) return;

			for (int i = 0; i < numPts; i++) {
				weight[i] = weightVector.get(i);
				if (weight[i] < 0) return;
			}
		}
		else {
			for (int i = 0; i < numPts; i++)
				weight[i] = 1;
		}

		super.appendTo(mp);
	}

	public void resetMemory() {
		super.resetMemory();
		if (nw.length > 0) {
			nw = new double[0];
			weight = new double[0];
		}
	}
}