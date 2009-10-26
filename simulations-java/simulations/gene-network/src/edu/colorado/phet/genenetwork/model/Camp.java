/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


/**
 * Class that represents the cAMP, which is an acronym for something, but I
 * don't know what.
 * 
 * @author John Blanco
 */
public class Camp extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = Color.RED;
	private static float DIAMETER = 2.0f;  // In nanometers.
	
	public Camp(Point2D initialPosition) {
		super(createShape(), initialPosition, ELEMENT_PAINT);
	}
	
	public Camp() {
		this(new Point2D.Double());
	}
	
	@Override
	SimpleElementType getType() {
		return SimpleElementType.CAMP;
	}
	
	private static Shape createShape(){
		return new Ellipse2D.Double(-DIAMETER/2, -DIAMETER/2, DIAMETER, DIAMETER);
	}
}
