/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;

/**
 * This class contains much of the behavior that is common to all nuclei that
 * exhibit beta decay.
 * 
 * @author John Blanco
 */
public abstract class AbstractBetaDecayNucleus extends AtomicNucleus {

	public AbstractBetaDecayNucleus(NuclearPhysicsClock clock, Point2D position,
			int numProtons, int numNeutrons, double decayTimeScalingFactor) {
		super(clock, position, numProtons, numNeutrons, decayTimeScalingFactor);
	}
	
	/**
	 * Take the actions that simulate alpha decay.
	 */
	protected void decay( ClockEvent clockEvent ){
        ArrayList byProducts = new ArrayList();
        // TODO: Need to implement the actual decay.
        System.err.println(getClass().getName() + " - Warning: Beta decay not yet implemented.");
        _numNeutrons -= 1;
        _numProtons += 1;

        // Set the final value of the time that this nucleus existed prior to
        // decaying.
        _activatedLifetime += clockEvent.getSimulationTimeChange();
        
        // Send out the decay event to all listeners.
        notifyNucleusChangeEvent(byProducts);
        
        // Set the decay time to 0 to indicate that decay has occurred and
        // should not occur again.
        _decayTime = 0;
	}
}
