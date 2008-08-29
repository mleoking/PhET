/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents a single atom of hydrogen in the model.
 *
 * @author John Blanco
 */
public class HydrogenAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 120;     // In picometers.
    private static final double MASS = 1.00794;  // In atomic mass units.
    
    public HydrogenAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
}
