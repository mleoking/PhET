// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.enums.GameLevel;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.module.FourierAbstractModule;
import edu.colorado.phet.fourier.view.game.GameManager;


/**
 * GameControlPanel is the control panel for the "Wave Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameControlPanel extends FourierAbstractControlPanel implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layout parameters
    private static final int LEFT_MARGIN = 25; // pixels
    private static final int MATH_MODE_LEFT_MARGIN = 10; // pixels
    private static final int SUBPANEL_SPACING = 10; // pixels
    
    private static final DecimalFormat AMPLITUDE_FORMAT = new DecimalFormat( "0.00" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Things to be controlled
    private GameManager _gameManager;
    
    // UI components
    private FourierComboBox _levelComboBox;
    private FourierComboBox _presetComboBox;
    private JButton _newGameButton;
    private JButton _hintButton;
    private JLabel _hintText;
    private int _hintLevel;
    
    // Choices
    private ArrayList _levelChoices;
    private ArrayList _presetChoices;
    
    private EventListener _eventListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public GameControlPanel( FourierAbstractModule module, GameManager gameManager ) {
        super( module );
        
        assert( gameManager != null );
        
        _gameManager = gameManager;
        
        _gameManager.getRandomFourierSeries().addObserver( this );
        
        // Set the control panel's minimum width.
        int width = FourierResources.getInt( "GameControlPanel.width" ,275);
        setMinimumWidth( width );
        
        // Game controls panel
        FourierTitledPanel gameControlsPanel = new FourierTitledPanel( FourierResources.getString( "GameControlPanel.gameControls" ) );
        {
            // Level
            {
                // Label
                String label = FourierResources.getString( "GameControlPanel.level" );

                // Choices
                _levelChoices = new ArrayList();
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL1, "1 (" + FourierResources.getString( "GameControlPanel.level.easiest") + ")" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL2, "2" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL3, "3" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL4, "4" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL5, "5" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL6, "6" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL7, "7" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL8, "8" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL9, "9" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.LEVEL10, "10 ( " + FourierResources.getString( "GameControlPanel.level.hardest") + ")" ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.PRESET, FourierResources.getString( "GameControlPanel.level.preset" ) ) );

                // Levels combo box
                _levelComboBox = new FourierComboBox( label, _levelChoices );
                _levelComboBox.getComboBox().setMaximumRowCount( _levelChoices.size() );
            }
            
            // Preset
            {
                // Label
                String label = FourierResources.getString( "GameControlPanel.preset" );

                // Choices
                _presetChoices = new ArrayList();
                _presetChoices.add( new FourierComboBox.Choice( Preset.SINE_COSINE, FourierResources.getString( "preset.sine" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.TRIANGLE, FourierResources.getString( "preset.triangle" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.SQUARE, FourierResources.getString( "preset.square" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.SAWTOOTH, FourierResources.getString( "preset.sawtooth" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.WAVE_PACKET, FourierResources.getString( "preset.wavePacket" ) ) );

                // Presets combo box
                _presetComboBox = new FourierComboBox( label, _presetChoices );
                _presetComboBox.getComboBox().setMaximumRowCount( _presetChoices.size() );
            }
            
            // New Game button
            _newGameButton = new JButton( FourierResources.getString( "GameControlPanel.newGame" ) );
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _levelComboBox, row++, column );
            layout.addComponent( _presetComboBox, row++, column );
            layout.addComponent( _newGameButton, row++, column );
            gameControlsPanel.setLayout( new BorderLayout() );
            gameControlsPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Hints panel
        FourierTitledPanel hintsPanel = new FourierTitledPanel( FourierResources.getString( "GameControlPanel.hints" ) );
        {
            // Hint button
            _hintButton = new JButton();
            
            // Hint text
            _hintText = new JLabel();
            
            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _hintButton, row++, column );
            layout.addComponent( _hintText, row++, column );
            hintsPanel.setLayout( new BorderLayout() );
            hintsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Layout
        addFullWidth( gameControlsPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addFullWidth( hintsPanel );

        // Set the state of the controls.
        reset();

        // Wire up event handling (after setting state with reset).
        {
            _eventListener = new EventListener();
            // ActionListeners
            _newGameButton.addActionListener( _eventListener );
            _hintButton.addActionListener( _eventListener );
            // ItemListeners
            _levelComboBox.addItemListener( _eventListener );
            _presetComboBox.addItemListener( _eventListener );
        }
    }
    
    public void cleanup() {
        _gameManager.getRandomFourierSeries().removeObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setGameLevel( GameLevel gameLevel ) {
        if ( gameLevel != null ) {
            _levelComboBox.setSelectedKey( gameLevel );
            handleLevel();
        }
    }
    
    public GameLevel getGameLevel() {
        return (GameLevel) _levelComboBox.getSelectedKey();
    }
    
    public void setPreset( Preset preset ) {
        if ( preset != null ) {
            _presetComboBox.setSelectedKey( preset );
            handlePreset();
        }
    }
    
    public Preset getPreset() {
        return (Preset) _presetComboBox.getSelectedKey();
    }
    
    //----------------------------------------------------------------------------
    // FourierControlPanel implementation
    //----------------------------------------------------------------------------

    public void reset() {
        _levelComboBox.setSelectedKey( GameLevel.LEVEL1 );
        _presetComboBox.setSelectedKey( Preset.SINE_COSINE );
        _presetComboBox.setEnabled( _levelComboBox.getSelectedKey() == GameLevel.PRESET );
        _gameManager.setGameLevel( (GameLevel) _levelComboBox.getSelectedKey() );
        if ( _levelComboBox.getSelectedKey() == GameLevel.PRESET ) {
            _gameManager.setPreset( (Preset) _presetComboBox.getSelectedKey() );
        }
        else {
            _gameManager.setPreset( Preset.CUSTOM );
        }
        resetHints();
    }
    
    private void resetHints() {
        _hintLevel = 0;
        _hintText.setText( "" );
        _hintButton.setText( FourierResources.getString( "GameControlPanel.hintButton0" ) );
        _hintButton.setEnabled( true );
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Hides the hints when the random Fourier series changes.
     */
    public void update() {
        resetHints();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ItemListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _newGameButton ) {
                handleNewGame();
            }
            else if ( event.getSource() == _hintButton ) {
                handleHint();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _levelComboBox.getComboBox() ) {
                    handleLevel();
                }
                else if ( event.getSource() == _presetComboBox.getComboBox() ) {
                    handlePreset();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleLevel() {
        setWaitCursorEnabled( true );
        GameLevel gameLevel = (GameLevel) _levelComboBox.getSelectedKey();
        _presetComboBox.setEnabled(  gameLevel == GameLevel.PRESET ); 
        if ( gameLevel == GameLevel.PRESET ) {
            Preset preset = (Preset) _presetComboBox.getSelectedKey();
            _gameManager.setPreset( preset );
        }
        _gameManager.setGameLevel( gameLevel );
        setWaitCursorEnabled( false );
    }
    
    private void handlePreset() {
        setWaitCursorEnabled( true );
        Preset preset = (Preset) _presetComboBox.getSelectedKey();
        _gameManager.setPreset( preset );
        setWaitCursorEnabled( false );
    }
    
    private void handleNewGame() {
        setWaitCursorEnabled( true );
        _gameManager.newGame();
        setWaitCursorEnabled( false );
    }
    
    private void handleHint() {
        
        FourierSeries randomFourierSeries = _gameManager.getRandomFourierSeries();
        
        String subString0 = "";
        String subString1 = "";
        String subString2 = "";
        int numberOfNonZeroHarmonics = 0;
        for ( int i = 0; i < randomFourierSeries.getNumberOfHarmonics(); i++ ) {
            double amplitude = randomFourierSeries.getHarmonic( i ).getAmplitude();
            if ( amplitude != 0 ) {
                numberOfNonZeroHarmonics++;
                // WORKAROUND: DecimalFormat uses non-standard rounding, so round ala GameManager.isMatch
                int percent = 0;
                if ( amplitude < 0 ) {
                    percent = (int) ( 100 * amplitude - 0.005 );
                }
                else {
                    percent = (int) ( 100 * amplitude + 0.005 );
                }
                double roundedAmplitude = percent * 0.01;
                String sAmplitude = AMPLITUDE_FORMAT.format( roundedAmplitude );
                subString0 += "<br>A<sub>?</sub> = ?";
                subString1 += "<br>A<sub>" + ( i + 1 ) + "</sub> = ?";
                subString2 += "<br>A<sub>" + ( i + 1 ) + "</sub> = " + sAmplitude;
            }
        }
        
        if ( _hintLevel == 0 ) {
            /*
             * Hint #0 - reveal the number of non-zero components
             */
            String string = null;
            if ( numberOfNonZeroHarmonics == 1 ) {
                string = FourierResources.getString( "GameControlPanel.hint0.singular" );
            }
            else {
                String format = FourierResources.getString( "GameControlPanel.hint0.plural" );
                Object[] args = { new Integer( numberOfNonZeroHarmonics ) };
                string = MessageFormat.format( format, args );
            }
            String html = "<html>" + string + "<br>" + subString0 + "</html>";
            _hintText.setText( html );
            _hintButton.setText( FourierResources.getString( "GameControlPanel.hintButton1" ) );
        }
        else if ( _hintLevel == 1 ) {
            /*
             * Hint #1 - reveal which components we need to match
             */
            String html = "<html>";
            if ( numberOfNonZeroHarmonics == 1 ) {
                html += FourierResources.getString( "GameControlPanel.hint1.singular" );
            }
            else {
                html += FourierResources.getString( "GameControlPanel.hint1.plural" );
            }
            html += "<br>" + subString1 + "</html>";
            _hintText.setText( html );
            _hintButton.setText( FourierResources.getString( "GameControlPanel.hintButton2" ) );
        }
        else {
            /*
             * Hint #2 (aka "Cheat") - reveal the amplitudes we need to match
             */
            String html = "<html>";
            if ( numberOfNonZeroHarmonics == 1 ) {
                html += FourierResources.getString( "GameControlPanel.hint2.singular" );
            }
            else {
                html += FourierResources.getString( "GameControlPanel.hint2.plural" );
            }
            html += "<br>" + subString2 + "</html>";
            _hintText.setText( html );
            _hintButton.setEnabled( false );
        }
        _hintLevel++;
    }
}
