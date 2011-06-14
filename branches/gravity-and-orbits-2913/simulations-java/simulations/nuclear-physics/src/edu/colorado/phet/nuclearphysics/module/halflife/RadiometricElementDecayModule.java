// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.halflife;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricDecayDefaults;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates alpha decay of a single atomic
 * nucleus. 
 *
 * @author John Blanco
 */
public class RadiometricElementDecayModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RadiometricElementDecayModel _model;
    private RadiometricElementDecayCanvas _canvas;
    private IsotopeSelectionControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RadiometricElementDecayModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_RADIOMETRIC_ELEMENT_HALF_LIFE,
               new NuclearPhysicsClock( RadiometricDecayDefaults.CLOCK_FRAME_RATE, RadiometricDecayDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new RadiometricElementDecayModel(clock);

        // Canvas
        _canvas = new RadiometricElementDecayCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new IsotopeSelectionControlPanel( this, parentFrame, _model, true );
        setControlPanel( _controlPanel );
        
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
        _model.reset();
        setClockRunningWhenActive( RadiometricDecayDefaults.CLOCK_RUNNING );
        
        // Reset the canvas and its sub-nodes.
        _canvas.reset();
    }
}
