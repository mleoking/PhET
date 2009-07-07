package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.EventObject;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.common.Cleanupable;

/**
 * This class extends the datable item class to add animation and other time-
 * driven behaviors.
 */
public abstract class AnimatedDatableItem extends DatableItem implements Cleanupable{

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// This enum defines the possible states with respect to closure, which
	// is the time at which the item begins aging radiometrically and its
	// radioactive elements start decreasing.  For example, if the item is
	// organic, closure occurs when the item dies.
	public enum CLOSURE_STATE {
		CLOSURE_NOT_POSSIBLE,    // Closure cannot be forced.
		CLOSURE_POSSIBLE,        // Closure has not occurred, but could be forced.
		CLOSED                   // Closure has occurred.
	};
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
    protected ConstantDtClock _clock;
    private double ageAdjustmentFactor;
    protected ClockAdapter _clockAdapter;
    private double age = 0; // Age in milliseconds of this datable item.
    protected ModelAnimationInterpreter animationIterpreter;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
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
		animationIterpreter = new ModelAnimationInterpreter(this, getAnimationSequence() );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    protected abstract AnimationSequence getAnimationSequence();

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
    
    /**
     * Event used to convey information about changes to the closure state.
     */
    public static class RadiometricClosureEvent extends EventObject{

    	private final CLOSURE_STATE closureState;
    	
		public RadiometricClosureEvent(Object source, CLOSURE_STATE closureState) {
			super(source);
			this.closureState = closureState;
		}

		public CLOSURE_STATE getClosureState() {
			return closureState;
		}
    }
}
