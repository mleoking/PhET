package com.graphbuilder.curve;

/**
Curves that extend the ParametricCurve class are continuous and can use the
BinaryCurveApproximationAlgorithm class to generate a sequence of points that
approximate the curve.  Note: Approximate means a finite set of points that
are <i>on</i> the curve, <u>not</u> close to the curve.

@see com.graphbuilder.curve.BinaryCurveApproximationAlgorithm
@see com.graphbuilder.curve.Curve
*/
public abstract class ParametricCurve extends Curve {

	public ParametricCurve(ControlPath cp, GroupIterator gp) {
		super(cp, gp);
	}

	/**
	The eval method evaluates a point on a curve given a parametric value "t".  The parametric
	value "t" is stored in the last index location of the specified double array.  <i>This value
	should not be changed</i>.  The dimension of the point to evaluate is p.length - 1.  The result
	of the evaluation is placed in index locations 0 .. p.length - 2 (inclusive).

	The eval method should remain protected except for those curves that do no need any
	preparation to be done in the appendTo method.
	*/
	protected abstract void eval(double[] p);

	/**
	The sample limit specifies how many additional subdivisions are done to ensure that there
	are no missed pieces of the curve.  The sample limit must be >= 0.
	*/
	public abstract int getSampleLimit();
}