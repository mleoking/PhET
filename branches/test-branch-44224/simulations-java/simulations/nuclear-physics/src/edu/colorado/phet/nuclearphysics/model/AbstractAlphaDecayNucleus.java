/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;

/**
 * This class contains much of the behavior that is common to all nuclei that
 * exhibit alpha decay.
 * 
 * @author John Blanco
 */
public abstract class AbstractAlphaDecayNucleus extends AtomicNucleus {

	public AbstractAlphaDecayNucleus(NuclearPhysicsClock clock, Point2D position,
			int numProtons, int numNeutrons) {
		super(clock, position, numProtons, numNeutrons);
	}
	
	/**
	 * Take the actions that simulate alpha decay.
	 */
	protected void decay( ClockEvent clockEvent ){
		
		super.decay(clockEvent);
		
		_numNeutrons -= 2;
		_numProtons -= 2;
        ArrayList byProducts = new ArrayList();
        byProducts.add( new AlphaParticle(_position.getX(), _position.getY()));

        // Send out the decay event to all listeners.
        notifyNucleusChangeEvent(byProducts);
	}
}
