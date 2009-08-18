/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;

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

    // Time scaling factor - scales the rate at which decay occurs so that we
    // don't really have to wait around thousands of years.  Smaller values
    // cause quicker decay.
    private static double DECAY_TIME_SCALING_FACTOR = 100 / HalfLifeInfo.getHalfLifeForNucleusType(NucleusType.HYDROGEN_3);
    
    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    static final int HYDROGEN_3_AGITATION_FACTOR = 5;
    static final int HELIUM_3_AGITATION_FACTOR = 1;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    public Hydrogen3CompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS, DECAY_TIME_SCALING_FACTOR);
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
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
	        if (_numNeutrons == 1){
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
}
