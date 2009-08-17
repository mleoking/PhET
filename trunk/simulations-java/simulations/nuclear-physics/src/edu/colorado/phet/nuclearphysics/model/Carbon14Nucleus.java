/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * This class represents a non-composite Carbon 14 nucleus.  Because it is
 * non-composite, this nucleus does not create or keep track of any 
 * constituent nucleons.
 *
 * @author John Blanco
 */
public class Carbon14Nucleus extends AbstractBetaDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.
    public static final int ORIGINAL_NUM_PROTONS = 6;
    public static final int ORIGINAL_NUM_NEUTRONS = 8;
    
    // Half life for Carbon 14.
    public static double HALF_LIFE = 1.81E14; // 5,730 years, converted into milliseconds.
    
    // Time scaling factor - scales the rate at which decay occurs so that we
    // don't really have to wait around thousands of years.  Smaller values
    // cause quicker decay.
    private static double DECAY_TIME_SCALING_FACTOR = 1500 / HALF_LIFE;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Parameter that controls whether this nucleus returns a diameter value
    // that is larger than carbon-14 is in real life.  This was added as a
    // bit of "Hollywooding" so that carbon-14 wouldn't be so much smaller
    // than heavier nuclei, such as Uranium.
    private boolean _enlarged = false;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Carbon14Nucleus(NuclearPhysicsClock clock, Point2D position, boolean enlarged){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS, DECAY_TIME_SCALING_FACTOR);
        _enlarged = enlarged;
    }
    
    public Carbon14Nucleus(NuclearPhysicsClock clock, Point2D position){
        this(clock, position, false);
    }
    
    public Carbon14Nucleus(NuclearPhysicsClock clock){
        this(clock, new Point2D.Double(0, 0));
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
     * This override is for "hollywooding" purposes - it provides a diameter
     * that is nearly the same as Uranium so that we don't have to scale atoms
     * of very different scales appearing on the same canvas.
     */
    public double getDiameter() {
    	if (!_enlarged){
    		return super.getDiameter();
    	}
    	else{
    		// Return an artificially large value.
    		return (1.6 * Math.pow( 100, 0.362));
    	}
	}
}
