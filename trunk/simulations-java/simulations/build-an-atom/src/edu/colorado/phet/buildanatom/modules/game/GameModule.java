/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game;

import java.awt.Frame;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the game tab.
 */
public class GameModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GameCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameModule() {
        super( BuildAnAtomStrings.TITLE_GAME_MODULE, new BuildAnAtomClock( ) );
        setClockControlPanel( null );

        // Canvas
        canvas = new GameCanvas( new BuildAnAtomGameModel() );
        setSimulationPanel( canvas );

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
    }
}
