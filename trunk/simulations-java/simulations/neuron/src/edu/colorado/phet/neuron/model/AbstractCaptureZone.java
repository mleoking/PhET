/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Base class for "Capture Zones", which are essentially two dimensional
 * spaces where particles can be captured.
 * 
 * @author John Blanco
 */
public abstract class AbstractCaptureZone {

	abstract Shape getShape();
	abstract boolean isPointInZone(Point2D pt);
}
