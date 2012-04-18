// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.games;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.games.GameSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;

import static edu.colorado.phet.common.games.GameConstants.BUTTON_START;

/**
 * Control panel that provides settings for a game.
 * Provides settings for level, timer on/off, and sound on/off.
 * You can add additional controls via the addControl method.
 * <p/>
 * This panel was generalized from the game in reactants-products-and-leftovers.
 * <p/>
 * Note that this panel creates observers and attaches them to the GameSettings.
 * Remember to call cleanup before disposing of this panel, or it will continue
 * to be attached to the GameSettings, resulting in a memory leak.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSettingsPanel extends GridPanel {

    // "look" properties
    private static final Border BORDER = new LineBorder( Color.BLACK, 1 );
    private static final Color BACKGROUND_FILL_COLOR = new Color( 180, 205, 255 );

    // layout properties
    private static final int X_MARGIN = 5;
    private static final int X_SPACING = 5;
    private static final int Y_SPACING = 6;

    private final VoidFunction0 cleanupFunction;
    private final GridPanel inputPanel;
    private int inputRow; // next control added to the "input panel" will appear in this row

    //Default color for the "start game" and "new game" buttons
    public static final Color DEFAULT_BUTTON_COLOR = new Color( 235, 235, 235 );

    /**
     * This constructor handles observes properties in GameSettings, and handles setting up the synchronization
     * between Property<T> observers and old-style GameSettingsPanelListener.
     * Clients who use this constructor must remember to call cleanup before releasing all references,
     * or a memory leak will occur.
     *
     * @param gameSettings
     * @param startFunction
     */
    public GameSettingsPanel( GameSettings gameSettings, VoidFunction0 startFunction ) {
        this( gameSettings, startFunction, new PhetFont( 24 ), new PhetFont(), new PhetFont(), DEFAULT_BUTTON_COLOR );
    }

    public GameSettingsPanel( GameSettings gameSettings, VoidFunction0 startFunction, Color startButtonColor ) {
        this( gameSettings, startFunction, new PhetFont( 24 ), new PhetFont(), new PhetFont(), startButtonColor );
    }

    public GameSettingsPanel( final GameSettings gameSettings, final VoidFunction0 startFunction, PhetFont titleFont, PhetFont labelFont, PhetFont controlFont, final Color startButtonColor ) {
        setBorder( BORDER );
        setBackground( BACKGROUND_FILL_COLOR );

        // Title
        JLabel titleLabel = new JLabel( GameConstants.TITLE_GAME_SETTINGS );
        titleLabel.setFont( titleFont );

        // title separator
        JSeparator titleSeparator = new JSeparator();
        titleSeparator.setForeground( Color.BLACK );

        // Level control
        JLabel levelLabel = new JLabel( GameConstants.LABEL_LEVEL_CONTROL );
        levelLabel.setFont( labelFont );
        JPanel levelPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        final ArrayList<PropertyRadioButton<Integer>> levelRadioButtons = new ArrayList<PropertyRadioButton<Integer>>();
        levelPanel.setOpaque( false );
        for ( int level = gameSettings.level.getMin(); level <= gameSettings.level.getMax(); level++ ) {
            PropertyRadioButton<Integer> button = new PropertyRadioButton<Integer>( UserComponentChain.chain( UserComponents.levelRadioButton, level ), String.valueOf( level ), gameSettings.level, level );
            button.setFont( controlFont );
            button.setOpaque( false );
            levelRadioButtons.add( button );
            levelPanel.add( button );
        }

        // Timer control
        JLabel timerLabel = new JLabel( new ImageIcon( GameConstants.STOPWATCH_ICON ) );
        timerLabel.setFont( labelFont );
        final PropertyRadioButton<Boolean> timerOnRadioButton = new PropertyRadioButton<Boolean>( UserComponents.timerOnRadioButton, GameConstants.RADIO_BUTTON_ON, gameSettings.timerEnabled, true );
        timerOnRadioButton.setFont( controlFont );
        timerOnRadioButton.setOpaque( false );
        final PropertyRadioButton<Boolean> timerOffRadioButton = new PropertyRadioButton<Boolean>( UserComponents.timerOffRadioButton, GameConstants.RADIO_BUTTON_OFF, gameSettings.timerEnabled, false );
        timerOffRadioButton.setFont( controlFont );
        timerOffRadioButton.setOpaque( false );
        ButtonGroup timerButtonGroup = new ButtonGroup();
        timerButtonGroup.add( timerOnRadioButton );
        timerButtonGroup.add( timerOffRadioButton );
        JPanel timerPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        timerPanel.setOpaque( false );
        timerPanel.add( timerOnRadioButton );
        timerPanel.add( timerOffRadioButton );

        // Sound control
        JLabel soundLabel = new JLabel( new ImageIcon( GameConstants.SOUND_ICON ) );
        soundLabel.setFont( labelFont );
        final PropertyRadioButton<Boolean> soundOnRadioButton = new PropertyRadioButton<Boolean>( UserComponents.soundOnRadioButton, GameConstants.RADIO_BUTTON_ON, gameSettings.soundEnabled, true );
        soundOnRadioButton.setFont( controlFont );
        soundOnRadioButton.setOpaque( false );
        final PropertyRadioButton<Boolean> soundOffRadioButton = new PropertyRadioButton<Boolean>( UserComponents.soundOffRadioButton, GameConstants.RADIO_BUTTON_OFF, gameSettings.soundEnabled, false );
        soundOffRadioButton.setFont( controlFont );
        soundOffRadioButton.setOpaque( false );
        ButtonGroup soundButtonGroup = new ButtonGroup();
        soundButtonGroup.add( soundOnRadioButton );
        soundButtonGroup.add( soundOffRadioButton );
        JPanel soundPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        soundPanel.setOpaque( false );
        soundPanel.add( soundOnRadioButton );
        soundPanel.add( soundOffRadioButton );

        // input panel
        inputPanel = new GridPanel();
        inputPanel.setOpaque( false );
        inputPanel.setInsets( new Insets( Y_SPACING / 2, X_SPACING, Y_SPACING / 2, X_SPACING ) );
        inputRow = 0;
        addControl( levelLabel, levelPanel );
        addControl( timerLabel, timerPanel );
        addControl( soundLabel, soundPanel );

        // panel separator
        JSeparator buttonSeparator = new JSeparator();
        buttonSeparator.setForeground( Color.BLACK );

        // Start! button
        // Use a piccolo button instead of swing button here since it looks much nicer.
        // JB said AP requested a piccolo button instead of a swing button here
        // Embed the piccolo button in a PhetPCanvas.  Note that this is doubly-embedded, so the cursor won't change
        PhetPCanvas startButton = new PhetPCanvas() {{

            //Add the button and make sure the canvas is big enough to hold the depressed button
            addScreenChild( new HTMLImageButtonNode( BUTTON_START ) {{
                setUserComponent( GameSimSharing.UserComponents.startGameButton );
                setBackground( startButtonColor );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        startFunction.apply();
                    }
                } );
                setPreferredSize( new Dimension( (int) getFullBounds().getWidth() + getShadowOffset(), (int) getFullBounds().getHeight() + getShadowOffset() ) );
            }} );

            //Match the background color
            setBackground( BACKGROUND_FILL_COLOR );

            //Suppress the border on the PhetPCanvas so it will look seamless with the rest of the controls
            setBorder( null );
        }};

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

        // cleanup function
        cleanupFunction = new VoidFunction0() {
            public void apply() {
                for ( PropertyRadioButton<Integer> radioButton : levelRadioButtons ) {
                    radioButton.cleanup();
                }
                timerOnRadioButton.cleanup();
                timerOffRadioButton.cleanup();
                soundOnRadioButton.cleanup();
                soundOffRadioButton.cleanup();
            }
        };
    }

    /**
     * Remove observers from the GameSettings.
     * If you forget to call this you'll have created a memory leak.
     */
    public void cleanup() {
        cleanupFunction.apply();
    }

    /**
     * Adds a control to the input portion of the panel, below the last control,
     * and above the separator that appears above the Start button.
     * The label is anchored east, the control is anchored west.
     * <p/>
     * Note: If you add controls that require cleanup, they must be cleaned up
     * by the client; they will not be handled via the cleanup method.
     *
     * @param label
     * @param control
     */
    public void addControl( Component label, Component control ) {
        inputPanel.add( label, inputRow, 0, Anchor.EAST );
        inputPanel.add( control, inputRow++, 1, Anchor.WEST );
    }

    /*
     * Test
     */
    public static void main( String[] args ) {

        // An additional property
        final Property<Boolean> powerEnabledProperty = new Property<Boolean>( true );
        powerEnabledProperty.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "powerEnabledProperty " + powerEnabledProperty.get() );
            }
        } );

        // Game settings
        final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, 5, 3 ), false, false );
        gameSettings.level.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "gameSettings.level " + gameSettings.level.get() );
            }
        } );
        gameSettings.timerEnabled.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "gameSettings.timerEnabled " + gameSettings.timerEnabled.get() );
            }
        } );
        gameSettings.soundEnabled.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println( "gameSettings.soundEnabled " + gameSettings.soundEnabled.get() );
            }
        } );
        VoidFunction0 startFunction = new VoidFunction0() {
            public void apply() {
                System.out.println( "START: level=" + gameSettings.level.get() +
                                    " timerEnabled=" + gameSettings.timerEnabled.get() +
                                    " soundEnabled=" + gameSettings.soundEnabled.get() +
                                    " powerEnabled=" + powerEnabledProperty.get() );
            }
        };

        // Game Settings panel, with added property
        GameSettingsPanel panel = new GameSettingsPanel( gameSettings, startFunction );
        panel.addControl( new JLabel( "power:" ), new PropertyCheckBox( "", powerEnabledProperty ) );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
