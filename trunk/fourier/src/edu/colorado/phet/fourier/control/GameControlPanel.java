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
import edu.colorado.phet.fourier.enum.GameLevel;
import edu.colorado.phet.fourier.enum.Preset;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.view.game.GameManager;


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
    private GameManager _gameManager;
    
    // UI components
    private FourierComboBox _levelComboBox;
    private FourierComboBox _presetComboBox;
    private JButton _newGameButton;
    private JCheckBox _cheatCheckBox;
    private FourierTitledPanel  _cheatPanel;
    
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
    public GameControlPanel( FourierModule module, GameManager gameManager ) {
        super( module );
        
        assert( gameManager != null );
        
        _gameManager = gameManager;
        
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
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.EASY, SimStrings.get( "GameControlPanel.level.easy" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.MEDIUM, SimStrings.get( "GameControlPanel.level.medium" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.HARD, SimStrings.get( "GameControlPanel.level.hard" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( GameLevel.PRESET, SimStrings.get( "GameControlPanel.level.preset" ) ) );

                // Presets combo box
                _levelComboBox = new FourierComboBox( label, _levelChoices );
            }
            
            // Preset
            {
                // Label
                String label = SimStrings.get( "GameControlPanel.preset" );

                // Choices
                _presetChoices = new ArrayList();
                _presetChoices.add( new FourierComboBox.Choice( Preset.SINE_COSINE, SimStrings.get( "preset.sine" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.TRIANGLE, SimStrings.get( "preset.triangle" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.SQUARE, SimStrings.get( "preset.square" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.SAWTOOTH, SimStrings.get( "preset.sawtooth" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.WAVE_PACKET, SimStrings.get( "preset.wavePacket" ) ) );

                // Presets combo box
                _presetComboBox = new FourierComboBox( label, _presetChoices );
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
            layout.addComponent( _newGameButton, row++, column );
            gameControlsPanel.setLayout( new BorderLayout() );
            gameControlsPanel.add( innerPanel, BorderLayout.WEST );
        }
        
        // Cheat checkbox
        _cheatCheckBox = new JCheckBox( "Cheat" );
        
        // Cheat panel
        _cheatPanel = new FourierTitledPanel( "Amplitudes to match" );

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
        _levelComboBox.setSelectedKey( GameLevel.EASY );
        _presetComboBox.setSelectedKey( Preset.SINE_COSINE );
        _presetComboBox.setEnabled( _levelComboBox.getSelectedKey() == GameLevel.PRESET );
        _gameManager.setGameLevel( (GameLevel) _levelComboBox.getSelectedKey() );
        if ( _levelComboBox.getSelectedKey() == GameLevel.PRESET ) {
            _gameManager.setPreset( (Preset) _presetComboBox.getSelectedKey() );
        }
        else {
            _gameManager.setPreset( Preset.CUSTOM );
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
        setWaitCursorEnabled( true );
        GameLevel gameLevel = (GameLevel) _levelComboBox.getSelectedKey();
        _presetComboBox.setEnabled(  gameLevel == GameLevel.PRESET ); 
        if ( gameLevel == GameLevel.PRESET ) {
            Preset preset = (Preset) _presetComboBox.getSelectedKey();
            _gameManager.setPreset( preset );
        }
        _gameManager.setGameLevel( gameLevel );
        updateCheatPanel();
        setWaitCursorEnabled( false );
    }
    
    private void handlePreset() {
        setWaitCursorEnabled( true );
        Preset preset = (Preset) _presetComboBox.getSelectedKey();
        _gameManager.setPreset( preset );
        updateCheatPanel();
        setWaitCursorEnabled( false );
    }
    
    private void handleNewGame() {
        setWaitCursorEnabled( true );
        _gameManager.newGame();
        updateCheatPanel();
        setWaitCursorEnabled( false );
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

            _cheatPanel.removeAll();
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _cheatPanel );
            _cheatPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.EAST );
            int row = 0;
            
            double[] amplitudes = _gameManager.getAmplitudes();
            for ( int i = 0; i < amplitudes.length; i++ ) {
                String sAmplitude = CHEAT_FORMAT.format( amplitudes[i] );
                
                JLabel label = new JLabel( "<html>A<sub>" + (i+1) + "</sub> = </html>" );
                JLabel value = new JLabel( sAmplitude );
                layout.addComponent( label, row, 0 );
                layout.addComponent( value, row, 1 );
                row++;
            }
            _cheatPanel.revalidate();
        }
    }
}
