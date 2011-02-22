// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.view.BCECanvas;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

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
        PNode gameSettingsNode = new PSwing( new GameSettingsPanel( model.getGameSettings(), startFunction ) );
        gameSettingsNode.scale( BCEConstants.SWING_SCALE );
        addChild( gameSettingsNode );

//        // Game results
//        GameOverNode gameOverNode = new GameOverNode( 1,1,1,new DecimalFormat("0"),1,1,false,false );//XXX
//        addChild( gameOverNode );

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
