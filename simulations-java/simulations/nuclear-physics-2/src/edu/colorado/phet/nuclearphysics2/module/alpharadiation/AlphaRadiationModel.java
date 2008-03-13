/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

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

    private AtomicNucleus _atomicNucleus = new AtomicNucleus(0, 0);
    private ArrayList _alphaParticles = new ArrayList();
    private ArrayList _listeners = new ArrayList();
    private ConstantDtClock _clock;
    
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
        
                for (int i = 0; i < _alphaParticles.size(); i++)
                {
                    AlphaParticle alpha = (AlphaParticle)_alphaParticles.get( i );
                    alpha.translate( 0.1, 0.01 );
                }
            }
        });
        
        // Start the clock.
        _clock.start();
    }
    
    public AtomicNucleus getAtom()
    {
        return _atomicNucleus;
    }
    
    public static interface Listener {
        public void particleAdded(AlphaParticle alphaParticle);
        public void particleRemoved(AlphaParticle alphaParticle);
    }
    
    public void addListener(Listener listener)
    {
        assert !_listeners.contains( listener );
        
        _listeners.add( listener );
    }
}
