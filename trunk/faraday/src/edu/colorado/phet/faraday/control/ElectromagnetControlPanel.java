/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.module.ElectromagnetModule;
import edu.colorado.phet.faraday.view.CoilGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;

/**
 * ElectromagnetControlPanel is the control panel for the "Electromagnet" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetControlPanel extends FaradayControlPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model & view components to be controlled.
    private SourceCoil _sourceCoilModel;
    private Battery _batteryModel;
    private ACSource _acSourceModel;
    private Compass _compassModel;
    private CoilGraphic _coilGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;

    // UI components
    private JRadioButton _batteryRadioButton;
    private JRadioButton _acRadioButton;
    private JCheckBox _gridCheckBox;
    private JCheckBox _fieldMeterCheckBox;
    private JCheckBox _compassCheckBox; 
    private JSlider _batteryAmplitudeSlider;
    private JLabel _batteryAmplitudeValue;
    private JSlider _acMaxAmplitudeSlider;
    private JLabel _acMaxAmplitudeValue;
    private JSlider _acFrequencySlider;
    private JLabel _acFrequencyValue;
    private JPanel _batteryPanel;
    private JPanel _acPanel;
    private JSpinner _loopsSpinner;
    private JCheckBox _electronsCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * <p>
     * The structure of the code (the way that code blocks are nested)
     * reflects the structure of the panel.
     * 
     * @param module the module that this control panel is associated with.
     * @param batteryModel
     */
    public ElectromagnetControlPanel( 
            ElectromagnetModule module,
            SourceCoil sourceCoilModel,
            Battery batteryModel,
            ACSource acSourceModel,
            Compass compassModel,
            CoilGraphic coilGraphic,
            CompassGridGraphic gridGraphic, 
            FieldMeterGraphic fieldMeterGraphic ) {

        super( module );
        
        assert ( sourceCoilModel != null );
        assert ( batteryModel != null );
        assert ( acSourceModel != null );
        assert ( compassModel != null );
        assert ( coilGraphic != null );
        assert ( gridGraphic != null );
        assert ( fieldMeterGraphic != null );

        // Things we'll be controlling.
        _sourceCoilModel = sourceCoilModel;
        _batteryModel = batteryModel;
        _acSourceModel = acSourceModel;
        _compassModel = compassModel;
        _coilGraphic = coilGraphic;
        _gridGraphic = gridGraphic;
        _fieldMeterGraphic = fieldMeterGraphic;

        Font defaultFont = this.getFont();
        Font titleFont = new Font( defaultFont.getName(), defaultFont.getStyle(), defaultFont.getSize() + 4 );

        JPanel fillerPanel = new JPanel();
        {
            fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
            // WORKAROUND: Filler to set consistent panel width
            fillerPanel.add( Box.createHorizontalStrut( FaradayConfig.CONTROL_PANEL_MIN_WIDTH ) );
        }
        
        JPanel magnetPanel = new JPanel();
        {
            // Title
            Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
            String title = SimStrings.get( "ElectromagnetModule.magnetControls" );
            TitledBorder magnetBorder = BorderFactory.createTitledBorder( border, title );
            magnetBorder.setTitleFont( getTitleFont() );
            magnetPanel.setBorder( magnetBorder );
            
            // Compass Grid on/off
            _gridCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetModule.showGrid" ) );
            
            // Field Meter on/off
            _fieldMeterCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetModule.showFieldMeter" ) );

            // Compass on/off
            _compassCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetModule.showCompass" ) );

            // Electrons on/off
            _electronsCheckBox = new JCheckBox( SimStrings.get( "ElectromagnetModule.showElectrons" ) );
            
            // Number of loops
            JPanel loopsPanel = new JPanel();
            {
                JLabel loopsLabel = new JLabel( SimStrings.get( "ElectromagnetModule.numberOfLoops" ) );

                // Spinner, keyboard editing disabled.
                SpinnerNumberModel spinnerModel = new SpinnerNumberModel();
                spinnerModel.setMaximum( new Integer( FaradayConfig.ELECTROMAGNET_LOOPS_MAX ) );
                spinnerModel.setMinimum( new Integer( FaradayConfig.ELECTROMAGNET_LOOPS_MIN ) );
                spinnerModel.setValue( new Integer( FaradayConfig.ELECTROMAGNET_LOOPS_MIN ) );
                _loopsSpinner = new JSpinner( spinnerModel );
                JFormattedTextField tf = ( (JSpinner.DefaultEditor) _loopsSpinner.getEditor() ).getTextField();
                tf.setEditable( false );

                // Dimensions
                _loopsSpinner.setPreferredSize( SPINNER_SIZE );
                _loopsSpinner.setMaximumSize( SPINNER_SIZE );
                _loopsSpinner.setMinimumSize( SPINNER_SIZE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( loopsPanel );
                loopsPanel.setLayout( layout );
                layout.addAnchoredComponent( loopsLabel, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _loopsSpinner, 0, 1, GridBagConstraints.WEST );
            }
            
            JPanel sourcePanel = new JPanel();
            {
                // Title
                TitledBorder indicatorBorder = new TitledBorder( SimStrings.get( "ElectromagnetModule.currentSource" ) );
                sourcePanel.setBorder( indicatorBorder );

                // Radio buttons
                _batteryRadioButton = new JRadioButton( SimStrings.get( "ElectromagnetModule.dcSource" ) );
                _acRadioButton = new JRadioButton( SimStrings.get( "ElectromagnetModule.acSource" ) );
                ButtonGroup group = new ButtonGroup();
                group.add( _batteryRadioButton );
                group.add( _acRadioButton );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( sourcePanel );
                sourcePanel.setLayout( layout );
                layout.addAnchoredComponent( _batteryRadioButton, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _acRadioButton, 1, 0, GridBagConstraints.WEST );
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( magnetPanel );
            magnetPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _gridCheckBox, row++, 0 );
            layout.addComponent( _compassCheckBox, row++, 0 );
            layout.addComponent( _fieldMeterCheckBox, row++, 0 );
            layout.addComponent( _electronsCheckBox, row++, 0 );
            layout.addComponent( loopsPanel, row++, 0 );
            layout.addFilledComponent( sourcePanel, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Battery panel
        _batteryPanel = new JPanel();
        {
            // Titled border with some space above it.
            Border outsideBorder = BorderFactory.createEmptyBorder( 10, 0, 0, 0 );  // top, left, bottom, right
            Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
            String title = SimStrings.get( "ElectromagnetModule.batteryControls" );
            TitledBorder insideBorder = BorderFactory.createTitledBorder( border, title );
            insideBorder.setTitleFont( getTitleFont() );
            Border batteryBorder = BorderFactory.createCompoundBorder( outsideBorder, insideBorder );
            _batteryPanel.setBorder( batteryBorder );

            JPanel batteryAmpitudePanel = new JPanel();
            {
                batteryAmpitudePanel.setBorder( BorderFactory.createEtchedBorder() );
                
                // Range of values
                int max = (int) ( 100.0 * FaradayConfig.BATTERY_AMPLITUDE_MAX );
                int min = (int) ( 100.0 * FaradayConfig.BATTERY_AMPLITUDE_MIN );
                int range = max - min;

                // Slider
                _batteryAmplitudeSlider = new JSlider();
                _batteryAmplitudeSlider.setMaximum( max );
                _batteryAmplitudeSlider.setMinimum( min );
                _batteryAmplitudeSlider.setValue( min );

                // Slider tick marks
                _batteryAmplitudeSlider.setMajorTickSpacing( range );
                _batteryAmplitudeSlider.setMinorTickSpacing( range / 10 );
                _batteryAmplitudeSlider.setSnapToTicks( false );
                _batteryAmplitudeSlider.setPaintTicks( true );
                _batteryAmplitudeSlider.setPaintLabels( true );

                // Value
                _batteryAmplitudeValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( batteryAmpitudePanel );
                batteryAmpitudePanel.setLayout( layout );
                layout.addAnchoredComponent( _batteryAmplitudeValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _batteryAmplitudeSlider, 1, 0, GridBagConstraints.WEST );
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _batteryPanel );
            _batteryPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( batteryAmpitudePanel, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        _acPanel = new JPanel();
        {
            // Titled border with some space above it.
            Border outsideBorder = BorderFactory.createEmptyBorder( 10, 0, 0, 0 );  // top, left, bottom, right
            Border border = BorderFactory.createLineBorder( Color.BLACK, 2 );
            String title = SimStrings.get( "ElectromagnetModule.acControls" );
            TitledBorder insideBorder = BorderFactory.createTitledBorder( border, title );
            insideBorder.setTitleFont( getTitleFont() );
            Border acBorder = BorderFactory.createCompoundBorder( outsideBorder, insideBorder );
            _acPanel.setBorder( acBorder );
            
            JPanel acAmplitudePanel = new JPanel();
            {
                acAmplitudePanel.setBorder( BorderFactory.createEtchedBorder() );
                
                // Range of values
                int max = (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MAX );
                int min = (int) ( 100.0 * FaradayConfig.AC_MAXAMPLITUDE_MIN );
                int range = max - min;

                // Slider
                _acMaxAmplitudeSlider = new JSlider();
                _acMaxAmplitudeSlider.setMaximum( max );
                _acMaxAmplitudeSlider.setMinimum( min );
                _acMaxAmplitudeSlider.setValue( min );

                // Slider tick marks
                _acMaxAmplitudeSlider.setMajorTickSpacing( range );
                _acMaxAmplitudeSlider.setMinorTickSpacing( range / 10 );
                _acMaxAmplitudeSlider.setSnapToTicks( false );
                _acMaxAmplitudeSlider.setPaintTicks( true );
                _acMaxAmplitudeSlider.setPaintLabels( true );

                // Value
                _acMaxAmplitudeValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( acAmplitudePanel );
                acAmplitudePanel.setLayout( layout );
                layout.addAnchoredComponent( _acMaxAmplitudeValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _acMaxAmplitudeSlider, 1, 0, GridBagConstraints.WEST );
            }
            
            JPanel acFrequencyPanel = new JPanel();
            {
                acFrequencyPanel.setBorder( BorderFactory.createEtchedBorder() );
                
                // Range of values
                int max = (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MAX );
                int min = (int) ( 100.0 * FaradayConfig.AC_FREQUENCY_MIN );;
                int range = max - min;

                // Slider
                _acFrequencySlider = new JSlider();
                _acFrequencySlider.setMaximum( max );
                _acFrequencySlider.setMinimum( min );
                _acFrequencySlider.setValue( max );

                // Slider tick marks
                _acFrequencySlider.setMajorTickSpacing( range );
                _acFrequencySlider.setMinorTickSpacing( range / 10 );
                _acFrequencySlider.setSnapToTicks( false );
                _acFrequencySlider.setPaintTicks( true );
                _acFrequencySlider.setPaintLabels( true );

                // Value
                _acFrequencyValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( acFrequencyPanel );
                acFrequencyPanel.setLayout( layout );
                layout.addAnchoredComponent( _acFrequencyValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _acFrequencySlider, 1, 0, GridBagConstraints.WEST );
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _acPanel );
            _acPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( acAmplitudePanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( acFrequencyPanel, row++, 0, GridBagConstraints.HORIZONTAL );
        }

        // Add panels.
        addFullWidth( fillerPanel );
        addFullWidth( magnetPanel );
        addFullWidth( _batteryPanel );
        addFullWidth( _acPanel );
        
        // Wire up event handling.
        EventListener listener = new EventListener();
        _batteryRadioButton.addActionListener( listener );
        _acRadioButton.addActionListener( listener );
        _gridCheckBox.addActionListener( listener );
        _fieldMeterCheckBox.addActionListener( listener );
        _compassCheckBox.addActionListener( listener );
        _batteryAmplitudeSlider.addChangeListener( listener );
        _acMaxAmplitudeSlider.addChangeListener( listener );
        _acFrequencySlider.addChangeListener( listener );
        _electronsCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );

        // Update control panel to match the components that it's controlling.
        _batteryRadioButton.setSelected( _batteryModel.isEnabled() );
        _acRadioButton.setSelected( _acSourceModel.isEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterGraphic.isVisible() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        _batteryAmplitudeSlider.setValue( (int) ( 100.0 * _batteryModel.getVoltage() / FaradayConfig.BATTERY_VOLTAGE_MAX ) );
        _acMaxAmplitudeSlider.setValue( (int) ( _acSourceModel.getMaxAmplitude() * 100.0 ) );
        _acFrequencySlider.setValue( (int) ( _acSourceModel.getFrequency() * 100.0 ) );
        _electronsCheckBox.setSelected( _coilGraphic.isElectronAnimationEnabled() );
        _loopsSpinner.setValue( new Integer( _sourceCoilModel.getNumberOfLoops() ) );
        
        _batteryPanel.setVisible( _batteryRadioButton.isSelected() );
        _acPanel.setVisible( _acRadioButton.isSelected() );
    }
    
    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class EventListener implements ActionListener, ChangeListener {
        
        /** Sole constructor */
        public EventListener() {}
        
        /**
         * ActionEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void actionPerformed( ActionEvent e ) {
            if ( e.getSource() == _batteryRadioButton ) {
                // Battery (DC) source
                _batteryPanel.setVisible( true );
                _acPanel.setVisible( false );
                _batteryModel.setEnabled( true );
                _acSourceModel.setEnabled( false );
                _sourceCoilModel.setVoltageSource( _batteryModel );
            }
            else if ( e.getSource() == _acRadioButton ) {
                // AC source
                _batteryPanel.setVisible( false );
                _acPanel.setVisible( true );
                _batteryModel.setEnabled( false );
                _acSourceModel.setEnabled( true );
                _sourceCoilModel.setVoltageSource( _acSourceModel );
            }
            else if ( e.getSource() == _gridCheckBox ) {
                // Grid enable
                _gridGraphic.resetSpacing();
                _gridGraphic.setVisible( _gridCheckBox.isSelected() );
            }
            else if ( e.getSource() == _fieldMeterCheckBox ) {
                // Meter enable
                _fieldMeterGraphic.setVisible( _fieldMeterCheckBox.isSelected() );
            }
            else if ( e.getSource() == _compassCheckBox ) {
                // Compass enable
                _compassModel.setEnabled( _compassCheckBox.isSelected() );
            }
            else if ( e.getSource() == _electronsCheckBox ) {
                // Electrons enable
                _coilGraphic.setElectronAnimationEnabled( _electronsCheckBox.isSelected() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
        
        /**
         * ChangeEvent handler.
         * 
         * @param e the event
         * @throws IllegalArgumentException if the event is unexpected
         */
        public void stateChanged( ChangeEvent e ) {
            if ( e.getSource() == _loopsSpinner ) {
                // Read the value.
                int numberOfLoops = ( (Integer) _loopsSpinner.getValue() ).intValue();
                // Update the model.
                _sourceCoilModel.setNumberOfLoops( numberOfLoops );
            }
            else if ( e.getSource() == _batteryAmplitudeSlider ) {
                // Read the value.
                int percent = _batteryAmplitudeSlider.getValue();
                // Update the model.
                _batteryModel.setAmplitude( percent / 100.0 );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "ElectromagnetModule.batteryVoltage" ), args );
                _batteryAmplitudeValue.setText( text );
            }
            else if ( e.getSource() == _acMaxAmplitudeSlider ) {
                // Read the value.
                int percent = _acMaxAmplitudeSlider.getValue();
                // Update the model.
                _acSourceModel.setMaxAmplitude( percent / 100.0 );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "ElectromagnetModule.acAmplitude" ), args );
                _acMaxAmplitudeValue.setText( text );
            }
            else if ( e.getSource() == _acFrequencySlider ) {
                // Read the value.
                int percent = _acFrequencySlider.getValue();
                // Update the model.
                _acSourceModel.setFrequency( percent / 100.0 );
                // Update the label.
                Object[] args = { new Integer( percent ) };
                String text = MessageFormat.format( SimStrings.get( "ElectromagnetModule.acFrequency" ), args );
                _acFrequencyValue.setText( text );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}