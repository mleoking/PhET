/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.multinucleusalphadecay;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.model.Polonium211CompositeNucleus;

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used to demonstrate Alpha Decay for a single atomic
 * nucleus.
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayModel {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private Polonium211CompositeNucleus _atomicNucleus;
    private AlphaParticle _tunneledAlpha;
    private ConstantDtClock _clock;
    private ArrayList _listeners = new ArrayList();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public MultiNucleusAlphaDecayModel(NuclearPhysicsClock clock)
    {
        _clock = clock;

        // Register as a listener to the clock.
        clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Reset the nucleus, including passing the alpha particle
                // back to it.
                _atomicNucleus.reset( _tunneledAlpha );
                _tunneledAlpha = null;
            }
        });

        // Create the nucleus that will demonstrate alpha decay.
        _atomicNucleus = new Polonium211CompositeNucleus(clock, new Point2D.Double(0, 0));
        
        // Register as a listener for the nucleus so we can handle the
        // particles thrown off by alpha decay.
        
        _atomicNucleus.addListener( new AtomicNucleus.Adapter(){
            
            public void atomicWeightChanged(AtomicNucleus nucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                
                if (byProducts != null){
                    // There are some byproducts of this event that need to be
                    // managed by this object.
                    for (int i = 0; i < byProducts.size(); i++){
                        Object byProduct = byProducts.get( i );
                        if (byProduct instanceof AlphaParticle){
                            _tunneledAlpha = (AlphaParticle)byProduct;
                        }
                        else {
                            // We should never get here, debug it if it does.
                            System.err.println("Error: Unexpected byproduct of decay event.");
                            assert false;
                        }
                    }
                }
            }
            
            public void positionChanged(){
                // Ignore this event here.
            }
        });
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    /**
     * Get a reference to the nucleus, of which there is only one in this
     * model.
     * 
     * @return - Reference to the nucleus model element.
     */
    public Polonium211CompositeNucleus getAtomNucleus()
    {
        return _atomicNucleus;
    }
    
    public ConstantDtClock getClock(){
        return _clock;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

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
    // Private Methods
    //------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
        if (_tunneledAlpha != null){
            // We have a particle that has tunneled and needs to be moved.
            _tunneledAlpha.moveOut();
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
