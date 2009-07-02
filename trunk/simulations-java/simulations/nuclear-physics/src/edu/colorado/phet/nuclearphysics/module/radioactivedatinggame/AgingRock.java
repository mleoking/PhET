/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.common.Cleanupable;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * rock that can be dated by radiometric means, and that starts off looking
 * hot and then cools down.
 * 
 * @author John Blanco
 */
public class AgingRock extends DatableItem implements Cleanupable {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final String NAME = "Aging Rock"; // For debugging, no need to translate. 
	private static final String HOT_ROCK_IMAGE_NAME = "rock_1.png";
	private static final String COOL_ROCK_IMAGE_NAME = "rock_1.png";
	
	// Age adjustment factor - used to convert the amount of simulation time
	// into the age of the item so that users don't have to wait around for
	// thousands of years for anything to happen.
	private static final double AGE_ADJUSTMENT_FACTOR = MultiNucleusDecayModel.convertYearsToMs(1E9) / 5000;
	
	// Animation sequence.
	private static ArrayList<ModelAnimationDelta> ANIMATION_SEQUENCE = new ArrayList<ModelAnimationDelta>();
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	private final ModelAnimationDeltaInterpreter animationIterpreter;
	private final ClockAdapter _clockAdapter;
	private double age = 0; // Age in milliseconds of this datable item.
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public AgingRock(ConstantDtClock clock, Point2D center, double width) {
		super(NAME, HOT_ROCK_IMAGE_NAME, center, width, 0, 0);
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
		
		// Create the animation interpreter that will execute the animation.
		animationIterpreter = new ModelAnimationDeltaInterpreter(this, ANIMATION_SEQUENCE);
	}

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	public void cleanup() {
		_clock.removeClockListener(_clockAdapter);
	}
	
	private void handleClockTicked(){
		age = _clock.getSimulationTime() * AGE_ADJUSTMENT_FACTOR;
		animationIterpreter.setTime(age);
	}

	private void handleSimulationTimeReset(){
		age = 0;
	}

	@Override
	public double getAge() {
		return age;
	}
	
	//------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
	// will change as it ages.
    //------------------------------------------------------------------------
	static{
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(120E6),  new Point2D.Double(-0.35, .6), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(130E6),  new Point2D.Double(-0.35, .6), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(140E6),  new Point2D.Double(-0.35, .6), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(150E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(160E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(170E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(180E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(190E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(200E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(210E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(220E6),  new Point2D.Double(-0.35, 0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(230E6),  new Point2D.Double(-0.35, 0.4), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(240E6),  new Point2D.Double(-0.35, 0.3), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(250E6),  new Point2D.Double(-0.35, 0.2), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(260E6),  new Point2D.Double(-0.35, 0.1), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(270E6),  new Point2D.Double(-0.35, 0), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(280E6),  new Point2D.Double(-0.35, -0.1), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(290E6),  new Point2D.Double(-0.35, -0.1), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(300E6),  new Point2D.Double(-0.35, -0.4), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(310E6),  new Point2D.Double(-0.35, -0.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(320E6),  new Point2D.Double(-0.35, -0.6), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(330E6),  new Point2D.Double(-0.35, -.9), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(340E6),  new Point2D.Double(-0.35, -1), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(350E6),  new Point2D.Double(-0.35, -1.1), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(360E6),  new Point2D.Double(-0.35, -1.2), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(370E6),  new Point2D.Double(-0.35, -1.2), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(380E6),  new Point2D.Double(-0.35, -1.3), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(390E6),  new Point2D.Double(-0.35, -1.3), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(300E6),  new Point2D.Double(-0.35, -1.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(310E6),  new Point2D.Double(-0.35, -1.5), 0, 1.07, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(320E6),  new Point2D.Double(-0.35, -1.5), 0, 1.07, 0, 0, 0));
		
	}
}
