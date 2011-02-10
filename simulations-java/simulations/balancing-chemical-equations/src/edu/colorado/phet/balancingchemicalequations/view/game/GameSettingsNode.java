// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import edu.colorado.phet.balancingchemicalequations.model.ConstrainedIntegerProperty;
import edu.colorado.phet.balancingchemicalequations.model.GameSettings;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.games.GameSettingsPanel.GameSettingsPanelListener;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * The "Game Settings" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
//TODO most of the code here deals with providing an adapter between Listener and Observer paradigms, push this into phetcommon.games
public class GameSettingsNode extends PhetPNode {

    public GameSettingsNode( final GameSettings gameSettings ) { //TODO add a startFunction arg

        ConstrainedIntegerProperty levelProperty = gameSettings.getLevelProperty();
        IntegerRange levelRange = new IntegerRange( levelProperty.getMin(), levelProperty.getMax(), levelProperty.getValue() );

        final GameSettingsPanel panel = new GameSettingsPanel( levelRange ); //TODO this panel should take gameSettings and startFunction args
        addChild( new PSwing( panel ) );

        // changes to controls are applied to game settings
        panel.addGameSettingsPanelListener( new GameSettingsPanelListener() {

            public void levelChanged() {
                gameSettings.setLevel( panel.getLevel() );
            }

            public void timerChanged() {
                gameSettings.setTimerEnabled( panel.isTimerOn() );
            }

            public void soundChanged() {
                gameSettings.setSoundEnabled( panel.isSoundOn() );
            }

            public void startButtonPressed() {
                //TODO call startFunction
            }
        } );

        // changes to game settings are applied to controls
        gameSettings.getLevelProperty().addObserver( new SimpleObserver() {
            public void update() {
                panel.setLevel( gameSettings.getLevel() );
            }
        } );
        gameSettings.getTimerEnabledProperty().addObserver( new SimpleObserver() {
            public void update() {
                panel.setTimerOn( gameSettings.isTimerEnabled() );
            }
        } );
        gameSettings.getSoundEnabledProperty().addObserver( new SimpleObserver() {
            public void update() {
                panel.setSoundOn( gameSettings.isSoundEnabled() );
            }
        } );
    }

}
