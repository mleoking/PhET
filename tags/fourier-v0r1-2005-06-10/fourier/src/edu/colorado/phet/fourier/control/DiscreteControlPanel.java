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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.view.HarmonicsGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


/**
 * DiscreteControlPanel is the control panel for the "Discrete" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteControlPanel extends FourierControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TITLED_BORDER_WIDTH = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private FourierSeries _fourierSeriesModel;
    private HarmonicsGraphic _harmonicsGraphic;
    private SumGraphic _sumGraphic;

    // UI components
    private ControlPanelComboBox _domainComboBox;
    private ControlPanelComboBox _presetsComboBox;
    private JCheckBox _showInfiniteCheckBox;
    private JCheckBox _showWavelengthCheckBox;
    private JComboBox _showWavelengthComboBox;
    private JCheckBox _showPeriodCheckBox;
    private JComboBox _showPeriodComboBox;
    private ControlPanelComboBox _waveTypeComboBox;
    private ControlPanelSlider _numberOfHarmonicsSlider;
    private ControlPanelSlider _fundamentalFrequencySlider;
    private JCheckBox _playSoundCheckBox;
    private JCheckBox _showMathCheckBox;
    private ControlPanelComboBox _mathFormComboBox;
    private JCheckBox _expandSumCheckBox;
    
    // Choices
    private ArrayList _domainChoices;
    private ArrayList _presetChoices;
    private ArrayList _showWavelengthChoices;
    private ArrayList _showPeriodChoices;
    private ArrayList _waveTypeChoices;
    private ArrayList _spaceMathFormChoices;
    private ArrayList _timeMathFormChoices;
    private ArrayList _spaceAndTimeMathFormChoices;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param fourierSeriesModel
     */
    public DiscreteControlPanel( 
            FourierModule module, 
            FourierSeries fourierSeriesModel, 
            HarmonicsGraphic harmonicsGraphic, 
            SumGraphic sumGraphic ) {
        
        super( module );
        
        assert ( fourierSeriesModel != null );
        assert( harmonicsGraphic != null );
        assert( sumGraphic != null );
        
        // Things we'll be controlling.
        _fourierSeriesModel = fourierSeriesModel;
        _harmonicsGraphic = harmonicsGraphic;
        _sumGraphic = sumGraphic;

        // Functions panel
        JPanel functionsPanel = new JPanel();
        {
            //  Title
            Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, TITLED_BORDER_WIDTH );
            String title = SimStrings.get( "DiscreteControlPanel.title.functions" );
            TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
            functionsPanel.setBorder( titleBorder );
            
            // Domain
            {
                // Label
                String label = SimStrings.get( "DiscreteControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new ControlPanelComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
                _domainChoices.add( new ControlPanelComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
                _domainChoices.add( new ControlPanelComboBox.Choice( FourierConstants.DOMAIN_SPACE_AND_TIME, SimStrings.get( "domain.spaceAndTime" ) ) );

                // Function combo box
                _domainComboBox = new ControlPanelComboBox( label, _domainChoices );
            }

            // Presets
            {
                // Label
                String label = SimStrings.get( "DiscreteControlPanel.presets" );

                // Choices
                _presetChoices = new ArrayList();
                _presetChoices.add( new ControlPanelComboBox.Choice( FourierConstants.PRESET_SINE_COSINE, SimStrings.get( "preset.sinecosine" ) ) );
                _presetChoices.add( new ControlPanelComboBox.Choice( FourierConstants.PRESET_SAWTOOTH, SimStrings.get( "preset.sawtooth" ) ) );
                _presetChoices.add( new ControlPanelComboBox.Choice( FourierConstants.PRESET_TRIANGLE, SimStrings.get( "preset.triangle" ) ) );
                _presetChoices.add( new ControlPanelComboBox.Choice( FourierConstants.PRESET_WAVE_PACKET, SimStrings.get( "preset.wavePacket" ) ) );
                _presetChoices.add( new ControlPanelComboBox.Choice( FourierConstants.PRESET_CUSTOM, SimStrings.get( "preset.custom" ) ) );

                // Presets combo box
                _presetsComboBox = new ControlPanelComboBox( label, _presetChoices );
            }
            
            // Show infinite...
            _showInfiniteCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showInfinite" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( functionsPanel );
            functionsPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _domainComboBox, row++, 0 );
            layout.addComponent( _presetsComboBox, row++, 0 );
            layout.addComponent( _showInfiniteCheckBox, row++, 0 );
        }
        
        // Wave Properties panel
        JPanel wavePropertiesPanel = new JPanel();
        {
            //  Title
            Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, TITLED_BORDER_WIDTH );
            String title = SimStrings.get( "DiscreteControlPanel.title.waveProperties" );
            TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
            wavePropertiesPanel.setBorder( titleBorder );
            
            // Show Wavelength
            JPanel showWavelengthPanel = new JPanel();
            {
                _showWavelengthCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showWavelength" ) );

                _showWavelengthComboBox = new JComboBox();
                
                // Choices
                _showWavelengthChoices = new ArrayList();
                String symbol = SimStrings.get( "symbol.wavelength" );
                for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                    String choice = symbol + ( i + 1 );
                    _showWavelengthChoices.add( choice );
                }

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( showWavelengthPanel );
                showWavelengthPanel.setLayout( layout );
                layout.addAnchoredComponent( _showWavelengthCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _showWavelengthComboBox, 0, 1, GridBagConstraints.WEST );
            }

            // Show Wavelength
            JPanel showPeriodPanel = new JPanel();
            {
                _showPeriodCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showPeriod" ) );

                _showPeriodComboBox = new JComboBox();
                
                // Choices
                _showPeriodChoices = new ArrayList();
                String symbol = SimStrings.get( "symbol.period" );
                for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                    String choice = symbol + ( i + 1 );
                    _showPeriodChoices.add( choice );
                }

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( showPeriodPanel );
                showPeriodPanel.setLayout( layout );
                layout.addAnchoredComponent( _showPeriodCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _showPeriodComboBox, 0, 1, GridBagConstraints.WEST );
            }
            
            // Wave Type
            {
                // Label
                String label = SimStrings.get( "DiscreteControlPanel.waveType" );
                
                // Choices
                _waveTypeChoices = new ArrayList();
                _waveTypeChoices.add( new ControlPanelComboBox.Choice( FourierConstants.WAVE_TYPE_SINE, SimStrings.get( "waveType.sines" ) ) );
                _waveTypeChoices.add( new ControlPanelComboBox.Choice( FourierConstants.WAVE_TYPE_COSINE, SimStrings.get( "waveType.cosines" ) ) );
                
                // Wave Type combo box
                _waveTypeComboBox = new ControlPanelComboBox( label, _waveTypeChoices ); 
            }

            // Number of harmonics
            {
                String format = SimStrings.get( "DiscreteControlPanel.numberOfHarmonics" );
                _numberOfHarmonicsSlider = new ControlPanelSlider( format );
                _numberOfHarmonicsSlider.setMaximum( FourierConfig.MAX_HARMONICS );
                _numberOfHarmonicsSlider.setMinimum( FourierConfig.MIN_HARMONICS );
                _numberOfHarmonicsSlider.setMajorTickSpacing( 2 );
                _numberOfHarmonicsSlider.setMinorTickSpacing( 1 );
                _numberOfHarmonicsSlider.setSnapToTicks( true );
            }
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( wavePropertiesPanel );
            wavePropertiesPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( showWavelengthPanel, row++, 0 );
            layout.addComponent( showPeriodPanel, row++, 0 );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
            layout.addFilledComponent( _numberOfHarmonicsSlider, row++, 0, GridBagConstraints.HORIZONTAL );
        }

        // Sound panel
        JPanel soundPanel = new JPanel();
        {
            //  Title
            Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, TITLED_BORDER_WIDTH );
            String title = SimStrings.get( "DiscreteControlPanel.title.sound" );
            TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
            soundPanel.setBorder( titleBorder );
            
            // Fundamental frequency
            {
                String format = SimStrings.get( "DiscreteControlPanel.fundamentalFrequency" );
                _fundamentalFrequencySlider = new ControlPanelSlider( format );
                _fundamentalFrequencySlider.setMaximum( 1200 );
                _fundamentalFrequencySlider.setMinimum( 200 );
                _fundamentalFrequencySlider.setValue( 440 );
                _fundamentalFrequencySlider.setMajorTickSpacing( 250 );
                _fundamentalFrequencySlider.setMinorTickSpacing( 50 );
                _fundamentalFrequencySlider.setSnapToTicks( false );
            }

            // Play Sound
            _playSoundCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.playSound" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( soundPanel );
            soundPanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _fundamentalFrequencySlider, row++, 0 );
            layout.addComponent( _playSoundCheckBox, row++, 0 );
        }
        
        // Math Mode panel
        JPanel mathModePanel = new JPanel();
        {
            //  Title
            Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, TITLED_BORDER_WIDTH );
            String title = SimStrings.get( "DiscreteControlPanel.title.mathMode" );
            TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
            mathModePanel.setBorder( titleBorder );
            
            // Show Math
            _showMathCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showMath" ) );

            // Math Forms
            {
                String label = SimStrings.get( "DiscreteControlPanel.mathForm" );

                // Choices
                {
                    _spaceMathFormChoices = new ArrayList();
                    _spaceMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_WAVE_NUMBER, SimStrings.get( "mathForm.waveNumber" ) ) );
                    _spaceMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_WAVELENGTH, SimStrings.get( "mathForm.wavelength" ) ) );
                    _spaceMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );

                    _timeMathFormChoices = new ArrayList();
                    _timeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_ANGULAR_FREQUENCY, SimStrings.get( "mathForm.angularFrequency" ) ) );
                    _timeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_FREQUENCY, SimStrings.get( "mathForm.frequency" ) ) );
                    _timeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_PERIOD, SimStrings.get( "mathForm.period" ) ) );
                    _timeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );

                    _spaceAndTimeMathFormChoices = new ArrayList();
                    _spaceAndTimeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY, SimStrings.get( "mathForm.waveNumberAndAngularFrequency" ) ) );
                    _spaceAndTimeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD, SimStrings.get( "mathForm.wavelengthAndPeriod" ) ) );
                    _spaceAndTimeMathFormChoices.add( new ControlPanelComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );
                }

                // Math form combo box
                _mathFormComboBox = new ControlPanelComboBox( label, _spaceMathFormChoices );
            }
            
            // Expand Sum
            _expandSumCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.expandSum" ) );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( mathModePanel );
            mathModePanel.setLayout( layout );
            int row = 0;
            layout.addComponent( _showMathCheckBox, row++, 0 );
            layout.addComponent( _mathFormComboBox, row++, 0 );
            layout.addComponent( _expandSumCheckBox, row++, 0 );
        }
        
        // Layout
        addFullWidth( functionsPanel );
        addFullWidth( wavePropertiesPanel );
        addFullWidth( soundPanel );
        addFullWidth( mathModePanel );

        // Wire up event handling.
        EventListener listener = new EventListener();
        _domainComboBox.addActionListener( listener );
        _presetsComboBox.addActionListener( listener );
        _showInfiniteCheckBox.addActionListener( listener );
        _showWavelengthCheckBox.addActionListener( listener );
        _showWavelengthComboBox.addActionListener( listener );
        _showPeriodCheckBox.addActionListener( listener );
        _showPeriodComboBox.addActionListener( listener );
        _waveTypeComboBox.addActionListener( listener );
        _numberOfHarmonicsSlider.addChangeListener( listener );
        _fundamentalFrequencySlider.addChangeListener( listener );
        _playSoundCheckBox.addActionListener( listener );
        _showMathCheckBox.addActionListener( listener );
        _mathFormComboBox.addActionListener( listener );
        _expandSumCheckBox.addActionListener( listener );

        // Set the state of the controls.
        reset();
    }

    public void reset() {
        
        // Domain
        _domainComboBox.setSelectedItem( SimStrings.get( "domain.space" ) );
        
        // Preset
        _presetsComboBox.setSelectedItem( SimStrings.get( "preset.sinecosine" ) );
        
        // Show Infinite Number of Harmonics
        _showInfiniteCheckBox.setSelected( false );
        
        // Show Wavelength
        _showWavelengthCheckBox.setSelected( false );
        _showWavelengthCheckBox.setEnabled( true );
        _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
        for ( int i = 0; i < _fourierSeriesModel.getNumberOfHarmonics(); i++ ) {
            _showWavelengthComboBox.addItem( _showWavelengthChoices.get( i ) );
        }
        
        // Show Period
        _showPeriodCheckBox.setSelected( false );
        _showPeriodCheckBox.setEnabled( false );
        _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
        for ( int i = 0; i < _fourierSeriesModel.getNumberOfHarmonics(); i++ ) {
            _showPeriodComboBox.addItem( _showPeriodChoices.get( i ) );
        }
        
        // Wave Type
        {
            Object item = null;
            switch ( _harmonicsGraphic.getWaveType() ) {
            case FourierConstants.WAVE_TYPE_SINE:
                item = SimStrings.get( "waveType.sines" );//XXX SimString in non-init code
                break;
            case FourierConstants.WAVE_TYPE_COSINE:
                item = SimStrings.get( "waveType.cosines" );//XXX SimString in non-init code
                break;
            default:
            }
            assert ( item != null );
            _waveTypeComboBox.setSelectedItem( item );
        }
        
        // Number of harmonics
        _numberOfHarmonicsSlider.setValue( _fourierSeriesModel.getNumberOfHarmonics() );
        
        // Sound
        _fundamentalFrequencySlider.setValue( (int) _fourierSeriesModel.getFundamentalFrequency() );
        _playSoundCheckBox.setSelected( false );
        
        // Math Mode
        _showMathCheckBox.setSelected( false );
        _mathFormComboBox.setChoices( _spaceMathFormChoices );
        _mathFormComboBox.setEnabled( false );
        _expandSumCheckBox.setEnabled( false );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener implements ActionListener, ChangeListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {

            if ( event.getSource() == _domainComboBox.getComboBox() ) {
                System.out.println( "_domainComboBox " + _domainComboBox.getSelectedItem() ); //XXX
                handleDomain();
            }
            else if ( event.getSource() == _presetsComboBox.getComboBox() ) {
                System.out.println( "preset " + _presetsComboBox.getSelectedItem() ); //XXX
                handlePreset();
            }
            else if ( event.getSource() == _showInfiniteCheckBox ) {
                System.out.println( "_showInfiniteCheckBox " + _showInfiniteCheckBox.isSelected() );//XXX
                handleShowInfinite();
            }
            else if ( event.getSource() == _showWavelengthCheckBox ) {
                System.out.println( "_showWavelengthCheckBox " + _showWavelengthCheckBox.isSelected() );//XXX
                handleShowWavelength();
            }
            else if ( event.getSource() == _showWavelengthComboBox ) {
                System.out.println( "showWavelengthTextfield " + _showWavelengthComboBox.getSelectedItem() );//XXX
                handleShowWavelength();
            }
            else if ( event.getSource() == _showPeriodCheckBox ) {
                System.out.println( "_showPeriodCheckBox " + _showPeriodCheckBox.isSelected() );//XXX
                handleShowPeriod();
            }
            else if ( event.getSource() == _showPeriodComboBox ) {
                System.out.println( "_showPeriodTextField " + _showPeriodComboBox.getSelectedItem() );//XXX
                handleShowPeriod();
            }
            else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                System.out.println( "_waveTypeComboBox " + _waveTypeComboBox.getSelectedItem() ); //XXX
                handleWaveType();
            }
            else if ( event.getSource() == _playSoundCheckBox ) {
                System.out.println( "_playSoundCheckBox " + _playSoundCheckBox.isSelected() );//XXX
                handlePlaySound();
            }
            else if ( event.getSource() == _showMathCheckBox ) {
                System.out.println( "_showMathCheckBox " + _showMathCheckBox.isSelected() ); //XXX
                handleShowMath();
            }
            else if ( event.getSource() == _mathFormComboBox.getComboBox() ) {
                System.out.println( "_mathFormComboBox " + _mathFormComboBox.getSelectedItem() ); //XXX
                handleMathForm();
            }
            else if ( event.getSource() == _expandSumCheckBox ) {
                System.out.println( "_expandSumCheckBox " + _expandSumCheckBox.isSelected() );//XXX
                handleExpandSum();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfHarmonicsSlider ) {
                if ( !_numberOfHarmonicsSlider.getSlider().getValueIsAdjusting() ) {
                    handleNumberOfHarmonics();
                }
            }
            else if ( event.getSource() == _fundamentalFrequencySlider ) {
                handleFundamentalFrequency();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleDomain() {
        switch ( _domainComboBox.getSelectedKey() ) {
        case FourierConstants.DOMAIN_SPACE:
            _mathFormComboBox.setChoices( _spaceMathFormChoices );
            _showWavelengthCheckBox.setEnabled( true );
            _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
            _showPeriodCheckBox.setEnabled( false );
            _showPeriodComboBox.setEnabled( false );
            break;
        case FourierConstants.DOMAIN_TIME:
            _mathFormComboBox.setChoices( _timeMathFormChoices );
            _showWavelengthCheckBox.setEnabled( false );
            _showWavelengthComboBox.setEnabled( false );
            _showPeriodCheckBox.setEnabled( true );
            _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
            break;
        case FourierConstants.DOMAIN_SPACE_AND_TIME:
            _mathFormComboBox.setChoices( _spaceAndTimeMathFormChoices );
            _showWavelengthCheckBox.setEnabled( false );
            _showWavelengthComboBox.setEnabled( false );
            _showPeriodCheckBox.setEnabled( true );
            _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
            break;
        default:
            assert( 1 == 0 ); // programming error
        }
    }
    
    private void handlePreset() {
        
    }
    
    private void handleShowInfinite() {
        
    }
    
    private void handleShowWavelength() {
        _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
    }
    
    private void handleShowPeriod() {
        _showPeriodComboBox.setEnabled(  _showPeriodCheckBox.isSelected() );
    }
    
    private void handleWaveType() {
        int waveType = _waveTypeComboBox.getSelectedKey();
        _harmonicsGraphic.setWaveType( waveType );
        _sumGraphic.setWaveType( waveType );
    }
    
    private void handleNumberOfHarmonics() {
        
        int numberOfHarmonics = _numberOfHarmonicsSlider.getValue();
        
        // Update the Fourier series.
        _fourierSeriesModel.setNumberOfHarmonics( numberOfHarmonics );
        
        // Update the "Show Wavelength" control.
        int selectedWavelengthIndex = _showWavelengthComboBox.getSelectedIndex();
        _showWavelengthComboBox.removeAllItems();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            _showWavelengthComboBox.addItem( _showWavelengthChoices.get( i ) );
        }
        if ( selectedWavelengthIndex >= numberOfHarmonics) {
            //XXX hide the wavelength graphic
            _showWavelengthCheckBox.setSelected( false );
            _showWavelengthComboBox.setEnabled( false );
        }
        else {
            _showWavelengthComboBox.setSelectedIndex( selectedWavelengthIndex );
        }
        
        // Update the "Show Period" control.
        int selectedPeriodIndex = _showPeriodComboBox.getSelectedIndex();
        _showPeriodComboBox.removeAllItems();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            _showPeriodComboBox.addItem( _showPeriodChoices.get( i ) );
        }
        if ( selectedPeriodIndex >= numberOfHarmonics) {
            //XXX hide the period graphic
            _showPeriodCheckBox.setSelected( false );
            _showPeriodComboBox.setEnabled( false );
        }
        else {
            _showPeriodComboBox.setSelectedIndex( selectedPeriodIndex );
        }
    }
    
    private void handleFundamentalFrequency() {
        int fundamentalFrequency = _fundamentalFrequencySlider.getValue();
        _fourierSeriesModel.setFundamentalFrequency( fundamentalFrequency );
    }
    
    private void handlePlaySound() {
        
    }
    
    private void handleShowMath() {
        _mathFormComboBox.setEnabled( _showMathCheckBox.isSelected() );
        _expandSumCheckBox.setEnabled( _showMathCheckBox.isSelected() );
    }
    
    private void handleMathForm() {
        
    }
    
    private void handleExpandSum() {
        
    }
}
