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
    // Constructor
    //------------------------------------------------------------------------

    public AgingTree( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Tree", Arrays.asList( "tree_1.png", "tree_dead_1.png" ), center, width, 0, 0, clock, MultiNucleusDecayModel.convertYearsToMs( 1000 ) / 5000 );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //-----------------------------------------------------------------------

    protected AnimationSequence getAnimationSequence() {
        Point2D GROWTH_COMPENSATION = new Point2D.Double( 0, 0.6 );

        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();


        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 100 ) );

        // Grow.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), GROWTH_COMPENSATION, 0, 1.1, 0, 0, 0 ) );

        // Switch image to dead tree.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.1, 1, 0, 0 ) );

        // Fall over.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.2, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.3, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.3, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.2, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.2, 1.0, 0, 0, 0 ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.2, 1.0, 0, 0, 0 ) );

        return new StaticAnimationSequence(animationSequence);
    }
}
