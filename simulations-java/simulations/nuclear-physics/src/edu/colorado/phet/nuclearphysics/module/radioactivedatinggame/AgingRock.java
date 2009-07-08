/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * rock that can be dated by radiometric means, and that starts off looking
 * hot and then cools down.
 *
 * @author John Blanco
 */
public class AgingRock extends AnimatedDatableItem {

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingRock( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Rock", Arrays.asList( "molten_rock_large.png", "rock_volcanic_larger.png" ), center, width, 0, 
        		0, clock, EruptingVolcano.VOLCANO_AGE_FACTOR );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //------------------------------------------------------------------------
    protected AnimationSequence getAnimationSequence() {
        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 10E6 ) );
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        RadiometricClosureEvent closureOccurredEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSED);
        RadiometricClosureEvent closurePossibleEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSURE_POSSIBLE);
        
        // Rock flies out of volcano in a parabolic arc and gets larger in
        // order to look like it is getting closer.  Also spins.
        int flightSteps = 50;
        double totalGrowthFactor = 10;
        double growthPerStep = Math.pow(totalGrowthFactor, 1/(double)flightSteps);
        double totalRotation = -8 * Math.PI;
        double rotationPerStep = totalRotation / flightSteps;
        double arcHeightControl = 0.04; // Higher for higher arc, lower for lower arc.
        double flightXTranslation = -0.38; // Higher positive or negative number move further.
        for (int i = 0; i < flightSteps; i++){
        	double flightYTranslation = arcHeightControl * (((double)flightSteps * 0.42) - i);
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        			new Point2D.Double( flightXTranslation, flightYTranslation ), rotationPerStep, growthPerStep, 0, 
        			0, 0, null ) );
        }
        
        // Rock should be sitting on the ground now, so closure is possible.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0, closurePossibleEvent ) );

        // Pause for a while.
        for (int i = 0; i < 50; i++){
        	timeUpdater.updateTime();
        }
        
        // Rock cools down.
        int coolSteps = 70;
        double coolFadePerStep = 1 / (double)coolSteps;
        for (int i = 0; i < coolSteps; i++){
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, coolFadePerStep, null ) );
        }
        
        // Done cooling, so radiometric closure occurs.
        double tempTime = timeUpdater.updateTime();
        setClosureAge(tempTime);
        animationSequence.add( new ModelAnimationDelta( tempTime, null, 0, 1, 0, 0, 0, closureOccurredEvent ) );
        
        return new StaticAnimationSequence(animationSequence);
    }
}
