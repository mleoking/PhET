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
public class Lactose2 extends CompositeModelElement {
	
    public Area area;

    /**
     * This constructor uses existing glucose and galactose molecules.
     * 
     * @param glucoseMolecule
     * @param galactoseMolecule
     */
    public Lactose2( Glucose glucoseMolecule, Galactose galactoseMolecule ) {
    	
    	// Offset the positions of the molecules so that the binding point is
    	// in the same place.
		Dimension2D bindingPointOffset = glucoseMolecule.getBindingPointForElement(galactoseMolecule.getType()).getOffset();
		glucoseMolecule.setPosition(-bindingPointOffset.getWidth(), -bindingPointOffset.getHeight());
		galactoseMolecule.setPosition(bindingPointOffset.getWidth(), bindingPointOffset.getHeight());
		
		// Add these molecules to the list of constituents.
		addModelElement(glucoseMolecule);
		addModelElement(galactoseMolecule);
    }
    
    /**
     * This constructor creates new glucose and galactose molecules.
     */
    public Lactose2() {
    	this(new Glucose(), new Galactose());
	}
    
    public Shape getShape(){
    	return new Area(area);
    }
}
