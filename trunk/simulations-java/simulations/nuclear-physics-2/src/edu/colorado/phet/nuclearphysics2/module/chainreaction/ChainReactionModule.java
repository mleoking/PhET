/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.chainreaction;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.defaults.AlphaRadiationDefaults;
import edu.colorado.phet.nuclearphysics2.defaults.ChainReactionDefaults;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationCanvas;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationControlPanel;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModel;

/**
 * This is the class where the model and the view for the Chain Reaction tab
 * are created and initialized.
 *
 * @author John Blanco
 */
public class ChainReactionModule extends PiccoloModule {
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ChainReactionModel _model;
    private ChainReactionCanvas _canvas;
    private ChainReactionControlPanel _controlPanel;
    private ClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ChainReactionModule( Frame parentFrame ) {
        super( NuclearPhysics2Strings.TITLE_CHAIN_REACTION_MODULE,
               new NuclearPhysics2Clock( ChainReactionDefaults.CLOCK_FRAME_RATE, ChainReactionDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysics2Clock clock = (NuclearPhysics2Clock) getClock();
        _model = new ChainReactionModel(clock);

        // Canvas
        _canvas = new ChainReactionCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new ChainReactionControlPanel( this, parentFrame );
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
    public ChainReactionModel getChainReactionModel(){
        return _model;
    }
    
}
