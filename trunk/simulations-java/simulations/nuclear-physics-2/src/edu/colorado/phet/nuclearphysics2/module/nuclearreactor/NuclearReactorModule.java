/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.defaults.ChainReactionDefaults;
import edu.colorado.phet.nuclearphysics2.defaults.NuclearReactorDefaults;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.module.chainreaction.ChainReactionCanvas;
import edu.colorado.phet.nuclearphysics2.module.chainreaction.ChainReactionControlPanel;
import edu.colorado.phet.nuclearphysics2.module.chainreaction.ChainReactionModel;


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
    private ClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public NuclearReactorModule( Frame parentFrame ) {
        super( NuclearPhysics2Strings.TITLE_NUCLEAR_REACTOR_MODULE,
               new NuclearPhysics2Clock( ChainReactionDefaults.CLOCK_FRAME_RATE, NuclearReactorDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysics2Clock clock = (NuclearPhysics2Clock) getClock();
        _model = new NuclearReactorModel(clock);

        // Canvas
        _canvas = new NuclearReactorCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new NuclearReactorControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new ClockControlPanel( (NuclearPhysics2Clock) getClock() );
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
        
        // Reset the model to its original state.
        _model.reset();
    }
    
    /**
     * Gets the specific model for this module.
     */
    public NuclearReactorModel getChainReactionModel(){
        return _model;
    }
}
