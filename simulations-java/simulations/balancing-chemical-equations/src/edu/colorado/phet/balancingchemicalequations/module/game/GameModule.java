// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.awt.Frame;

import edu.colorado.phet.balancingchemicalequations.BCEModule;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * The "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends BCEModule {

    public GameModule( Frame parentFrame ) {
        super( parentFrame, BCEStrings.TITLE_GAME, new BCEClock(), true /* startsPaused */ );
        setSimulationPanel( new PhetPCanvas() );//XXX
    }
}
