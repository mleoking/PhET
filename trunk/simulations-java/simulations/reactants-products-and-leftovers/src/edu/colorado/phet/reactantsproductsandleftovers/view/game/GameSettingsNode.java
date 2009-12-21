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


public class GameSettingsNode extends PhetPNode {
    
    private static final Border BORDER = new CompoundBorder( new LineBorder( Color.BLUE, 4 ), new CompoundBorder( new LineBorder( Color.BLACK, 2 ), new EmptyBorder( 14, 14, 14, 14 ) ) );
    private static final Color BACKGROUND = Color.YELLOW;

    private final JRadioButton[] levelRadioButtons;
    private final JRadioButton timerOnRadioButton, timerOffRadioButton;
    
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
            levelRadioButtons[i] = button;
            levelPanel.add( button );
            levelButtonGroup.add( button );
        }
        
        // Timer control
        JLabel timerLabel = new JLabel( new ImageIcon( RPALImages.STOPWATCH, RPALStrings.LABEL_TIMER ) );
        timerOnRadioButton = new JRadioButton( "on" );
        timerOnRadioButton.setOpaque( false );
        timerOffRadioButton = new JRadioButton( "off" );
        timerOffRadioButton.setOpaque( false );
        ButtonGroup timerButtonGroup = new ButtonGroup();
        timerButtonGroup.add( timerOnRadioButton );
        timerButtonGroup.add( timerOffRadioButton );
        timerOffRadioButton.setSelected( true );
        JPanel timerPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        timerPanel.setOpaque( false );
        timerPanel.add( timerOnRadioButton );
        timerPanel.add( timerOffRadioButton );
        
        JSeparator separator = new JSeparator();
        separator.setForeground( Color.BLACK );
        
        // Start! button
        JButton startButton = new JButton( "Start!" );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.startGame( getLevel(), isTimerOnSelected() );
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
        });
        
        // initial state
        setLevel( model.getLevel() );
        setTimerOnSelected( model.isTimerVisible() );
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
}
