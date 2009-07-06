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
public class AnimationTestModelElement extends AnimatedDatableItem {

    //------------------------------------------------------------------------
    // Class Data
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

    protected AnimationSequence getAnimationSequence() {

        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();


        TimeUpdater timeUpdater = new TimeUpdater( 0, 1000 );

        // Rotate.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, Math.PI/8, 1.0, 0, 0, 0 ) );

        return new StaticAnimationSequence(animationSequence);
    }
}
