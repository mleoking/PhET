package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * This class defines a molecule of Lactose, which is a combination of one
 * molecule of Glucose and one of Galactose.  This class is intended for use
 * primarily in constructing the visual representations of other molecules
 * that bind to lactose within the sim, and is not intended to be directly
 * used in the model.
 * 
 * @author John Blanco
 */
public class Lactose {
	
    public Area area;

    public Lactose() {
		Shape glucoseShape = new Glucose().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	-glucoseShape.getBounds2D().getWidth()/2, 0 );
		Shape glucoseShiftedLeft = transform.createTransformedShape(glucoseShape);
		Shape galactoseShape = new Galactose().getShape();
		transform.setToTranslation(	galactoseShape.getBounds2D().getWidth()/2, 0 );
		Shape galactoseShiftedRight = transform.createTransformedShape(galactoseShape);
		area = new Area(glucoseShiftedLeft);
		area.add(new Area(galactoseShiftedRight));
	}
    
    public Shape getShape(){
    	return new Area(area);
    }
}
