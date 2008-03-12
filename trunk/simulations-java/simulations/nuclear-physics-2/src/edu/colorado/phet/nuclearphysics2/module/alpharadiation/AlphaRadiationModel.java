/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

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

    private AtomicNucleus _atomicNucleus;
    private AlphaParticle _alphaParticle;
    private ConstantDtClock _clock;
    
    public AlphaRadiationModel()
    {
        // Create the elements of the model.
        _atomicNucleus = new AtomicNucleus(0, 0);
        _alphaParticle = new AlphaParticle(0, 0);

        // Create the clock that will drive this model.
        _clock = new ConstantDtClock(30, 1.0);
        _clock.addClockListener( new ClockAdapter(){
            public void clockTicked(ClockEvent clockEvent){
                _atomicNucleus.translate(0, 0);
                _alphaParticle.translate(-0.5, 0.25);
            }
        });
        
        // Start the clock.
        _clock.start();
    }
    
    public AtomicNucleus getAtom()
    {
        return _atomicNucleus;
    }

    public AlphaParticle getAlphaParticle()
    {
        return _alphaParticle;
    }
}
