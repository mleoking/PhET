package com.graphbuilder.curve;

import java.awt.geom.*;

class ShapeMultiPathIterator implements PathIterator {

	private final int ai0;
	private final int ai1;
	private final AffineTransform at;
	private final ShapeMultiPath smp;
	private final int windingRule;
	private int n = 0;

	ShapeMultiPathIterator(ShapeMultiPath smp, AffineTransform at) {
		this.smp = smp;
		int[] bv = smp.getBasisVectors();
		ai0 = bv[0];
		ai1 = bv[1];
		this.at = at;
		windingRule = smp.getWindingRule();
	}

	public int getWindingRule() {
		return windingRule;
	}

	public boolean isDone() {
		return n >= smp.getNumPoints();
	}

	public void next() {
		n++;
	}

	public int currentSegment(float[] coords) {
		double[] p = smp.get(n);
		coords[0] = (float) p[ai0];
		coords[1] = (float) p[ai1];

		if (n > 0 && p == smp.get(0))
			return PathIterator.SEG_CLOSE;

		if (at != null)
			at.transform(coords, 0, coords, 0, 1);

		if (smp.getType(n) == MultiPath.MOVE_TO)
			return PathIterator.SEG_MOVETO;

		return PathIterator.SEG_LINETO;
	}

	public int currentSegment(double[] coords) {
		double[] p = smp.get(n);
		coords[0] = p[ai0];
		coords[1] = p[ai1];

		if (n > 0 && p == smp.get(0))
			return PathIterator.SEG_CLOSE;

		if (at != null)
			at.transform(coords, 0, coords, 0, 1);


		if (smp.getType(n) == MultiPath.MOVE_TO)
			return PathIterator.SEG_MOVETO;

		return PathIterator.SEG_LINETO;
	}
}
