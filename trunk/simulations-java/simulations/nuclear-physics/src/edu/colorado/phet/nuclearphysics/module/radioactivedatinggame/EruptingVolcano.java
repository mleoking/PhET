/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * volcano that can can be dated by radiometric means, and that can erupt,
 * and that sends out the appropriate animation notifications when it does.
 * 
 * @author John Blanco
 */
public class EruptingVolcano extends DatableItem {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final String NAME = "Volcano"; // For debugging, no need to translate.
	
	// Images for the volcano.
	private static final String HOT_VOLCANO_IMAGE_NAME = "volcano_hot.png";
	private static final String DORMANT_VOLCANO_IMAGE_NAME = "volcano_cool.png";
	private static final ArrayList<String> VOLCANO_IMAGES = new ArrayList<String>();
	
	static{
		VOLCANO_IMAGES.add(DORMANT_VOLCANO_IMAGE_NAME);
		VOLCANO_IMAGES.add(HOT_VOLCANO_IMAGE_NAME);
	}
	
	// Age adjustment factor - used to convert the amount of simulation time
	// into the age of the item so that users don't have to wait around for
	// thousands of years for anything to happen.
	private static final double AGE_ADJUSTMENT_FACTOR = MultiNucleusDecayModel.convertYearsToMs(1E9) / 5000;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	private final ClockAdapter _clockAdapter;
	private double age = 0; // Age in milliseconds of this datable item.
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public EruptingVolcano(ConstantDtClock clock, Point2D center, double width) {
		super(NAME, VOLCANO_IMAGES, center, width, 0, 0);
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
