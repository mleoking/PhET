/* Copyright 2005, University of Colorado */

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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.RandomFourierSeries;
import edu.colorado.phet.fourier.module.FourierModule;


/**
 * GameControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layout parameters
    private static final int LEFT_MARGIN = 25; // pixels
    private static final int MATH_MODE_LEFT_MARGIN = 10; // pixels
    private static final int SUBPANEL_SPACING = 10; // pixels
    
    private DecimalFormat CHEAT_FORMAT = new DecimalFormat( "0.00" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Things to be controlled
    private FourierSeries _userFourierSeries;
    private RandomFourierSeries _randomFourierSeries;
    
    // UI components
    private FourierComboBox _levelComboBox;
    private FourierComboBox _presetComboBox;
    private JButton _newGameButton;
    private JLabel _closenessValue;
    private JCheckBox _cheatCheckBox;
    private FourierTitledPanel  _cheatPanel;
    private ArrayList _cheatValues; // array of JLabel
    
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
    public GameControlPanel( FourierModule module, FourierSeries userFourierSeries, RandomFourierSeries randomFourierSeries ) {
        super( module );
        
        assert( userFourierSeries != null );
        assert( randomFourierSeries != null );
        
        _userFourierSeries = userFourierSeries;
        _randomFourierSeries = randomFourierSeries;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "GameControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );
        
        // Game controls panel
        FourierTitledPanel gameControlsPanel = new FourierTitledPanel( SimStrings.get( "GameControlPanel.gameControls" ) );
        {
            // Level
            {
                // Label
                String label = SimStrings.get( "GameControlPanel.level" );

                // Choices
                _levelChoices = new ArrayList();
                _levelChoices.add( new FourierComboBox.Choice( FourierConstants.GAME_LEVEL_PRESET, SimStrings.get( "GameControlPanel.level.preset" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( FourierConstants.GAME_LEVEL_EASY, SimStrings.get( "GameControlPanel.level.easy" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( FourierConstants.GAME_LEVEL_MEDIUM, SimStrings.get( "GameControlPanel.level.medium" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( FourierConstants.GAME_LEVEL_HARD, SimStrings.get( "GameControlPanel.level.hard" ) ) );

                // Presets combo box
                _levelComboBox = new FourierComboBox( label, _levelChoices );
            }
            
            // Preset
            {
                // Label
                String label = SimStrings.get( "GameControlPanel.preset" );

                // Choices
                _presetChoices = new ArrayList();
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SINE_COSINE, SimStrings.get( "preset.sinecosine" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_TRIANGLE, SimStrings.get( "preset.triangle" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SQUARE, SimStrings.get( "preset.square" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SAWTOOTH, SimStrings.get( "preset.sawtooth" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_WAVE_PACKET, SimStrings.get( "preset.wavePacket" ) ) );

                // Presets combo box
                _presetComboBox = new FourierComboBox( label, _presetChoices );
            }
            
            // How close am I?
            JPanel closenessPanel = new JPanel();
            {
                JLabel closenessLabel = new JLabel( SimStrings.get( "GameControlPanel.howCloseAmI" ) );
                _closenessValue = new JLabel( "? %" );
                EasyGridBagLayout layout = new EasyGridBagLayout( closenessPanel );
                closenessPanel.setLayout( layout );
                layout.addComponent( closenessLabel, 0, 0 );
                layout.addComponent( _closenessValue, 0, 1 );
            }
            
            // New Game button
            _newGameButton = new JButton( SimStrings.get( "GameControlPanel.newGame" ) );
            
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
            layout.addComponent( closenessPanel, row++, column );
            layout.addComponent( _newGameButton, row++, column );
            gameControlsPanel.setLayout( new BorderLayout() );
            gameControlsPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Cheat checkbox
        _cheatCheckBox = new JCheckBox( "Cheat" );
        
        // Cheat panel
        _cheatPanel = new FourierTitledPanel( "Amplitudes to match" );
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( _cheatPanel );
            _cheatPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.EAST );
            int row = 0;
            
            _cheatValues = new ArrayList();
            for ( int i = 0; i < _randomFourierSeries.getNumberOfHarmonics(); i++ ) {
                JLabel label = new JLabel( "<html>A<sub>" + (i+1) + "</sub> = </html>" );
                JLabel value = new JLabel( "" );
                _cheatValues.add( value );
                layout.addComponent( label, row, 0 );
                layout.addComponent( value, row, 1 );
                row++;
            }
        }

        // Layout
        addFullWidth( gameControlsPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addFullWidth( _cheatCheckBox );
        addVerticalSpace( SUBPANEL_SPACING );
        addFullWidth( _cheatPanel );

        // Set the state of the controls.
        reset();

        // Wire up event handling (after setting state with reset).
        {
            _eventListener = new EventListener();
            // ActionListeners
            _newGameButton.addActionListener( _eventListener );
            _cheatCheckBox.addActionListener( _eventListener );
            // ItemListeners
            _levelComboBox.addItemListener( _eventListener );
            _presetComboBox.addItemListener( _eventListener );
        }
    }
    
    public void cleanup() {
        // XXX
    }
    
    //----------------------------------------------------------------------------
    // FourierControlPanel implementation
    //----------------------------------------------------------------------------

    public void reset() {
        _levelComboBox.setSelectedKey( FourierConstants.GAME_LEVEL_PRESET );
        _presetComboBox.setSelectedKey( FourierConstants.PRESET_SINE_COSINE );
        _presetComboBox.setEnabled( _levelComboBox.getSelectedKey() == FourierConstants.GAME_LEVEL_PRESET );
        _randomFourierSeries.setGameLevel( _levelComboBox.getSelectedKey() );
        if ( _levelComboBox.getSelectedKey() == FourierConstants.GAME_LEVEL_PRESET ) {
            _randomFourierSeries.setPreset( _presetComboBox.getSelectedKey() );
        }
        else {
            _randomFourierSeries.setPreset( FourierConstants.PRESET_CUSTOM );
        }
        _cheatCheckBox.setSelected( false );
        _cheatPanel.setVisible( _cheatCheckBox.isSelected() );
        updateCheatPanel();
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
            else if ( event.getSource() == _cheatCheckBox ) {
                handleCheat();
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
        int gameLevel = _levelComboBox.getSelectedKey();
        _presetComboBox.setEnabled( gameLevel == FourierConstants.GAME_LEVEL_PRESET );
        _randomFourierSeries.setGameLevel( gameLevel );
        if ( gameLevel == FourierConstants.GAME_LEVEL_PRESET ) {
            int preset = _presetComboBox.getSelectedKey();
            _randomFourierSeries.setPreset( preset );
        }
        else {
            _randomFourierSeries.setPreset( FourierConstants.PRESET_CUSTOM );
        }
        handleNewGame();
    }
    
    private void handlePreset() {
        int preset = _presetComboBox.getSelectedKey();
        _randomFourierSeries.setPreset( preset );
        handleNewGame();
    }
    
    private void handleNewGame() {
        // Set all the user's harmonic amplitudes to zero.
        for ( int i = 0; i < _userFourierSeries.getNumberOfHarmonics(); i++ ) {
            _userFourierSeries.getHarmonic( i ).setAmplitude( 0 );
        }
        // Generate a new random series
        _randomFourierSeries.generate();
        updateCheatPanel();
    }
    
    private void handleCheat() {
        boolean isVisible = _cheatCheckBox.isSelected();
        _cheatPanel.setVisible( isVisible );
        if ( isVisible ) {
            updateCheatPanel();
        }
    }
    
    private void updateCheatPanel() {
        if ( _cheatPanel.isVisible() ) {
            for ( int i = 0; i < _cheatValues.size(); i++ ) {
                double dAmplitude = _randomFourierSeries.getHarmonic( i ).getAmplitude();
                String sAmplitude = CHEAT_FORMAT.format( dAmplitude );
                JLabel label = (JLabel) _cheatValues.get( i );
                label.setText( sAmplitude );
            }
        }
    }
}
