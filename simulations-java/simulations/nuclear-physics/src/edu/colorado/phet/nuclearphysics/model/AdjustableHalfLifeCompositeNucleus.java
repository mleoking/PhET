/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.model.AtomicNucleus.Listener;


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
    
    private double _halfLife = Polonium211CompositeNucleus.HALF_LIFE;  // Use Polonium half life as default.
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public AdjustableHalfLifeCompositeNucleus(NuclearPhysicsClock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        // Decide when alpha decay will occur.
        _alphaDecayTime = clock.getSimulationTime() + calcDecayTime();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public double getHalfLife(){
    	return _halfLife;
    }
    
    public void setHalfLife(double halfLife){
    	if (halfLife != _halfLife){
    		_halfLife = halfLife;
    	}
    }
    
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
	 * Return a new value for the simulation time at which this nucleus should decay.
	 */
	protected double calculateDecayTime(){
		return _clock.getSimulationTime() + calcDecayTime();
	}

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
    	if (_halfLife == Double.POSITIVE_INFINITY){
    		return Double.POSITIVE_INFINITY;
    	}
    	
    	double decayConstant = 0.693/(_halfLife / 1000);
        double randomValue = _rand.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        return (-(Math.log( 1 - randomValue ) / decayConstant)) * 1000;
    }
}
