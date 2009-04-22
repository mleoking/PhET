/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;


public class Uranium238Nucleus extends AtomicNucleus {
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Uranium-238.
    public static final int ORIGINAL_NUM_PROTONS = 92;
    public static final int ORIGINAL_NUM_NEUTRONS = 146;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Uranium238Nucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Returns true if the particle can be captured by this nucleus, false if
     * not.  Note that the particle itself is unaffected, and it is up to the
     * caller to remove the captured particle from the model if desired.
     * 
     * @param freeParticle - The free particle that could potentially be
     * captured.
     * @return true if the particle is captured, false if not.
     */
    public boolean captureParticle(Nucleon freeParticle){

        boolean retval = false;
        
        if ((freeParticle instanceof Neutron) && (_numNeutrons == ORIGINAL_NUM_NEUTRONS)){
            
            // Increase our neutron count.
            _numNeutrons++;
            
            // Let the listeners know that the atomic weight has changed.
            notifyNucleusChangeEvent(null);
            
            // Indicate that the nucleus was captured.
            retval = true;
        }
        
        return retval;
    }
    
    /**
     * Resets the nucleus to its original state, before any neutron absorption has
     * occurred.
     */
    public void reset(){
        
        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Fission or absorption has occurred.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyNucleusChangeEvent(null);
        }
    }
}
