/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;


/**
 * Class that represents the CAP, which is an acronym for something, but I
 * don't know what.
 * 
 * @author John Blanco
 */
public class Cap extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(237, 179, 122);
	private static float WIDTH = CapBindingRegion.WIDTH;
	private static float HEIGHT = 4;  // In nanometers.
	
	public Cap(Point2D initialPosition) {
		super(createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
	}
	
	public Cap() {
		this(new Point2D.Double());
	}
	
	@Override
	public SimpleElementType getType() {
		return SimpleElementType.CAP;
	}
	
	private static Shape createActiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(-WIDTH / 2, 0);
		outline.curveTo(-WIDTH/2, HEIGHT, 0, HEIGHT / 2, 0, HEIGHT/4);
		outline.lineTo(WIDTH/2, HEIGHT/4);
		outline.lineTo(WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, -HEIGHT/2);
		outline.closePath();
		Area area = new Area(outline);
		
		// Get the shape of the binding region and shift it to the appropriate
		// position.
		Shape bindingRegionShape = new CapBindingRegion().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, -HEIGHT/2 );
		bindingRegionShape = transform.createTransformedShape(bindingRegionShape);
		
		// Subtract off the shape of the binding region.
		area.subtract(new Area(bindingRegionShape));
		
		// Get the shape of the cAMP and shift it to the appropriate location.
		Shape campShape = new Camp().getShape();
		transform = new AffineTransform();
		transform.setToTranslation(	-WIDTH/2, 0 );
		campShape = transform.createTransformedShape(campShape);
		
		// Subtract off the shape of the camp.
		area.subtract(new Area(campShape));
		
		return area;
	}
	
}
