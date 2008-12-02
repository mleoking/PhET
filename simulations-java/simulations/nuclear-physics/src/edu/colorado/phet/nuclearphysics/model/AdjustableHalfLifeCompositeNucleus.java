/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;


/**
 * This class defines the behavior of a composite nucleus that exhibits alpha
 * decay and allows for adjustment of its half life.
 *
 * @author John Blanco
 */
public class AdjustableHalfLifeCompositeNucleus extends AlphaDecayCompositeNucleus {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Number of neutrons and protons in the nucleus upon construction.  These
    // values values are for bismuth 208, which is used as the "adjustable" or
	// "custom" nucleus throughout the sim.
    public static final int ORIGINAL_NUM_PROTONS = 83;
    public static final int ORIGINAL_NUM_NEUTRONS = 125;

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    static final int PRE_DECAY_AGITATION_FACTOR = 5;
    static final int POST_DECAY_AGITATION_FACTOR = 1;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    public AdjustableHalfLifeCompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        // Decide when alpha decay will occur.
        _alphaDecayTime = clock.getSimulationTime() + calcDecayTime();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
	protected void updateAgitationFactor() {
	    // Determine the amount of agitation that should be exhibited by this
	    // particular nucleus based on its atomic weight.
	    
	    switch (_numProtons){
	    
	    case ORIGINAL_NUM_PROTONS:
	        // Bismuth.
	        _agitationFactor = PRE_DECAY_AGITATION_FACTOR;
	        break;
	        
	    case ORIGINAL_NUM_PROTONS - 2:
	        // Thallium
            _agitationFactor = POST_DECAY_AGITATION_FACTOR;
	        break;
	        
	    default:
	        // If we reach this point in the code, there is a problem
	        // somewhere that should be debugged.
	        System.err.println("Error: Unexpected atomic weight in alpha decay nucleus.");
	        assert(false);
	    }        
	}

    /**
     * This method generates a value indicating the number of milliseconds for
     * a Polonium 211 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Polonium 211.
     * 
     * @return
     */
    double calcDecayTime(){
        double randomValue = _rand.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        // TODO: JPB TBD - Need to make the following work for adjustable half life.
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / 1.343)) * 1000;
        return tunnelOutMilliseconds;
    }
}
