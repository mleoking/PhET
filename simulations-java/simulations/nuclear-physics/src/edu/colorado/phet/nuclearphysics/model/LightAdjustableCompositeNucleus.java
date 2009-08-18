/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;

/**
 * This class defines the behavior of a composite nucleus that exhibits beta
 * decay and that has an adjustable half life.
 *
 * @author John Blanco
 */
public class LightAdjustableCompositeNucleus extends BetaDecayCompositeNucleus {
	
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Oxygen-16, which by convention in this sim is the
	// light nucleus with adjustable half life.
    public static final int ORIGINAL_NUM_PROTONS = 8;
    public static final int ORIGINAL_NUM_NEUTRONS = 8;
    
    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    static final int PRE_DECAY_AGITATION_FACTOR = 5;
    static final int POST_DECAY_AGITATION_FACTOR = 1;
    
    // Time scaling factor - scales the rate at which decay occurs so that we
    // don't really have to wait around thousands of years.  Smaller values
    // cause quicker decay.
    private static double DECAY_TIME_SCALING_FACTOR = 
    	700 / HalfLifeInfo.getHalfLifeForNucleusType(NucleusType.LIGHT_CUSTOM);

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    public LightAdjustableCompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS, DECAY_TIME_SCALING_FACTOR);
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
	protected void updateAgitationFactor() {
	    // Determine the amount of agitation that should be exhibited by this
	    // particular nucleus based on its atomic weight.
	    
	    switch (_numProtons){
	    
	    case 8:
	        // Oxygen
            _agitationFactor = PRE_DECAY_AGITATION_FACTOR;
	        break;
	        
	    case 9:
	        // Flourine
            _agitationFactor = POST_DECAY_AGITATION_FACTOR;
	        break;
	        
	    default:
	        // If we reach this point in the code, there is a problem
	        // somewhere that should be debugged.
	        System.err.println(getClass().getName() + "Error: Unexpected atomic weight in beta decay nucleus.");
	        assert(false);
	    }        
	}
}
