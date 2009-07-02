/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * volcano that can can be dated by radiometric means, and that can erupt,
 * and that sends out the appropriate animation notifications when it does.
 * 
 * @author John Blanco
 */
public class EruptingVolcano extends AnimatedDatableItem {
    public static final double VOLCANO_AGE_FACTOR = MultiNucleusDecayModel.convertYearsToMs(1E9) / 5000;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	public EruptingVolcano(ConstantDtClock clock, Point2D center, double width) {
		super("Volcano", Arrays.asList( "volcano_cool.png","volcano_hot.png" ), center, width, 0, 0, clock , VOLCANO_AGE_FACTOR );
	}

    protected ArrayList<ModelAnimationDelta> getAnimationSequence() {
        TimeUpdater timeUpdater=new TimeUpdater(0, MultiNucleusDecayModel.convertYearsToMs( 10E6 ));
        ArrayList<ModelAnimationDelta> ANIMATION_SEQUENCE = new ArrayList<ModelAnimationDelta>();
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.3, 0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.3, -0.10), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0, 0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0, -0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.2, -0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.2, 0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));

        // Fade image to hot volcano.
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2));

        // More shaking.
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.3, 0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.3, -0.10), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0, 0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0, -0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.2, -0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.2, 0.1), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(-0.25, 0), 0, 1.0, 0, 0, 0));
        ANIMATION_SEQUENCE.add(new ModelAnimationDelta(timeUpdater.updateTime(),  new Point2D.Double(0.25, 0), 0, 1.0, 0, 0, 0));
        return ANIMATION_SEQUENCE;
    }
}
