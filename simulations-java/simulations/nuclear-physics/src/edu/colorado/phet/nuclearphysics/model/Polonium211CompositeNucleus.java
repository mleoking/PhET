/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;


/**
 * This class defines the behavior of the nucleus of Polonium 211, which
 * exhibits alpha decay behavior.
 *
 * @author John Blanco
 */
public class Polonium211CompositeNucleus extends AlphaDecayCompositeNucleus {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Polonium-211.
    public static final int ORIGINAL_NUM_PROTONS = 84;
    public static final int ORIGINAL_NUM_NEUTRONS = 127;
    
    // Half life for this nucleus.
    public static double HALF_LIFE = 516; // In milliseconds.

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    static final int POLONIUM_211_AGITATION_FACTOR = 5;
    static final int LEAD_207_AGITATION_FACTOR = 1;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    public Polonium211CompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        // Decide when alpha decay will occur.
        _timeUntilDecay = calcPolonium211DecayTime();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public double getHalfLife(){
    	return HALF_LIFE;
    }
    
	protected void updateAgitationFactor() {
	    // Determine the amount of agitation that should be exhibited by this
	    // particular nucleus based on its atomic weight.
	    
	    switch (_numProtons){
	    
	    case 84:
	        // Polonium.
	        if (_numNeutrons == 127){
	            // Polonium 211.
	            _agitationFactor = POLONIUM_211_AGITATION_FACTOR;
	        }
	        break;
	        
	    case 82:
	        // Lead
	        if (_numNeutrons == 125){
	            // Lead 207
	            _agitationFactor = LEAD_207_AGITATION_FACTOR;
	        }
	        break;
	        
	    default:
	        // If we reach this point in the code, there is a problem
	        // somewhere that should be debugged.
	        System.err.println("Error: Unexpected atomic weight in alpha decay nucleus.");
	        assert(false);
	    }        
	}
	
	/**
	 * Return a new value for the time at which this nucleus should decay.
	 */
	protected double calculateDecayTime(){
		return calcPolonium211DecayTime();
	}

    /**
     * This method generates a value indicating the number of milliseconds for
     * a Polonium 211 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Polonium 211.
     * 
     * @return
     */
    private double calcPolonium211DecayTime(){
        double randomValue = _rand.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / (0.693 / HALF_LIFE)));
        return tunnelOutMilliseconds;
    }
}
