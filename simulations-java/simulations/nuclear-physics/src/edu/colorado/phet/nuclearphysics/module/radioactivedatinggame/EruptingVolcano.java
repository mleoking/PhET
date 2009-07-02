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
	
	// Animation sequence.
	private static ArrayList<ModelAnimationDelta> ANIMATION_SEQUENCE = new ArrayList<ModelAnimationDelta>();
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private final ConstantDtClock _clock;
	private final ClockAdapter _clockAdapter;
	private double age = 0; // Age in milliseconds of this datable item.
	private final ModelAnimationDeltaInterpreter animationIterpreter;
	
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
		
		// Create the animation interpreter that will execute the animation.
		animationIterpreter = new ModelAnimationDeltaInterpreter(this, ANIMATION_SEQUENCE);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
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
		Point2D GROWTH_COMPENSATION = new Point2D.Double(0, 0.6);
		
		// Shake.
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(10E6),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(20E6),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(30E6),  new Point2D.Double(-0.3, 0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(40E6),  new Point2D.Double(0.3, -0.10), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(50E6),  new Point2D.Double(0, 0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(60E6),  new Point2D.Double(0, -0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(70E6),  new Point2D.Double(-0.2, -0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(80E6),  new Point2D.Double(0.2, 0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(90E6),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(100E6),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
		
		// Switch image to hot volcano.
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(110E6), null, 0, 1.0, 1, 0, 0));
		
		// More shaking.
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(120E6),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(130E6),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(140E6),  new Point2D.Double(-0.3, 0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(150E6),  new Point2D.Double(0.3, -0.10), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(160E6),  new Point2D.Double(0, 0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(170E6),  new Point2D.Double(0, -0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(180E6),  new Point2D.Double(-0.2, -0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(190E6),  new Point2D.Double(0.2, 0.1), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(200E6),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
		ANIMATION_SEQUENCE.add(new ModelAnimationDelta(MultiNucleusDecayModel.convertYearsToMs(210E6),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
	}
}
