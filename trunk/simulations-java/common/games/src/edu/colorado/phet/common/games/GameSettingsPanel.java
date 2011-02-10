// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.games;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Control panel that provides settings for a game.
 * Provides settings for level, timer on/off, and sound on/off.
 * You can add additional controls via the addControl method.
 * <p>
 * This panel was generalized from the game in reactants-products-and-leftovers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettingsPanel extends GridPanel {

    // images
    private static final BufferedImage SOUND_ICON = GamesResources.getImage( "sound-icon.png" );
    private static final BufferedImage STOPWATCH_ICON = GamesResources.getImage( "blue-stopwatch.png" );

    // localized strings
    private static final String TITLE_GAME_SETTINGS = PhetCommonResources.getString( "Games.title.gameSettings" );
    private static final String LABEL_LEVEL_CONTROL = PhetCommonResources.getString( "Games.label.levelControl" );
    private static final String RADIO_BUTTON_ON = PhetCommonResources.getString( "Games.radioButton.on" );
    private static final String RADIO_BUTTON_OFF = PhetCommonResources.getString( "Games.radioButton.off" );
    private static final String BUTTON_START = PhetCommonResources.getString( "Games.button.start" );

    // "look" properties
    private static final PhetFont TITLE_FONT = new PhetFont( 24 );
    private static final PhetFont LABEL_FONT = new PhetFont();
    private static final PhetFont CONTROL_FONT = new PhetFont();
    private static final Border BORDER = new LineBorder( Color.BLACK, 1 );
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );

    // layout properties
    private static final int X_MARGIN = 5;
    private static final int X_SPACING = 5;
    private static final int Y_SPACING = 6;

    private final IntegerRange levelRange;
    private final EventListenerList listeners;
    private final JRadioButton[] levelRadioButtons;
    private final JRadioButton timerOnRadioButton, timerOffRadioButton;
    private final JRadioButton soundOnRadioButton, soundOffRadioButton;
    private final GridPanel inputPanel;

    private int inputRow; // next control added to the "input panel" will appear in this row

    /**
     * This constructor handles observes properties in GameSettings, and handles setting up the synchronization
     * between Property<T> observers and old-style GameSettingsPanelListener.
     *
     * @param gameSettings
     * @param startFunction
     */
    public GameSettingsPanel( final GameSettings gameSettings, final VoidFunction0 startFunction ) {
        this(  new IntegerRange( gameSettings.level.getMin(), gameSettings.level.getMax(), gameSettings.level.getValue() ) );

        // changes to controls are applied to game settings
        addGameSettingsPanelListener( new GameSettingsPanelListener() {

            public void levelChanged() {
                gameSettings.level.setValue( getLevel() );
            }

            public void timerChanged() {
                gameSettings.timerEnabled.setValue(isTimerOn() );
            }

            public void soundChanged() {
                gameSettings.soundEnabled.setValue(isSoundOn() );
            }

            public void startButtonPressed() {
                startFunction.apply();
            }
        } );

        // changes to game settings are applied to controls
        gameSettings.level.addObserver( new SimpleObserver() {
            public void update() {
               setLevel( gameSettings.level.getValue() );
            }
        } );
        gameSettings.timerEnabled.addObserver( new SimpleObserver() {
            public void update() {
               setTimerOn( gameSettings.timerEnabled.getValue() );
            }
        } );
        gameSettings.soundEnabled.addObserver( new SimpleObserver() {
            public void update() {
               setSoundOn( gameSettings.soundEnabled.getValue() );
            }
        } );
    }

    public GameSettingsPanel( IntegerRange levelRange ) {
        this( levelRange, TITLE_FONT, LABEL_FONT, CONTROL_FONT );
    }

    public GameSettingsPanel( IntegerRange levelRange, PhetFont titleFont, PhetFont labelFont, PhetFont controlFont ) {
        setBorder( BORDER );
        setBackground( BACKGROUND_FILL_COLOR );

        this.levelRange = new IntegerRange( levelRange );
        this.listeners = new EventListenerList();

        // Title
        JLabel titleLabel = new JLabel( TITLE_GAME_SETTINGS );
        titleLabel.setFont( TITLE_FONT );

        // title separator
        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setForeground( Color.BLACK );

        // input panel
        inputPanel = new GridPanel();
        inputPanel.setOpaque( false );
        {
            // Level control
            JLabel levelLabel = new JLabel( LABEL_LEVEL_CONTROL );
            levelLabel.setFont( LABEL_FONT );
            JPanel levelPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            levelRadioButtons = new JRadioButton[levelRange.getLength() + 1];
            ButtonGroup levelButtonGroup = new ButtonGroup();
            levelPanel.setOpaque( false );
            for ( int i = 0; i < levelRadioButtons.length; i++ ) {
                int level = levelRange.getMin() + i;
                JRadioButton button = new JRadioButton( String.valueOf( level ) );
                button.setFont( CONTROL_FONT );
                button.setOpaque( false );
                levelRadioButtons[i] = button;
                levelPanel.add( button );
                levelButtonGroup.add( button );
            }

            // Timer control
            JLabel timerLabel = new JLabel( new ImageIcon( STOPWATCH_ICON ) );
            timerLabel.setFont( LABEL_FONT );
            timerOnRadioButton = new JRadioButton( RADIO_BUTTON_ON );
            timerOnRadioButton.setFont( CONTROL_FONT );
            timerOnRadioButton.setOpaque( false );
            timerOffRadioButton = new JRadioButton( RADIO_BUTTON_OFF );
            timerOffRadioButton.setFont( CONTROL_FONT );
            timerOffRadioButton.setOpaque( false );
            ButtonGroup timerButtonGroup = new ButtonGroup();
            timerButtonGroup.add( timerOnRadioButton );
            timerButtonGroup.add( timerOffRadioButton );
            JPanel timerPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            timerPanel.setOpaque( false );
            timerPanel.add( timerOnRadioButton );
            timerPanel.add( timerOffRadioButton );

            // Sound control
            JLabel soundLabel = new JLabel( new ImageIcon( SOUND_ICON ) );
            soundLabel.setFont( LABEL_FONT );
            soundOnRadioButton = new JRadioButton( RADIO_BUTTON_ON );
            soundOnRadioButton.setFont( CONTROL_FONT );
            soundOnRadioButton.setOpaque( false );
            soundOffRadioButton = new JRadioButton( RADIO_BUTTON_OFF );
            soundOffRadioButton.setFont( CONTROL_FONT );
            soundOffRadioButton.setOpaque( false );
            ButtonGroup soundButtonGroup = new ButtonGroup();
            soundButtonGroup.add( soundOnRadioButton );
            soundButtonGroup.add( soundOffRadioButton );
            JPanel soundPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            soundPanel.setOpaque( false );
            soundPanel.add( soundOnRadioButton );
            soundPanel.add( soundOffRadioButton );

            // layout
            inputPanel.setInsets( new Insets( Y_SPACING / 2, X_SPACING, Y_SPACING / 2, X_SPACING ) );
            inputRow = 0;
            addControl( levelLabel, levelPanel );
            addControl( timerLabel, timerPanel );
            addControl( soundLabel, soundPanel );
        }

        // panel separator
        JSeparator buttonSeparator = new JSeparator();
        buttonSeparator.setForeground( Color.BLACK );

        // Start! button
        JButton startButton = new JButton( BUTTON_START );
        startButton.setOpaque( false );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireStartButtonPressed();
            }
        } );

        // this panel
        int row = 0;
        int column = 0;
        setInsets( new Insets( Y_SPACING, X_MARGIN, 0, X_MARGIN ) );
        add( titleLabel, row++, column );
        add( titleSeparator, row++, column, Fill.HORIZONTAL );
        setInsets( new Insets( Y_SPACING / 2, X_MARGIN, Y_SPACING / 2, X_MARGIN ) );
        add( inputPanel, row++, column );
        add( buttonSeparator, row++, column, Fill.HORIZONTAL );
        setInsets( new Insets( Y_SPACING / 2, X_MARGIN, Y_SPACING, X_MARGIN ) );
        add( startButton, row++, column );

        // default state
        timerOnRadioButton.setSelected( true );
        levelRadioButtons[0].setSelected( true );
        soundOnRadioButton.setSelected( true );
    }

    /**
     * Adds a control to the input portion of the panel, below the last control,
     * and above the separator that appears above the Start button.
     * The label is anchored east, the control is anchored west.
     *
     * @param label
     * @param control
     */
    public void addControl( Component label, Component control ) {
        inputPanel.add( label, inputRow, 0, Anchor.EAST );
        inputPanel.add( control, inputRow++, 1, Anchor.WEST );
    }

    public void setLevel( int level ) {
        if ( level != getLevel() ) {
            int index = level - levelRange.getMin();
            levelRadioButtons[index].setSelected( true );
            fireLevelChanged();
        }
    }

    public int getLevel() {
        int level = 0;
        boolean found = false;
        for ( int i = 0; i < levelRadioButtons.length; i++ ) {
            if ( levelRadioButtons[i].isSelected() ) {
                level = levelRange.getMin() + i;
                found = true;
                break;
            }
        }
        assert( found == true );
        return level;
    }

    public void setTimerOn( boolean b ) {
        if ( b != isTimerOn() ) {
            timerOnRadioButton.setSelected( b );
            timerOffRadioButton.setSelected( !b );
            fireTimerChanged();
        }
    }

    public boolean isTimerOn() {
        return timerOnRadioButton.isSelected();
    }

    public void setSoundOn( boolean b ) {
        if ( b != isSoundOn() ) {
            soundOnRadioButton.setSelected( b );
            soundOffRadioButton.setSelected( !b );
            fireSoundChanged();
        }
    }

    public boolean isSoundOn() {
        return soundOnRadioButton.isSelected();
    }

    /**
     * Interface for notification of property changes and actions.
     * In most cases, you'll only need to implement startButtonPressed,
     * then get all of the property values to initialize your game.
     */
    public interface GameSettingsPanelListener extends EventListener {
        public void levelChanged();
        public void timerChanged();
        public void soundChanged();
        public void startButtonPressed();
    }

    public static class GameSettingsPanelAdapater implements GameSettingsPanelListener {
        public void levelChanged() {}
        public void timerChanged() {}
        public void soundChanged() {}
        public void startButtonPressed() {}
    }

    public void addGameSettingsPanelListener( GameSettingsPanelListener listener ) {
        listeners.add(  GameSettingsPanelListener.class, listener );
    }

    public void removeGameSettingsPanelListener( GameSettingsPanelListener listener ) {
        listeners.remove(  GameSettingsPanelListener.class, listener );
    }

    private void fireLevelChanged() {
        for ( GameSettingsPanelListener listener : listeners.getListeners( GameSettingsPanelListener.class ) ) {
            listener.levelChanged();
        }
    }

    private void fireTimerChanged() {
        for ( GameSettingsPanelListener listener : listeners.getListeners( GameSettingsPanelListener.class ) ) {
            listener.timerChanged();
        }
    }

    private void fireSoundChanged() {
        for ( GameSettingsPanelListener listener : listeners.getListeners( GameSettingsPanelListener.class ) ) {
            listener.soundChanged();
        }
    }

    private void fireStartButtonPressed() {
        for ( GameSettingsPanelListener listener : listeners.getListeners( GameSettingsPanelListener.class ) ) {
            listener.startButtonPressed();
        }
    }

    public static void main( String[] args ) {

        final GameSettingsPanel panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
        panel.addControl( new JLabel( "myLabel1:" ), new JLabel( "myControl1" ) );
        panel.addControl( new JLabel( "myLabel2:" ), new JLabel( "myControl2" ) );
        panel.addGameSettingsPanelListener( new GameSettingsPanelAdapater() {
            @Override
            public void startButtonPressed() {
                System.out.println( "level=" + panel.getLevel() + " timerOn=" + panel.isTimerOn() + " soundOn=" + panel.isSoundOn() );
            }
        } );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
