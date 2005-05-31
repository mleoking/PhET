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
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.view.HarmonicsGraphic;
import edu.colorado.phet.fourier.view.SineWaveGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


/**
 * DiscreteControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteControlPanel extends FourierControlPanel {

    public static final int DOMAIN_SPACE = 0;
    public static final int DOMAIN_TIME = 1;
    public static final int DOMAIN_SPACE_AND_TIME = 2;
    
    public static final int PRESET_SINE_COSINE = 0;
    public static final int PRESET_SAWTOOTH = 1;
    public static final int PRESET_TRIANGLE = 2;
    public static final int PRESET_WAVE_PACKET = 3;
    public static final int PRESET_CUSTOM = 4;
    
    public static final int MATH_FORM_WAVE_NUMBER = 0;
    public static final int MATH_FORM_WAVELENGTH = 1;
    public static final int MATH_FORM_MODE = 2;
    public static final int MATH_FORM_ANGULAR_FREQUENCY = 3;
    public static final int MATH_FORM_FREQUENCY = 4;
    public static final int MATH_FORM_PERIOD = 5;
    public static final int MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY = 6;
    public static final int MATH_FORM_WAVELENGTH_AND_PERIOD = 7;
    
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
    private JTextField _showWavelengthTextField;
    private JCheckBox _showPeriodCheckBox;
    private JTextField _showPeriodTextField;
    private ControlPanelComboBox _waveTypeComboBox;
    private ControlPanelSlider _numberOfHarmonicsSlider;
    private ControlPanelSlider _fundamentalFrequencySlider;
    private JCheckBox _playSoundCheckBox;
    private JCheckBox _showMathCheckBox;
    private ControlPanelComboBox _mathFormComboBox;
    private JCheckBox _expandSumCheckBox;
    
    // Choices
    private Hashtable _domainChoices;
    private Hashtable _presetChoices;
    private Hashtable _waveTypeChoices;
    private Hashtable _spaceMathFormChoices;
    private Hashtable _timeMathFormChoices;
    private Hashtable _spaceAndTimeMathFormChoices;
    private String _sinesString, _cosinesString;
    
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
                _domainChoices = new Hashtable();
                _domainChoices.put( SimStrings.get( "domain.space" ), new Integer( DOMAIN_SPACE ) );
                _domainChoices.put( SimStrings.get( "domain.time" ), new Integer( DOMAIN_TIME ) );
                _domainChoices.put( SimStrings.get( "domain.spaceAndTime" ), new Integer( DOMAIN_SPACE_AND_TIME ) );

                // Function combo box
                _domainComboBox = new ControlPanelComboBox( label, _domainChoices );
            }

            // Presets
            {
                // Label
                String label = SimStrings.get( "DiscreteControlPanel.presets" );

                // Choices
                _presetChoices = new Hashtable();
                _presetChoices.put( SimStrings.get( "preset.sinecosine" ), new Integer( PRESET_SINE_COSINE ) );
                _presetChoices.put( SimStrings.get( "preset.sawtooth" ), new Integer( PRESET_SAWTOOTH ) );
                _presetChoices.put( SimStrings.get( "preset.triangle" ), new Integer( PRESET_TRIANGLE ) );
                _presetChoices.put( SimStrings.get( "preset.wavePacket" ), new Integer( PRESET_WAVE_PACKET ) );
                _presetChoices.put( SimStrings.get( "preset.custom" ), new Integer( PRESET_CUSTOM ) );

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

                _showWavelengthTextField = new JTextField();
                _showWavelengthTextField.setColumns( 5 );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( showWavelengthPanel );
                showWavelengthPanel.setLayout( layout );
                layout.addAnchoredComponent( _showWavelengthCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _showWavelengthTextField, 0, 1, GridBagConstraints.WEST );
            }

            // Show Wavelength
            JPanel showPeriodPanel = new JPanel();
            {
                _showPeriodCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showPeriod" ) );

                _showPeriodTextField = new JTextField();
                _showPeriodTextField.setColumns( 5 );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( showPeriodPanel );
                showPeriodPanel.setLayout( layout );
                layout.addAnchoredComponent( _showPeriodCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _showPeriodTextField, 0, 1, GridBagConstraints.WEST );
            }
            
            // Wave Type
            {
                // Label
                String label = SimStrings.get( "DiscreteControlPanel.waveType" );
                
                // Choices
                _sinesString = SimStrings.get( "waveType.sines" );
                _cosinesString = SimStrings.get( "waveType.cosines" );
                _waveTypeChoices = new Hashtable();
                _waveTypeChoices.put( _sinesString, new Integer( SineWaveGraphic.WAVE_TYPE_SINE ) );
                _waveTypeChoices.put( _cosinesString, new Integer( SineWaveGraphic.WAVE_TYPE_COSINE ) );
                
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
                    _spaceMathFormChoices = new Hashtable();
                    _spaceMathFormChoices.put( SimStrings.get( "mathForm.waveNumber" ), new Integer( MATH_FORM_WAVE_NUMBER ) );
                    _spaceMathFormChoices.put( SimStrings.get( "mathForm.wavelength" ), new Integer( MATH_FORM_WAVELENGTH ) );
                    _spaceMathFormChoices.put( SimStrings.get( "mathForm.mode" ), new Integer( MATH_FORM_MODE ) );

                    _timeMathFormChoices = new Hashtable();
                    _timeMathFormChoices.put( SimStrings.get( "mathForm.angularFrequency" ), new Integer( MATH_FORM_ANGULAR_FREQUENCY ) );
                    _timeMathFormChoices.put( SimStrings.get( "mathForm.frequency" ), new Integer( MATH_FORM_FREQUENCY ) );
                    _timeMathFormChoices.put( SimStrings.get( "mathForm.period" ), new Integer( MATH_FORM_PERIOD ) );
                    _timeMathFormChoices.put( SimStrings.get( "mathForm.mode" ), new Integer( MATH_FORM_MODE ) );

                    _spaceAndTimeMathFormChoices = new Hashtable();
                    _spaceAndTimeMathFormChoices.put( SimStrings.get( "mathForm.waveNumberAndAngularFrequency" ), new Integer( MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY ) );
                    _spaceAndTimeMathFormChoices.put( SimStrings.get( "mathForm.wavelengthAndPeriod" ), new Integer( MATH_FORM_WAVELENGTH_AND_PERIOD ) );
                    _spaceAndTimeMathFormChoices.put( SimStrings.get( "mathForm.mode" ), new Integer( MATH_FORM_MODE ) );
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
        _showWavelengthTextField.addActionListener( listener );
        _showPeriodCheckBox.addActionListener( listener );
        _showPeriodTextField.addActionListener( listener );
        _waveTypeComboBox.addActionListener( listener );
        _numberOfHarmonicsSlider.addChangeListener( listener );
        _fundamentalFrequencySlider.addChangeListener( listener );
        _playSoundCheckBox.addActionListener( listener );
        _showMathCheckBox.addActionListener( listener );
        _mathFormComboBox.addActionListener( listener );
        _expandSumCheckBox.addActionListener( listener );

        // Set the state of the controls.
        { /* XXX - this block should be replaced by model sync in update */
            _domainComboBox.setSelectedItem( SimStrings.get( "domain.space" ) );
            _showInfiniteCheckBox.setSelected( false );
            _showWavelengthTextField.setEnabled( false );
            _showPeriodTextField.setEnabled( false );
            _mathFormComboBox.setChoices( _spaceMathFormChoices );
            _mathFormComboBox.setEnabled( false );
            _expandSumCheckBox.setEnabled( false );
        }
        update();
    }

    /**
     * Updates the control panel to match the state of the things that it's controlling.
     */
    public void update() {
        // Function
        //XXX
        
        // Presets 
        //XXX
        
        // Show wavelength
        //XXX
        
        // Show period
        //XXX
        
        // Number of harmonics
        _numberOfHarmonicsSlider.setValue( _fourierSeriesModel.getNumberOfHarmonics() );
        
        // Fundamental frequency
        _fundamentalFrequencySlider.setValue( (int) _fourierSeriesModel.getFundamentalFrequency() );
        
        // Wave Type
        {
            Object item = null;
            switch ( _harmonicsGraphic.getWaveType() ) {
            case SineWaveGraphic.WAVE_TYPE_SINE:
                item = _sinesString;
                break;
            case SineWaveGraphic.WAVE_TYPE_COSINE:
                item = _cosinesString;
                break;
            default:
            }
            assert ( item != null );
            _waveTypeComboBox.setSelectedItem( item );
        }
        
        // Show Math
        //XXX
        
        // Math Form
        //XXX
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
                Object key = _domainComboBox.getSelectedItem();
                System.out.println( "_domainComboBox " + key ); //XXX
                Object value = _domainChoices.get( key );
                assert( value != null && value instanceof Integer ); // programming error
                int domain = ((Integer)value).intValue();
                switch ( domain ) {
                case DOMAIN_SPACE:
                    _mathFormComboBox.setChoices( _spaceMathFormChoices );
                    _showWavelengthCheckBox.setEnabled( true );
                    _showPeriodCheckBox.setEnabled( false );
                    break;
                case DOMAIN_TIME:
                    _mathFormComboBox.setChoices( _timeMathFormChoices );
                    _showWavelengthCheckBox.setEnabled( false );
                    _showPeriodCheckBox.setEnabled( true );
                    break;
                case DOMAIN_SPACE_AND_TIME:
                    _mathFormComboBox.setChoices( _spaceAndTimeMathFormChoices );
                    _showWavelengthCheckBox.setEnabled( true );
                    _showPeriodCheckBox.setEnabled( true );
                    break;
                default:
                    assert( 1 == 0 ); // programming error
                }
            }
            else if ( event.getSource() == _presetsComboBox.getComboBox() ) {
                Object key = _presetsComboBox.getSelectedItem();
                System.out.println( "preset " + key ); //XXX
            }
            else if ( event.getSource() == _showInfiniteCheckBox ) {
                System.out.println( "_showInfiniteCheckBox " + _showInfiniteCheckBox.isSelected() );//XXX
            }
            else if ( event.getSource() == _showWavelengthCheckBox ) {
                System.out.println( "_showWavelengthCheckBox " + _showWavelengthCheckBox.isSelected() );//XXX
                _showWavelengthTextField.setEnabled( _showWavelengthCheckBox.isSelected() );
            }
            else if ( event.getSource() == _showWavelengthTextField ) {
                System.out.println( "showWavelengthTextfield " + _showWavelengthTextField.getText() );//XXX
            }
            else if ( event.getSource() == _showPeriodCheckBox ) {
                System.out.println( "_showPeriodCheckBox " + _showPeriodCheckBox.isSelected() );//XXX
                _showPeriodTextField.setEnabled(  _showPeriodCheckBox.isSelected() );
            }
            else if ( event.getSource() == _showPeriodTextField ) {
                System.out.println( "_showPeriodTextField " + _showPeriodTextField.getText() );//XXX
            }
            else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                // Use the selection to lookup the associated symbolic constant.
                Object key = _waveTypeComboBox.getSelectedItem();
                Object value = _waveTypeChoices.get( key );
                assert( value != null && value instanceof Integer ); // programming error
                int waveType = ((Integer)value).intValue();
                _harmonicsGraphic.setWaveType( waveType );
                _sumGraphic.setWaveType( waveType );
            }
            else if ( event.getSource() == _playSoundCheckBox ) {
                System.out.println( "_playSoundCheckBox " + _playSoundCheckBox.isSelected() );//XXX
            }
            else if ( event.getSource() == _showMathCheckBox ) {
                System.out.println( "_showMathCheckBox " + _showMathCheckBox.isSelected() ); //XXX
                _mathFormComboBox.setEnabled( _showMathCheckBox.isSelected() );
                _expandSumCheckBox.setEnabled( _showMathCheckBox.isSelected() );
            }
            else if ( event.getSource() == _mathFormComboBox.getComboBox() ) {
                Object key = _mathFormComboBox.getSelectedItem();
                System.out.println( "_mathFormComboBox " + key ); //XXX
            }
            else if ( event.getSource() == _expandSumCheckBox ) {
                System.out.println( "_expandSumCheckBox " + _expandSumCheckBox.isSelected() );//XXX
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfHarmonicsSlider ) {
                if ( !_numberOfHarmonicsSlider.getSlider().getValueIsAdjusting() ) {
                    int numberOfHarmonics = _numberOfHarmonicsSlider.getValue();
                    _fourierSeriesModel.setNumberOfHarmonics( numberOfHarmonics );
                }
            }
            else if ( event.getSource() == _fundamentalFrequencySlider ) {
                int fundamentalFrequency = _fundamentalFrequencySlider.getValue();
                _fourierSeriesModel.setFundamentalFrequency( fundamentalFrequency );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }

}
