/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Class that defines a capture zone that contains nothing.  This is useful
 * when wanting to avoid having to do a bunch of null checks.
 * 
 * @author John Blanco
 */
public class NullCaptureZone extends CaptureZone {

	@Override
	public Shape getShape() {
		return new Ellipse2D.Double(0, 0, 0, 0);
	}

	@Override
	public boolean isPointInZone(Point2D pt) {
		return false;
	}

	@Override
	public void setOriginPoint(Point2D centerPoint) {
		// Does nothing.
	}

	@Override
	public Point2D getOriginPoint() {
		return null;
	}
	
	@Override
	public void setRotationalAngle(double angle) {
		// Does nothing.
	}

}
