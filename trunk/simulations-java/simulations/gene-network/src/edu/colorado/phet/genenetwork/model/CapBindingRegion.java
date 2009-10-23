/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * Class that represents CAP binding region on the DNA strand.
 * 
 * @author John Blanco
 */
public class CapBindingRegion extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(222, 111, 19);
	private static float HEIGHT = 2.5f;
	public static float WIDTH = 5;
	
	public CapBindingRegion(Point2D initialPosition) {
		super(createShape(), initialPosition, ELEMENT_PAINT);
	}
	
	public CapBindingRegion() {
		this(new Point2D.Double());
	}
	
	private static Shape createShape(){
		
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(WIDTH/2, HEIGHT/2);
		outline.lineTo(WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, HEIGHT/2);
		outline.lineTo(-WIDTH * 0.3f, 0);
		outline.lineTo(0, HEIGHT/2);
		outline.lineTo(WIDTH * 0.3f, 0);
		outline.closePath();
		
		return outline;
	}
}
