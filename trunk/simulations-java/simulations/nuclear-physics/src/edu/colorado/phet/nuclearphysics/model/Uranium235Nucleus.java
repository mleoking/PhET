/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.common.model.Neutron;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;

/**
 * This class represents a non-composite Uranium235 nucleus.  In other words,
 * this nucleus does not create or keep track of any constituent nucleons.
 *
 * @author John Blanco
 */
public class Uranium235Nucleus extends AtomicNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Uranium-235.
    public static final int ORIGINAL_NUM_PROTONS = 92;
    public static final int ORIGINAL_NUM_NEUTRONS = 143;
    
    // Number of neutrons and protons in the daughter nucleus that is produced
    // if and when this nucleus fissions.  This nucleus represents Krypton-92.
    public static final int DAUGHTER_NUCLEUS_PROTONS = 36;
    public static final int DAUGHTER_NUCLEUS_NEUTRONS = 56;
    
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Interval from the time a neutron capture occurs until fission occurs.
    private double _fissionInterval;
    
    // Time at which fission will occur.
    private double _fissionTime = 0;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Uranium235Nucleus(NuclearPhysicsClock clock, Point2D position, double fissionInterval){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        _fissionInterval = fissionInterval;
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public double getFissionTime(){
        return _fissionTime;
    }
    
    public double getFissionInterval(){
        return _fissionInterval;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
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
            notifyAtomicWeightChanged(null);
            
            // Start a timer to kick off fission.
            _fissionTime = _clock.getSimulationTime() + _fissionInterval;
            
            // Indicate that the nucleus was captured.
            retval = true;
        }
        
        return retval;
    }
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(){
        
        // Reset the fission time to 0, indicating that it shouldn't occur
        // until something changes.
        _fissionTime = 0;

        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Fission or absorption has occurred.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyAtomicWeightChanged(null);
        }
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response we check if it is time to fission.
     */
    protected void handleClockTicked(ClockEvent clockEvent)
    {
        super.handleClockTicked( clockEvent );
        
        // See if fission should occur.
        if ((_fissionTime != 0) && (clockEvent.getSimulationTime() >= _fissionTime ))
        {
            // Fission the nucleus.  First create three neutrons as byproducts 
            // of this decay event.
            ArrayList byProducts = new ArrayList();
            for (int i = 0; i < 3; i++){
                byProducts.add( new Neutron(_position.getX(), _position.getY(), false) );
            }
            _numNeutrons -= 3;

            // Now create the appropriate daughter nucleus and add it to the
            // list of byproducts.
            Point2D location = new Point2D.Double();
            location.setLocation( _position );
            DaughterNucleus daughterNucleus = 
                new DaughterNucleus(_clock, location, DAUGHTER_NUCLEUS_PROTONS, DAUGHTER_NUCLEUS_NEUTRONS);
            byProducts.add( daughterNucleus );
            
            // Reduce our constituent particles appropriately.
            _numProtons -= DAUGHTER_NUCLEUS_PROTONS;
            _numNeutrons -= DAUGHTER_NUCLEUS_NEUTRONS;
            
            // Send out the decay event to all listeners.
            notifyAtomicWeightChanged(byProducts);
            
            // Set the fission time to 0 to indicate that no more fissioning
            // should occur.
            _fissionTime = 0;
        }
    }
}
