/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricDecayDefaults;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that allows the user to date multiple items and
 * guess their ages.
 *
 * @author John Blanco
 */
public class RadioactiveDatingGameModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RadioactiveDatingGameModel _model;
    private RadioactiveDatingGameCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RadioactiveDatingGameModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_RADIOACTIVE_DATING_GAME,
               new NuclearPhysicsClock( RadiometricDecayDefaults.CLOCK_FRAME_RATE, RadiometricDecayDefaults.CLOCK_DT ));
 
        // Physical model
        _model = new RadioactiveDatingGameModel();

        // Canvas
        _canvas = new RadioactiveDatingGameCanvas( _model );
        setSimulationPanel( _canvas );
        
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
