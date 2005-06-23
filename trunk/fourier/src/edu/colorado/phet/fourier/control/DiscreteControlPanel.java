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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.view.*;


/**
 * DiscreteControlPanel is the control panel for the "Discrete" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteControlPanel extends FourierControlPanel implements ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TITLED_BORDER_WIDTH = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private FourierSeries _fourierSeries;
    private HarmonicsGraph _harmonicsGraph;
    private SumGraph _sumGraph;
    private WaveMeasurementTool _wavelengthTool, _periodTool;
    private PeriodDisplay _periodDisplay;

    // UI components
    private FourierComboBox _domainComboBox;
    private FourierComboBox _presetsComboBox;
    private JCheckBox _showInfiniteCheckBox;
    private JCheckBox _showWavelengthCheckBox;
    private JComboBox _showWavelengthComboBox;
    private JCheckBox _showPeriodCheckBox;
    private JComboBox _showPeriodComboBox;
    private FourierComboBox _waveTypeComboBox;
    private FourierSlider _numberOfHarmonicsSlider;
    private JCheckBox _showMathCheckBox;
    private FourierComboBox _mathFormComboBox;
    private JCheckBox _expandSumCheckBox;
    private ExpandSumDialog _expandSumDialog;
    
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
     * @param fourierSeries
     */
    public DiscreteControlPanel( 
            FourierModule module, 
            FourierSeries fourierSeries, 
            HarmonicsGraph harmonicsGraph, 
            SumGraph sumGraph,
            WaveMeasurementTool wavelengthTool,
            WaveMeasurementTool periodTool,
            PeriodDisplay periodDisplay ) {
        
        super( module );
        
        assert ( fourierSeries != null );
        assert( harmonicsGraph != null );
        assert( sumGraph != null );
        assert( wavelengthTool != null );
        assert( periodTool != null );
        assert( periodDisplay != null );
        
        // Things we'll be controlling.
        _fourierSeries = fourierSeries;
        _harmonicsGraph = harmonicsGraph;
        _sumGraph = sumGraph;
        _wavelengthTool = wavelengthTool;
        _periodTool = periodTool;
        _periodDisplay = periodDisplay;
        
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
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE, SimStrings.get( "domain.space" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_TIME, SimStrings.get( "domain.time" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( FourierConstants.DOMAIN_SPACE_AND_TIME, SimStrings.get( "domain.spaceAndTime" ) ) );

                // Function combo box
                _domainComboBox = new FourierComboBox( label, _domainChoices );
            }

            // Presets
            {
                // Label
                String label = SimStrings.get( "DiscreteControlPanel.presets" );

                // Choices
                _presetChoices = new ArrayList();
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SINE_COSINE, SimStrings.get( "preset.sinecosine" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SQUARE, SimStrings.get( "preset.square" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SAWTOOTH, SimStrings.get( "preset.sawtooth" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_TRIANGLE, SimStrings.get( "preset.triangle" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_WAVE_PACKET, SimStrings.get( "preset.wavePacket" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_CUSTOM, SimStrings.get( "preset.custom" ) ) );

                // Presets combo box
                _presetsComboBox = new FourierComboBox( label, _presetChoices );
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
                String wavelengthSymbol = SimStrings.get( "symbol.wavelength" );
                for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                    String choice = "<html>" + wavelengthSymbol + "<sub>" + ( i + 1 ) + "</sub></html>";
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
                String periodSymbol = SimStrings.get( "symbol.period" );
                for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                    String choice = "<html>" + periodSymbol + "<sub>" + ( i + 1 ) + "</sub></html>";
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
                _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_SINE, SimStrings.get( "waveType.sines" ) ) );
                _waveTypeChoices.add( new FourierComboBox.Choice( FourierConstants.WAVE_TYPE_COSINE, SimStrings.get( "waveType.cosines" ) ) );
                
                // Wave Type combo box
                _waveTypeComboBox = new FourierComboBox( label, _waveTypeChoices ); 
            }

            // Number of harmonics
            {
                String format = SimStrings.get( "DiscreteControlPanel.numberOfHarmonics" );
                _numberOfHarmonicsSlider = new FourierSlider( format );
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
            layout.addFilledComponent( _numberOfHarmonicsSlider, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
            layout.addComponent( showWavelengthPanel, row++, 0 );
            layout.addComponent( showPeriodPanel, row++, 0 );
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
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVE_NUMBER, SimStrings.get( "mathForm.waveNumber" ) ) );
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVELENGTH, SimStrings.get( "mathForm.wavelength" ) ) );
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );

                    _timeMathFormChoices = new ArrayList();
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_ANGULAR_FREQUENCY, SimStrings.get( "mathForm.angularFrequency" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_FREQUENCY, SimStrings.get( "mathForm.frequency" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_PERIOD, SimStrings.get( "mathForm.period" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );

                    _spaceAndTimeMathFormChoices = new ArrayList();
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY, SimStrings.get( "mathForm.waveNumberAndAngularFrequency" ) ) );
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD, SimStrings.get( "mathForm.wavelengthAndPeriod" ) ) );
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );
                }

                // Math form combo box
                _mathFormComboBox = new FourierComboBox( label, _spaceMathFormChoices );
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
        addFullWidth( mathModePanel );

        // Dialogs
        Frame parentFrame = PhetApplication.instance().getPhetFrame();
        _expandSumDialog = new ExpandSumDialog( parentFrame, _fourierSeries );
        
        // Set the state of the controls.
        reset();
        
        // Wire up event handling (after setting state with reset).
        {
            EventListener listener = new EventListener();
            // WindowListeners
            _expandSumDialog.addWindowListener( listener );
            // ActionListeners
            _showInfiniteCheckBox.addActionListener( listener );
            _showWavelengthCheckBox.addActionListener( listener );
            _showPeriodCheckBox.addActionListener( listener );
            _showMathCheckBox.addActionListener( listener );
            _expandSumCheckBox.addActionListener( listener );
            _expandSumDialog.getCloseButton().addActionListener( listener );
            // ChangeListeners
            _numberOfHarmonicsSlider.addChangeListener( listener );
            // ItemListeners
            _domainComboBox.addItemListener( listener );
            _presetsComboBox.addItemListener( listener );
            _waveTypeComboBox.addItemListener( listener );
            _showWavelengthComboBox.addItemListener( listener );
            _showPeriodComboBox.addItemListener( listener );
            _mathFormComboBox.addItemListener( listener );
        }    
    }

    public void cleanup() {
        _expandSumDialog.cleanup();
    }
    
    public void reset() {
        
        // Domain
        _domainComboBox.setSelectedKey( FourierConstants.DOMAIN_SPACE );
        
        // Preset
        int preset = _fourierSeries.getPreset();
        _presetsComboBox.setSelectedKey( preset );
        
        // Show Infinite Number of Harmonics
        _showInfiniteCheckBox.setEnabled( true );
        _showInfiniteCheckBox.setForeground( Color.BLACK );
        _showInfiniteCheckBox.setSelected( false );
        _sumGraph.setPresetEnabled( _showInfiniteCheckBox.isSelected() );
        
        // Show Wavelength
        _showWavelengthCheckBox.setSelected( false );
        _showWavelengthCheckBox.setEnabled( true );
        _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
        _showWavelengthComboBox.removeAllItems();
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _showWavelengthComboBox.addItem( _showWavelengthChoices.get( i ) );
        }
        _showWavelengthComboBox.setSelectedIndex( 0 );
        _wavelengthTool.setVisible( _showWavelengthCheckBox.isSelected() );

        // Show Period
        _showPeriodCheckBox.setSelected( false );
        _showPeriodCheckBox.setEnabled( false );
        _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
        _showPeriodComboBox.removeAllItems();
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _showPeriodComboBox.addItem( _showPeriodChoices.get( i ) );
        }
        _showPeriodComboBox.setSelectedIndex( 0 );
        _periodTool.setVisible( _showPeriodCheckBox.isSelected() );
        
        // Wave Type
        int waveType = _fourierSeries.getWaveType();
        _waveTypeComboBox.setSelectedKey( waveType );
        
        // Number of harmonics
        _numberOfHarmonicsSlider.setValue( _fourierSeries.getNumberOfHarmonics() );
        
        // Math Mode
        _showMathCheckBox.setSelected( false );
        _mathFormComboBox.setChoices( _spaceMathFormChoices );
        _mathFormComboBox.setSelectedIndex( 0 );
        _mathFormComboBox.setEnabled( _showMathCheckBox.isSelected() );
        _expandSumCheckBox.setEnabled( _showMathCheckBox.isSelected() );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * EventListener is a nested class that is private to this control panel.
     * It handles dispatching of all events generated by the controls.
     */
    private class EventListener extends WindowAdapter implements ActionListener, ChangeListener, ItemListener {

        public EventListener() {}

        public void windowClosing( WindowEvent event ) {
            if ( event.getSource() == _expandSumDialog ) {
                handleCloseExpandSumDialog();
            }
        }
        
        public void actionPerformed( ActionEvent event ) {

            if ( event.getSource() == _showInfiniteCheckBox ) {
                handleShowInfinite();
            }
            else if ( event.getSource() == _showWavelengthCheckBox ) {
                handleShowWavelength();
            }
            else if ( event.getSource() == _showPeriodCheckBox ) {
                handleShowPeriod();
            }
            else if ( event.getSource() == _showMathCheckBox ) {
                handleShowMath();
            }
            else if ( event.getSource() == _expandSumCheckBox ) {
                handleExpandSum();
            }
            else if ( event.getSource() == _expandSumDialog.getCloseButton() ) {
                handleCloseExpandSumDialog();
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
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _domainComboBox.getComboBox() ) {
                    handleDomain();
                }
                else if ( event.getSource() == _presetsComboBox.getComboBox() ) {
                    handlePreset();
                }
                else if ( event.getSource() == _showWavelengthComboBox ) {
                    handleShowWavelength();
                }
                else if ( event.getSource() == _showPeriodComboBox ) {
                    handleShowPeriod();
                }
                else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                    handleWaveType();
                }
                else if ( event.getSource() == _mathFormComboBox.getComboBox() ) {
                    handleMathForm();
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
    
    private void handleDomain() { 
        int domain = _domainComboBox.getSelectedKey();
        
        switch ( domain ) {
        case FourierConstants.DOMAIN_SPACE:
            _mathFormComboBox.setChoices( _spaceMathFormChoices );
            _mathFormComboBox.setSelectedIndex( 0 );
            _showWavelengthCheckBox.setEnabled( true );
            _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
            _wavelengthTool.setVisible( _showWavelengthCheckBox.isSelected() );
            _showPeriodCheckBox.setEnabled( false );
            _showPeriodComboBox.setEnabled( false );
            _periodTool.setVisible( false );
            _periodDisplay.setVisible( false );
            break;
        case FourierConstants.DOMAIN_TIME:
            _mathFormComboBox.setChoices( _timeMathFormChoices );
            _mathFormComboBox.setSelectedIndex( 0 );
            _showWavelengthCheckBox.setEnabled( false );
            _showWavelengthComboBox.setEnabled( false );
            _wavelengthTool.setVisible( false );
            _showPeriodCheckBox.setEnabled( true );
            _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
            _periodTool.setVisible( _showPeriodCheckBox.isSelected() );
            _periodDisplay.setVisible( false );
            break;
        case FourierConstants.DOMAIN_SPACE_AND_TIME:
            _mathFormComboBox.setChoices( _spaceAndTimeMathFormChoices );
            _mathFormComboBox.setSelectedIndex( 0 );
            _showWavelengthCheckBox.setEnabled( true );
            _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
            _wavelengthTool.setVisible( _showWavelengthCheckBox.isSelected() );
            _showPeriodCheckBox.setEnabled( true );
            _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
            _periodTool.setVisible( false );
            _periodDisplay.setVisible( _showPeriodCheckBox.isSelected() );
            break;
        default:
            assert( 1 == 0 ); // programming error
        }
        
        int mathForm = _mathFormComboBox.getSelectedKey(); // get this after setting stuff above
        _sumGraph.setDomainAndMathForm( domain, mathForm );
        _harmonicsGraph.setDomainAndMathForm( domain, mathForm );
        _expandSumDialog.setDomainAndMathForm( domain, mathForm );
    }
    
    private void handlePreset() {
        int waveType = _waveTypeComboBox.getSelectedKey();
        int preset = _presetsComboBox.getSelectedKey();
        if ( waveType == FourierConstants.WAVE_TYPE_COSINE && preset == FourierConstants.PRESET_SAWTOOTH ) {
            showSawtoothCosinesErrorDialog();
            _waveTypeComboBox.setSelectedKey( FourierConstants.WAVE_TYPE_SINE );
            _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
            _harmonicsGraph.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        }
        boolean showInfiniteEnabled = 
            ( preset != FourierConstants.PRESET_WAVE_PACKET && preset != FourierConstants.PRESET_CUSTOM );
        _showInfiniteCheckBox.setEnabled( showInfiniteEnabled );
        _showInfiniteCheckBox.setForeground( showInfiniteEnabled ? Color.BLACK : Color.GRAY );
        _fourierSeries.setPreset( preset );
    }
    
    private void handleShowInfinite() {
        boolean enabled = _showInfiniteCheckBox.isSelected();
        _sumGraph.setPresetEnabled( enabled );
    }
    
    private void handleShowWavelength() {
        _showWavelengthComboBox.setEnabled( _showWavelengthCheckBox.isSelected() );
        _wavelengthTool.setVisible( _showWavelengthCheckBox.isSelected() );
        int harmonicOrder = _showWavelengthComboBox.getSelectedIndex();
        if ( harmonicOrder >= 0 ) {
            Harmonic harmonic = _fourierSeries.getHarmonic( harmonicOrder );
            _wavelengthTool.setHarmonic( harmonic );
        }
    }
    
    private void handleShowPeriod() {
        
        _showPeriodComboBox.setEnabled( _showPeriodCheckBox.isSelected() );
        
        int domain = _domainComboBox.getSelectedKey();
        int harmonicOrder = _showPeriodComboBox.getSelectedIndex();
        
        if ( domain == FourierConstants.DOMAIN_TIME ) {
            _periodTool.setVisible( _showPeriodCheckBox.isSelected() );
        }
        else {
            _periodDisplay.setVisible( _showPeriodCheckBox.isSelected() );
        }
        
        if ( harmonicOrder >= 0 ) {
            Harmonic harmonic = _fourierSeries.getHarmonic( harmonicOrder );
            _periodTool.setHarmonic( harmonic );
            _periodDisplay.setHarmonic( harmonic );
        }
    }
    
    private void handleWaveType() {
        int waveType = _waveTypeComboBox.getSelectedKey();
        int preset = _presetsComboBox.getSelectedKey();
        if ( waveType == FourierConstants.WAVE_TYPE_COSINE && preset == FourierConstants.PRESET_SAWTOOTH ) {
            showSawtoothCosinesErrorDialog();
            _waveTypeComboBox.setSelectedKey( FourierConstants.WAVE_TYPE_SINE );
            _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
            _harmonicsGraph.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        }
        else {
            _fourierSeries.setWaveType( waveType );
            _harmonicsGraph.setWaveType( waveType );
        }
    }
    
    private void handleNumberOfHarmonics() {
        
        int numberOfHarmonics = _numberOfHarmonicsSlider.getValue();
        
        // Update the Fourier series.
        _fourierSeries.setNumberOfHarmonics( numberOfHarmonics );
        
        // Update the "Show Wavelength" control.
        int selectedWavelengthIndex = _showWavelengthComboBox.getSelectedIndex();
        _showWavelengthComboBox.removeAllItems();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            _showWavelengthComboBox.addItem( _showWavelengthChoices.get( i ) );
        }
        if ( selectedWavelengthIndex >= numberOfHarmonics) {
            _showWavelengthCheckBox.setSelected( false );
            _showWavelengthComboBox.setEnabled( false );
            _wavelengthTool.setVisible( _showWavelengthCheckBox.isSelected() );
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
            _showPeriodCheckBox.setSelected( false );
            _showPeriodComboBox.setEnabled( false );
            int domain = _domainComboBox.getSelectedKey();
            if ( domain == FourierConstants.DOMAIN_TIME ) {
                _periodTool.setVisible( _showPeriodCheckBox.isSelected() );
                _periodDisplay.setVisible( false );
            }
            else {
                _periodTool.setVisible( false );
                _periodDisplay.setVisible( _showPeriodCheckBox.isSelected() );
            }
        }
        else {
            _showPeriodComboBox.setSelectedIndex( selectedPeriodIndex );
        }
    }

    private void handleShowMath() {
        _mathFormComboBox.setEnabled( _showMathCheckBox.isSelected() );
        _expandSumCheckBox.setEnabled( _showMathCheckBox.isSelected() );
        _harmonicsGraph.setMathEnabled( _showMathCheckBox.isSelected() );
        _sumGraph.setMathEnabled( _showMathCheckBox.isSelected() );
    }
    
    private void handleMathForm() {
        int domain = _domainComboBox.getSelectedKey();
        int mathForm = _mathFormComboBox.getSelectedKey();
        _harmonicsGraph.setDomainAndMathForm( domain, mathForm );
        _sumGraph.setDomainAndMathForm( domain, mathForm );
        _expandSumDialog.setDomainAndMathForm( domain, mathForm );
    }
    
    private void handleExpandSum() {
        if ( _expandSumCheckBox.isSelected() ) {
            _expandSumDialog.show();
        }
        else {
            _expandSumDialog.hide();
        }
    }
    
    private void handleCloseExpandSumDialog() {
        _expandSumDialog.hide();
        _expandSumCheckBox.setSelected( false );
    }
    
    /*
     * Displays a modal error dialog if the user attempts to select
     * sawtooth preset and cosines wave type.  
     * You can't make a sawtooth wave out of cosines because it is asymmetric.
     */
    private void showSawtoothCosinesErrorDialog() {
        String message = SimStrings.get( "SawtoothCosinesErrorDialog.message" );
        JOptionPane op = new JOptionPane( message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION );
        op.createDialog( this, null ).show();
    }
    
    //----------------------------------------------------------------------------
    // ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Changes the preset selection to "Custom" when an amplitude slider
     * is physically moved.
     */
    public void stateChanged( ChangeEvent event ) {
        if ( event.getSource() instanceof AmplitudeSlider ) {
            _presetsComboBox.setSelectedKey( FourierConstants.PRESET_CUSTOM );
        }
    }
}
