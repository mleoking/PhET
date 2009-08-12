/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;

/**
 * This class represents a non-composite Hydrogen 3 nucleus.  In other words,
 * this nucleus does not create or keep track of any constituent nucleons.
 *
 * @author John Blanco
 */
public class Hydrogen3Nucleus extends AbstractBetaDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.
    public static final int ORIGINAL_NUM_PROTONS = 1;
    public static final int ORIGINAL_NUM_NEUTRONS = 2;
    
    // Time scaling factor - scales the rate at which decay occurs so that we
    // don't really have to wait around thousands of years.  Smaller values
    // cause quicker decay.
    private static double DECAY_TIME_SCALING_FACTOR = 1500 / HalfLifeInfo.getHalfLifeForNucleusType(NucleusType.HYDROGEN_3);
    
    // Random number generator used for calculating decay time based on decay constant.
    private static final Random RAND = new Random();
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Hydrogen3Nucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS, DECAY_TIME_SCALING_FACTOR);
    }
    
    public Hydrogen3Nucleus(NuclearPhysicsClock clock){
        this(clock, new Point2D.Double(0, 0));
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public double getHalfLife(){
    	return HalfLifeInfo.getHalfLifeForNucleusConfig(_numProtons, _numNeutrons);
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(){
    	
    	super.reset();
        
        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Decay has occurred.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyNucleusChangeEvent(null);
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
     * a Hydrogen 3 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Hydrogen 3.
     * 
     * @return
     */
    private double calcDecayTime(){
        double randomValue = RAND.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / (0.693 / getHalfLife())));
        return tunnelOutMilliseconds;
    }
}
