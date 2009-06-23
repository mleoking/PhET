/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.decayrates;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.RadiometricDecayDefaults;
import edu.colorado.phet.nuclearphysics.module.halflife.IsotopeSelectionControlPanel;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates alpha decay of a single atomic
 * nucleus. 
 *
 * @author John Blanco
 */
public class DecayRatesModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private DecayRatesModel _model;
    private DecayRatesCanvas _canvas;
    private IsotopeSelectionControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DecayRatesModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_LOTS_OF_NUCLEI_DECAYING,
               new NuclearPhysicsClock( RadiometricDecayDefaults.CLOCK_FRAME_RATE, RadiometricDecayDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new DecayRatesModel(clock);

        // Canvas
        _canvas = new DecayRatesCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new IsotopeSelectionControlPanel( this, parentFrame, _model, false );
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
    }
}
