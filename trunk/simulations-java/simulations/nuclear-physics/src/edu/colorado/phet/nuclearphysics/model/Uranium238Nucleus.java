/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
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
    
    // Half life for this nucleus.
    public static double HALF_LIFE = 1.41E20; // 4.46 billion years, converted into milliseconds.
    
    // Time scaling factor - scales the rate at which decay occurs so that we
    // don't really have to wait around thousands of years.  Smaller numbers
    // will bring about faster decay.
    private static double DECAY_TIME_SCALING_FACTOR = 2500 / HALF_LIFE;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Uranium238Nucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS, DECAY_TIME_SCALING_FACTOR);
    }
    
    public Uranium238Nucleus(NuclearPhysicsClock clock){

        this(clock, new Point2D.Double(0, 0));
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    public double getHalfLife(){
    	return HALF_LIFE;
    }
    
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
        
    	super.reset();
    	
        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Fission or absorption has occurred.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyNucleusChangeEvent(null);
        }
    }

    /**
     * Activate the nucleus, meaning that it will now decay after some amount
     * of time.
     */
    public void activateDecay(){
    	// Only allow activation if the nucleus hasn't already decayed.
    	if (_numNeutrons == ORIGINAL_NUM_NEUTRONS){
    		_decayTime = _clock.getSimulationTime() + (calcDecayTime() * _decayTimeScalingFactor);
    	}
    }

	protected void decay(ClockEvent clockEvent) {
		
		// Decay into Lead 206.
        _numNeutrons -= 22;
        _numProtons -= 10;

        // Set the final value of the time that this nucleus existed prior to
        // decaying.
        _activatedLifetime += clockEvent.getSimulationTimeChange();
        
        // Send out the decay event to all listeners.
        notifyNucleusChangeEvent(null);
        
        // Set the decay time to 0 to indicate that decay has occurred and
        // should not occur again.
        _decayTime = 0;
	}
}
