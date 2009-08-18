/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * Base class for composite nuclei (i.e. nuclei that are made up of multiple
 * individual protons and neutrons) that exhibit beta decay.
 * 
 * @author John Blanco
 */
public abstract class BetaDecayCompositeNucleus extends CompositeAtomicNucleus {

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	protected double   _timeUntilDecay = 0;
	protected double   _startTime;         // Simulation time at which this nucleus was created or last reset.
	protected double   _pauseStartTime;    // Time at which this nucleus was paused.
	protected double   _preDecayLifeTime;  // Milliseconds of existence prior to decay.
	private boolean    _hasDecayed = false;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public BetaDecayCompositeNucleus(NuclearPhysicsClock clock,
			Point2D position, int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons);
		_startTime = clock.getSimulationTime();
	}

	public BetaDecayCompositeNucleus(NuclearPhysicsClock clock,
			Point2D position, ArrayList constituents) {
		super(clock, position, constituents);
		_startTime = clock.getSimulationTime();
	}

	/**
	 * Resets the nucleus to its original state, before any beta decay has
	 * occurred.
	 */
	public void reset() {
	    
	    // Reset the decay time.
	    _timeUntilDecay = calculateDecayTime();
	    _hasDecayed = false;
	    _startTime = _clock.getSimulationTime();
	    
	    // Change one proton into a neutron.
	    _numNeutrons++;
	    _numProtons++;
	
        // Update our agitation level.
        updateAgitationFactor();
	        
        // Let the listeners know that the nucleus has changed.
        notifyNucleusChangeEvent(null);
	}
	
	/**
	 * This method lets this model element know that the clock has ticked.  In
	 * response, the nucleus generally 'agitates' a bit, may also perform some
	 * sort of decay, and may move.
	 */
	protected void handleClockTicked(ClockEvent clockEvent) {
	    super.handleClockTicked( clockEvent );
	    
	    if (!_paused){
		    // See if beta decay should occur.
		    if ((_timeUntilDecay != Double.POSITIVE_INFINITY) && (getTimeOfExistence() >= _timeUntilDecay))
		    {
		    	// TODO: Beta decay not yet implemented.
		    	System.err.println("Warning: Beta decay is not yet implemented.");
		        
		    	// Mark that decay has happened.
		    	_hasDecayed = true;
		    	_preDecayLifeTime = _timeUntilDecay;
	
		    	// Set the decay time to infinity to indicate that no more tunneling out
		        // should occur.
		        _timeUntilDecay = Double.POSITIVE_INFINITY;
		    }
	    }
	}
	
	public void setPaused(boolean paused) {
		if (_paused != paused){
			super.setPaused(paused);
			if (paused){
				// Record the time at which the pause began.
				_pauseStartTime = _clock.getSimulationTime();
			}
			else{
				// Update the start time for this nucleus based on
				// the amount of time that it was paused.
				_startTime = _startTime + (_clock.getSimulationTime() - _pauseStartTime);
			}
		}
	}

	/**
	 * Get the time for which this nucleus has existed.
	 * 
	 * @return - Existence time in milliseconds.
	 */
	protected double getTimeOfExistence(){
		return (_clock.getSimulationTime() - _startTime);
	}

	abstract protected void updateAgitationFactor();
	abstract protected double calculateDecayTime();
	abstract public double getHalfLife();
}