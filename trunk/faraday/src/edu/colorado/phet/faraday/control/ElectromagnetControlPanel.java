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
import edu.colorado.phet.faraday.model.ACSource;
import edu.colorado.phet.faraday.model.Battery;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.SourceCoil;
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
    private JSlider _voltageSlider;
    private JLabel _voltageValue;
    private JSlider _amplitudeSlider;
    private JLabel _amplitudeValue;
    private JSlider _frequencySlider;
    private JLabel _frequencyValue;
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

            JPanel voltagePanel = new JPanel();
            {
                voltagePanel.setBorder( BorderFactory.createEtchedBorder() );
                
                // Range of values
                int max = (int) FaradayConfig.BATTERY_VOLTAGE_MAX;
                int min = (int) -FaradayConfig.BATTERY_VOLTAGE_MAX;
                int range = max - min;

                // Slider
                _voltageSlider = new JSlider();
                _voltageSlider.setMaximum( max );
                _voltageSlider.setMinimum( min );
                _voltageSlider.setValue( min );

                // Slider tick marks
                _voltageSlider.setMajorTickSpacing( range );
                _voltageSlider.setMinorTickSpacing( range / 10 );
                _voltageSlider.setSnapToTicks( false );
                _voltageSlider.setPaintTicks( true );
                _voltageSlider.setPaintLabels( true );

                // Value
                _voltageValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( voltagePanel );
                voltagePanel.setLayout( layout );
                layout.addAnchoredComponent( _voltageValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _voltageSlider, 1, 0, GridBagConstraints.WEST );
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _batteryPanel );
            _batteryPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( voltagePanel, row++, 0, GridBagConstraints.HORIZONTAL );
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
            
            JPanel amplitudePanel = new JPanel();
            {
                amplitudePanel.setBorder( BorderFactory.createEtchedBorder() );
                
                // Range of values
                int max = (int) ( FaradayConfig.AC_AMPLITUDE_MAX * 100.0 );
                int min = 0;
                int range = max - min;

                // Slider
                _amplitudeSlider = new JSlider();
                _amplitudeSlider.setMaximum( max );
                _amplitudeSlider.setMinimum( min );
                _amplitudeSlider.setValue( min );

                // Slider tick marks
                _amplitudeSlider.setMajorTickSpacing( range );
                _amplitudeSlider.setMinorTickSpacing( range / 10 );
                _amplitudeSlider.setSnapToTicks( false );
                _amplitudeSlider.setPaintTicks( true );
                _amplitudeSlider.setPaintLabels( true );

                // Value
                _amplitudeValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( amplitudePanel );
                amplitudePanel.setLayout( layout );
                layout.addAnchoredComponent( _amplitudeValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _amplitudeSlider, 1, 0, GridBagConstraints.WEST );
            }
            
            JPanel frequencyPanel = new JPanel();
            {
                frequencyPanel.setBorder( BorderFactory.createEtchedBorder() );
                
                // Range of values
                int max = (int) ( FaradayConfig.AC_FREQUENCY_MAX * 100.0 );
                int min = (int) ( FaradayConfig.AC_FREQUENCY_MIN * 100.0 );
                int range = max - min;

                // Slider
                _frequencySlider = new JSlider();
                _frequencySlider.setMaximum( max );
                _frequencySlider.setMinimum( min );
                _frequencySlider.setValue( max );

                // Slider tick marks
                _frequencySlider.setMajorTickSpacing( range );
                _frequencySlider.setMinorTickSpacing( range / 10 );
                _frequencySlider.setSnapToTicks( false );
                _frequencySlider.setPaintTicks( true );
                _frequencySlider.setPaintLabels( true );

                // Value
                _frequencyValue = new JLabel( UNKNOWN_VALUE );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( frequencyPanel );
                frequencyPanel.setLayout( layout );
                layout.addAnchoredComponent( _frequencyValue, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _frequencySlider, 1, 0, GridBagConstraints.WEST );
            }
            
            EasyGridBagLayout layout = new EasyGridBagLayout( _acPanel );
            _acPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( amplitudePanel, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( frequencyPanel, row++, 0, GridBagConstraints.HORIZONTAL );
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
        _voltageSlider.addChangeListener( listener );
        _amplitudeSlider.addChangeListener( listener );
        _frequencySlider.addChangeListener( listener );
        _electronsCheckBox.addActionListener( listener );
        _loopsSpinner.addChangeListener( listener );

        // Update control panel to match the components that it's controlling.
        _batteryRadioButton.setSelected( _batteryModel.isEnabled() );
        _acRadioButton.setSelected( _acSourceModel.isEnabled() );
        _gridCheckBox.setSelected( _gridGraphic.isVisible() );
        _fieldMeterCheckBox.setSelected( _fieldMeterGraphic.isVisible() );
        _compassCheckBox.setSelected( _compassModel.isEnabled() );
        _voltageSlider.setValue( (int) _batteryModel.getVoltage() );
        _amplitudeSlider.setValue( (int) ( _acSourceModel.getAmplitude() * 100.0 ) );
        _frequencySlider.setValue( (int) ( _acSourceModel.getFrequency() * 100.0 ) );
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
            else if ( e.getSource() == _voltageSlider ) {
                // Read the value.
                int value = _voltageSlider.getValue();
                // Update the model.
                _batteryModel.setVoltage( value );
                // Update the label.
                Object[] args = { new Integer( value ) };
                String text = MessageFormat.format( SimStrings.get( "ElectromagnetModule.batteryVoltage" ), args );
                _voltageValue.setText( text );
            }
            else if ( e.getSource() == _amplitudeSlider ) {
                // Read the value.
                int value = _amplitudeSlider.getValue();
                // Update the model.
                _acSourceModel.setAmplitude( value / 100.0 );
                // Update the label.
                Object[] args = { new Integer( value ) };
                String text = MessageFormat.format( SimStrings.get( "ElectromagnetModule.acAmplitude" ), args );
                _amplitudeValue.setText( text );
            }
            else if ( e.getSource() == _frequencySlider ) {
                // Read the value.
                int value = _frequencySlider.getValue();
                // Update the model.
                _acSourceModel.setFrequency( value / 100.0 );
                // Update the label.
                Object[] args = { new Integer( value ) };
                String text = MessageFormat.format( SimStrings.get( "ElectromagnetModule.acFrequency" ), args );
                _frequencyValue.setText( text );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + e );
            }
        }
    }
}