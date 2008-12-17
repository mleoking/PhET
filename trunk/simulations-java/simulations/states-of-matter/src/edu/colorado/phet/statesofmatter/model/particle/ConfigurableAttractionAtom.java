/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents a single atom for which the attraction (also known as
 * the interaction strength or epsilon) and be configured..
 *
 * @author John Blanco
 */
public class ConfigurableAttractionAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 175;    // In picometers.
    private static final double MASS = 25; // In atomic mass units.
    private static final double SIGMA = 308;    // In picometers.
    private static final double INITIAL_EPSILON = 85;   // epsilon/k-Boltzmann is in Kelvin.

    
    public ConfigurableAttractionAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
    public static double getEpsilon(){
        return INITIAL_EPSILON;
    }

    public static double getSigma(){
        return SIGMA;
    }
}
