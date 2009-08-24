/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay.multinucleus;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.BetaDecayDefaults;
import edu.colorado.phet.nuclearphysics.module.betadecay.BetaDecayControlPanel;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates beta decay of a bunch of
 * nuclei.
 *
 * @author John Blanco
 */
public class MultiNucleusBetaDecayModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MultiNucleusBetaDecayModel _model;
    private MultiNucleusBetaDecayCanvas _canvas;
    private BetaDecayControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MultiNucleusBetaDecayModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_MULTI_ATOM_BETA_DECAY_MODULE,
               new NuclearPhysicsClock( BetaDecayDefaults.CLOCK_FRAME_RATE, BetaDecayDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new MultiNucleusBetaDecayModel(clock);

        // Canvas
        _canvas = new MultiNucleusBetaDecayCanvas( _model );
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

        // Reset the clock and the model.
        _model.getClock().resetSimulationTime();
        _model.reset();
        setClockRunningWhenActive( BetaDecayDefaults.CLOCK_RUNNING );
        
        // Reset the canvas and its sub-nodes.
        _canvas.reset();
    }
}
