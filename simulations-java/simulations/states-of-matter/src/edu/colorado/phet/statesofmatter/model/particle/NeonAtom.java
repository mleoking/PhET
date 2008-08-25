/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents a single atom of neon in the model.
 *
 * @author John Blanco
 */
public class NeonAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 154;
    private static final double MASS = 1.0; // TODO: JPB TBD - This is not currently used, clean up eventually.
    private static final double SIGMA = 2.0;
    private static final double EPSILON = 100;
    
    public NeonAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
    public static double getEpsilon(){
        return EPSILON;
    }

    public static double getSigma(){
        return SIGMA;
    }
}
