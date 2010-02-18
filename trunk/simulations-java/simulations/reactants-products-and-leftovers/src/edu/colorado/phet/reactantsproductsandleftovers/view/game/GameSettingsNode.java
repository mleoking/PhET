/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control panel that provides settings for the Game.
 * At the start of each game, the user is asked to select their preferences via this panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettingsNode extends PhetPNode {
    
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ),  new EmptyBorder( 5, 14, 5, 14 ) );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );

    private final JRadioButton[] levelRadioButtons;
    private final JRadioButton timerOnRadioButton, timerOffRadioButton;
    private final JRadioButton soundOnRadioButton, soundOffRadioButton;
    private final JRadioButton imagesShowRadioButton, imagesHideRadioButton;
    
    public GameSettingsNode( final GameModel model ) {
        super();
        
        // Title
        JLabel titleLabel = new JLabel( "Game Settings" );
        titleLabel.setFont( new PhetFont( 24 ) );
        
        // Level control
        levelRadioButtons = new JRadioButton[ GameModel.getLevelRange().getLength() + 1 ];
        JLabel levelLabel = new JLabel( "Level:" );
        ButtonGroup levelButtonGroup = new ButtonGroup();
        JPanel levelPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        levelPanel.setOpaque( false );
        for ( int i = 0; i < levelRadioButtons.length; i++ ) {
            int level = GameModel.getLevelRange().getMin() + i;
            JRadioButton button = new JRadioButton( String.valueOf( level ) );
            button.setOpaque( false );
            levelRadioButtons[i] = button;
            levelPanel.add( button );
            levelButtonGroup.add( button );
        }
        
        // Timer control
        JLabel timerLabel = new JLabel( new ImageIcon( RPALImages.STOPWATCH ) );
        timerOnRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_ON );
        timerOnRadioButton.setOpaque( false );
        timerOffRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_OFF );
        timerOffRadioButton.setOpaque( false );
        ButtonGroup timerButtonGroup = new ButtonGroup();
        timerButtonGroup.add( timerOnRadioButton );
        timerButtonGroup.add( timerOffRadioButton );
        JPanel timerPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        timerPanel.setOpaque( false );
        timerPanel.add( timerOnRadioButton );
        timerPanel.add( timerOffRadioButton );
        
        // Sound control
        JLabel soundLabel = new JLabel( new ImageIcon( RPALImages.SOUND_ICON ) );
        soundOnRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_ON );
        soundOnRadioButton.setOpaque( false );
        soundOffRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_OFF );
        soundOffRadioButton.setOpaque( false );
        ButtonGroup soundButtonGroup = new ButtonGroup();
        soundButtonGroup.add( soundOnRadioButton );
        soundButtonGroup.add( soundOffRadioButton );
        JPanel soundPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        soundPanel.setOpaque( false );
        soundPanel.add( soundOnRadioButton );
        soundPanel.add( soundOffRadioButton );
        
        // Molecule images control
        JLabel imagesLabel = new JLabel( new ImageIcon( RPALImages.CO ) );
        imagesShowRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_SHOW );
        imagesShowRadioButton.setOpaque( false );
        imagesHideRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_HIDE );
        imagesHideRadioButton.setOpaque( false );
        ButtonGroup imageButtonGroup = new ButtonGroup();
        imageButtonGroup.add( imagesShowRadioButton );
        imageButtonGroup.add( imagesHideRadioButton );
        JPanel imagesPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        imagesPanel.setOpaque( false );
        imagesPanel.add( imagesShowRadioButton );
        imagesPanel.add( imagesHideRadioButton );
        
        JSeparator separator = new JSeparator();
        separator.setForeground( Color.BLACK );
        
        // Start! button
        JButton startButton = new JButton( "Start!" );
        startButton.setOpaque( false );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.startGame( getLevel(), isTimerOnSelected(), isSoundOnSelected(), isImagesOnSelected() );
            }
        });
        
        // panel
        JPanel panel = new JPanel();
        panel.setBackground( BACKGROUND );
        panel.setBorder( BORDER );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column, 2, 1, GridBagConstraints.CENTER );
        layout.addAnchoredComponent( levelLabel, row, column++, GridBagConstraints.EAST );
        layout.addComponent( levelPanel, row++, column );
        column = 0;
        layout.addAnchoredComponent( timerLabel, row, column++, GridBagConstraints.EAST );
        layout.addComponent( timerPanel, row++, column );
        column = 0;
        layout.addAnchoredComponent( soundLabel, row, column++, GridBagConstraints.EAST );
        layout.addComponent( soundPanel, row++, column );
        column = 0;
        layout.addAnchoredComponent( imagesLabel, row, column++, GridBagConstraints.EAST );
        layout.addComponent( imagesPanel, row++, column );
        column = 0;
        layout.addFilledComponent( separator, row++, column, 2, 1, GridBagConstraints.HORIZONTAL );
        layout.addComponent( startButton, row++, column, 2, 1, GridBagConstraints.CENTER );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        // listen to model
        model.addGameListener( new GameAdapter() {

            @Override
            public void levelChanged() {
                setLevel( model.getLevel() );
            }

            @Override
            public void timerVisibleChanged() {
                setTimerOnSelected( model.isTimerVisible() );
            }
            
            @Override
            public void soundEnabledChanged() {
                setSoundOnSelected( model.isSoundEnabled() );
            }
            
            @Override
            public void imagesVisibleChanged() {
                setImagesOnSelected( model.isImagesVisible() );
            }
        });
        
        // initial state
        setLevel( model.getLevel() );
        setTimerOnSelected( model.isTimerVisible() );
        setSoundOnSelected( model.isSoundEnabled() );
        setImagesOnSelected( model.isImagesVisible() );
    }
    
    private void setLevel( int level ) {
        int index = level - GameModel.getLevelRange().getMin();
        levelRadioButtons[index].setSelected( true );
    }
    
    private int getLevel() {
        int level = GameModel.getLevelRange().getMin() - 1; // invalid value, outside range
        for ( int i = 0; i < levelRadioButtons.length; i++ ) {
            if ( levelRadioButtons[i].isSelected() ) {
                level = GameModel.getLevelRange().getMin() + i;
                break;
            }
        }
        assert( GameModel.getLevelRange().contains( level ) );
        return level;
    }
    
    private void setTimerOnSelected( boolean selected ) {
        timerOnRadioButton.setSelected( selected );
        timerOffRadioButton.setSelected( !selected );
    }
    
    private boolean isTimerOnSelected() {
        return timerOnRadioButton.isSelected();
    }
    
    private void setSoundOnSelected( boolean selected ) {
        soundOnRadioButton.setSelected( selected );
        soundOffRadioButton.setSelected( !selected );
    }
    
    private boolean isSoundOnSelected() {
        return soundOnRadioButton.isSelected();
    }
    
    private void setImagesOnSelected( boolean selected ) {
        imagesShowRadioButton.setSelected( selected );
        imagesHideRadioButton.setSelected( !selected );
    }
    
    private boolean isImagesOnSelected() {
        return imagesShowRadioButton.isSelected();
    }
}
