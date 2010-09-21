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
    // Constructor
    //------------------------------------------------------------------------

	public AlphaDecayCompositeNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons, 1);
	}

	/**
	 * Resets the nucleus to its original state, before any alpha decay has
	 * occurred.
	 * 
	 * @param alpha - Particle that had previously tunneled out of this nucleus.
	 */
	public void reset(AlphaParticle alpha) {
	    
		super.reset();
		
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
	        notifyNucleusChangeEvent(null);
	    }
	    
	    // On this tab, the nucleus activates right away.
	    activateDecay();
	}

	/**
	 * Override of the decay function, needed because an alpha particle must
	 * tunnel out of the nucleus upon decay. 
	 */
	@Override
	protected void decay(ClockEvent clockEvent) {
		
		super.decay(clockEvent);
		
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
                notifyNucleusChangeEvent(byProducts);
                break;
            }
        }
	}
}