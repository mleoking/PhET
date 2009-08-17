/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;
import edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.AnimatedDatableItem.TimeUpdater;

/**
 * This class implements the behavior of a model element that represents a
 * volcano that can can be dated by radiometric means, and that can erupt,
 * and that sends out the appropriate animation notifications when it does.
 * 
 * @author John Blanco
 */
public class EruptingVolcano extends StaticAnimatedDatableItem {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final double PRE_ERUPTION_INITIAL_AGE = HalfLifeInfo.convertYearsToMs(1E9);
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public EruptingVolcano(ConstantDtClock clock, Point2D center, double width, double timeConversionFactor) {
		super("Volcano", Arrays.asList( "volcano_cool.png","volcano_hot.png" ), center, width, 0, 0, clock , 
				timeConversionFactor, false );
	}

    protected AnimationSequence createAnimationSequence() {
        TimeUpdater timeUpdater=new TimeUpdater(0, HalfLifeInfo.convertHoursToMs( 10 ));
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        Random rand = new Random();
        
        // Shake back and forth.
        double maxXShakePerStep = 0.3;
        double maxYShakePerStep = 0.1;
        Point2D.Double shakeTranslation = new Point2D.Double();
        int shakeSteps = 10;  // Must be divisible by 2 or volcano will end up in different spot.
        for (int i = 0; i < shakeSteps / 2; i++){
        	double xTranslation = maxXShakePerStep * rand.nextDouble();
        	double yTranslation = maxYShakePerStep * rand.nextDouble();
        	shakeTranslation.setLocation(xTranslation, yTranslation);
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), shakeTranslation, 0, 1.0, 0, 0, 0, null));
        	shakeTranslation.setLocation(-xTranslation, -yTranslation);
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), shakeTranslation, 0, 1.0, 0, 0, 0, null));
        }
        
        // Add the sending of the "closure possible" event.
        animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0,
        		new RadiometricClosureEvent(this, RadiometricClosureState.CLOSURE_POSSIBLE)));

        // Fade image to hot volcano while continuing to shake.
        int fadeSteps = 60;  // Must be divisible by 2.
        double fadeAmountPerStep = 1 / (double)fadeSteps;
        for (int i = 0; i < fadeSteps; i += 2){
        	double xTranslation = maxXShakePerStep * rand.nextDouble();
        	double yTranslation = maxYShakePerStep * rand.nextDouble();
        	shakeTranslation.setLocation(xTranslation, yTranslation);
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), shakeTranslation, 0, 1.0, 0, 0,
        			fadeAmountPerStep, null));
        	shakeTranslation.setLocation(-xTranslation, -yTranslation);
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), shakeTranslation, 0, 1.0, 0, 0,
        			fadeAmountPerStep, null));
        }

        // More shaking.
        shakeSteps = 90;  // Must be divisible by 2 or volcano will end up in different spot.
        for (int i = 0; i < shakeSteps / 2; i++){
        	double xTranslation = maxXShakePerStep * rand.nextDouble();
        	double yTranslation = maxYShakePerStep * rand.nextDouble();
        	shakeTranslation.setLocation(xTranslation, yTranslation);
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), shakeTranslation, 0, 1.0, 0, 0, 0, null));
        	shakeTranslation.setLocation(-xTranslation, -yTranslation);
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), shakeTranslation, 0, 1.0, 0, 0, 0, null));
        }
        
        // Fade back to dormant-looking image.
        fadeSteps = 70;  // Must be divisible by 2.
        fadeAmountPerStep = -1 / (double)fadeSteps;
        for (int i = 0; i < fadeSteps; i++){
        	animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0,
        			fadeAmountPerStep, null));
        }
        
        // Add the sending of the "closure occurred" event.
        animationSequence.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0,
        		new RadiometricClosureEvent(this, RadiometricClosureState.CLOSED)));
        
        return new StaticAnimationSequence(animationSequence);
    }

    /**
     * The volcano is a little unique in that it has a radiometric age before
     * it does anything, which then gets reset to zero while erupting, and
     * then it starts to age again.
     */
	@Override
	public double getRadiometricAge() {
		double radiometricAge;
		
		if (getClosureState() == RadiometricClosureState.CLOSURE_NOT_POSSIBLE){
			// This means that the eruption hasn't started, so show the per-eruption age.
			radiometricAge = PRE_ERUPTION_INITIAL_AGE + getTotalAge();
		}
		else if (getClosureState() == RadiometricClosureState.CLOSURE_POSSIBLE){
			// This indicates that the volcano is erupting, so the radiometric age is 0.
			radiometricAge = 0;
		}
		else {
			// Done erupting.  The base class implementation is now relevant.
			radiometricAge = super.getRadiometricAge();
		}
		
		return radiometricAge;
	}
}
