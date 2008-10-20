/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

/**
 * This is the base class for the objects that directly change the state of
 * the molecules within the multi-particle simulation.
 * 
 * @author John Blanco
 *
 */
public abstract class AbstractPhaseStateChanger implements PhaseStateChanger {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	public static final double SOLID_TEMPERATURE = 0.15;
	public static final double LIQUID_TEMPERATURE = 0.42;
	public static final double GAS_TEMPERATURE = 1.0;
    protected static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.3;  // In particle diameters.


	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	protected MultipleParticleModel2 m_model;
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public AbstractPhaseStateChanger( MultipleParticleModel2 model ) {
		m_model = model;
	}

	public void setPhase(int phaseID) {
		// Stubbed in base class. 
	}
}
