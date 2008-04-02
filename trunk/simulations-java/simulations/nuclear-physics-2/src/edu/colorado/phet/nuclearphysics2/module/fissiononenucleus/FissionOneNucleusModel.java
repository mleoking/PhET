/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.model.Nucleon;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModel.Listener;
import edu.colorado.phet.nuclearphysics2.view.NeutronNode;

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
    private ArrayList _freeNucleons = new ArrayList();
    private ArrayList _listeners = new ArrayList();
    private ConstantDtClock _clock;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public FissionOneNucleusModel(NuclearPhysics2Clock clock)
    {
        // Add a nucleus of Uranium 235 to the model.
        _atomicNucleus = new AtomicNucleus(clock, new Point2D.Double( 0, 0 ), 92, 143);
        
        // Add the neutron source to the side of the model.
        _neutronSource = new NeutronSource(-30, 0);
        
        // Register as a listener to the neutron source so that we know when
        // new neutrons are generated.
        _neutronSource.addListener( new NeutronSource.Listener (){
            public void neutronGenerated(Neutron neutron){
                // Add this new neutron to the list of free particles.
                _freeNucleons.add( neutron );
            }
            public void positionChanged(){
                // Ignore this, since we don't really care about it.
            }
        });
        
        // Create the clock that will drive this model.
        _clock = new ConstantDtClock(30, 1.0);
        
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // TODO: JPB TBD
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
    
    /**
     * Handle a clock tick event.
     * 
     * @param ce - The clock event representing the sim clock at this point int time.
     */
    private void handleClockTicked(ClockEvent ce){
        
        // Move any free particles that exist.
        for (int i = 0; i < _freeNucleons.size(); i++){
            Nucleon freeNucleon = (Nucleon)_freeNucleons.get( i );
            assert freeNucleon instanceof Nucleon;
            freeNucleon.translate();

            // Check if any of the free particles have collided with the nucleus
            // and, if so, transfer the particle into the nucleus.
            if (Point2D.distance(freeNucleon.getPosition().getX(), freeNucleon.getPosition().getY(), 0, 0) <
                _atomicNucleus.getDiameter() / 2){
                
                _atomicNucleus.captureNeutron( freeNucleon );
                _freeNucleons.remove( i );
            }
        }
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
