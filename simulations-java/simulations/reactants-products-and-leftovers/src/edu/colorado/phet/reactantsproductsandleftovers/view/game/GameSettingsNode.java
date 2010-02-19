/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.*;
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
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control panel that provides settings for the Game.
 * At the start of each game, the user is asked to select their preferences via this panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettingsNode extends PhetPNode {
    
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLACK, 1 ),  new EmptyBorder( 5, 14, 5, 14 ) );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );

    private final JRadioButton[] levelRadioButtons;
    private final JRadioButton timerOnRadioButton, timerOffRadioButton;
    private final JRadioButton soundOnRadioButton, soundOffRadioButton;
    private final JRadioButton showImagesRadioButton, hideImagesRadioButton;
    private final JRadioButton showNumbersRadioButton, hideNumbersRadioButton;
    
    public GameSettingsNode( final GameModel model ) {
        super();
        
        // Title
        JLabel titleLabel = new JLabel( "Game Settings" );
        titleLabel.setFont( TITLE_FONT );
        
        // separator
        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setForeground( Color.BLACK );
        
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
        PImage imageNode = new PImage( RPALImages.CO );
        imageNode.scale( 0.65 );
        JLabel imagesLabel = new JLabel( new ImageIcon( imageNode.toImage() ) );
        showImagesRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_SHOW );
        showImagesRadioButton.setOpaque( false );
        hideImagesRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_HIDE );
        hideImagesRadioButton.setOpaque( false );
        hideImagesRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( hideImagesRadioButton.isSelected() && hideNumbersRadioButton.isSelected() ) {
                    setShowNumbersSelected( true );
                }
            }
        });
        ButtonGroup imageButtonGroup = new ButtonGroup();
        imageButtonGroup.add( showImagesRadioButton );
        imageButtonGroup.add( hideImagesRadioButton );
        JPanel imagesPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        imagesPanel.setOpaque( false );
        imagesPanel.add( showImagesRadioButton );
        imagesPanel.add( hideImagesRadioButton );
        
        // Numbers controls
        JLabel numbersLabel = new JLabel( "1 2 3" );
        showNumbersRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_SHOW );
        showNumbersRadioButton.setOpaque( false );
        hideNumbersRadioButton = new JRadioButton( RPALStrings.RADIO_BUTTON_HIDE );
        hideNumbersRadioButton.setOpaque( false );
        hideNumbersRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( hideNumbersRadioButton.isSelected() && hideImagesRadioButton.isSelected() ) {
                    setShowImagesSelected( true );
                }
            }
        });
        ButtonGroup numbersButtonGroup = new ButtonGroup();
        numbersButtonGroup.add( showNumbersRadioButton );
        numbersButtonGroup.add( hideNumbersRadioButton );
        JPanel numbersPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        numbersPanel.setOpaque( false );
        numbersPanel.add( showNumbersRadioButton );
        numbersPanel.add( hideNumbersRadioButton );
        
        // separator
        JSeparator buttonSeparator = new JSeparator();
        buttonSeparator.setForeground( Color.BLACK );
        
        // Start! button
        JButton startButton = new JButton( "Start!" );
        startButton.setOpaque( false );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.startGame( getLevel(), isTimerOnSelected(), isSoundOnSelected(), isShowImagesSelected(), isShowNumbersSelected() );
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
        layout.addFilledComponent( titleSeparator, row++, column, 2, 1, GridBagConstraints.HORIZONTAL );
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
        layout.addAnchoredComponent( numbersLabel, row, column++, GridBagConstraints.EAST );
        layout.addComponent( numbersPanel, row++, column );
        column = 0;
        layout.addFilledComponent( buttonSeparator, row++, column, 2, 1, GridBagConstraints.HORIZONTAL );
        layout.addComponent( startButton, row++, column, 2, 1, GridBagConstraints.CENTER );
        
        // PSwing wrapper
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        // initial state
        setLevel( model.getLevel() );
        setTimerOnSelected( model.isTimerVisible() );
        setSoundOnSelected( model.isSoundEnabled() );
        setShowImagesSelected( model.isImagesVisible() );
        setShowNumbersSelected( model.isNumbersVisible() );
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
    
    private void setShowImagesSelected( boolean selected ) {
        showImagesRadioButton.setSelected( selected );
        hideImagesRadioButton.setSelected( !selected );
    }
    
    private boolean isShowImagesSelected() {
        return showImagesRadioButton.isSelected();
    }
    
    private void setShowNumbersSelected( boolean selected ) {
        showNumbersRadioButton.setSelected( selected );
        hideNumbersRadioButton.setSelected( !selected );
    }
    
    private boolean isShowNumbersSelected() {
        return showNumbersRadioButton.isSelected();
    }
}
