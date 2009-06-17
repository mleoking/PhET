/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricDecayDefaults;

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
    private ProbeTypeModel _probeTypeModel;
    private RadiometricMeasurementControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RadiometricMeasurementModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_RADIOMETRIC_MEASUREMENT,
               new NuclearPhysicsClock( RadiometricDecayDefaults.CLOCK_FRAME_RATE, RadiometricDecayDefaults.CLOCK_DT ));
 
        // Physical model
        _model = new RadiometricMeasurementModel();

        // Model components
        _probeTypeModel = new ProbeTypeModel();

        // Canvas
        _canvas = new RadiometricMeasurementCanvas( _model, _probeTypeModel );
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

}
