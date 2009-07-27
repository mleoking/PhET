/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricMeasurementDefaults;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates radiometric dating of an
 * individual item.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RadiometricMeasurementModel _model;
    private RadiometricMeasurementCanvas _canvas;
    private RadiometricMeasurementControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RadiometricMeasurementModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_RADIOMETRIC_MEASUREMENT, RadiometricMeasurementDefaults.CLOCK );
 
        // Physical model
        _model = new RadiometricMeasurementModel( RadiometricMeasurementDefaults.CLOCK );

        // Canvas
        _canvas = new RadiometricMeasurementCanvas( _model );
        setSimulationPanel( _canvas );
        
        // Control Panel
        _controlPanel = new RadiometricMeasurementControlPanel( this, parentFrame, _model );
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
    @Override
    public void reset() {
        // Reset the clock and set it to not be running.
        _model.getClock().resetSimulationTime();
        setClockRunningWhenActive( RadiometricMeasurementDefaults.CLOCK_RUNNING );
    }
}
