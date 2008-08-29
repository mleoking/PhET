/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents a single atom of neon in the model.
 *
 * @author John Blanco
 */
public class ArgonAtom extends StatesOfMatterAtom {

    public static final double RADIUS = 181;    // In picometers.
    private static final double MASS = 39.948;  // In atomic mass units.
    private static final double SIGMA = 362;    // In picometers.
    private static final double EPSILON = 100;  // epsilon/k-boltzmann is in Kelvin.
    
    public ArgonAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
    public static double getEpsilon(){
        return EPSILON;
    }

    public static double getSigma(){
        return SIGMA;
    }
}
