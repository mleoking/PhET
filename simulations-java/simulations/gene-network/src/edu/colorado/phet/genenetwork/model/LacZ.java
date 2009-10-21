/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 * 
 * @author John Blanco
 */
public class LacZ extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(219, 198, 212);
	private static double SIZE = 8;
	
	public LacZ(Point2D initialPosition) {
		super(createShape(), initialPosition, ELEMENT_PAINT);
	}
	
	public LacZ() {
		this(new Point2D.Double());
	}
	
	private static Shape createShape(){
		Ellipse2D startingShape = new Ellipse2D.Double(-SIZE/2, -SIZE/2, SIZE, SIZE);
		Area area = new Area(startingShape);
		area.subtract(new Area(new LactoseModelElement().getShape()));
		return area;
		
	}
}
