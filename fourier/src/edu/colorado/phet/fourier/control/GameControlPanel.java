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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
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
    
    // Game levels
    private static final int LEVEL_PRESET = 0;
    private static final int LEVEL_EASY   = 1;
    private static final int LEVEL_MEDIUM = 2;
    private static final int LEVEL_HARD   = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Things to be controlled
    
    // UI components
    private FourierComboBox _levelComboBox;
    private FourierComboBox _presetComboBox;
    private JButton _newGameButton;
    private JLabel _closenessValue;
    
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
    public GameControlPanel( FourierModule module ) {
        super( module );
        
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
                _levelChoices.add( new FourierComboBox.Choice( LEVEL_PRESET, SimStrings.get( "GameControlPanel.level.preset" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( LEVEL_EASY, SimStrings.get( "GameControlPanel.level.easy" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( LEVEL_MEDIUM, SimStrings.get( "GameControlPanel.level.medium" ) ) );
                _levelChoices.add( new FourierComboBox.Choice( LEVEL_HARD, SimStrings.get( "GameControlPanel.level.hard" ) ) );

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
                _closenessValue = new JLabel( "0%" );
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

        // Layout
        addFullWidth( gameControlsPanel );
        addVerticalSpace( SUBPANEL_SPACING );

        // Set the state of the controls.
        reset();

        // Wire up event handling (after setting state with reset).
        {
            _eventListener = new EventListener();
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
        _levelComboBox.setSelectedKey( LEVEL_PRESET );
        _presetComboBox.setSelectedKey( FourierConstants.PRESET_SINE_COSINE );
        _presetComboBox.setEnabled( _levelComboBox.getSelectedKey() == LEVEL_PRESET );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ItemListener {

        public EventListener() {}

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
        System.out.println( "level = " + _levelComboBox.getSelectedItem() );//XXX
        _presetComboBox.setEnabled( _levelComboBox.getSelectedKey() == LEVEL_PRESET );
    }
    
    private void handlePreset() {
        System.out.println( "preset = " + _presetComboBox.getSelectedItem() );//XXX
    }
}
