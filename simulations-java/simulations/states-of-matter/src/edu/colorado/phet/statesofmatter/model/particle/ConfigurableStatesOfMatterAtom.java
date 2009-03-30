package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.AtomType;

/**
 * This class represents an atom that has configurable radius and dual-
 * particle interaction potential.
 * 
 * @author John Blanco
 */
public class ConfigurableStatesOfMatterAtom extends StatesOfMatterAtom {

	public static final double DEFAULT_INTERACTION_POTENTIAL = StatesOfMatterConstants.MAX_EPSILON / 2;
    public static final double DEFAULT_RADIUS = 175;    // In picometers.
    private static final double MASS = 25; // In atomic mass units.

	
	private double m_dualParticleInteractionPotential = DEFAULT_INTERACTION_POTENTIAL;  // Interaction potential for a pair of this type of atom.
	
	public ConfigurableStatesOfMatterAtom(double x, double y) {
		super(x, y, DEFAULT_RADIUS, MASS);
	}

	public AtomType getType() {
		return AtomType.ADJUSTABLE;
	}

	public double getInteractionPotential() {
		return m_dualParticleInteractionPotential;
	}

	public void setInteractionPotential( double particleInteractionPotential) {
		m_dualParticleInteractionPotential = particleInteractionPotential;
	}
	
	public void setRadius(double radius){
		m_radius = radius;
		notifyRadiusChanged();
	}
}
