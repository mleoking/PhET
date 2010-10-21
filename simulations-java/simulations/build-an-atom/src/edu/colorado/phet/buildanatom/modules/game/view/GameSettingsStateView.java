package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.GameModel;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
* @author Sam Reid
*/
public class GameSettingsStateView extends StateView {
    private final GameSettingsPanel panel;
    private final PNode gameSettingsNode;

    GameSettingsStateView( GameCanvas gameCanvas, final GameModel model ) {
        super( gameCanvas, model.getGameSettingsState() );
        panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
        gameSettingsNode = new PSwing( panel );
        panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
            @Override
            public void startButtonPressed() {
                model.startGame( panel.getLevel(), panel.isTimerOn(), panel.isSoundOn() );
            }
        } );
    }

    public void teardown() {
        removeChild( gameSettingsNode );
    }

    public void init() {
        gameSettingsNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameSettingsNode.getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameSettingsNode.getFullBoundsReference().height / 2 );
        addChild( gameSettingsNode );
    }
}
