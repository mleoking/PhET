// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeVisibility;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.RPALGameSettings;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control panel that provides settings for the Game.
 * At the start of each game, the user is asked to select their preferences via this panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettingsNode extends PhetPNode {

    private static final Color BACKGROUND = new Color( 180, 205, 255 ); // light blue

    private final GameSettingsPanel panel;

    public GameSettingsNode( final GameModel model ) {
        super();

        // Game settings panel
        VoidFunction0 startFunction = new VoidFunction0() {
            public void apply() {
                model.startGame();
            }
        };
        panel = new RPALGameSettingsPanel( model.getGameSettings(), startFunction );
        panel.setBackground( BACKGROUND );

        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
    }

    /*
     * Customized game settings panel, with a "Hide" control.
     * Note that the semantics of the control are the opposite of the enum ChallengeVisibility.
     */
    private static class RPALGameSettingsPanel extends GameSettingsPanel {

        private final PropertyRadioButton<ChallengeVisibility> hideNothingRadioButton, hideMoleculesRadioButton, hideNumbersRadioButton;

        public RPALGameSettingsPanel( RPALGameSettings gameSettings, VoidFunction0 startFunction ) {
            super( gameSettings, startFunction );

            // customize the game settings by adding a "hide" control
            JLabel hideLabel = new JLabel( RPALStrings.LABEL_HIDE );

            hideNothingRadioButton = new PropertyRadioButton<ChallengeVisibility>( RPALStrings.RADIO_BUTTON_NOTHING, gameSettings.challengeVisibility,ChallengeVisibility.BOTH );
            hideNothingRadioButton.setOpaque( false );

            hideMoleculesRadioButton = new PropertyRadioButton<ChallengeVisibility>( RPALStrings.RADIO_BUTTON_MOLECULES, gameSettings.challengeVisibility,ChallengeVisibility.NUMBERS );
            hideMoleculesRadioButton.setOpaque( false );

            hideNumbersRadioButton = new PropertyRadioButton<ChallengeVisibility>( RPALStrings.RADIO_BUTTON_NUMBERS, gameSettings.challengeVisibility,ChallengeVisibility.MOLECULES );
            hideNumbersRadioButton.setOpaque( false );

            JPanel hidePanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            hidePanel.setOpaque( false );
            hidePanel.add( hideNothingRadioButton );
            hidePanel.add( hideMoleculesRadioButton );
            hidePanel.add( hideNumbersRadioButton );
            addControl( hideLabel, hidePanel );
        }

        @Override
        public void cleanup() {
            super.cleanup();
            hideNothingRadioButton.cleanup();
            hideMoleculesRadioButton.cleanup();
            hideNumbersRadioButton.cleanup();
        }
    }
}
