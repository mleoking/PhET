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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.view.ComponentsGraphic;
import edu.colorado.phet.fourier.view.SineWaveGraphic;
import edu.colorado.phet.fourier.view.SumGraphic;


/**
 * DiscreteControlPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteControlPanel extends FourierControlPanel {

    public static final int FUNCTION_SPACE = 0;
    public static final int FUNCTION_TIME = 1;
    public static final int FUNCTION_SPACE_AND_TIME = 2;
    
    public static final int PRESET_SINE = 0;
    public static final int PRESET_SAWTOOTH = 1;
    public static final int PRESET_TRIANGLE = 2;
    public static final int PRESET_WAVE_PACKET = 3;
    public static final int PRESET_CUSTOM = 4;
    
    public static final int MATH_FORM_WAVE_NUMBER = 0;
    public static final int MATH_FORM_WAVELENGTH = 1;
    public static final int MATH_FORM_MODE = 2;
    public static final int MATH_FORM_ANGULAR = 3;
    public static final int MATH_FORM_FREQUENCY = 4;
    public static final int MATH_FORM_PERIOD = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private FourierSeries _fourierSeriesModel;
    private ComponentsGraphic _componentsGraphic;
    private SumGraphic _sumGraphic;

    // UI components
    private ControlPanelComboBox _functionComboBox;
    private ControlPanelComboBox _presetsComboBox;
    private JCheckBox _showWavelengthCheckBox;
    private JTextField _showWavelengthTextField;
    private JCheckBox _showPeriodCheckBox;
    private JTextField _showPeriodTextField;
    private ControlPanelSlider _numberOfComponentsSlider;
    private ControlPanelSlider _fundamentalFrequencySlider;
    private ControlPanelComboBox _waveTypeComboBox;
    private JButton _playSoundButton;
    private JCheckBox _showMathCheckBox;
    private ControlPanelComboBox _mathFormComboBox;
    
    // Choices
    private Hashtable _functionChoices;
    private Hashtable _presetChoices;
    private Hashtable _waveTypeChoices;
    private Hashtable _spaceMathFormChoices;
    private Hashtable _timeMathFormChoices;
    
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
            ComponentsGraphic componentsGraphic, 
            SumGraphic sumGraphic ) {
        
        super( module );
        
        assert ( fourierSeriesModel != null );
        assert( componentsGraphic != null );
        assert( sumGraphic != null );
        
        // Things we'll be controlling.
        _fourierSeriesModel = fourierSeriesModel;
        _componentsGraphic = componentsGraphic;
        _sumGraphic = sumGraphic;

        // Function
        {
            // Label
            String label = SimStrings.get( "DiscreteControlPanel.showFunctionOf" );
            
            // Choices
            _functionChoices = new Hashtable();
            _functionChoices.put( SimStrings.get( "DiscreteControlPanel.space" ), new Integer( FUNCTION_SPACE ) );
            _functionChoices.put( SimStrings.get( "DiscreteControlPanel.time" ), new Integer( FUNCTION_TIME ) );
            _functionChoices.put( SimStrings.get( "DiscreteControlPanel.spaceAndTime" ), new Integer( FUNCTION_SPACE_AND_TIME ) );
            
            // Function combo box
            _functionComboBox = new ControlPanelComboBox( label, _functionChoices ); 
        }
        
        // Presets
        {
            // Label
            String label = SimStrings.get( "DiscreteControlPanel.selectFunction" );
            
            // Choices
            _presetChoices = new Hashtable();
            _presetChoices.put( SimStrings.get( "DiscreteControlPanel.sine" ), new Integer( PRESET_SINE ) );
            _presetChoices.put( SimStrings.get( "DiscreteControlPanel.sawtooth" ), new Integer( PRESET_SAWTOOTH ) );
            _presetChoices.put( SimStrings.get( "DiscreteControlPanel.triangle" ), new Integer( PRESET_TRIANGLE ) );
            _presetChoices.put( SimStrings.get( "DiscreteControlPanel.wavePacket" ), new Integer( PRESET_WAVE_PACKET ) );
            _presetChoices.put( SimStrings.get( "DiscreteControlPanel.custom" ), new Integer( PRESET_CUSTOM ) );
            
            // Presets combo box
            _presetsComboBox = new ControlPanelComboBox( label, _presetChoices ); 
        }
        
        // Show Wavelength
        JPanel showWavelengthPanel = new JPanel();
        {
            _showWavelengthCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showWavelength" ) );
        
            _showWavelengthTextField = new JTextField();
            _showWavelengthTextField.setColumns( 5 );
            _showWavelengthTextField.setEnabled( false );
            
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
            _showPeriodTextField.setEnabled( false );
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( showPeriodPanel );
            showPeriodPanel.setLayout( layout );
            layout.addAnchoredComponent( _showPeriodCheckBox, 0, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _showPeriodTextField, 0, 1, GridBagConstraints.WEST );
        } 
        
        // Number of harmonics
        {
            String format = SimStrings.get( "DiscreteControlPanel.numberOfComponents" );
            _numberOfComponentsSlider = new ControlPanelSlider( format );
            _numberOfComponentsSlider.setMaximum( 15 );
            _numberOfComponentsSlider.setMinimum( 5 );
            _numberOfComponentsSlider.setValue( 7 );
            _numberOfComponentsSlider.setMajorTickSpacing( 2 );
            _numberOfComponentsSlider.setMinorTickSpacing( 1 );
            _numberOfComponentsSlider.setSnapToTicks( true );
        }

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
        
        // Play Sound button
        _playSoundButton = new JButton( SimStrings.get( "DiscreteControlPanel.playSound" ) );
        
        // Wave Type
        {
            // Label
            String label = SimStrings.get( "DiscreteControlPanel.show" );
            
            // Choices
            _waveTypeChoices = new Hashtable();
            _waveTypeChoices.put( SimStrings.get( "DiscreteControlPanel.sines" ), new Integer( SineWaveGraphic.WAVE_TYPE_SINE ) );
            _waveTypeChoices.put( SimStrings.get( "DiscreteControlPanel.cosines" ), new Integer( SineWaveGraphic.WAVE_TYPE_COSINE ) );
            
            // Wave Type combo box
            _waveTypeComboBox = new ControlPanelComboBox( label, _waveTypeChoices ); 
        }
        
        // Math Mode
        _showMathCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showMath" ) );
        
        // Math Forms
        {
            String label = SimStrings.get( "DiscreteControlPanel.mathForm" );
            
            // Choices
            _timeMathFormChoices = new Hashtable();
            _timeMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.waveNumberForm" ), new Integer( MATH_FORM_WAVE_NUMBER ) );
            _timeMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.wavelengthForm" ), new Integer( MATH_FORM_WAVELENGTH ) );
            _timeMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.modeForm" ), new Integer( MATH_FORM_MODE ) );
            _spaceMathFormChoices = new Hashtable();
            _spaceMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.angularForm" ), new Integer( MATH_FORM_ANGULAR ) );
            _spaceMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.frequencyForm" ), new Integer( MATH_FORM_FREQUENCY ) );
            _spaceMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.periodForm" ), new Integer( MATH_FORM_PERIOD ) );
            _spaceMathFormChoices.put( SimStrings.get( "DiscreteControlPanel.modeForm" ), new Integer( MATH_FORM_MODE ) );
            
            // Math form combo box
            _mathFormComboBox = new ControlPanelComboBox( label, _spaceMathFormChoices );
            _mathFormComboBox.setVisible( false );
        }
        
        // Layout
        addFullWidth( _functionComboBox );
        addFullWidth( _presetsComboBox );
        addFullWidth( showWavelengthPanel );
        addFullWidth( showPeriodPanel );
        addFullWidth( _numberOfComponentsSlider );
        addFullWidth( _fundamentalFrequencySlider );
        add( _playSoundButton );
        addFullWidth( _waveTypeComboBox );
        add( _showMathCheckBox );
        add( _mathFormComboBox );

        // Wire up event handling.
        EventListener listener = new EventListener();
        _functionComboBox.getComboBox().addActionListener( listener );
        _presetsComboBox.getComboBox().addActionListener( listener );
        _showWavelengthCheckBox.addActionListener( listener );
        _showWavelengthTextField.addActionListener( listener );
        _showPeriodCheckBox.addActionListener( listener );
        _showPeriodTextField.addActionListener( listener );
        _numberOfComponentsSlider.addChangeListener( listener );
        _fundamentalFrequencySlider.addChangeListener( listener );
        _waveTypeComboBox.getComboBox().addActionListener( listener );
        _playSoundButton.addActionListener( listener );
        _showMathCheckBox.addActionListener( listener );
        _mathFormComboBox.getComboBox().addActionListener( listener );

        // Set the state of the controls.
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
        
        // Number of components
        _numberOfComponentsSlider.setValue( _fourierSeriesModel.getNumberOfComponents() );
        
        // Fundamental frequency
        _fundamentalFrequencySlider.setValue( (int) _fourierSeriesModel.getFundamentalFrequency() );
        
        // Wave Type
        {
            Object item = null;
            switch ( _componentsGraphic.getWaveType() ) {
            case SineWaveGraphic.WAVE_TYPE_SINE:
                item = SimStrings.get( "DiscreteControlPanel.sines" );
                break;
            case SineWaveGraphic.WAVE_TYPE_COSINE:
                item = SimStrings.get( "DiscreteControlPanel.cosines" );
                break;
            default:
            }
            assert ( item != null );
            _waveTypeComboBox.getComboBox().setSelectedItem( item );
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
            if ( event.getSource() == _functionComboBox.getComboBox() ) {
                Object key = _functionComboBox.getComboBox().getSelectedItem();
                System.out.println( "function " + key ); //XXX
                Object value = _functionChoices.get( key );
                assert( value != null && value instanceof Integer ); // programming error
                int functionType = ((Integer)value).intValue();
                if ( functionType == FUNCTION_SPACE || functionType == FUNCTION_SPACE_AND_TIME ) {
                    _mathFormComboBox.setChoices( _spaceMathFormChoices );
                }
                else {
                    _mathFormComboBox.setChoices( _timeMathFormChoices );
                }
            }
            else if ( event.getSource() == _presetsComboBox.getComboBox() ) {
                Object key = _presetsComboBox.getComboBox().getSelectedItem();
                System.out.println( "preset " + key ); //XXX
            }
            else if ( event.getSource() == _showWavelengthCheckBox ) {
                System.out.println( "showWavelengthCheckBox " + _showWavelengthCheckBox.isSelected() );//XXX
                _showWavelengthTextField.setEnabled( _showWavelengthCheckBox.isSelected() );
            }
            else if ( event.getSource() == _showWavelengthTextField ) {
                System.out.println( "showWavelengthTextfield " + _showWavelengthTextField.getText() );//XXX
            }
            else if ( event.getSource() == _showPeriodCheckBox ) {
                System.out.println( "showPeriodCheckBox " + _showPeriodCheckBox.isSelected() );//XXX
                _showPeriodTextField.setEnabled(  _showPeriodCheckBox.isSelected() );
            }
            else if ( event.getSource() == _showPeriodTextField ) {
                System.out.println( "showPeriodTextField " + _showPeriodTextField.getText() );//XXX
            }
            else if ( event.getSource() == _waveTypeComboBox.getComboBox() ) {
                // Use the selection to lookup the associated symbolic constant.
                Object key = _waveTypeComboBox.getComboBox().getSelectedItem();
                Object value = _waveTypeChoices.get( key );
                assert( value != null && value instanceof Integer ); // programming error
                int waveType = ((Integer)value).intValue();
                _componentsGraphic.setWaveType( waveType );
                _sumGraphic.setWaveType( waveType );
            }
            else if ( event.getSource() == _playSoundButton ) {
                System.out.println( "playSoundButton" ); //XXX
            }
            else if ( event.getSource() == _showMathCheckBox ) {
                System.out.println( "mathMode " + _showMathCheckBox.isSelected() ); //XXX
                _mathFormComboBox.setVisible( _showMathCheckBox.isSelected() );
            }
            else if ( event.getSource() == _mathFormComboBox.getComboBox() ) {
                Object key = _mathFormComboBox.getComboBox().getSelectedItem();
                System.out.println( "mathForm " + key ); //XXX
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
        
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _numberOfComponentsSlider ) {
                if ( !_numberOfComponentsSlider.getSlider().getValueIsAdjusting() ) {
                    int numberOfComponents = _numberOfComponentsSlider.getValue();
                    _fourierSeriesModel.setNumberOfComponents( numberOfComponents );
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
