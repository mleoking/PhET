/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine.kinetic;

/**
 * This interface is used to adjust the temperature for a system.
 *  
 * @author John Blanco
 */
public interface Thermostat {

	/**
	 * Set the temperature for the system.  Note that it is NOT implied that
	 * the internal temperature of the system will immediately go to this
	 * value - just that the system will move toward the value.
	 * 
	 * @param temperature - Target temperature for the system in arbitrary
	 * model units.
	 */
	public void setTargetTemperature(double temperature);
	
	/**
	 * Make an adjustment towards the target temperature using whatever
	 * internal algorithm the implementor is using.  This is meant to be
	 * called periodically as a simulation is running.
	 */
	public void adjustTemperature( );

	/**
	 * Adjust the temperature, providing the current values for the kinetic
	 * energy.  This version of this call is provided for the sake of
	 * efficiency, and allows the thermostat to avoid recalculating the
	 * kinetic energy of the molecular system if such a calculation has
	 * already been performed.
	 * 
	 * @param kineticEnergy
	 */
	public void adjustTemperature( double kineticEnergy );
}
