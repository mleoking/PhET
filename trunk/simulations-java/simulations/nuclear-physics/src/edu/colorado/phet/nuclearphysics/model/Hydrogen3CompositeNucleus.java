/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class defines the behavior of the nucleus of Hydrogen 3, which
 * exhibits beta decay behavior.
 *
 * @author John Blanco
 */
public class Hydrogen3CompositeNucleus extends BetaDecayCompositeNucleus {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Hydrogen-3.
    public static final int ORIGINAL_NUM_PROTONS = 1;
    public static final int ORIGINAL_NUM_NEUTRONS = 2;
    
    // Half life for this nucleus.
    public static double HALF_LIFE = MultiNucleusDecayModel.convertDaysToMs(4500);

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    static final int HYDROGEN_3_AGITATION_FACTOR = 5;
    static final int HELIUM_3_AGITATION_FACTOR = 1;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    public Hydrogen3CompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        // Decide when alpha decay will occur.
        _timeUntilDecay = calcDecayTime();
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
	    
	    case 1:
	        // Hydrogen.
	        if (_numNeutrons == 2){
	            // Hydrogen-3
	            _agitationFactor = HYDROGEN_3_AGITATION_FACTOR;
	        }
	        break;
	        
	    case 2:
	        // Helium
	        if (_numNeutrons == 125){
	            // Helium-3
	            _agitationFactor = HELIUM_3_AGITATION_FACTOR;
	        }
	        break;
	        
	    default:
	        // If we reach this point in the code, there is a problem
	        // somewhere that should be debugged.
	        System.err.println("Error: Unexpected atomic weight in beta decay nucleus.");
	        assert(false);
	    }        
	}
	
	/**
	 * Return a new value for the time at which this nucleus should decay.
	 */
	protected double calculateDecayTime(){
		return calcDecayTime();
	}

    /**
     * This method generates a value indicating the number of milliseconds for
     * a hydrogen 3 nucleus to decay.  This calculation is based on the 
     * standard exponential decay formula.
     * 
     * @return
     */
    private double calcDecayTime(){
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
