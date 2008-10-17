/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

/**
 * This interface allows users to directly set the phase state (i.e. solid,
 * liquid, or gas) of the implementer.
 * 
 * @author John Blanco
 *
 */
public interface PhaseStateChanger {
	
	public static final int PHASE_SOLID = 1;
	public static final int PHASE_LIQUID = 2;
	public static final int PHASE_GAS = 3;
	
	public void setPhase( int phaseID );

}
