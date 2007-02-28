package com.graphbuilder.curve;

/**
The CatmullRomSpline is equal to the CardinalSpline with the value of alpha fixed at 0.5.
The implementation for the CatmullRomSpline is about 10% faster then the CardinalSpline
implementation.

@see com.graphbuilder.curve.CardinalSpline
*/
public class CatmullRomSpline extends ParametricCurve {

	private static double[][] pt = new double[4][];

	public CatmullRomSpline(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	protected void eval(double[] p) {
		double t = p[p.length - 1];
		double t2 = t * t;
		double t3 = t2 * t;

		// Note: The 0.5 does NOT represent alpha. It is a result of the simplification.

		for (int i = 0; i < p.length - 1; i++) {
			p[i] = 0.5 * ((pt[3][i] - pt[0][i] + 3 * (pt[1][i] - pt[2][i])) * t3
				+ (2 * (pt[0][i] + 2*pt[2][i]) - 5*pt[1][i] - pt[3][i]) * t2
				+ (pt[2][i] - pt[0][i]) * t) + pt[1][i];
		}
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
