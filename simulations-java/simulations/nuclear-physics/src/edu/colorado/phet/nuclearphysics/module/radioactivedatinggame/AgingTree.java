/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
	private static final double AGE_OF_NATURAL_DEATH = MultiNucleusDecayModel.convertYearsToMs(500);
	private static final int SWAY_COUNT = 30; // Controls how long tree sways before falling over.
	private static final double MAX_SWAY_DEFLECTION = 0.01; // In radians, controls amount of sway.
	private static final int FALL_COUNT = 40; // Controls how long it takes the tree to fall over.
	private static final int BOUNCE_COUNT = 11; // Controls length of bounce after falling.
	private static final double BOUNCE_PROPORTION = 0.01; // Controls magnitude of bounds.
	private static final Random RAND = new Random();

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private boolean _closurePossibleSent = false;
	private int _swayCounter = SWAY_COUNT;
	private int _fallCounter = FALL_COUNT;
	private int _bounceCounter = BOUNCE_COUNT;
	private double _previousAngle;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingTree( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Tree", Arrays.asList( "tree_1.png", "tree_dead_2.png" ), center, width, 0, 0, clock, 
        		MultiNucleusDecayModel.convertYearsToMs( 500 ) / 5000 );
    }
    
    //------------------------------------------------------------------------
    // Methods.
    //------------------------------------------------------------------------
    
    @Override
    protected void handleClockTicked() {
    	super.handleClockTicked();
    	animate(getClock().getSimulationTime() * getTimeConversionFactor() - getBirthTime());
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
    	if (getClosureState() != RadiometricClosureState.CLOSED && time > AGE_OF_NATURAL_DEATH){
    		// Time to die, a.k.a. to radiometrically "close".
    		setClosureState(RadiometricClosureState.CLOSED);
    	}

    	// Handle the post-closure animation.
    	if ( getClosureState() == RadiometricClosureState.CLOSED ){
    		
    		if (getFadeFactor() < 1.0){
    			// Handle fading from live to dead image.
	    		double currentFadeFactor = getFadeFactor();
	    		
	    		double fadeRate = 0.025;
	    		if (time < AGE_OF_NATURAL_DEATH){
	    			// Fade faster if closer was forced so that users don't get
	    			// impatient.
	    			fadeRate *= 1.5;
	    		}
				setFadeFactor(Math.min(currentFadeFactor + 0.02, 1.0));
    		}
    		else if (_swayCounter > 0){
    			
    			// Set the angle for the sway.
    			double swayDeflection = 
    				-Math.sin(((double)(_swayCounter - SWAY_COUNT) / (double)SWAY_COUNT) * Math.PI * 2) * MAX_SWAY_DEFLECTION; 
    			
    			rotateAboutBottomCenter(swayDeflection);
    			
    			// Move to the next step in the cycle.
    			_swayCounter--;
    		}
    		else if (_fallCounter > 0){
    			
    			rotateAboutBottomCenter(Math.PI / 2 /(double)FALL_COUNT);
    			
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
    				setRotationalAngle(_previousAngle + (RAND.nextDouble() * Math.PI / 20));
    			}
    			else if ((BOUNCE_COUNT - _bounceCounter) % 2 == 0){
    				setRotationalAngle(_previousAngle);
    			}
    			_bounceCounter--;
    		}
    	}
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //------------------------------------------------------------------------

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
    
    private void rotateAboutBottomCenter(double deltaTheta){
    	
    	double totalTranslation = getSize().getHeight() * Math.sin( deltaTheta / 2 );
    	double centerAngle = getRotationalAngle() + ( deltaTheta / 2 );
    	double xTranslation = totalTranslation * Math.cos( centerAngle );
    	double yTranslation = -totalTranslation * Math.sin( centerAngle );
    	setRotationalAngle( getRotationalAngle() + deltaTheta );
    	setPosition( getPosition().getX() + xTranslation, getPosition().getY() + yTranslation );
    }
}
