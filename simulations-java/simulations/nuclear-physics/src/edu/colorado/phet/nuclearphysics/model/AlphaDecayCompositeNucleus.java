/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

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

	protected double _alphaDecayTime = 0;
	private double _startTime;  // Simulation time at which this nucleus was created or last reset.
	private boolean _hasDecayed = false;
	
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

	public double getDecayTime() {
	    return _alphaDecayTime;
	}
	
	/**
	 * Get a value that indicates how long this nucleus has existed without
	 * having decayed, or the amount of time that it was around prior to
	 * decay.
	 * 
	 * @return - pre-decay time of existence, in seconds.
	 */
	public double getElapsedPreDecayTime(){
		if (hasDecayed()){
			return _alphaDecayTime - _startTime;
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
	    _alphaDecayTime = calculateDecayTime();
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
	    
	    // See if alpha decay should occur.
	    if ((_alphaDecayTime != 0) && (clockEvent.getSimulationTime() >= _alphaDecayTime ))
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
	        
	        // Set the decay time to 0 to indicate that no more tunneling out
	        // should occur.
	        _alphaDecayTime = 0;
	    }
	}

	abstract protected void updateAgitationFactor();
	abstract protected double calculateDecayTime();
	abstract protected double getHalfLife();
}