/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents an atom that the user can change some of the values
 * for.
 *
 * @author John Blanco
 */
public class UserDefinedAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 100;    // In picometers.
    private static final double MASS = 14;      // In atomic mass units.
    private static final double SIGMA = 200;    // In picometers.
    private static final double EPSILON = 65;   // epsilon/k-Boltzmann is in Kelvin.

    
    public UserDefinedAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
    public static double getEpsilon(){
        return EPSILON;
    }

    public static double getSigma(){
        return SIGMA;
    }
}
