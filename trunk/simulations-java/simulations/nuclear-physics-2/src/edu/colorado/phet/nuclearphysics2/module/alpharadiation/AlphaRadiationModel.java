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

/**
 * This class contains the Model portion of the Model-View-Controller 
 * architecture that is used demonstrate Alpha Radiation within this sim.
 *
 * @author John Blanco
 */
public class AlphaRadiationModel {

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private AtomicNucleus _atomicNucleus = new AtomicNucleus(0, 0);
    private ArrayList _alphaParticles = new ArrayList();
    private ArrayList _listeners = new ArrayList();
    private ConstantDtClock _clock;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationModel()
    {
        // Seed the random number generator.
        final Random random = new Random();
        
        // Create the clock that will drive this model.
        _clock = new ConstantDtClock(30, 1.0);
        _clock.addClockListener( new ClockAdapter(){
            public void clockTicked(ClockEvent clockEvent){
                
                if (random.nextDouble() <= 0.02)
                {
                    AlphaParticle alpha = new AlphaParticle(0, 0);
                    _alphaParticles.add( alpha );
                    
                    for (int i = 0; i < _listeners.size(); i++)
                    {
                        Listener listener = (Listener)_listeners.get( i );
                        listener.particleAdded(alpha);
                    }
                }
        
                // Test removal by removing any particles that are far away from the nucleus.
                for (int i = 0; i < _alphaParticles.size(); i++)
                {
                    AlphaParticle alpha = (AlphaParticle)_alphaParticles.get( i );
                    Point2D pos = alpha.getPosition();
                    if (pos.getX() > 20)
                    {
                        // Notify the listeners that the particle is being removed.
                        for (int j = 0; j < _listeners.size(); j++)
                        {
                            Listener listener = (Listener)_listeners.get( i );
                            listener.particleRemoved(alpha);
                        }
                        
                        // Remove the particle from our list.
                        _alphaParticles.remove( i );
                        
                        // Only remove one per clock tick.
                        break;
                    }
                    
                    // Move the particle.
                    alpha.translate( 0.2, .01 );
                }
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
    public AtomicNucleus getAtom()
    {
        return _atomicNucleus;
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
