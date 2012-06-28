package edu.colorado.phet.fractionmatcher.view;

import fj.data.List;

import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameConstants;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractionmatcher.model.Mode;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.ParameterKeys;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.fractionmatcher.model.MatchingGameState.newLevel;
import static edu.colorado.phet.fractionmatcher.view.MatchingGameCanvas.setNodeVisible;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;

/**
 * Shows all the parts of the start screen, including title, level selection buttons, audio+timer buttons
 *
 * @author Sam Reid
 */
public class StartScreen extends PNode {
    public StartScreen( final MatchingGameModel model, final String title, final List<PNode> patterns ) {
        //Game settings
        final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, 8, 1 ), false, false );

        //Function invoked when the user pushes a level button to start the game.
        final VoidFunction0 startGame = new VoidFunction0() {
            public void apply() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {

                        final MatchingGameState m = newLevel( gameSettings.level.get(), model.state.get().gameResults, model.levelFactory ).
                                withMode( Mode.USER_IS_MOVING_OBJECTS_TO_THE_SCALES ).
                                withAudio( gameSettings.soundEnabled.get() ).
                                withTimerVisible( gameSettings.timerEnabled.get() );
                        model.state.set( m );
                    }
                } );
            }
        };

        //Dialog for selecting and starting a level
        final PNode levelSelectionDialog = new ZeroOffsetNode( new LevelSelectionNode( startGame, gameSettings, model.gameResults, patterns ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );

            model.choosingSettings.addObserver( setNodeVisible( this ) );
        }};

        //Title text, only shown when the user is choosing a level
        final PNode titleText = new PNode() {{
            addChild( new PhetPText( title, new PhetFont( 38, true ) ) );
            model.choosingSettings.addObserver( setNodeVisible( this ) );

            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, levelSelectionDialog.getFullBounds().getMinY() / 3 - getFullBounds().getHeight() / 2 );
        }};
        addChild( levelSelectionDialog );
        addChild( titleText );

        final int iconWidth = 40;
        final BufferedImage stopwatchIcon = BufferedImageUtils.multiScaleToWidth( GameConstants.STOPWATCH_ICON, iconWidth );
        final BufferedImage soundIcon = BufferedImageUtils.multiScaleToWidth( GameConstants.SOUND_ICON, iconWidth );
        final BufferedImage soundOffIcon = BufferedImageUtils.multiScaleToWidth( GameConstants.SOUND_OFF_ICON, iconWidth );
        final int maxIconWidth = Math.max( stopwatchIcon.getWidth(), soundIcon.getWidth() ) + 10;
        final int maxIconHeight = Math.max( stopwatchIcon.getHeight(), soundIcon.getHeight() ) + 10;
        final ToggleButtonNode stopwatchButton = new ToggleButtonNode( new PaddedIcon( maxIconWidth, maxIconHeight, new PImage( stopwatchIcon ) ),
                                                                       gameSettings.timerEnabled,
                                                                       new VoidFunction0() {
                                                                           public void apply() {
                                                                               sendUserMessage( Components.stopwatchButton, UserComponentTypes.toggleButton, UserActions.pressed, parameterSet( ParameterKeys.timerEnabled, !gameSettings.timerEnabled.get() ) );
                                                                               gameSettings.timerEnabled.toggle();
                                                                           }
                                                                       }, ToggleButtonNode.FAINT_GREEN, false );

        // REVIEW - UpdateNode has been used elsewhere for this pattern.  Why not here?  Consider making this consistent with other similar code (and use UpdateNode).
        class SoundIconNode extends PNode {
            SoundIconNode() {
                gameSettings.soundEnabled.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( final Boolean enabled ) {
                        removeAllChildren();
                        addChild( new PaddedIcon( maxIconWidth, maxIconHeight, new PImage( enabled ? soundIcon : soundOffIcon ) ) );
                    }
                } );
            }
        }

        final ToggleButtonNode soundButton = new ToggleButtonNode( new SoundIconNode(), gameSettings.soundEnabled, new VoidFunction0() {
            public void apply() {

                sendUserMessage( Components.soundButton, UserComponentTypes.toggleButton, UserActions.pressed, parameterSet( ParameterKeys.soundEnabled, !gameSettings.soundEnabled.get() ) );
                gameSettings.soundEnabled.toggle();
            }
        }, ToggleButtonNode.FAINT_GREEN, false );
        addChild( new HBox( stopwatchButton, soundButton ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, STAGE_SIZE.height - getFullBounds().getHeight() - INSET );
            model.choosingSettings.addObserver( setNodeVisible( this ) );
        }} );
    }
}