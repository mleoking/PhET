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

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public AlphaDecayCompositeNucleus(NuclearPhysicsClock clock,
			Point2D position, int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons);
	}

	public AlphaDecayCompositeNucleus(NuclearPhysicsClock clock,
			Point2D position, ArrayList constituents) {
		super(clock, position, constituents);
	}

	public double getDecayTime() {
	    return _alphaDecayTime;
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
}