package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.Cleanupable;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jul 2, 2009
 * Time: 3:24:52 PM
 */
public abstract class AnimatedDatableItem extends DatableItem implements Cleanupable{// Age adjustment factor - used to convert the amount of simulation time
// into the age of the item so that users don't have to wait around for
// thousands of years for anything to happen.

    public static interface ImageGetter{
        ArrayList<String> getResourceImageNames();
    }

    protected ConstantDtClock _clock;
    private double ageAdjustmentFactor;
    protected ClockAdapter _clockAdapter;
    private double age = 0; // Age in milliseconds of this datable item.
    protected ModelAnimationDeltaInterpreter animationIterpreter;

    public AnimatedDatableItem( String name, List<String> resourceImageNames, Point2D center, double width, double rotationAngle, double age, ConstantDtClock clock,double ageAdjustmentFactor ) {
        super( name, resourceImageNames, center, width, rotationAngle, age );
        _clock = clock;
        this.ageAdjustmentFactor = ageAdjustmentFactor;
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


        //------------------------------------------------------------------------
        // The animation sequence that defines how the appearance of the tree
        // will change as it ages.
        //------------------------------------------------------------------------

		// Create the animation interpreter that will execute the animation.
		animationIterpreter = new ModelAnimationDeltaInterpreter(this, getAnimationSequence() );
    }

    protected abstract AnimationSequence getAnimationSequence();



    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    protected void handleClockTicked(){
        age = _clock.getSimulationTime() * ageAdjustmentFactor;
        animationIterpreter.setTime(age);
    }

    public void cleanup() {
		_clock.removeClockListener(_clockAdapter);
	}

    protected void handleSimulationTimeReset(){
        age = 0;
    }

    @Override
	public double getAge() {
        return age;
    }

    public static class TimeUpdater {
        private double time;
        private double dt;

        public TimeUpdater( double startTimeMS, double dtMS ) {
            this.time = startTimeMS;
            this.dt = dtMS;
        }

        double updateTime() {
            time = time + dt;
            return time;
        }
    }
}
