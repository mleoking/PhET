// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.games.GameSettingsPanel.GameSettingsPanelAdapater;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeVisibility;
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
    private final JRadioButton hideNothingRadioButton, hideMoleculesRadioButton, hideNumbersRadioButton;
    
    public GameSettingsNode( final GameModel model ) {
        super();
        
        // Game settings panel
        panel = new GameSettingsPanel( GameModel.getLevelRange() );
        panel.setBackground( BACKGROUND );
        panel.addGameSettingsPanelListener( new GameSettingsPanelAdapater() {
            @Override
            public void startButtonPressed() {
                model.startGame( panel.getLevel(), panel.isTimerOn(), panel.isSoundOn(), getChallengeVisibility() );
            }
        });
        
        // customize the game settings by adding a "hide" control
        JLabel hideLabel = new JLabel( RPALStrings.LABEL_HIDE );
        hideNothingRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_NOTHING );
        hideNothingRadioButton.setOpaque( false );
        hideMoleculesRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_MOLECULES );
        hideMoleculesRadioButton.setOpaque( false );
        hideNumbersRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_NUMBERS );
        hideNumbersRadioButton.setOpaque( false );
        ButtonGroup hideButtonGroup = new ButtonGroup();
        hideButtonGroup.add( hideNothingRadioButton );
        hideButtonGroup.add( hideMoleculesRadioButton );
        hideButtonGroup.add( hideNumbersRadioButton );
        JPanel hidePanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        hidePanel.setOpaque( false );
        hidePanel.add( hideNothingRadioButton );
        hidePanel.add( hideMoleculesRadioButton );
        hidePanel.add( hideNumbersRadioButton );
        panel.addControl( hideLabel, hidePanel );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        // initial state
        panel.setLevel( model.getLevel() );
        panel.setTimerOn( model.isTimerVisible() );
        panel.setSoundOn( model.isSoundEnabled() );
        setChallengeVisibility( model.getChallengeVisibility() );
    }
    
    private void setChallengeVisibility( ChallengeVisibility challengeVisibility ) {
        hideNothingRadioButton.setSelected( challengeVisibility == ChallengeVisibility.BOTH );
        hideMoleculesRadioButton.setSelected( challengeVisibility == ChallengeVisibility.NUMBERS );
        hideNumbersRadioButton.setSelected( challengeVisibility == ChallengeVisibility.MOLECULES );
    }
    
    private ChallengeVisibility getChallengeVisibility() {
        ChallengeVisibility challengeVisibility = ChallengeVisibility.BOTH;
        if ( hideMoleculesRadioButton.isSelected() ) {
            challengeVisibility = ChallengeVisibility.NUMBERS;
        }
        else if ( hideNumbersRadioButton.isSelected() ) {
            challengeVisibility = ChallengeVisibility.MOLECULES;
        }
        return challengeVisibility;
    }
}
