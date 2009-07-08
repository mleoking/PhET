/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * tree that can be dated by radiometric means, and that grows, dies, and
 * falls over as time goes by.
 *
 * @author John Blanco
 */
public class AgingTree extends AnimatedDatableItem {

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingTree( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Tree", Arrays.asList( "tree_1.png", "tree_dead_2.png" ), center, width, 0, 0, clock, MultiNucleusDecayModel.convertYearsToMs( 1000 ) / 5000 );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //-----------------------------------------------------------------------

    protected AnimationSequence getAnimationSequence() {
    	
    	// Vars needed in the sequence.
        double growthRate = 1.075;
        double verticalGrowthCompensation = 0.035;  // Higher values move up more.
        RadiometricClosureEvent closureOccurredEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSED);
        RadiometricClosureEvent closurePossibleEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSURE_POSSIBLE);
        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 25 ) );

        // Create the sequence.
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        
        //--------------------------------------------------------------------
        // Add the individual animation deltas to the sequence.
        //--------------------------------------------------------------------
        
        // Signal that closure is now possible.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, growthRate, 0, 0, 0, 
        		closurePossibleEvent ) );

        // Grow.
        for (int i = 0; i < 40; i++){
            animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
            		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
            verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        }

        // Signal that closure has occurred.
        double tempTime = timeUpdater.updateTime();
        setClosureAge(tempTime);
        animationSequence.add( new ModelAnimationDelta( tempTime, null, 0, 1, 0, 0, 0, closureOccurredEvent ) );
        
        // Fade from the live to the dead tree.
        int fadeSteps = 10;
        double fadeAmountPerStep = 1 / (double)fadeSteps;
        for (int i = 0; i < fadeSteps; i++){
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.0, 0, 0, fadeAmountPerStep, null ) );
        }
        
        // Pause.
        timeUpdater.updateTime();
        timeUpdater.updateTime();
        timeUpdater.updateTime();
        timeUpdater.updateTime();

        // Sway back and forth a bit.
        Point2D swayRightTranslation = new Point2D.Double(0.5, 0);
        Point2D swayLeftTranslation = new Point2D.Double(-0.5, 0);
        double swayAngleChange = 0.05;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayRightTranslation, swayAngleChange, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayRightTranslation, swayAngleChange, 1.0, 0, 0, 0, null ) );
        timeUpdater.updateTime();
        timeUpdater.updateTime();
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayLeftTranslation, -swayAngleChange, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayLeftTranslation, -swayAngleChange, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayLeftTranslation, -swayAngleChange, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayLeftTranslation, -swayAngleChange, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayRightTranslation, swayAngleChange, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), swayRightTranslation, swayAngleChange, 1.0, 0, 0, 0, null ) );
        
        // Fall over.
        int fallSteps = 6;
        Point2D fallTranslation = new Point2D.Double();
        double fallRotationPerStep = Math.PI / 2 / (double)fallSteps;
        double fallTranslationPerStep = 2;
        for (int i = 0; i < fallSteps; i++){
        	fallTranslation.setLocation(fallTranslationPerStep * Math.cos(fallRotationPerStep * i),
        			-fallTranslationPerStep * Math.sin(fallRotationPerStep * i));
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), fallTranslation, fallRotationPerStep, 1.0, 0, 0, 0, null ) );
        }
        
        // Sink a little.
        Point2D sinkTranslation = new Point2D.Double(0, -0.3);
        int sinkSteps = 5;
        for (int i = 0; i < sinkSteps; i++){
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), sinkTranslation, 0, 1.0, 0, 0, 0, null ) );
        }
        
        return new StaticAnimationSequence(animationSequence);
    }
}
