/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.model.AtomType;

/**
 * The class represents an atom where some of the parameters can be changed by
 * the user.
 *
 * @author John Blanco
 */
public class UserDefinedAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 195;    // In picometers.
    private static final double MASS = 14;      // In atomic mass units.
    private static final double SIGMA = 390;    // In picometers.
    private static final double EPSILON = 155;   // epsilon/k-Boltzmann is in Kelvin.
    protected static final AtomType ATOM_TYPE = AtomType.ADJUSTABLE;
    
    public UserDefinedAtom(double xPos, double yPos){
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
