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
	
	/**
	 * Get the system pressure in model units.
	 * 
	 * @return - Internal system pressure.
	 */
	public double getPressure();
	
	/**
	 * Get the system temperature in model units.
	 * 
	 * @return - Internal system temperature.
	 */
	public double getTemperature();
	
	/**
	 * Set the scaled value for Epsilon, which is the parameter that
	 * defines the strength of particle interaction.
	 * 
	 * @param scaledEpsilon - A value for the interaction strength.  A
	 * value of zero signifies no interaction, 1 is the default amount, 2 is
	 * twice the default amount, and so on.
	 */
	public void setScaledEpsilon(double scaledEpsilon);
	
	/**
	 * Get the scaled value of the epsilon parameter, a.k.a. the interaction
	 * strength.
	 */
	public double getScaledEpsilon();
}
