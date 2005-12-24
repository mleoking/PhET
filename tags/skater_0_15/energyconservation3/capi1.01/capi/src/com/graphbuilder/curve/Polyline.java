package com.graphbuilder.curve;

/**
A polyline is a sequence of connected line segments based on the control-path points specified
by the group-iterator.
*/

public class Polyline extends Curve {

	public Polyline(ControlPath cp, GroupIterator gi) {
		super(cp, gi);
	}

	public void appendTo(MultiPath mp) {
		if (!gi.isInRange(0, cp.numPoints())) return;

		gi.set(0, 0);

		if (connect)
			mp.lineTo(cp.getPoint(gi.next()).getLocation());
		else
			mp.moveTo(cp.getPoint(gi.next()).getLocation());

		while (gi.hasNext())
			mp.lineTo(cp.getPoint(gi.next()).getLocation());
	}
}