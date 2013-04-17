// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Piccolo dialog for changing the game level, whether the timer and sound are enabled, and for the starting the game.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameSettingsStateView extends StateView {
    private final PNode gameSettingsNode;

    public GameSettingsStateView( BuildAnAtomGameCanvas gameCanvas, final BuildAnAtomGameModel model ) {
        super( model, model.getGameSettingsState(), gameCanvas );
        VoidFunction0 startFunction = new VoidFunction0() {
            public void apply() {
                model.startGame();
            }
        };
        final GameSettingsPanel panel = new GameSettingsPanel( model.getGameSettings(), startFunction );
        gameSettingsNode = new PSwing( panel ) {
            {
                scale( 1.5 );
            }
        };
    }

    @Override
    public void teardown() {
        removeChild( gameSettingsNode );
    }

    @Override
    public void init() {
        gameSettingsNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameSettingsNode.getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameSettingsNode.getFullBoundsReference().height / 2 );
        addChild( gameSettingsNode );
    }
}
