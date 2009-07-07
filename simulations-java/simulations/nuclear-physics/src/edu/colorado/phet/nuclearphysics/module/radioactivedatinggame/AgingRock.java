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
        super( "Aging Rock", Arrays.asList( "molten_rock.png", "rock_volcanic.png" ), center, width, 0, 0, clock, EruptingVolcano.VOLCANO_AGE_FACTOR );
        System.out.println( "clock = " + clock );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //------------------------------------------------------------------------
    protected AnimationSequence getAnimationSequence() {
        TimeUpdater timeUpdater = new TimeUpdater( MultiNucleusDecayModel.convertYearsToMs( 20 * 10E6 ), MultiNucleusDecayModel.convertYearsToMs( 10E6 ) );
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        
        // Rock flies out of volcano.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, .6 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, .6 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, .6 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.4 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.3 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.2 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0.1 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, 0 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -0.1 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -0.1 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -0.4 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -0.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -0.6 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -.9 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.1 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.2 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.2 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.3 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.3 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.5 ), 0, 1.07, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double( -0.35, -1.5 ), 0, 1.07, 0, 0, 0, null ) );
        
        // Rock cools down.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0.1, null ) );
        
        return new StaticAnimationSequence(animationSequence);
    }
}
