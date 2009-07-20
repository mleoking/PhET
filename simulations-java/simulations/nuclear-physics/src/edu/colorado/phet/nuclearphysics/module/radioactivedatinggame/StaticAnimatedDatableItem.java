package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.EventObject;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * An animated datable item that has a static animation sequence, meaning
 * one that doesn't change regardless of whatever else is happening within the
 * model or is commanded by the user.
 * 
 * @author John Blanco
 */
public abstract class StaticAnimatedDatableItem extends AnimatedDatableItem {

	private final ModelAnimationInterpreter _animationIterpreter;

	public StaticAnimatedDatableItem(String name, List<String> resourceImageNames, Point2D center, double width,
			double rotationAngle, double age, ConstantDtClock clock, double ageAdjustmentFactor, boolean isOrganic) {

		super(name, resourceImageNames, center, width, rotationAngle, age, clock,
				ageAdjustmentFactor, isOrganic);
		
		// Create (initialize) the animation sequence.
		AnimationSequence animationSequence = createAnimationSequence();
		
		// Create the animation interpreter that will execute the static animation.
		_animationIterpreter = new ModelAnimationInterpreter(this, animationSequence );
		
		// Register with the animation interpreter for any animation events
		// that occur during the interpretation of the sequence.
		_animationIterpreter.addListener(new ModelAnimationInterpreter.Listener(){
			public void animationNotificationEventOccurred(EventObject event) {
				handleAnimationEvent(event);
			}
		});
	}

	protected abstract AnimationSequence createAnimationSequence();
	
    protected void handleClockTicked(ClockEvent clockEvent){
    	super.handleClockTicked(clockEvent);
        _animationIterpreter.setTime(getClock().getSimulationTime() * getTimeConversionFactor() - getBirthTime());
    }
}
