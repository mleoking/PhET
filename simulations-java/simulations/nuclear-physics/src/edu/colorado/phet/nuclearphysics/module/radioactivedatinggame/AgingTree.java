/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;
import edu.umd.cs.piccolo.util.PDimension;

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
	
	private static final double FULL_GROWN_TREE_HEIGHT = 25; // Model units, roughly meters.
	private static final double GROWTH_RATE = 1.03; // High number for faster growth.
	private static final double AGE_OF_NATURAL_DEATH = MultiNucleusDecayModel.convertYearsToMs(3000);
	private static final int FADE_TO_DEAD_TREE_COUNT = 30; // Controls how long it takes for tree to die.
	private static final int SWAY_COUNT = 30; // Controls how long tree sways before falling over.
	private static final double MAX_SWAY_DEFLECTION = 0.01; // In radians, controls amount of sway.
	private static final int FALL_COUNT = 30; // Controls how long it takes the tree to fall over.
	private static final double FALL_ANGLE_SCALE_FACTOR = Math.PI / (double)(FALL_COUNT * FALL_COUNT);
	private static final int BOUNCE_COUNT = 9; // Controls length of bounce after falling.
	private static final double BOUNCE_PROPORTION = 0.01; // Controls magnitude of bounds.
	private static final Random RAND = new Random();
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private boolean _closurePossibleSent = false;
	private int _fadeCounter = FADE_TO_DEAD_TREE_COUNT;
	private int _swayCounter = SWAY_COUNT;
	private int _fallCounter = FALL_COUNT;
	private int _bounceCounter = BOUNCE_COUNT;
	private double _previousAngle;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingTree( ConstantDtClock clock, Point2D center, double width, double timeConversionFactor ) {
        super( "Aging Tree", Arrays.asList( "tree_1.png", "tree_dead_3.png" ), center, width, 0, 0, clock, 
        		timeConversionFactor, true );
    }
    
    //------------------------------------------------------------------------
    // Methods.
    //------------------------------------------------------------------------
    
    @Override
	protected void handleClockTicked(ClockEvent clockEvent) {
		// TODO Auto-generated method stub
		super.handleClockTicked(clockEvent);
		animate(getTotalAge());
	}
    
    /**
     * Implement the next steps in the animation of the tree based on a
     * number of factors, such as its age, whether it has reached full size,
     * whether closure has occurred, etc.
     * 
     * @param time
     */
    private void animate(double time){
    	
    	if (!_closurePossibleSent){
    		// At the moment of birth for the tree, closure is possible.  If
    		// we haven't set the state to indicate this, do it now.
    		setClosureState(RadiometricClosureState.CLOSURE_POSSIBLE);
    		_closurePossibleSent = true;
    	}
    	
    	// Handle growth.
    	
    	Dimension2D size = getSize();
    	if (( size.getHeight() < FULL_GROWN_TREE_HEIGHT && getClosureState() != RadiometricClosureState.CLOSED )){
    		
    		// Grow a little bit.
    		setSize(new PDimension(size.getWidth() * GROWTH_RATE, size.getHeight() * GROWTH_RATE));
    		
    		// Shift up a bit so that it looks like the tree is growing up out
    		// of the ground.
    		Point2D centerPos = getPosition();
    		setPosition(centerPos.getX(), centerPos.getY() + size.getHeight() * 0.012);
    	}
    	
    	// Handle death by natural causes.
    	if (getClosureState() != RadiometricClosureState.CLOSED && time > AGE_OF_NATURAL_DEATH && _fadeCounter > 0){

    		double fadeAmt = 1 / (double)FADE_TO_DEAD_TREE_COUNT;
    		
    		setFadeFactor(Math.min(getFadeFactor() + fadeAmt, 1));

    		_fadeCounter--;
    		
    		// Time to die, a.k.a. to radiometrically "close".
    		if (_fadeCounter == 0){
    			setClosureState(RadiometricClosureState.CLOSED);
    		}
    	}

    	// Handle the post-closure animation.
    	if ( getClosureState() == RadiometricClosureState.CLOSED ){

    		// Make sure fully faded.
   			setFadeFactor(1);
    		
    		if (_swayCounter > 0){
    			
    			// Set the angle for the sway.
    			double swayDeflection = 
    				Math.cos(((double)(_swayCounter - SWAY_COUNT) / (double)SWAY_COUNT) * Math.PI * 2) * MAX_SWAY_DEFLECTION; 
    			
    			rotateAboutBottomCenter(swayDeflection);
    			
    			// Move to the next step in the cycle.
    			_swayCounter--;
    		}
    		else if (_fallCounter > 0){
    			
    			rotateAboutBottomCenter(FALL_ANGLE_SCALE_FACTOR * (FALL_COUNT - _fallCounter));
    			
    			// Tweak the translation just a little such that if the user
    			// puts the probe at the base of the tree it will remain in
    			// contact with the tree after it has fallen.  This was
    			// requested by Noah P.
    			if (_fallCounter < FALL_COUNT / 2){
    				setPosition(getPosition().getX() - getSize().getWidth() * 0.01, getPosition().getY());
    			}
    			
    			// Move to the next step in the cycle.
    			_fallCounter--;
    		}
    		else if (_bounceCounter > 0){
    			
    			double yTranslation = -Math.sin(((double)(_bounceCounter - BOUNCE_COUNT) / (double)BOUNCE_COUNT) * Math.PI * 2) 
    				* (BOUNCE_PROPORTION * getSize().getWidth());
    			setPosition(getPosition().getX(), getPosition().getY() + yTranslation);
    			
    			// Give it a little random rotation to make it look a bit
    			// more like a real bounce.
    			if ((BOUNCE_COUNT - _bounceCounter) % 4 == 0){
    				_previousAngle = getRotationalAngle();
    				setRotationalAngle(_previousAngle + (RAND.nextDouble() * Math.PI / 24));
    			}
    			else if ((BOUNCE_COUNT - _bounceCounter) % 2 == 0){
    				setRotationalAngle(_previousAngle);
    			}
    			_bounceCounter--;
    		}
    	}
    }

	private void rotateAboutBottomCenter(double deltaTheta){
    	
    	double totalTranslation = getSize().getHeight() * Math.sin( deltaTheta / 2 );
    	double centerAngle = getRotationalAngle() + ( deltaTheta / 2 );
    	double xTranslation = totalTranslation * Math.cos( centerAngle );
    	double yTranslation = -totalTranslation * Math.sin( centerAngle );
    	setRotationalAngle( getRotationalAngle() + deltaTheta );
    	setPosition( getPosition().getX() + xTranslation, getPosition().getY() + yTranslation );
    }
}
