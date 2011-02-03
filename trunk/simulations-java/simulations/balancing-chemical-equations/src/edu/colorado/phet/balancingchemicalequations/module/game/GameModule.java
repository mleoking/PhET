// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.awt.Frame;

import edu.colorado.phet.balancingchemicalequations.BCEModule;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.BalancingChemicalEquationsApplication;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;

/**
 * The "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends BCEModule {

    private final GameModel model;
    private final GameCanvas canvas;

    public GameModule( BalancingChemicalEquationsApplication app, Frame parentFrame, boolean dev ) {
        super( app, parentFrame, BCEStrings.GAME, new BCEClock(), true /* startsPaused */ );
        model = new GameModel();
        canvas = new GameCanvas( model );
        setSimulationPanel( canvas );
    }

    public void setMoleculesVisible( boolean moleculesVisible ) {
        canvas.setMoleculesVisible( moleculesVisible );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}
