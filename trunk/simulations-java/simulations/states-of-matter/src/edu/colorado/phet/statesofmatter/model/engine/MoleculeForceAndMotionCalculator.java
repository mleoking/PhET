/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

/**
 * This interface is used to control an object that simulates the interaction
 * of a set of molecules.  Through this interface the simulation can be
 * stepped, reset, etc.
 * 
 * @author John Blanco
 *
 */
public interface MoleculeForceAndMotionCalculator {

	/**
	 * Do the next set of calculations of force and motion.
	 */
	public void updateForcesAndMotion();
}
