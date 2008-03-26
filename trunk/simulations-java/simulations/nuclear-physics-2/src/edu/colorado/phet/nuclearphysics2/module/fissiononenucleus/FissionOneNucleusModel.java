/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModel.Listener;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate atomic fission of a single nucleus
 * for this sim.
 *
 * @author John Blanco
 */
public class FissionOneNucleusModel {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    public static final double NUCLEUS_RADIUS = 15.0; // In femtometers.
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private AtomicNucleus _atomicNucleus;
    private NeutronSource _neutronSource;
    private ArrayList _listeners = new ArrayList();
    private ConstantDtClock _clock;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public FissionOneNucleusModel()
    {
        // Add the atomic nucleus to the center of this model.
        _atomicNucleus = new AtomicNucleus(0, 0, 235);
        
        // Add the neutron source to the side of the model.
        _neutronSource = new NeutronSource(-50, 0);
        
        // Create the clock that will drive this model.
        _clock = new ConstantDtClock(30, 1.0);

        // Add ourself as a clock tick listener and define our response.
        _clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                _atomicNucleus.clockTicked(clockEvent);
            }
        });
        
        // Start the clock.
        _clock.start();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * Get a reference to the nucleus, of which there is only one in this
     * model.
     * 
     * @return - Reference to the nucleus model element.
     */
    public AtomicNucleus getAtomicNucleus(){
        return _atomicNucleus;
    }
    
    /**
     * Get a reference to the neutron source, of which there is only one
     * in this model.
     * 
     * @return - Reference to the neutron generator model element.
     */
    public NeutronSource getNeutronSource(){
        return _neutronSource;
    }
    
    /**
     * This method allows the caller to register for changes in the overall
     * model, as opposed to changes in the individual model elements.
     * 
     * @param listener
     */
    public void addListener(Listener listener)
    {
        assert !_listeners.contains( listener );
        
        _listeners.add( listener );
    }

    //------------------------------------------------------------------------
    // Inner interfaces
    //------------------------------------------------------------------------
    
    /**
     * This listener interface allows listeners to get notified when an alpha
     * particle is added (i.e. come in to existence by separating from the
     * nucleus) or is removed (i.e. recombines with the nucleus).
     */
    public static interface Listener {
        /**
         * This informs the listener that an alpha particle has been added
         * to the model.
         * 
         * @param alphaParticle - Reference to the newly added particle.
         */
        public void particleAdded(AlphaParticle alphaParticle);
        
        /**
         * This is invoked when a particle is removed from the model.
         * 
         * @param alphaParticle - Reference to the particle that was removed.
         */
        public void particleRemoved(AlphaParticle alphaParticle);
    }
}
