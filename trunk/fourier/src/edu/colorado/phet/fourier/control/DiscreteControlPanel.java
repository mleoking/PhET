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
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.control.sliders.AbstractFourierSlider;
import edu.colorado.phet.fourier.control.sliders.DefaultFourierSlider;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.module.FourierModule;
import edu.colorado.phet.fourier.view.AmplitudeSlider;
import edu.colorado.phet.fourier.view.AnimationCycleController;
import edu.colorado.phet.fourier.view.DiscreteHarmonicsView;
import edu.colorado.phet.fourier.view.DiscreteSumView;
import edu.colorado.phet.fourier.view.tools.HarmonicPeriodDisplay;
import edu.colorado.phet.fourier.view.tools.HarmonicPeriodTool;
import edu.colorado.phet.fourier.view.tools.HarmonicWavelengthTool;


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
    private DiscreteHarmonicsView _harmonicsGraph;
    private DiscreteSumView _sumGraph;
    private HarmonicWavelengthTool _wavelengthTool;
    private HarmonicPeriodTool _periodTool;
    private HarmonicPeriodDisplay _periodDisplay;
    private AnimationCycleController _animationCycleController;

    // UI components
    private FourierComboBox _domainComboBox;
    private FourierComboBox _presetsComboBox;
    private JCheckBox _showInfiniteCheckBox;
    private JCheckBox _wavelengthToolCheckBox;
    private JComboBox _wavelengthToolComboBox;
    private JCheckBox _periodToolCheckBox;
    private JComboBox _periodToolComboBox;
    private FourierComboBox _waveTypeComboBox;
    private AbstractFourierSlider _numberOfHarmonicsSlider;
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
    
    private int _mathFormKeySpace;
    private int _mathFormKeyTime;
    private int _mathFormKeySpaceAndTime;
    private EventListener _eventListener;
    
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
            DiscreteHarmonicsView harmonicsGraph, 
            DiscreteSumView sumGraph,
            HarmonicWavelengthTool wavelengthTool,
            HarmonicPeriodTool periodTool,
            HarmonicPeriodDisplay periodDisplay,
            AnimationCycleController animationCycleController ) {
        
        super( module );
        
        assert ( fourierSeries != null );
        assert( harmonicsGraph != null );
        assert( sumGraph != null );
        assert( wavelengthTool != null );
        assert( periodTool != null );
        assert( periodDisplay != null );
        assert( animationCycleController != null );
        
        // Things we'll be controlling.
        _fourierSeries = fourierSeries;
        _harmonicsGraph = harmonicsGraph;
        _sumGraph = sumGraph;
        _wavelengthTool = wavelengthTool;
        _periodTool = periodTool;
        _periodDisplay = periodDisplay;
        _animationCycleController = animationCycleController;
        
        // Set the control panel's minimum width.
        String widthString = SimStrings.get( "DiscreteControlPanel.width" );
        int width = Integer.parseInt( widthString );
        setMinumumWidth( width );
        
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
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_TRIANGLE, SimStrings.get( "preset.triangle" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SQUARE, SimStrings.get( "preset.square" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( FourierConstants.PRESET_SAWTOOTH, SimStrings.get( "preset.sawtooth" ) ) );
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
            layout.addComponent( _presetsComboBox, row++, 0 );
            layout.addComponent( _showInfiniteCheckBox, row++, 0 );
            layout.addComponent( _domainComboBox, row++, 0 );
        }
        
        // Wave Properties panel
        JPanel wavePropertiesPanel = new JPanel();
        {
            //  Title
            Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, TITLED_BORDER_WIDTH );
            String title = SimStrings.get( "DiscreteControlPanel.title.waveProperties" );
            TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
            wavePropertiesPanel.setBorder( titleBorder );
            
            // Wavelength Tool
            JPanel wavelengthToolPanel = new JPanel();
            {
                _wavelengthToolCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showWavelength" ) );

                _wavelengthToolComboBox = new JComboBox();
                
                // Choices
                _showWavelengthChoices = new ArrayList();
                char wavelengthSymbol = MathStrings.C_WAVELENGTH;
                for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                    String choice = "<html>" + wavelengthSymbol + "<sub>" + ( i + 1 ) + "</sub></html>";
                    _showWavelengthChoices.add( choice );
                }

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( wavelengthToolPanel );
                wavelengthToolPanel.setLayout( layout );
                layout.addAnchoredComponent( _wavelengthToolCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _wavelengthToolComboBox, 0, 1, GridBagConstraints.WEST );
            }

            // Period Tool
            JPanel periodToolPanel = new JPanel();
            {
                _periodToolCheckBox = new JCheckBox( SimStrings.get( "DiscreteControlPanel.showPeriod" ) );

                _periodToolComboBox = new JComboBox();
                
                // Choices
                _showPeriodChoices = new ArrayList();
                char periodSymbol = MathStrings.C_PERIOD;
                for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                    String choice = "<html>" + periodSymbol + "<sub>" + ( i + 1 ) + "</sub></html>";
                    _showPeriodChoices.add( choice );
                }

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( periodToolPanel );
                periodToolPanel.setLayout( layout );
                layout.addAnchoredComponent( _periodToolCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _periodToolComboBox, 0, 1, GridBagConstraints.WEST );
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
                _numberOfHarmonicsSlider = new DefaultFourierSlider( format );
                _numberOfHarmonicsSlider.getSlider().setMaximum( FourierConfig.MAX_HARMONICS );
                _numberOfHarmonicsSlider.getSlider().setMinimum( FourierConfig.MIN_HARMONICS );
                _numberOfHarmonicsSlider.getSlider().setMajorTickSpacing( 2 );
                _numberOfHarmonicsSlider.getSlider().setMinorTickSpacing( 1 );
                _numberOfHarmonicsSlider.getSlider().setSnapToTicks( true );
                _numberOfHarmonicsSlider.getSlider().setPaintLabels( true );
                _numberOfHarmonicsSlider.getSlider().setPaintTicks( true );
            }
            
            // Layout
            EasyGridBagLayout layout = new EasyGridBagLayout( wavePropertiesPanel );
            wavePropertiesPanel.setLayout( layout );
            int row = 0;
            layout.addFilledComponent( _numberOfHarmonicsSlider, row++, 0, GridBagConstraints.HORIZONTAL );
            layout.addComponent( _waveTypeComboBox, row++, 0 );
            layout.addComponent( wavelengthToolPanel, row++, 0 );
            layout.addComponent( periodToolPanel, row++, 0 );
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
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVELENGTH, SimStrings.get( "mathForm.wavelength" ) ) );
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVE_NUMBER, SimStrings.get( "mathForm.waveNumber" ) ) );
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );

                    _timeMathFormChoices = new ArrayList();
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_PERIOD, SimStrings.get( "mathForm.period" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_ANGULAR_FREQUENCY, SimStrings.get( "mathForm.angularFrequency" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_FREQUENCY, SimStrings.get( "mathForm.frequency" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_MODE, SimStrings.get( "mathForm.mode" ) ) );

                    _spaceAndTimeMathFormChoices = new ArrayList();
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD, SimStrings.get( "mathForm.wavelengthAndPeriod" ) ) );
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY, SimStrings.get( "mathForm.waveNumberAndAngularFrequency" ) ) );
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
        addVerticalSpace( 5 );
        addFullWidth( wavePropertiesPanel );
        addVerticalSpace( 5 );
        addFullWidth( mathModePanel );

        // Dialogs
        Frame parentFrame = PhetApplication.instance().getPhetFrame();
        _expandSumDialog = new ExpandSumDialog( parentFrame, _fourierSeries );
        
        // Set the state of the controls.
        reset();
        
        // Wire up event handling (after setting state with reset).
        {
            _eventListener = new EventListener();
            // WindowListeners
            _expandSumDialog.addWindowListener( _eventListener );
            // ActionListeners
            _showInfiniteCheckBox.addActionListener( _eventListener );
            _wavelengthToolCheckBox.addActionListener( _eventListener );
            _periodToolCheckBox.addActionListener( _eventListener );
            _showMathCheckBox.addActionListener( _eventListener );
            _expandSumCheckBox.addActionListener( _eventListener );
            _expandSumDialog.getCloseButton().addActionListener( _eventListener );
            // ChangeListeners
            _numberOfHarmonicsSlider.addChangeListener( _eventListener );
            // ItemListeners
            _domainComboBox.addItemListener( _eventListener );
            _presetsComboBox.addItemListener( _eventListener );
            _waveTypeComboBox.addItemListener( _eventListener );
            _wavelengthToolComboBox.addItemListener( _eventListener );
            _periodToolComboBox.addItemListener( _eventListener );
            _mathFormComboBox.addItemListener( _eventListener );
        }    
    }

    public void cleanup() {
        _expandSumDialog.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // FourierControlPanel implementation
    //----------------------------------------------------------------------------
    
    public void reset() {
        
        // Domain
        _domainComboBox.setSelectedKey( FourierConstants.DOMAIN_SPACE );
        _animationCycleController.setEnabled( false );
        
        // Preset
        int preset = _fourierSeries.getPreset();
        _presetsComboBox.setSelectedKey( preset );
        
        // Show Infinite Number of Harmonics
        _showInfiniteCheckBox.setEnabled( true );
        _showInfiniteCheckBox.setForeground( Color.BLACK );
        _showInfiniteCheckBox.setSelected( false );
        _sumGraph.setPresetEnabled( _showInfiniteCheckBox.isSelected() );
        
        // Wavelength Tool
        _wavelengthToolCheckBox.setSelected( false );
        _wavelengthToolCheckBox.setEnabled( true );
        _wavelengthToolComboBox.setEnabled( _wavelengthToolCheckBox.isSelected() );
        _wavelengthToolComboBox.removeAllItems();
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _wavelengthToolComboBox.addItem( _showWavelengthChoices.get( i ) );
        }
        _wavelengthToolComboBox.setSelectedIndex( 0 );
        _wavelengthTool.setVisible( _wavelengthToolCheckBox.isSelected() );

        // Period Tool
        _periodToolCheckBox.setSelected( false );
        _periodToolCheckBox.setEnabled( false );
        _periodToolComboBox.setEnabled( _periodToolCheckBox.isSelected() );
        _periodToolComboBox.removeAllItems();
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _periodToolComboBox.addItem( _showPeriodChoices.get( i ) );
        }
        _periodToolComboBox.setSelectedIndex( 0 );
        _periodTool.setVisible( _periodToolCheckBox.isSelected() );
        
        // Wave Type
        int waveType = _fourierSeries.getWaveType();
        _waveTypeComboBox.setSelectedKey( waveType );
        
        // Number of harmonics
        _numberOfHarmonicsSlider.setValue( _fourierSeries.getNumberOfHarmonics() );
        
        // Math Mode
        {
            _mathFormKeySpace = FourierConstants.MATH_FORM_WAVELENGTH;
            _mathFormKeyTime = FourierConstants.MATH_FORM_PERIOD;
            _mathFormKeySpaceAndTime = FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD;
            _showMathCheckBox.setSelected( false );
            _mathFormComboBox.setChoices( _spaceMathFormChoices );
            _mathFormComboBox.setSelectedKey( _mathFormKeySpace );
            _mathFormComboBox.setEnabled( _showMathCheckBox.isSelected() );
            _expandSumCheckBox.setEnabled( _showMathCheckBox.isSelected() );
            _expandSumCheckBox.setSelected( false );
            _expandSumDialog.hide();
        }
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
            else if ( event.getSource() == _wavelengthToolCheckBox ) {
                handleWavelengthTool();
            }
            else if ( event.getSource() == _periodToolCheckBox ) {
                handlePeriodTool();
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
                if ( !_numberOfHarmonicsSlider.isAdjusting() ) {
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
                else if ( event.getSource() == _wavelengthToolComboBox ) {
                    handleWavelengthTool();
                }
                else if ( event.getSource() == _periodToolComboBox ) {
                    handlePeriodTool();
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
            _mathFormComboBox.removeItemListener( _eventListener );
            _mathFormComboBox.setChoices( _spaceMathFormChoices );
            _mathFormComboBox.addItemListener( _eventListener );
            _mathFormComboBox.setSelectedKey( _mathFormKeySpace );
            _wavelengthToolCheckBox.setEnabled( true );
            _wavelengthToolComboBox.setEnabled( _wavelengthToolCheckBox.isSelected() );
            _wavelengthTool.setVisible( _wavelengthToolCheckBox.isSelected() );
            _periodToolCheckBox.setEnabled( false );
            _periodToolComboBox.setEnabled( false );
            _periodTool.setVisible( false );
            _periodDisplay.setVisible( false );
            _animationCycleController.setEnabled( false );
            break;
        case FourierConstants.DOMAIN_TIME:
            _mathFormComboBox.removeItemListener( _eventListener );
            _mathFormComboBox.setChoices( _timeMathFormChoices );
            _mathFormComboBox.addItemListener( _eventListener );
            _mathFormComboBox.setSelectedKey( _mathFormKeyTime );
            _wavelengthToolCheckBox.setEnabled( false );
            _wavelengthToolComboBox.setEnabled( false );
            _wavelengthTool.setVisible( false );
            _periodToolCheckBox.setEnabled( true );
            _periodToolComboBox.setEnabled( _periodToolCheckBox.isSelected() );
            _periodTool.setVisible( _periodToolCheckBox.isSelected() );
            _periodDisplay.setVisible( false );
            _animationCycleController.setEnabled( false );
            break;
        case FourierConstants.DOMAIN_SPACE_AND_TIME:
            _mathFormComboBox.removeItemListener( _eventListener );
            _mathFormComboBox.setChoices( _spaceAndTimeMathFormChoices );
            _mathFormComboBox.addItemListener( _eventListener );
            _mathFormComboBox.setSelectedKey( _mathFormKeySpaceAndTime );
            _wavelengthToolCheckBox.setEnabled( true );
            _wavelengthToolComboBox.setEnabled( _wavelengthToolCheckBox.isSelected() );
            _wavelengthTool.setVisible( _wavelengthToolCheckBox.isSelected() );
            _periodToolCheckBox.setEnabled( true );
            _periodToolComboBox.setEnabled( _periodToolCheckBox.isSelected() );
            _periodTool.setVisible( false );
            _periodDisplay.setVisible( _periodToolCheckBox.isSelected() );
            _animationCycleController.reset();
            _animationCycleController.setEnabled( true );
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
        _animationCycleController.reset(); // do this first or preset animation will be out of sync!
        int waveType = _waveTypeComboBox.getSelectedKey();
        int preset = _presetsComboBox.getSelectedKey();
        if ( waveType == FourierConstants.WAVE_TYPE_COSINE && preset == FourierConstants.PRESET_SAWTOOTH ) {
            showSawtoothCosinesErrorDialog();
            _waveTypeComboBox.setSelectedKey( FourierConstants.WAVE_TYPE_SINE );
            _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        }
        boolean showInfiniteEnabled = 
            ( preset != FourierConstants.PRESET_WAVE_PACKET && preset != FourierConstants.PRESET_CUSTOM );
        _showInfiniteCheckBox.setEnabled( showInfiniteEnabled );
        _showInfiniteCheckBox.setForeground( showInfiniteEnabled ? Color.BLACK : Color.GRAY );
        if ( !showInfiniteEnabled ) {
            _showInfiniteCheckBox.setSelected( false );
            _sumGraph.setPresetEnabled( false );
        }
        _fourierSeries.setPreset( preset );
    }
    
    private void handleShowInfinite() {
        boolean enabled = _showInfiniteCheckBox.isSelected();
        _sumGraph.setPresetEnabled( enabled );
    }
    
    private void handleWavelengthTool() {
        _wavelengthToolComboBox.setEnabled( _wavelengthToolCheckBox.isEnabled() && _wavelengthToolCheckBox.isSelected() );
        _wavelengthTool.setVisible( _wavelengthToolCheckBox.isEnabled() && _wavelengthToolCheckBox.isSelected() );
        int harmonicOrder = _wavelengthToolComboBox.getSelectedIndex();
        if ( harmonicOrder >= 0 ) {
            Harmonic harmonic = _fourierSeries.getHarmonic( harmonicOrder );
            _wavelengthTool.setHarmonic( harmonic );
        }
    }
    
    private void handlePeriodTool() {
        
        _periodToolComboBox.setEnabled( _periodToolCheckBox.isEnabled() && _periodToolCheckBox.isSelected() );
        
        int domain = _domainComboBox.getSelectedKey();
        int harmonicOrder = _periodToolComboBox.getSelectedIndex();
        
        if ( domain == FourierConstants.DOMAIN_TIME ) {
            _periodTool.setVisible( _periodToolCheckBox.isEnabled() && _periodToolCheckBox.isSelected() );
        }
        else if ( domain == FourierConstants.DOMAIN_SPACE_AND_TIME ) {
            _periodDisplay.setVisible( _periodToolCheckBox.isEnabled() && _periodToolCheckBox.isSelected() );
        }
        
        if ( harmonicOrder >= 0 ) {
            Harmonic harmonic = _fourierSeries.getHarmonic( harmonicOrder );
            _periodTool.setHarmonic( harmonic );
            _periodDisplay.setHarmonic( harmonic );
        }
    }
    
    private void handleWaveType() {
        _animationCycleController.reset(); // do this first or preset animation will be out of sync!
        int waveType = _waveTypeComboBox.getSelectedKey();
        int preset = _presetsComboBox.getSelectedKey();
        if ( waveType == FourierConstants.WAVE_TYPE_COSINE && preset == FourierConstants.PRESET_SAWTOOTH ) {
            showSawtoothCosinesErrorDialog();
            _waveTypeComboBox.setSelectedKey( FourierConstants.WAVE_TYPE_SINE );
            _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        }
        else {
            _fourierSeries.setWaveType( waveType );
        }
    }
    
    private void handleNumberOfHarmonics() {
        
        setWaitCursorEnabled( true );
        
        _animationCycleController.reset(); // do this first or preset animation will be out of sync!
        
        int numberOfHarmonics = (int)_numberOfHarmonicsSlider.getValue();
        
        // Update the Fourier series.
        _fourierSeries.setNumberOfHarmonics( numberOfHarmonics );
        
        // Update the "Wavelength Tool" control.
        {
            // Remember the selection
            int selectedWavelengthIndex = _wavelengthToolComboBox.getSelectedIndex();
            
            // Repopulate the combo box
            _wavelengthToolComboBox.removeAllItems();
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                _wavelengthToolComboBox.addItem( _showWavelengthChoices.get( i ) );
            }
            
            if ( selectedWavelengthIndex < numberOfHarmonics ) {
                // Restore the selection
                _wavelengthToolComboBox.setSelectedIndex( selectedWavelengthIndex );
            }
            else {
                // The selection is no longer valid.
                _wavelengthToolCheckBox.setSelected( false );
                _wavelengthTool.setVisible( false );
            }
        }
        
        // Update the "Period Tool" control.
        {
            // Remember the selection
            int selectedPeriodIndex = _periodToolComboBox.getSelectedIndex();
            
            // Repopulate the combo box
            _periodToolComboBox.removeAllItems();
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                _periodToolComboBox.addItem( _showPeriodChoices.get( i ) );
            }
            
            if ( selectedPeriodIndex < numberOfHarmonics ) {
                // Restore the selection
                _periodToolComboBox.setSelectedIndex( selectedPeriodIndex );
            }
            else {
                // The selection is no longer valid.
                _periodToolCheckBox.setSelected( false );
                _periodTool.setVisible( false );
                _periodDisplay.setVisible( false );
            }
        }
        
        setWaitCursorEnabled( false );
    }

    private void handleShowMath() {
        boolean isSelected = _showMathCheckBox.isSelected();
        _mathFormComboBox.setEnabled( isSelected );
        _expandSumCheckBox.setEnabled( isSelected );
        _harmonicsGraph.setMathEnabled( isSelected );
        _sumGraph.setMathEnabled( isSelected );
        if ( ! isSelected ) {
            _expandSumDialog.hide();
            _expandSumCheckBox.setSelected( false );
        }
    }
    
    private void handleMathForm() {
        int domain = _domainComboBox.getSelectedKey();
        int mathForm = _mathFormComboBox.getSelectedKey();
        _harmonicsGraph.setDomainAndMathForm( domain, mathForm );
        _sumGraph.setDomainAndMathForm( domain, mathForm );
        _expandSumDialog.setDomainAndMathForm( domain, mathForm );
        if ( domain == FourierConstants.DOMAIN_SPACE ) {
            _mathFormKeySpace = mathForm;
        }
        else if ( domain == FourierConstants.DOMAIN_TIME ) {
            _mathFormKeyTime = mathForm;
        }
        else {
            _mathFormKeySpaceAndTime = mathForm;
        }
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
