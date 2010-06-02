/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Base class for "Capture Zones", which are essentially two dimensional
 * spaces where particles can be captured.
 * 
 * @author John Blanco
 */
public abstract class CaptureZone {

	public abstract Shape getShape();
	public abstract boolean isPointInZone(Point2D pt);
	public abstract void setRotationalAngle(double angle);
	public abstract void setOriginPoint(Point2D centerPoint);
	public abstract Point2D getOriginPoint();
	
	/**
	 * Suggest a location for placing a new or relocated particle into this
	 * capture zone.
	 * 
	 * @return
	 */
	public Point2D getSuggestedNewParticleLocation(){
		return new Point2D.Double();
	}
}
