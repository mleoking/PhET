/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * Abstract base class for nuclei that will autonomously decay after some
 * period of time.
 * 
 * @author John Blanco
 */
public abstract class AbstractDecayNucleus extends AtomicNucleus implements NuclearDecayControl {

	protected double _decayTime = 0;
	protected double _activatedLifetime = 0;
	protected double _halfLife = 0;
	protected boolean _paused = false;
	protected final double _decayTimeScalingFactor;

	/**
	 * This constructor includes a decay time scaling factor that can be used
	 * to make the atom decay in a reasonable amount of time for a user,
	 * instead of forcing them to wait around for like thousands of years.
	 * 
	 * @param clock
	 * @param position
	 * @param numProtons
	 * @param numNeutrons
	 * @param decayTimeScalingFactor - smaller means sooner decay, larger means longer.
	 */
	public AbstractDecayNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons,
			double decayTimeScalingFactor) {
		super(clock, position, numProtons, numNeutrons);
		_decayTimeScalingFactor = decayTimeScalingFactor;
	}
	
	public AbstractDecayNucleus(NuclearPhysicsClock clock, Point2D position,
			int numProtons, int numNeutrons) {
		this(clock, position, numProtons, numNeutrons, 1);  // Use default decay time scaling factor.
	}

	public double getDecayTime() {
	    return _decayTime;
	}

	public double getHalfLife() {
	    return _halfLife;
	}
	
	public double getDecayTimeScalingFactor() {
		return _decayTimeScalingFactor;
	}

	/**
	 * Set the half life for this nucleus.
	 * 
	 * @param halfLife - Half life in milliseconds.
	 */
	public void setHalfLife(double halfLife) {
	    _halfLife = halfLife;
	}
	
	/**
	 * This method lets this model element know that the clock has ticked.  In
	 * response we check if it is time to decay.
	 */
	protected void handleClockTicked( ClockEvent clockEvent ) {
	    super.handleClockTicked( clockEvent );
	    
	    // See if this nucleus is active, i.e. moving toward decay.
	    if (_decayTime != 0){
	     
	    	if (!_paused){
	        	// See if decay should occur.
		        if (clockEvent.getSimulationTime() >= _decayTime ) {
		            // It is time to decay.
		        	decay( clockEvent );
		        }
		        else{
		        	// Not decaying yet, so updated the activated lifetime.
		        	_activatedLifetime += clockEvent.getSimulationTimeChange();
		        }
	    	}
	    	else{
	    		// This atom is currently paused, so extend the decay time.
	    		_decayTime += clockEvent.getSimulationTimeChange();
	    	}
	    }
	}
	
	/**
	 * This method is called when decay occurs, and it defines the behavior
	 * exhibited by the nucleus when it decays.  This method should be
	 * implemented by all subclasses.
	 */
	protected abstract void decay( ClockEvent clockEvent );
	
	public boolean isPaused() {
		return _paused;
	}

	public void setPaused(boolean paused) {
		_paused = paused;
	}

	/**
	 * This method starts the nucleus moving towards decay.
	 */
	public abstract void activateDecay();

	/**
	 * Returns a boolean value indicating whether the nucleus has decayed.
	 * This will return false if the nucleus has not been activated.
	 */
	public abstract boolean hasDecayed();

	/**
	 * Return true if decay is currently active and false if not.  Note that
	 * this will return false if the nucleus has already decayed.
	 */
	public boolean isDecayActive() {
		if (_decayTime != 0){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * Returns a value indicating how long in terms of simulation time the
	 * nucleus has been active without having decayed.
	 * 
	 * @return Simulation time for which this nucleus has been activated, i.e.
	 * progressing towards decay.
	 */
	public double getActivatedSimTime() {
		return _activatedLifetime;
	}
	
	/**
	 * Returns a value indicating the amount of adjusted time that the nucleus
	 * has been active without decaying.  Adjusted time is based on the time
	 * adjustment factor that is used to scale the amount of time that a model
	 * element has experienced such that it will generally decay in a
	 * reasonable time frame (so that users aren't waiting around for
	 * thousands of years for decay to occur).
	 * 
	 * @return Adjusted time in milliseconds for which this nucleus has been
	 * activated, i.e. progressing towards decay.
	 */
	public double getAdjustedActivatedTime() {
		return _activatedLifetime / _decayTimeScalingFactor;
	}

	public void reset() {
	    // Reset the decay time to 0, indicating that it shouldn't occur
	    // until something changes.
	    _decayTime = 0;
	    _activatedLifetime = 0;
	
	    // Make sure we are not paused.
		_paused = false;
	}
}
