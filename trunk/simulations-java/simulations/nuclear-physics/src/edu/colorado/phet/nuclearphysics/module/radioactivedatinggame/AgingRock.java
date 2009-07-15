/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class implements the behavior of a model element that represents a
 * rock that can be dated by radiometric means, and that starts off looking
 * hot and then cools down.
 *
 * @author John Blanco
 */
public class AgingRock extends AnimatedDatableItem {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final int FLY_COUNT = 50; // Controls how long it takes the rock to fly out and then hit the ground.
	private static final double FINAL_X_TRANSLATION = -20; // Model units, roughly meters.
	private static final double FINAL_ROCK_WIDTH = 10; // Model units, roughly meters.
	private static final double ARC_HEIGHT = 10; // Model units, roughly meters.
	private static final double ARC_HEIGHT_FACTOR = 0.04; // Higher for higher arc.
	private static final double ROTATION_PER_STEP = Math.PI * 0.1605; // Controls rate of rotation when flying.
	private static final double GRAV = 1;
	private static final int COOLING_START_PAUSE_STEPS = 100; // Length of pause before after landing & before starting to cool.
	private static final int COOLING_STEPS = 60; // Number of steps to cool down.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private int _flyCounter = FLY_COUNT;
	private double _growthPerStep;
	private Point2D _initialLocation;
	private double _deltaTime;
	private double _coolingStartPauseCounter = COOLING_START_PAUSE_STEPS;
	private double _coolingCounter = COOLING_STEPS;
	private boolean _closurePossibleSent = false;
	private boolean _closureOccurredSent = false;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingRock( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Rock", Arrays.asList( "molten_rock_large.png", "rock_volcanic_larger.png" ), center, width, 0, 
        		0, clock, EruptingVolcano.VOLCANO_AGE_FACTOR, false );
    
        _initialLocation = center;
        
        // Calculate the amount of growth needed per step in order to reach
        // the right size by the end of the flight.
    	_growthPerStep = Math.pow( FINAL_ROCK_WIDTH / width, 1 / (double)FLY_COUNT );
    	
    	double T = (Math.sqrt(2 * ARC_HEIGHT) + Math.sqrt(2 * ARC_HEIGHT + 4 * _initialLocation.getY())) / 2 * GRAV;
    	_deltaTime = T / (double)FLY_COUNT;
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
     * Implement the next steps in the animation of the rock based on a
     * number of factors, such as its age, whether closure has occurred,
     * etc.
     * 
     * @param time
     */
    private void animate(double time){

    	if (_flyCounter > 0){

    		// Move along the arc.
    		double flightXTranslation = FINAL_X_TRANSLATION / FLY_COUNT;
    		double flightYTranslation = (_flyCounter - (FLY_COUNT * 0.58)) * ARC_HEIGHT_FACTOR;
    		setPosition(getPosition().getX() + flightXTranslation, getPosition().getY() + flightYTranslation);
//    		double deltaXPos = FINAL_X_TRANSLATION / FLY_COUNT;
//    		double n = FLY_COUNT - _flyCounter;
//    		double yPos = _initialLocation.getY() + (Math.sqrt(2 * GRAV * ARC_HEIGHT) * n * _deltaTime) -
//    			(GRAV * n * n * _deltaTime * _deltaTime);
//    		setPosition(getPosition().getX() + deltaXPos, yPos);
    		
    		// Grow.
			Dimension2D size = getSize();
			size.setSize(size.getWidth() * _growthPerStep, size.getHeight() * _growthPerStep );
			setSize(size);
			
			// Rotate.
			setRotationalAngle(getRotationalAngle() + ROTATION_PER_STEP);
    		
			// Move to the next step.
    		_flyCounter--;
    	}
    	else if (_flyCounter <= 0 && !_closurePossibleSent){
    		// The rock has landed, so it is now possible to force closure if
    		// desired.
    		setClosureState(RadiometricClosureState.CLOSURE_POSSIBLE);
    		_closurePossibleSent = true;
    	}
    	else if (_coolingStartPauseCounter > 0){
    		if (getClosureState() != RadiometricClosureState.CLOSED){
    			_coolingStartPauseCounter--;
    		}
    		else{
    			// Closure has been forced externally - skip the rest of this
    			// stage.
    			_coolingStartPauseCounter = 0;
    			_closureOccurredSent = true;
    		}
    	}
    	else if (_coolingCounter > 0){
    		if (getClosureState() != RadiometricClosureState.CLOSED){
    			setFadeFactor(Math.min(getFadeFactor() + (1 / (double)COOLING_STEPS), 1));
    			_coolingCounter--;
    		}
    		else {
	    		// Closure has been forced externally - skip the rest of this
	    		// stage.
    			setFadeFactor( 1 );
	    		_coolingCounter = 0;
	    		_closureOccurredSent = true;
    		}
    	}
    	else if (!_closureOccurredSent){
    		// The rock has finished cooling, so closure occurs and the rock
    		// begins radiometrically aging.
    		setClosureState(RadiometricClosureState.CLOSED);
    		_closureOccurredSent = true;
    	}
    }
}
