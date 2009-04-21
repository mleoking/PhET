/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;


/**
 * This class represents a non-composite nucleus that has an adjustable half
 * life.  There is obviously no such thing in nature, so the atomic weight of
 * the atom is chosen arbitrarily and other portions of the simulation must
 * "play along".
 *
 * @author John Blanco
 */
public class AdjustableHalfLifeNucleus extends AbstractAlphaDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Bismuth 208.
    public static final int ORIGINAL_NUM_PROTONS = 83;
    public static final int ORIGINAL_NUM_NEUTRONS = 125;
    
    // Random number generator used for calculating decay time based on half life.
    private static final Random RAND = new Random();
    
    // Random number generator used for calculating decay time based on half life.
    private static final double DEFAULT_HALF_LIFE = 900;  // In milliseconds.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    public AdjustableHalfLifeNucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        _halfLife = DEFAULT_HALF_LIFE;
    }
    
    public AdjustableHalfLifeNucleus(NuclearPhysicsClock clock){

        this(clock, new Point2D.Double(0, 0));
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any decay has
     * occurred.
     */
    public void reset(){
        
    	super.reset();

        // Reset the decay time to 0, indicating that it shouldn't occur
        // until something changes.
        _decayTime = 0;
        _activatedLifetime = 0;

        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Decay had occurred prior to reset.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyAtomicWeightChanged(null);
        }

        // Notify all listeners of the potential position change.
        notifyPositionChanged();
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Activate the nucleus, meaning that it will now decay after some amount
     * of time.
     */
    public void activateDecay(){
    	
    	// Only allow activation if the nucleus hasn't already decayed.
    	if (_numNeutrons == ORIGINAL_NUM_NEUTRONS){
    		_decayTime = _clock.getSimulationTime() + calcDecayTime();
    	}
    }
    
    /**
     * Return a value indicating whether or not the nucleus has decayed.
     */
    public boolean hasDecayed(){
    	if (_numProtons < ORIGINAL_NUM_PROTONS){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    
    /**
     * This method generates a value indicating the number of milliseconds for
     * a nucleus decay based on the half life.  This calculation is based on the 
     * exponential decay formula.
     * 
     * @return - a time value in milliseconds
     */
    private double calcDecayTime(){
    	
    	if (_halfLife == 0){
    		return 0;
    	}
    	
    	double decayConstant = 0.693/_halfLife;
        double randomValue = RAND.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        return (-(Math.log( 1 - randomValue ) / decayConstant));
    }
}
