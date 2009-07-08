/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * rock flies out of a volcano in a random flight path.  The flight path is
 * intended to fly outside of the model boundaries.  This is done so that the
 * model will know when to remove it.
 *
 * @author John Blanco
 */
public class AnimatedFlyingRock extends AnimatedDatableItem {

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AnimatedFlyingRock( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Rock", Arrays.asList( "molten_rock.png" ), center, width, 0, 
        		0, clock, EruptingVolcano.VOLCANO_AGE_FACTOR );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //------------------------------------------------------------------------
    protected AnimationSequence getAnimationSequence() {
        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 10E6 ) );
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        
        // Rock flies out of volcano in a parabolic arc.
        int flightSteps = 50;
        double totalRotation = -8 * Math.PI;
        double rotationPerStep = totalRotation / flightSteps;
        double arcHeightControl = 0.08; // Higher for higher arc, lower for lower arc.
        double flightXTranslation = -0.38; // Higher positive or negative number move further.
        for (int i = 0; i < flightSteps; i++){
        	double flightYTranslation = arcHeightControl * (((double)flightSteps * 0.42) - i);
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        			new Point2D.Double( flightXTranslation, flightYTranslation ), rotationPerStep, 1, 0, 
        			0, 0, null ) );
        }
        
        return new StaticAnimationSequence(animationSequence);
    }
}
