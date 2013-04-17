// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.chainreaction;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.control.NuclearPhysicsClockControlPanel;
import edu.colorado.phet.nuclearphysics.defaults.ChainReactionDefaults;

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
    private NuclearPhysicsClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ChainReactionModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_CHAIN_REACTION_MODULE,
               new NuclearPhysicsClock( ChainReactionDefaults.CLOCK_FRAME_RATE, ChainReactionDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new ChainReactionModel(clock);

        // Canvas
        _canvas = new ChainReactionCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new ChainReactionControlPanel( this, parentFrame );
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
    public ChainReactionModel getChainReactionModel(){
        return _model;
    }
    
}
