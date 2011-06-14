// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * This is a "convenience class" and exists for one purpose: to supply the
 * shape of a lactose molecule to anyone who needs it.
 * 
 * @author John Blanco
 */
public class Lactose {
	
    /**
     * Not meant to be instantiated.
     */
    private Lactose( ) { }
    
    public static Shape getShape(){
		// Create an overall shape for this composite element.
		Shape glucoseShape = SimpleSugar.createShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	-glucoseShape.getBounds2D().getWidth()/2, 0 );
		Shape glucoseShiftedLeft = transform.createTransformedShape(glucoseShape);
		Shape galactoseShape = SimpleSugar.createShape();
		transform.setToTranslation(	galactoseShape.getBounds2D().getWidth()/2, 0 );
		Shape galactoseShiftedRight = transform.createTransformedShape(galactoseShape);
		Area area = new Area(glucoseShiftedLeft);
		area.add(new Area(galactoseShiftedRight));
		return area;
    }
}
