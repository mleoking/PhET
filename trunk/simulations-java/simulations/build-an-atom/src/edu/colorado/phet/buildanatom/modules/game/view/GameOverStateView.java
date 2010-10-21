package edu.colorado.phet.buildanatom.modules.game.view;

import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.GameModel;
import edu.colorado.phet.common.games.GameOverNode;

/**
* @author Sam Reid
*/
public class GameOverStateView extends StateView {
    private final GameOverNode gameOverNode;

    GameOverStateView( GameCanvas gameCanvas, final GameModel model ) {
        super( gameCanvas, model.getGameOverState() );
        gameOverNode = new GameOverNode( 1, 5, 5, new DecimalFormat( "0.#" ), 40000, 30000, false, true );
        gameOverNode.addGameOverListener( new GameOverNode.GameOverListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );
    }

    public void init() {
        gameOverNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameOverNode.getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameOverNode.getFullBoundsReference().height / 2 );
        addChild( gameOverNode );
    }

    public void teardown() {
        removeChild( gameOverNode );
    }
}
