/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.fissiononenucleus;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.control.NuclearPhysicsClockControlPanel;
import edu.colorado.phet.nuclearphysics.defaults.ChainReactionDefaults;
import edu.colorado.phet.nuclearphysics.defaults.FissionOneNucleusDefaults;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsClock;


/**
 * This class is where the model and view classes for the "Fission: One 
 * Nucleus" tab of this simulation are created and contained.
 *
 * @author John Blanco
 */
public class FissionOneNucleusModule extends PiccoloModule {
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FissionOneNucleusModel _model;
    private FissionOneNucleusCanvas _canvas;
    private FissionOneNucleusControlPanel _controlPanel;
    private NuclearPhysicsClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FissionOneNucleusModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_FISSION_ONE_NUCLEUS_MODULE, 
               new NuclearPhysicsClock( FissionOneNucleusDefaults.CLOCK_FRAME_RATE, FissionOneNucleusDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new FissionOneNucleusModel(clock);

        // Canvas
        _canvas = new FissionOneNucleusCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new FissionOneNucleusControlPanel( this, parentFrame );
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
}
