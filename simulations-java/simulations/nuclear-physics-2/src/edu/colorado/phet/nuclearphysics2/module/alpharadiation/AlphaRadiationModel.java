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
    private int _tickCounter = 0;
    
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationModel()
    {
        // Various random number generators needed for model behavior.
        final Random numParticlesRand = new Random();
        final Random particleLocationRand = new Random();
        
        // Create the clock that will drive this model.
        _clock = new ConstantDtClock(30, 1.0);
        _clock.addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                
                _tickCounter++;
                
                // Decide if any particles should be added or removed.  Note
                // that we don't do this on every tick, otherwise things look
                // too chaotic.
                if (_tickCounter % 20 == 0)
                {
                    int numParticles = _alphaParticles.size();
                    
                    double temp = Math.round( numParticlesRand.nextGaussian() + 3.0 );
                    if (temp < 0){
                        temp = 0;
                    }
                        
                    //int targetParticleNum = (int)((numParticlesRand.nextGaussian() + 1.0) * 3);
                    int targetParticleNum = (int)(temp);
                    
                    if (targetParticleNum > numParticles)
                    {
                        for (int i = 0; i < targetParticleNum - numParticles; i++)
                        {
                            // Add a new particle.
                            
                            double randPos = particleLocationRand.nextDouble();
                            double xPos = Math.sin( randPos * 2 * Math.PI ) * 7;
                            double yPos = Math.cos( randPos * 2 * Math.PI ) * 7;
                            AlphaParticle alpha = new AlphaParticle(xPos, yPos);
                            _alphaParticles.add( alpha );                    

                            for (int j = 0; j < _listeners.size(); j++)
                            {
                                Listener listener = (Listener)_listeners.get( j );
                                listener.particleAdded(alpha);
                            }
                        } 
                    }
                    else if (numParticles > targetParticleNum)
                    {
                        for (int i = 0; i < numParticles - targetParticleNum; i++)
                        {
                            // Get the particle to be removed.
                            AlphaParticle alphaToBeRemoved = (AlphaParticle)_alphaParticles.get( 0 );
                            
                            // Notify the listeners that the particle is being removed.
                            for (int j = 0; j < _listeners.size(); j++)
                            {
                                Listener listener = (Listener)_listeners.get( j );
                                listener.particleRemoved(alphaToBeRemoved);
                            }
                            
                            // Remove the particle from our list.
                            _alphaParticles.remove( alphaToBeRemoved );                            
                        }
                    }
                }
        
                // Move each of the particles.
                for (int i = 0; i < _alphaParticles.size(); i++)
                {
                    AlphaParticle alpha = (AlphaParticle)_alphaParticles.get( i );
                    /*
                    // Test particle removal.                    
                    Point2D pos = alpha.getPosition();
                    if (pos.getX() > 20)
                    {
                        // Notify the listeners that the particle is being removed.
                        for (int j = 0; j < _listeners.size(); j++)
                        {
                            Listener listener = (Listener)_listeners.get( j );
                            listener.particleRemoved(alpha);
                        }
                        
                        // Remove the particle from our list.
                        _alphaParticles.remove( i );
                        
                        // Only remove one per clock tick.
                        break;
                    }
                    */
                    
                    // Position the particle.
                    Point2D alphaPos = alpha.getPosition();
                    double normalizer = alphaPos.distance( 0, 0 );
                    alpha.translate( 0.2*(alphaPos.getX()/normalizer), 0.2*(alphaPos.getY()/normalizer ));
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
