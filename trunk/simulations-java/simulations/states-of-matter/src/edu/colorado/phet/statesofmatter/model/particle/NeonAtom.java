/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.model.AtomType;

/**
 * The class represents a single atom of neon in the model.
 *
 * @author John Blanco
 */
public class NeonAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 154;    // In picometers.
    private static final double MASS = 20.1797; // In atomic mass units.
    private static final double SIGMA = 308;    // In picometers.
    private static final double EPSILON = 32.8; // epsilon/k-Boltzmann is in Kelvin.
    protected static final AtomType ATOM_TYPE = AtomType.NEON;
    
    public NeonAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
    public static double getEpsilon(){
        return EPSILON;
    }

    public static double getSigma(){
        return SIGMA;
    }
    
	public AtomType getType() {
		return ATOM_TYPE;
	}
}
