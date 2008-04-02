/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Radiation within this sim.
 *
 * @author John Blanco
 */
public class AlphaRadiationModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    public static final double BREAKOUT_RADIUS = 11.0; // In femtometers, but not a realistic value.
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private AtomicNucleus _atomicNucleus;
    private ConstantDtClock _clock;
    private ArrayList _listeners = new ArrayList();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationModel(NuclearPhysics2Clock clock)
    {
        _clock = clock;
        
        // Create a nucleus of Polonium 211.
        _atomicNucleus = new AtomicNucleus(clock, new Point2D.Double(0, 0), 84, 127);
        
        // Start the clock.
        clock.start();
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
    public AtomicNucleus getAtomNucleus()
    {
        return _atomicNucleus;
    }
    
    public ConstantDtClock getClock(){
        return _clock;
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
