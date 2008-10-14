/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.nuclearreactor;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.control.NuclearPhysicsClockControlPanel;
import edu.colorado.phet.nuclearphysics.defaults.ChainReactionDefaults;
import edu.colorado.phet.nuclearphysics.defaults.NuclearReactorDefaults;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;


/**
 * This is the class where the model and the view for the Nuclear Reactor tab
 * are created and initialized.
 *
 * @author John Blanco
 */
public class NuclearReactorModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private NuclearReactorModel _model;
    private NuclearReactorCanvas _canvas;
    private NuclearReactorControlPanel _controlPanel;
    private NuclearPhysicsClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public NuclearReactorModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_NUCLEAR_REACTOR_MODULE,
               new NuclearPhysicsClock( NuclearReactorDefaults.CLOCK_FRAME_RATE, NuclearReactorDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new NuclearReactorModel(clock);

        // Canvas
        _canvas = new NuclearReactorCanvas( _model, parentFrame );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new NuclearReactorControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new NuclearPhysicsClockControlPanel( getClock() );
        setClockControlPanel( _clockControlPanel );
        
        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock, which ultimately resets the model too.
        _model.getClock().resetSimulationTime();
        setClockRunningWhenActive( ChainReactionDefaults.CLOCK_RUNNING );
    }
    
    /**
     * Gets the specific model for this module.
     */
    public NuclearReactorModel getNuclearReactorModel(){
        return _model;
    }
}
