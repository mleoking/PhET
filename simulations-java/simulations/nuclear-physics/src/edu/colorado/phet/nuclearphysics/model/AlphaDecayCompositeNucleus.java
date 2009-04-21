/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * Base class for composite nuclei (i.e. nuclei that are made up of lots of
 * individual protons and neutrons) that exhibit alpha decay.
 * 
 * @author John Blanco
 */
public abstract class AlphaDecayCompositeNucleus extends CompositeAtomicNucleus {

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

	public AlphaDecayCompositeNucleus(NuclearPhysicsClock clock,
			Point2D position, int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons);
		_startTime = clock.getSimulationTime();
	}

	public AlphaDecayCompositeNucleus(NuclearPhysicsClock clock,
			Point2D position, ArrayList constituents) {
		super(clock, position, constituents);
		_startTime = clock.getSimulationTime();
	}

	/**
	 * Get a value that indicates how long this nucleus has existed without
	 * having decayed, or the amount of time that it was around prior to
	 * decay.
	 * 
	 * @return - pre-decay time of existence, in milliseconds.
	 */
	public double getElapsedPreDecayTime(){
		if (hasDecayed()){
			return _preDecayLifeTime;
		}
		else if (_paused){
			return _pauseStartTime - _startTime;
		}
		else{
			return _clock.getSimulationTime() - _startTime;
		}
	}
	
	public boolean hasDecayed(){
		return _hasDecayed;
	}

	/**
	 * Resets the nucleus to its original state, before any alpha decay has
	 * occurred.
	 * 
	 * @param alpha - Particle that had previously tunneled out of this nucleus.
	 */
	public void reset(AlphaParticle alpha) {
	    
	    // Reset the decay time.
	    _timeUntilDecay = calculateDecayTime();
	    _hasDecayed = false;
	    _startTime = _clock.getSimulationTime();
	    
	    if (alpha != null){
	        // Add the tunneled particle back to our list.
	        _constituents.add( 0, alpha );
	        _numAlphas++;
	        _numNeutrons += 2;
	        _numProtons += 2;
	        alpha.resetTunneling();
	
	        // Update our agitation level.
	        updateAgitationFactor();
	        
	        // Let the listeners know that the atomic weight has changed.
	        notifyAtomicWeightChanged(null);
	    }
	}
	
	/**
	 * This method lets this model element know that the clock has ticked.  In
	 * response, the nucleus generally 'agitates' a bit, may also perform some
	 * sort of decay, and may move.
	 */
	protected void handleClockTicked(ClockEvent clockEvent) {
	    super.handleClockTicked( clockEvent );
	    
	    if (!_paused){
		    // See if alpha decay should occur.
		    if ((_timeUntilDecay != Double.POSITIVE_INFINITY) && (getTimeOfExistence() >= _timeUntilDecay))
		    {
		        // Pick an alpha particle to tunnel out and make it happen.
		        for (int i = 0; i < _constituents.size(); i++)
		        {
		            if (_constituents.get( i ) instanceof AlphaParticle){
		                
		                // This one will do.  Make it tunnel.
		                AlphaParticle tunnelingParticle = (AlphaParticle)_constituents.get( i );
		                _constituents.remove( i );
		                _numAlphas--;
		                _numProtons -= 2;
		                _numNeutrons -= 2;
		                tunnelingParticle.tunnelOut( _position, _tunnelingRegionRadius + 1.0 );
		                
		                // Update our agitation factor.
		                updateAgitationFactor();
		                
		                // Notify listeners of the change of atomic weight.
		                ArrayList byProducts = new ArrayList(1);
		                byProducts.add( tunnelingParticle );
		                notifyAtomicWeightChanged(byProducts);
		                break;
		            }
		        }
		        
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