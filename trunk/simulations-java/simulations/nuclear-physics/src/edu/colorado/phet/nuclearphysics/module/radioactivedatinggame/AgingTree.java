/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.Cleanupable;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * tree that can be dated by radiometric means, and that grows, dies, and
 * falls over as time goes by.
 * 
 * @author John Blanco
 */
public class AgingTree extends DatableItem implements Cleanupable {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final String NAME = "Aging Tree"; // For debugging, no need to translate. 
	private static final String LIVING_TREE_IMAGE_NAME = "tree_1.png";
	private static final String DEAD_TREE_IMAGE_NAME = "dead_tree.png";
	
	// Age adjustment factor - used to convert the amount of simulation time
	// into the age of the item so that users don't have to wait around for
	// thousands of years for anything to happen.
	private static final double AGE_ADJUSTMENT_FACTOR = MultiNucleusDecayModel.convertYearsToMs(1000) / 5000;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	private final ClockAdapter _clockAdapter;
	private double age = 0; // Age in milliseconds of this datable item.
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public AgingTree(ConstantDtClock clock, Point2D center, double width) {
		super(NAME, LIVING_TREE_IMAGE_NAME, center, width, 0, 0);
		_clock = clock;
		
		// Create the adapter that will listen to the clock.
		_clockAdapter = new ClockAdapter(){
		    public void clockTicked( ClockEvent clockEvent ) {
		    	handleClockTicked();
		    }
		    public void simulationTimeReset( ClockEvent clockEvent ) {
		    	handleSimulationTimeReset();
		    }
		};
		_clock.addClockListener(_clockAdapter);
	}

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	public void cleanup() {
		_clock.removeClockListener(_clockAdapter);
	}
	
	private void handleClockTicked(){
		age = _clock.getSimulationTime() * AGE_ADJUSTMENT_FACTOR;
	}

	private void handleSimulationTimeReset(){
		age = 0;
	}

	@Override
	public double getAge() {
		return age;
	}
}
