package edu.colorado.phet.genenetwork.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;

/**
 * This class defines a molecule of Lactose, which is a composite molecule
 * consisting of one molecule of Glucose and one of Galactose.
 * 
 * @author John Blanco
 */
public class Lactose extends CompositeModelElement {
	
    public Area area;

    /**
     * This constructor uses existing glucose and galactose molecules.
     * 
     * @param glucoseMolecule
     * @param galactoseMolecule
     */
    public Lactose( Glucose glucoseMolecule, Galactose galactoseMolecule ) {
    	
    	// Offset the positions of the molecules so that the binding point is
    	// in the same place.
		Dimension2D bindingPointOffset = glucoseMolecule.getAttachmentPointForElement(galactoseMolecule.getType()).getOffset();
		glucoseMolecule.setPosition(-bindingPointOffset.getWidth(), -bindingPointOffset.getHeight());
		galactoseMolecule.setPosition(bindingPointOffset.getWidth(), bindingPointOffset.getHeight());
		
		// Add these molecules to the list of constituents.
		addModelElement(glucoseMolecule);
		addModelElement(galactoseMolecule);
		
		// Set the motion strategy.
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
		
		// Create an overall shape for this composite element.
		Shape glucoseShape = glucoseMolecule.getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	-glucoseShape.getBounds2D().getWidth()/2, 0 );
		Shape glucoseShiftedLeft = transform.createTransformedShape(glucoseShape);
		Shape galactoseShape = galactoseMolecule.getShape();
		transform.setToTranslation(	galactoseShape.getBounds2D().getWidth()/2, 0 );
		Shape galactoseShiftedRight = transform.createTransformedShape(galactoseShape);
		area = new Area(glucoseShiftedLeft);
		area.add(new Area(galactoseShiftedRight));
    }
    
    /**
     * This constructor creates new glucose and galactose molecules.
     */
    public Lactose() {
    	this(new Glucose(null), new Galactose(null));
	}
    
    public Shape getShape(){
    	return new Area(area);
    }
}
