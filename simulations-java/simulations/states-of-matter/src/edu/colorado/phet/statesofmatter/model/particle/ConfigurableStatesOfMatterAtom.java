package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.AtomType;

/**
 * This class represents an atom that has configurable radius.
 * 
 * @author John Blanco
 */
public class ConfigurableStatesOfMatterAtom extends StatesOfMatterAtom {

	public static final double DEFAULT_INTERACTION_POTENTIAL = StatesOfMatterConstants.MAX_EPSILON / 2;
    public static final double DEFAULT_RADIUS = 175;    // In picometers.
    private static final double MASS = 25; // In atomic mass units.

	
	public ConfigurableStatesOfMatterAtom(double x, double y) {
		super(x, y, DEFAULT_RADIUS, MASS);
	}

	public AtomType getType() {
		return AtomType.ADJUSTABLE;
	}

	public void setRadius(double radius){
		m_radius = radius;
		notifyRadiusChanged();
	}
}
