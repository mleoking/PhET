/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents a single atom of oxygen in the model.
 *
 * @author John Blanco
 */
public class OxygenAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 152;
    private static final double MASS = 1.0; // TODO: JPB TBD - This is not currently used, clean up eventually.

    // TODO: JPB TBD - These values are guesses, need to get real ones.
    private static final double SIGMA = 324;    // In picometers.
    private static final double EPSILON = 200;  // epsilon/k-boltzman is in Kelvin.

    
    public OxygenAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
    public static double getEpsilon(){
        return EPSILON;
    }

    public static double getSigma(){
        return SIGMA;
    }
}
