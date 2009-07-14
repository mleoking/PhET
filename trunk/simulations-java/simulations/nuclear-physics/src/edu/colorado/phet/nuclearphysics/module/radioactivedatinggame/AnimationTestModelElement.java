/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class implements the behavior of a model element that represents a
 * tree that can be dated by radiometric means, and that grows, dies, and
 * falls over as time goes by.
 *
 * @author John Blanco
 */
public class AnimationTestModelElement extends StaticAnimatedDatableItem {

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public AnimationTestModelElement( ConstantDtClock clock, Point2D center, double width ) {
        super( "Animation Test", Arrays.asList( "magenta_block.png" ), center, width, 0, 0, clock, 1 );
    }

    //------------------------------------------------------------------------
    // The animation sequence.
    //-----------------------------------------------------------------------

    protected AnimationSequence createAnimationSequence() {

        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();


        TimeUpdater timeUpdater = new TimeUpdater( 0, 1000 );

        // Rotate full circle.
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
//        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        
        // Make it grow and shrink in place without moving (i.e. translating).
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 0.9, 0, 0, 0, null ) );

        return new StaticAnimationSequence(animationSequence);
    }
}
