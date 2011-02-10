// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.view.BCECanvas;
import edu.colorado.phet.balancingchemicalequations.view.game.GameSettingsNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends BCECanvas {

    public GameCanvas( final GameModel model, BCEGlobalProperties globalProperties, Resettable resettable ) {
        super( globalProperties.getCanvasColorProperty() );

        // Game settings
        VoidFunction0 startFunction = new VoidFunction0() {
            public void apply() {
                model.startGame();
            }
        };
        GameSettingsNode gameSettingsNode = new GameSettingsNode( model.getGameSettings(), startFunction );
        addChild( gameSettingsNode );

        // layout
        gameSettingsNode.setOffset( 0, 0 );

        // Observers
        globalProperties.getMoleculesVisibleProperty().addObserver( new SimpleObserver() {
            public void update() {
                // TODO hide molecules
            }
        } );
    }

    public void reset() {
        //XXX
    }
}
