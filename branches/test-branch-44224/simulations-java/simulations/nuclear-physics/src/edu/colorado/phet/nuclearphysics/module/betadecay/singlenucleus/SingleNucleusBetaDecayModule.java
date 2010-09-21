/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay.singlenucleus;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.BetaDecayDefaults;
import edu.colorado.phet.nuclearphysics.module.betadecay.BetaDecayControlPanel;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates beta decay of a single atomic
 * nucleus. 
 *
 * @author John Blanco
 */
public class SingleNucleusBetaDecayModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private SingleNucleusBetaDecayModel _model;
    private SingleNucleusBetaDecayCanvas _canvas;
    private BetaDecayControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SingleNucleusBetaDecayModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_SINGLE_ATOM_BETA_DECAY_MODULE,
               new NuclearPhysicsClock( BetaDecayDefaults.CLOCK_FRAME_RATE, BetaDecayDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new SingleNucleusBetaDecayModel(clock);

        // Canvas
        _canvas = new SingleNucleusBetaDecayCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new BetaDecayControlPanel( this, parentFrame, _model, _model.getLabelVisibilityModel() );
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
        setClockRunningWhenActive( BetaDecayDefaults.CLOCK_RUNNING );
    }
}
