/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 * 
 * @author John Blanco
 */
public class LacZModelElement extends SimpleModelElement {
	
	private static final Shape ELEMENT_SHAPE = new Ellipse2D.Double(-5, -10, 10, 20);
	private static final Paint ELEMENT_PAINT = Color.GREEN;
	
	public LacZModelElement(Point2D initialPosition) {
		super(ELEMENT_SHAPE, initialPosition, ELEMENT_PAINT);
	}
}
