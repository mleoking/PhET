// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import edu.colorado.phet.balancingchemicalequations.model.GameSettings;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.games.GameSettingsPanel.GameSettingsPanelListener;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * The "Game Settings" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
//TODO most of the code here deals with providing an adapter between Listener and Observer paradigms, push this into phetcommon.games
public class GameSettingsNode extends PhetPNode {

    public GameSettingsNode( final GameSettings gameSettings, final VoidFunction0 startFunction ) {

        IntegerRange levelRange = new IntegerRange( gameSettings.level.getMin(), gameSettings.level.getMax(), gameSettings.level.getValue() );

        final GameSettingsPanel panel = new GameSettingsPanel( levelRange ); //TODO this panel should take gameSettings and startFunction args
        addChild( new PSwing( panel ) );

        // changes to controls are applied to game settings
        panel.addGameSettingsPanelListener( new GameSettingsPanelListener() {

            public void levelChanged() {
                gameSettings.level.setValue( panel.getLevel() );
            }

            public void timerChanged() {
                gameSettings.timerEnabled.setValue( panel.isTimerOn() );
            }

            public void soundChanged() {
                gameSettings.soundEnabled.setValue( panel.isSoundOn() );
            }

            public void startButtonPressed() {
                startFunction.apply();
            }
        } );

        // changes to game settings are applied to controls
        gameSettings.level.addObserver( new SimpleObserver() {
            public void update() {
                panel.setLevel( gameSettings.level.getValue() );
            }
        } );
        gameSettings.timerEnabled.addObserver( new SimpleObserver() {
            public void update() {
                panel.setTimerOn( gameSettings.timerEnabled.getValue() );
            }
        } );
        gameSettings.soundEnabled.addObserver( new SimpleObserver() {
            public void update() {
                panel.setSoundOn( gameSettings.soundEnabled.getValue() );
            }
        } );
    }

}
