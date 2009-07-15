/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
public class AnimatedFlyingRock extends StaticAnimatedDatableItem {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final double MIN_ARC_HEIGHT_INCREMENT = 0.05;
	private static final double MAX_ARC_HEIGHT_INCREMENT = 0.15;
	private static final double MAX_X_TRANSLATION_INCREMENT = 1;
	private static final double MAX_ROTATION_CHANGE = Math.PI/10;
	private static final Random IMAGE_CHOICE_RAND = new Random();
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AnimatedFlyingRock( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Rock", Arrays.asList( chooseInitialImage() ), center, width, 0, 
        		0, clock, EruptingVolcano.VOLCANO_AGE_FACTOR, false );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //------------------------------------------------------------------------
    protected AnimationSequence createAnimationSequence() {
        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 10E6 ) );
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        Random rand = new Random();
        
        // Rock flies out of volcano in a parabolic arc.
        int flightSteps = 50;
        double rotationPerStep = (rand.nextDouble() - 0.5) * MAX_ROTATION_CHANGE;
        double arcHeightControl = MIN_ARC_HEIGHT_INCREMENT + rand.nextDouble() * (MAX_ARC_HEIGHT_INCREMENT - MIN_ARC_HEIGHT_INCREMENT);
        double flightXTranslation = (rand.nextDouble() - 0.5) * 2 * MAX_X_TRANSLATION_INCREMENT;
        for (int i = 0; i < flightSteps; i++){
        	double flightYTranslation = arcHeightControl * (((double)flightSteps * 0.42) - i);
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        			new Point2D.Double( flightXTranslation, flightYTranslation ), rotationPerStep, 1, 0, 
        			0, 0, null ) );
        }
        
        return new StaticAnimationSequence(animationSequence);
    }
    
    private static String chooseInitialImage(){
    	if (IMAGE_CHOICE_RAND.nextBoolean()){
    		return "molten_rock.png";
    	}
    	else{
    		return "molten_rock_large.png";
    	}
    }
}
