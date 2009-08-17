/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;


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
    // Constructor(s)
    //------------------------------------------------------------------------

    public AdjustableHalfLifeCompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
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
	
	public double getElapsedPreDecayTime() {
		if (!hasDecayed()){
			return getTimeOfExistence();
		}
		else{
			return _preDecayLifeTime;
		}
	}

	/**
	 * Get the adjusted time for which this nucleus was active.  This is an
	 * override because time changes exponentially for this particular atom.
	 */
	@Override
	public double getAdjustedActivatedTime() {
		return convertSimTimeToExponentialTime( getActivatedSimTime() );
	}
	
	/**
	 * Get the length of time for which this nucleus has existed, meaning that
	 * is has been moving towards decay.  This version maps simulation time
	 * to an exponential time.
	 * 
	 * @return - Time of existence in milliseconds.
	 */
	protected double getTimeOfExistence(){
		double simTimeOfExistence = 0;
		if (_paused){
			simTimeOfExistence = _pauseStartTime - _startTime;
		}
		else{
			simTimeOfExistence = _clock.getSimulationTime() - _startTime;
		}
		
		// Convert the linear simulation clock via an exponential function in
		// order to figure out how long this nucleus has been in existence.
		// Note that this is tweaked a bit to make the nucleus move along the
		// time chart at a reasonable rate.
		return Math.pow(10, simTimeOfExistence * 0.01) - 1;
	}
	
	@Override
	protected boolean isTimeToDecay(ClockEvent clockEvent) {
		double convertedActivatedLifetime = convertSimTimeToExponentialTime( getActivatedSimTime() );
		return convertedActivatedLifetime > getTotalUndecayedLifetime();
	}

	/**
	 * Function for converting simulation time to time that this nucleus has
	 * experienced.  This function is exponential in nature.
	 */
	private double convertSimTimeToExponentialTime(double simTime){
		return Math.pow(10, simTime * 0.01) - 1;
	}
}
