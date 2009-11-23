package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameChangeAdapter;


public class GameSettingsPanel extends JPanel {

    private final JRadioButton[] levelRadioButtons;
    private final JRadioButton timerOnRadioButton, timerOffRadioButton;
    
    public GameSettingsPanel( final GameModel model ) {
        super();
        setBackground( Color.YELLOW );
        setBorder( new CompoundBorder( new LineBorder( Color.BLUE, 4 ), new CompoundBorder( new LineBorder( Color.BLACK, 2 ), new EmptyBorder( 14, 14, 14, 14 ) ) ) );
        
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
        JLabel timerLabel = new JLabel( "Timer:" );
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
                model.newGame( getLevel(), isTimerEnabled() );
            }
        });
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) );
        this.setLayout( layout );
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
        
        // listen to model
        model.addGameChangeListener( new GameChangeAdapter() {

            public void levelChanged() {
                setLevel( model.getLevel() );
            }

            public void timerEnableChanged() {
                setTimerEnabled( model.isTimerEnabled() );
            }
        });
        
        // initial state
        setLevel( model.getLevel() );
        setTimerEnabled( model.isTimerEnabled() );
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
    
    private void setTimerEnabled( boolean enabled ) {
        timerOnRadioButton.setSelected( enabled );
        timerOffRadioButton.setSelected( !enabled );
    }
    
    private boolean isTimerEnabled() {
        return timerOnRadioButton.isSelected();
    }
}
