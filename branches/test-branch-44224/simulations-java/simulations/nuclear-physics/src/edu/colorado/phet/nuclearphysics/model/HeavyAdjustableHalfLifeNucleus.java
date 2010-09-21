/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;


/**
 * This class represents a non-composite nucleus that has an adjustable half
 * life.  There is obviously no such thing in nature, so the atomic weight of
 * the atom is chosen arbitrarily and other portions of the simulation must
 * "play along".
 *
 * @author John Blanco
 */
public class HeavyAdjustableHalfLifeNucleus extends AbstractAlphaDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Bismuth 208.
    public static final int ORIGINAL_NUM_PROTONS = 83;
    public static final int ORIGINAL_NUM_NEUTRONS = 125;
    
    // Random number generator used for calculating decay time based on half life.
    private static final double DEFAULT_HALF_LIFE = 900;  // In milliseconds.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    public HeavyAdjustableHalfLifeNucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        _halfLife = DEFAULT_HALF_LIFE;
    }
    
    public HeavyAdjustableHalfLifeNucleus(NuclearPhysicsClock clock){

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
            notifyNucleusChangeEvent(null);
        }

        // Notify all listeners of the potential position change.
        notifyPositionChanged();
    }
}
