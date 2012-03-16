// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.defaults.AlphaDecayDefaults;
import edu.colorado.phet.nuclearphysics.module.alphadecay.AlphaDecayControlPanel;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that demonstrates alpha decay of a single atomic
 * nucleus. 
 *
 * @author John Blanco
 */
public class MultiNucleusAlphaDecayModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MultiNucleusAlphaDecayModel _model;
    private MultiNucleusAlphaDecayCanvas _canvas;
    private AlphaDecayControlPanel _controlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MultiNucleusAlphaDecayModule( Frame parentFrame ) {
        super( NuclearPhysicsStrings.TITLE_MULTI_ATOM_ALPHA_DECAY_MODULE,
               new NuclearPhysicsClock( AlphaDecayDefaults.CLOCK_FRAME_RATE, AlphaDecayDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysicsClock clock = (NuclearPhysicsClock) getClock();
        _model = new MultiNucleusAlphaDecayModel(clock);

        // Canvas
        _canvas = new MultiNucleusAlphaDecayCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new AlphaDecayControlPanel( this, parentFrame, _model );
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
        setClockRunningWhenActive( AlphaDecayDefaults.CLOCK_RUNNING );
        
        // Reset the canvas and its sub-nodes.
        _canvas.reset();
    }
}
