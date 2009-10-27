/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * Class that represents CAP binding region on the DNA strand.
 * 
 * @author John Blanco
 */
public class CapBindingRegion extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(247, 143, 36);
	private static float HEIGHT = 2.5f;
	public static float WIDTH = 5;
	
	public CapBindingRegion(Point2D initialPosition) {
		super(createShape(), initialPosition, ELEMENT_PAINT);
	}
	
	public CapBindingRegion() {
		this(new Point2D.Double());
	}
	
	@Override
	SimpleElementType getType() {
		return SimpleElementType.CAP_BINDING_REGION;
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
