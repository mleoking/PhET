/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.defaults.ChainReactionDefaults;
import edu.colorado.phet.nuclearphysics2.defaults.FissionOneNucleusDefaults;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;


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
    private ClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FissionOneNucleusModule( Frame parentFrame ) {
        super( NuclearPhysics2Strings.TITLE_FISSION_ONE_NUCLEUS_MODULE, 
               new NuclearPhysics2Clock( FissionOneNucleusDefaults.CLOCK_FRAME_RATE, FissionOneNucleusDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysics2Clock clock = (NuclearPhysics2Clock) getClock();
        _model = new FissionOneNucleusModel(clock);

        // Canvas
        _canvas = new FissionOneNucleusCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new FissionOneNucleusControlPanel( this, parentFrame );
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
    }
}
