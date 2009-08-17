/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.NucleusType;


/**
 * This class represents a non-composite nucleus that has an adjustable half
 * life.  There is obviously no such thing in nature, so the atomic weight of
 * the atom is chosen arbitrarily and other portions of the simulation must
 * "play along".
 *
 * @author John Blanco
 */
public class LightAdjustableHalfLifeNucleus extends AbstractBetaDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Bismuth 208.
    public static final int ORIGINAL_NUM_PROTONS = 8;
    public static final int ORIGINAL_NUM_NEUTRONS = 8;
    
    // Time scaling factor - scales the rate at which decay occurs so that we
    // don't really have to wait around thousands of years.  Smaller values
    // cause quicker decay.
    private static double DECAY_TIME_SCALING_FACTOR = 1500 / HalfLifeInfo.getHalfLifeForNucleusType(NucleusType.LIGHT_CUSTOM);
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    public LightAdjustableHalfLifeNucleus(NuclearPhysicsClock clock, Point2D position, boolean enlarged){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS, DECAY_TIME_SCALING_FACTOR);
    }
    
    public LightAdjustableHalfLifeNucleus(NuclearPhysicsClock clock, Point2D position){
        this(clock, position, false);
    }
    
    public LightAdjustableHalfLifeNucleus(NuclearPhysicsClock clock){
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
