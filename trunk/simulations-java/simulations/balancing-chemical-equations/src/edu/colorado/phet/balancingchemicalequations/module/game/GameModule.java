// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEModule;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;

/**
 * The "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends BCEModule {

    private final GameModel model;
    private final GameCanvas canvas;

    public GameModule( BCEGlobalProperties globalProperties ) {
        super( globalProperties.getFrame(), BCEStrings.GAME, new BCEClock(), true /* startsPaused */ );
        model = new GameModel();
        canvas = new GameCanvas( model, globalProperties, this );
        setSimulationPanel( canvas );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
