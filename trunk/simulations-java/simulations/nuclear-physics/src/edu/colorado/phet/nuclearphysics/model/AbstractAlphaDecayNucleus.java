/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;

/**
 * This class contains much of the behavior that is common to all nuclei that
 * exhibit alpha decay.
 * 
 * @author John Blanco
 */
public abstract class AbstractAlphaDecayNucleus extends AbstractDecayNucleus {

	public AbstractAlphaDecayNucleus(NuclearPhysicsClock clock, Point2D position,
			int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons);
	}

	/**
	 * This method lets this model element know that the clock has ticked.  In
	 * response we check if it is time to decay.
	 */
	protected void handleClockTicked(ClockEvent clockEvent) {
	    super.handleClockTicked( clockEvent );
	    
	    // See if this nucleus is active, i.e. moving towards decay.
	    if (_decayTime != 0){
	     
	    	if (!_paused){
	        	// See if alpha decay should occur.
		        if (clockEvent.getSimulationTime() >= _decayTime ) {
		            // It is time to decay.  Cause alpha decay by generating an
		        	// alpha particle and reducing our atomic weight.
		            ArrayList byProducts = new ArrayList();
		            byProducts.add( new AlphaParticle(_position.getX(), _position.getY()));
		            _numNeutrons -= 2;
		            _numProtons -= 2;
		
		            // Set the final value for the activation time.
		            _activatedLifetime += clockEvent.getSimulationTimeChange();
		            
		            // Send out the decay event to all listeners.
		            notifyAtomicWeightChanged(byProducts);
		            
		            // Set the decay time to 0 to indicate that decay has occurred and
		            // should not occur again.
		            _decayTime = 0;
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
}
