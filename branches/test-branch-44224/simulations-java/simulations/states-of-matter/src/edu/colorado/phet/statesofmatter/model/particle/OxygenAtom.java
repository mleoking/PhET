/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.model.AtomType;

/**
 * The class represents a single atom of oxygen in the model.
 *
 * @author John Blanco
 */
public class OxygenAtom extends StatesOfMatterAtom {
    
    public static final double RADIUS = 162;    // In picometers.
    private static final double MASS = 15.9994; // In atomic mass units.
    protected static final AtomType ATOM_TYPE = AtomType.OXYGEN;

    public OxygenAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
    
	public AtomType getType() {
		return ATOM_TYPE;
	}
}
