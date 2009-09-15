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

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.control.sliders.AbstractFourierSlider;
import edu.colorado.phet.fourier.control.sliders.DefaultFourierSlider;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.event.SoundErrorEvent;
import edu.colorado.phet.fourier.event.SoundErrorListener;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.module.FourierAbstractModule;
import edu.colorado.phet.fourier.sound.FourierSoundPlayer;
import edu.colorado.phet.fourier.view.AmplitudeSlider;
import edu.colorado.phet.fourier.view.AnimationCycleController;
import edu.colorado.phet.fourier.view.discrete.DiscreteHarmonicsView;
import edu.colorado.phet.fourier.view.discrete.DiscreteSumView;
import edu.colorado.phet.fourier.view.tools.HarmonicPeriodDisplay;
import edu.colorado.phet.fourier.view.tools.HarmonicPeriodTool;
import edu.colorado.phet.fourier.view.tools.HarmonicWavelengthTool;


/**
 * DiscreteControlPanel is the control panel for the "Discrete" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteControlPanel extends FourierAbstractControlPanel implements ChangeListener, SoundErrorListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layout parameters
    private static final int LEFT_MARGIN = 25; // pixels
    private static final int MATH_MODE_LEFT_MARGIN = 10; // pixels
    private static final int SUBPANEL_SPACING = 10; // pixels

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Things to be controlled.
    private FourierSeries _fourierSeries;
    private DiscreteHarmonicsView _harmonicsView;
    private DiscreteSumView _sumView;
    private HarmonicWavelengthTool _wavelengthTool;
    private HarmonicPeriodTool _periodTool;
    private HarmonicPeriodDisplay _periodDisplay;
    private AnimationCycleController _animationCycleController;
    private FourierSoundPlayer _soundPlayer;

    // UI components
    private FourierComboBox _domainComboBox;
    private FourierComboBox _presetsComboBox;
    private JCheckBox _showInfiniteCheckBox;
    private JCheckBox _wavelengthToolCheckBox;
    private JComboBox _wavelengthToolComboBox;
    private JCheckBox _periodToolCheckBox;
    private JComboBox _periodToolComboBox;
    private JRadioButton _sinesRadioButton;
    private JRadioButton _cosinesRadioButton;
    private AbstractFourierSlider _numberOfHarmonicsSlider;
    private JCheckBox _showMathCheckBox;
    private FourierComboBox _mathFormComboBox;
    private JCheckBox _expandSumCheckBox;
    private JCheckBox _soundCheckBox;
    private JSlider _soundSlider;
    private ExpandSumDialog _expandSumDialog;

    // Choices
    private ArrayList _domainChoices;
    private ArrayList _presetChoices;
    private ArrayList _showWavelengthChoices;
    private ArrayList _showPeriodChoices;
    private ArrayList _spaceMathFormChoices;
    private ArrayList _timeMathFormChoices;
    private ArrayList _spaceAndTimeMathFormChoices;

    private MathForm _mathFormKeySpace;
    private MathForm _mathFormKeyTime;
    private MathForm _mathFormKeySpaceAndTime;
    private EventListener _eventListener;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param fourierSeries
     */
    public DiscreteControlPanel( FourierAbstractModule module, FourierSeries fourierSeries, DiscreteHarmonicsView harmonicsGraph, DiscreteSumView sumGraph, HarmonicWavelengthTool wavelengthTool, HarmonicPeriodTool periodTool, HarmonicPeriodDisplay periodDisplay, AnimationCycleController animationCycleController ) {

        super( module );

        assert ( fourierSeries != null );
        assert ( harmonicsGraph != null );
        assert ( sumGraph != null );
        assert ( wavelengthTool != null );
        assert ( periodTool != null );
        assert ( periodDisplay != null );
        assert ( animationCycleController != null );

        // Things we'll be controlling.
        _fourierSeries = fourierSeries;
        _harmonicsView = harmonicsGraph;
        _sumView = sumGraph;
        _wavelengthTool = wavelengthTool;
        _periodTool = periodTool;
        _periodDisplay = periodDisplay;
        _animationCycleController = animationCycleController;

        // Set the control panel's minimum width.
        int width = FourierResources.getInt( "DiscreteControlPanel.width",275 );
        setMinimumWidth( width );

        // Preset Controls panel
        FourierTitledPanel presetControlsPanel = new FourierTitledPanel( FourierResources.getString( "DiscreteControlPanel.presetControls" ) );
        {
            // Presets
            {
                // Label
                String label = FourierResources.getString( "DiscreteControlPanel.presets" );

                // Choices
                _presetChoices = new ArrayList();
                _presetChoices.add( new FourierComboBox.Choice( Preset.SINE_COSINE, FourierResources.getString( "preset.sinecosine" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.TRIANGLE, FourierResources.getString( "preset.triangle" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.SQUARE, FourierResources.getString( "preset.square" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.SAWTOOTH, FourierResources.getString( "preset.sawtooth" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.WAVE_PACKET, FourierResources.getString( "preset.wavePacket" ) ) );
                _presetChoices.add( new FourierComboBox.Choice( Preset.CUSTOM, FourierResources.getString( "preset.custom" ) ) );

                // Presets combo box
                _presetsComboBox = new FourierComboBox( label, _presetChoices );
            }

            // Number of harmonics
            {
                String format = FourierResources.getString( "DiscreteControlPanel.numberOfHarmonics" );
                _numberOfHarmonicsSlider = new DefaultFourierSlider( format );
                _numberOfHarmonicsSlider.getSlider().setMaximum( FourierConstants.MAX_HARMONICS );
                _numberOfHarmonicsSlider.getSlider().setMinimum( FourierConstants.MIN_HARMONICS );
                _numberOfHarmonicsSlider.getSlider().setMajorTickSpacing( 2 );
                _numberOfHarmonicsSlider.getSlider().setMinorTickSpacing( 1 );
                _numberOfHarmonicsSlider.getSlider().setSnapToTicks( true );
                _numberOfHarmonicsSlider.getSlider().setPaintLabels( true );
                _numberOfHarmonicsSlider.getSlider().setPaintTicks( true );
            }

            // Show infinite...
            _showInfiniteCheckBox = new JCheckBox( FourierResources.getString( "DiscreteControlPanel.showInfinite" ) );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _presetsComboBox, row++, column );
            layout.addComponent( _numberOfHarmonicsSlider, row++, column );
            layout.addComponent( _showInfiniteCheckBox, row++, column );
            presetControlsPanel.setLayout( new BorderLayout() );
            presetControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Graph Controls panel
        FourierTitledPanel graphControlsPanel = new FourierTitledPanel( FourierResources.getString( "DiscreteControlPanel.graphControls" ) );
        {
            // Domain
            {
                // Label
                String label = FourierResources.getString( "DiscreteControlPanel.domain" );

                // Choices
                _domainChoices = new ArrayList();
                _domainChoices.add( new FourierComboBox.Choice( Domain.SPACE, FourierResources.getString( "domain.space" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( Domain.TIME, FourierResources.getString( "domain.time" ) ) );
                _domainChoices.add( new FourierComboBox.Choice( Domain.SPACE_AND_TIME, FourierResources.getString( "domain.spaceAndTime" ) ) );

                // Function combo box
                _domainComboBox = new FourierComboBox( label, _domainChoices );
            }

            // Wave Type
            JPanel waveTypePanel = new JPanel();
            {
                // Radio buttons
                _sinesRadioButton = new JRadioButton( FourierResources.getString( "waveType.sines" ) );
                _cosinesRadioButton = new JRadioButton( FourierResources.getString( "waveType.cosines" ) );
                ButtonGroup group = new ButtonGroup();
                group.add( _sinesRadioButton );
                group.add( _cosinesRadioButton );

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( waveTypePanel );
                waveTypePanel.setLayout( layout );
                layout.setInsets( DEFAULT_INSETS );
                layout.addComponent( _sinesRadioButton, 0, 0 );
                layout.addComponent( _cosinesRadioButton, 0, 1 );
            }

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _domainComboBox, row++, column );
            layout.addComponent( waveTypePanel, row++, column );
            graphControlsPanel.setLayout( new BorderLayout() );
            graphControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Tool Controls panel
        FourierTitledPanel toolControlsPanel = new FourierTitledPanel( FourierResources.getString( "DiscreteControlPanel.toolControls" ) );
        {
            // Wavelength Tool
            JPanel wavelengthToolPanel = new JPanel();
            {
                _wavelengthToolCheckBox = new JCheckBox( FourierResources.getString( "DiscreteControlPanel.wavelengthTool" ) );

                _wavelengthToolComboBox = new JComboBox();

                // Choices
                _showWavelengthChoices = new ArrayList();
                char wavelengthSymbol = MathStrings.C_WAVELENGTH;
                for ( int i = 0; i < FourierConstants.MAX_HARMONICS; i++ ) {
                    String choice = "<html>" + wavelengthSymbol + "<sub>" + ( i + 1 ) + "</sub></html>";
                    _showWavelengthChoices.add( choice );
                }

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( wavelengthToolPanel );
                wavelengthToolPanel.setLayout( layout );
                layout.setInsets( DEFAULT_INSETS );
                layout.addAnchoredComponent( _wavelengthToolCheckBox, 0, 0, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _wavelengthToolComboBox, 0, 1, GridBagConstraints.WEST );
            }

            // Period Tool
            JPanel periodToolPanel = new JPanel();
            {
                _periodToolCheckBox = new JCheckBox( FourierResources.getString( "DiscreteControlPanel.periodTool" ) );

                _periodToolComboBox = new JComboBox();

                // Choices
                _showPeriodChoices = new ArrayList();
                char periodSymbol = MathStrings.C_PERIOD;
                for ( int i = 0; i < FourierConstants.MAX_HARMONICS; i++ ) {
                    String choice = "<html>" + periodSymbol + "<sub>" + ( i + 1 ) + "</sub></html>";
                    _showPeriodChoices.add( choice );
                }

                // Layout
                EasyGridBagLayout layout = new EasyGridBagLayout( periodToolPanel );
                periodToolPanel.setLayout( layout );
                layout.setInsets( DEFAULT_INSETS );
                // line up the combo box with the wavelength tool combo box
                layout.setMinimumWidth( 0, (int) _wavelengthToolCheckBox.getPreferredSize().getWidth() + 2 );
                layout.addAnchoredComponent( _periodToolCheckBox, 0, 0, GridBagConstraints.WEST );
                layout.addAnchoredComponent( _periodToolComboBox, 0, 1, GridBagConstraints.WEST );
            }

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( wavelengthToolPanel, row++, column );
            layout.addComponent( periodToolPanel, row++, column );
            toolControlsPanel.setLayout( new BorderLayout() );
            toolControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Math Mode panel
        FourierTitledPanel mathModePanel = new FourierTitledPanel( FourierResources.getString( "DiscreteControlPanel.mathMode" ) );
        {
            // Show Math
            _showMathCheckBox = new JCheckBox( FourierResources.getString( "DiscreteControlPanel.showMath" ) );

            // Math Forms
            {
                // Choices
                {
                    _spaceMathFormChoices = new ArrayList();
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( MathForm.WAVELENGTH, FourierResources.getString( "mathForm.wavelength" ) ) );
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( MathForm.WAVE_NUMBER, FourierResources.getString( "mathForm.waveNumber" ) ) );
                    _spaceMathFormChoices.add( new FourierComboBox.Choice( MathForm.MODE, FourierResources.getString( "mathForm.mode" ) ) );

                    _timeMathFormChoices = new ArrayList();
                    _timeMathFormChoices.add( new FourierComboBox.Choice( MathForm.FREQUENCY, FourierResources.getString( "mathForm.frequency" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( MathForm.PERIOD, FourierResources.getString( "mathForm.period" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( MathForm.ANGULAR_FREQUENCY, FourierResources.getString( "mathForm.angularFrequency" ) ) );
                    _timeMathFormChoices.add( new FourierComboBox.Choice( MathForm.MODE, FourierResources.getString( "mathForm.mode" ) ) );

                    _spaceAndTimeMathFormChoices = new ArrayList();
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( MathForm.WAVELENGTH_AND_PERIOD, FourierResources.getString( "mathForm.wavelengthAndPeriod" ) ) );
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( MathForm.WAVE_NUMBER_AND_ANGULAR_FREQUENCY, FourierResources.getString( "mathForm.waveNumberAndAngularFrequency" ) ) );
                    _spaceAndTimeMathFormChoices.add( new FourierComboBox.Choice( MathForm.MODE, FourierResources.getString( "mathForm.mode" ) ) );
                }

                // Math form combo box
                _mathFormComboBox = new FourierComboBox( "", _spaceMathFormChoices );
            }

            // Expand Sum
            _expandSumCheckBox = new JCheckBox( FourierResources.getString( "DiscreteControlPanel.expandSum" ) );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 0, MATH_MODE_LEFT_MARGIN );
            int row = 0;
            int column = 1;
            layout.addComponent( _showMathCheckBox, row++, column );
            layout.addComponent( _mathFormComboBox, row++, column );
            layout.addComponent( _expandSumCheckBox, row++, column );
            mathModePanel.setLayout( new BorderLayout() );
            mathModePanel.add( innerPanel, BorderLayout.WEST );
        }

        FourierTitledPanel audioControlsPanel = new FourierTitledPanel( FourierResources.getString( "DiscreteControlPanel.audioControls" ) );
        {
            // Sound on/off
            _soundCheckBox = new JCheckBox( FourierResources.getString( "DiscreteControlPanel.sound" ) );

            // Min & max icon labels
            ImageIcon soundMinIcon = new ImageIcon( FourierConstants.SOUND_MIN_IMAGE );
            JLabel soundMinLabel = new JLabel( soundMinIcon );
            ImageIcon soundMaxIcon = new ImageIcon( FourierConstants.SOUND_MAX_IMAGE );
            JLabel soundMaxLabel = new JLabel( soundMaxIcon );

            // Sound volume
            _soundSlider = new JSlider();
            _soundSlider.setMaximum( 100 );
            _soundSlider.setMinimum( 0 );
            _soundSlider.setValue( 50 );
            _soundSlider.setPreferredSize( new Dimension( 125, _soundSlider.getPreferredSize().height ) );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( DEFAULT_INSETS );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setMinimumWidth( 1, 10 );
            layout.addComponent( _soundCheckBox, 0, 0 ); // row, column
            layout.addComponent( soundMinLabel, 0, 2 );
            layout.addComponent( _soundSlider, 0, 3 );
            layout.addComponent( soundMaxLabel, 0, 4 );
            audioControlsPanel.setLayout( new BorderLayout() );
            audioControlsPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Layout
        addControlFullWidth( presetControlsPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addControlFullWidth( graphControlsPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addControlFullWidth( toolControlsPanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addControlFullWidth( mathModePanel );
        addVerticalSpace( SUBPANEL_SPACING );
        addControlFullWidth( audioControlsPanel );

        // Dialogs
        Frame parentFrame = PhetApplication.getInstance().getPhetFrame();
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
            _soundCheckBox.addActionListener( _eventListener );
            _sinesRadioButton.addActionListener( _eventListener );
            _cosinesRadioButton.addActionListener( _eventListener );
            // ChangeListeners
            _numberOfHarmonicsSlider.addChangeListener( _eventListener );
            _soundSlider.addChangeListener( _eventListener );
            // ItemListeners
            _domainComboBox.addItemListener( _eventListener );
            _presetsComboBox.addItemListener( _eventListener );
            _wavelengthToolComboBox.addItemListener( _eventListener );
            _periodToolComboBox.addItemListener( _eventListener );
            _mathFormComboBox.addItemListener( _eventListener );
        }
    }

    public void cleanup() {
        _expandSumDialog.cleanup();
        if ( _soundPlayer != null ) {
            _soundPlayer.cleanup();
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setPreset( Preset preset ) {
        _presetsComboBox.setSelectedKey( preset );
        handlePreset();
    }

    public Preset getPreset() {
        return (Preset) _presetsComboBox.getSelectedKey();
    }

    public void setNumberOfHarmonics( int n ) {
        _numberOfHarmonicsSlider.setValue( n );
        handleNumberOfHarmonics();
    }

    public int getNumberOfHarmonics() {
        return (int) _numberOfHarmonicsSlider.getValue();
    }

    public void setShowInfiniteEnabled( boolean enabled ) {
        _showInfiniteCheckBox.setSelected( enabled );
        handleShowInfinite();
    }

    public boolean isShowInfiniteEnabled() {
        return _showInfiniteCheckBox.isSelected();
    }

    public void setDomain( Domain domain ) {
        _domainComboBox.setSelectedKey( domain );
        handleDomain();
    }

    public Domain getDomain() {
        return (Domain) _domainComboBox.getSelectedKey();
    }

    public void setWaveType( WaveType waveType ) {
        if ( waveType == WaveType.SINES ) {
            _sinesRadioButton.setSelected( true );
        }
        else {
            _cosinesRadioButton.setSelected( true );
        }
        handleWaveType();
    }

    public WaveType getWaveType() {
        WaveType waveType = null;
        if ( _sinesRadioButton.isSelected() ) {
            waveType = WaveType.SINES;
        }
        else {
            waveType = WaveType.COSINES;
        }
        return waveType;
    }

    public void setWavelengthToolEnabled( boolean enabled ) {
        _wavelengthToolCheckBox.setSelected( enabled );
        handleWavelengthTool();
    }

    public boolean isWavelengthToolEnabled() {
        return _wavelengthToolCheckBox.isSelected();
    }

    public void setPeriodToolEnabled( boolean enabled ) {
        _periodToolCheckBox.setSelected( enabled );
        handlePeriodTool();
    }

    public boolean isPeriodToolEnabled() {
        return _periodToolCheckBox.isSelected();
    }

    public void setShowMathEnabled( boolean enabled ) {
        _showMathCheckBox.setSelected( enabled );
        handleShowMath();
    }

    public boolean isShowMathEnabled() {
        return _showMathCheckBox.isSelected();
    }

    public void setMathForm( MathForm mathForm ) {
        _mathFormComboBox.setSelectedKey( mathForm );
        handleMathForm();
    }

    public MathForm getMathForm() {
        return (MathForm) _mathFormComboBox.getSelectedKey();
    }

    public void setExpandSumEnabled( boolean enabled ) {
        _expandSumCheckBox.setSelected( enabled );
        handleExpandSum();
    }

    public boolean isExpandSumEnabled() {
        return _expandSumCheckBox.isSelected();
    }

    /**
     * Turns sound on and off, but does not initialize sound.
     * <p>
     * We only want to initialize sound when the user attempts
     * to use the feature by clicking on the "Sound" check box.
     *
     * @param enabled true or false
     */
    public void setSoundEnabled( boolean enabled ) {
        _soundCheckBox.removeActionListener( _eventListener );
        _soundCheckBox.setSelected( enabled );
        _soundCheckBox.addActionListener( _eventListener );
        handleSoundOnOff( false /* do not initialize */ );
    }

    /**
     * Determines whether sound is on or off.
     *
     * @return true or false
     */
    public boolean isSoundEnabled() {
        return _soundCheckBox.isSelected();
    }

    public void setSoundVolume( float volume ) {
        _soundSlider.setValue( (int) ( volume * 100F ) );
        handleSoundVolume();
    }

    public float getSoundVolume() {
        return _soundSlider.getValue() / 100F;
    }

    //----------------------------------------------------------------------------
    // FourierControlPanel implementation
    //----------------------------------------------------------------------------

    public void reset() {

        // Domain
        _domainComboBox.setSelectedKey( Domain.SPACE );
        _animationCycleController.setEnabled( false );

        // Preset
        Preset preset = (Preset) _fourierSeries.getPreset();
        _presetsComboBox.setSelectedKey( preset );

        // Show Infinite Number of Harmonics
        _showInfiniteCheckBox.setEnabled( true );
        _showInfiniteCheckBox.setForeground( Color.BLACK );
        _showInfiniteCheckBox.setSelected( false );
        _sumView.setPresetEnabled( _showInfiniteCheckBox.isSelected() );

        // Wavelength Tool
        _wavelengthToolCheckBox.setSelected( false );
        _wavelengthToolCheckBox.setEnabled( _domainComboBox.getSelectedKey() == Domain.SPACE || _domainComboBox.getSelectedKey() == Domain.SPACE_AND_TIME );
        _wavelengthToolComboBox.setEnabled( _wavelengthToolCheckBox.isSelected() );
        _wavelengthToolComboBox.removeAllItems();
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _wavelengthToolComboBox.addItem( _showWavelengthChoices.get( i ) );
        }
        _wavelengthToolComboBox.setSelectedIndex( 0 );
        _wavelengthTool.setVisible( _wavelengthToolCheckBox.isSelected() );

        // Period Tool
        _periodToolCheckBox.setSelected( false );
        _periodToolCheckBox.setEnabled( _domainComboBox.getSelectedKey() == Domain.TIME || _domainComboBox.getSelectedKey() == Domain.SPACE_AND_TIME );
        _periodToolComboBox.setEnabled( _periodToolCheckBox.isSelected() );
        _periodToolComboBox.removeAllItems();
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            _periodToolComboBox.addItem( _showPeriodChoices.get( i ) );
        }
        _periodToolComboBox.setSelectedIndex( 0 );
        _periodTool.setVisible( _periodToolCheckBox.isSelected() );

        // Wave Type
        WaveType waveType = _fourierSeries.getWaveType();
        _sinesRadioButton.setSelected( waveType == WaveType.SINES );

        // Number of harmonics
        _numberOfHarmonicsSlider.setValue( _fourierSeries.getNumberOfHarmonics() );

        // Math Mode
        {
            _showMathCheckBox.setSelected( false );
            _mathFormComboBox.setEnabled( _showMathCheckBox.isSelected() );

            _mathFormKeySpace = MathForm.WAVELENGTH;
            _mathFormKeyTime = MathForm.FREQUENCY;
            _mathFormKeySpaceAndTime = MathForm.WAVELENGTH_AND_PERIOD;
            if ( _domainComboBox.getSelectedKey() == Domain.SPACE ) {
                _mathFormComboBox.setChoices( _spaceMathFormChoices );
                _mathFormComboBox.setSelectedKey( _mathFormKeySpace );
            }
            else if ( _domainComboBox.getSelectedKey() == Domain.TIME ) {
                _mathFormComboBox.setChoices( _timeMathFormChoices );
                _mathFormComboBox.setSelectedKey( _mathFormKeyTime );
            }
            else {
                _mathFormComboBox.setChoices( _spaceAndTimeMathFormChoices );
                _mathFormComboBox.setSelectedKey( _mathFormKeySpaceAndTime );
            }

            _expandSumCheckBox.setEnabled( _showMathCheckBox.isSelected() );
            _expandSumCheckBox.setSelected( false );
            _expandSumDialog.hide();
        }

        // Sound
        if ( _soundPlayer != null ) {
            _soundCheckBox.setSelected( false );
            _soundPlayer.setSoundEnabled( _soundCheckBox.isSelected() );
            _soundSlider.setValue( (int)( _soundPlayer.getVolume() * 100 ) );
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
            else if ( event.getSource() == _soundCheckBox ) {
                handleSoundOnOff( true /* initialize sound if needed */ );
            }
            else if ( event.getSource() == _sinesRadioButton || event.getSource() == _cosinesRadioButton ) {
                handleWaveType();
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
            else if ( event.getSource() == _soundSlider ) {
                handleSoundVolume();
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
        Domain domain = (Domain) _domainComboBox.getSelectedKey();

        if ( domain == Domain.SPACE ) {
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
        }
        else if ( domain == Domain.TIME ) {
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
        }
        else if ( domain == Domain.SPACE_AND_TIME ) {
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
        }
        else {
            throw new IllegalArgumentException( "unsupported domain: " + domain );
        }

        MathForm mathForm = (MathForm) _mathFormComboBox.getSelectedKey(); // get this after setting stuff above
        _sumView.setDomainAndMathForm( domain, mathForm );
        _harmonicsView.setDomainAndMathForm( domain, mathForm );
        _expandSumDialog.setDomainAndMathForm( domain, mathForm );
    }

    private void handlePreset() {
        _animationCycleController.reset(); // do this first or preset animation will be out of sync!
        Preset preset = (Preset) _presetsComboBox.getSelectedKey();
        if ( _cosinesRadioButton.isSelected() && preset == Preset.SAWTOOTH ) {
            showSawtoothCosinesErrorDialog();
            _sinesRadioButton.setSelected( true );
            _fourierSeries.setWaveType( WaveType.SINES );
        }
        boolean showInfiniteEnabled = ( preset != Preset.WAVE_PACKET && preset != Preset.CUSTOM );
        _showInfiniteCheckBox.setEnabled( showInfiniteEnabled );
        _showInfiniteCheckBox.setForeground( showInfiniteEnabled ? Color.BLACK : Color.GRAY );
        if ( !showInfiniteEnabled ) {
            _showInfiniteCheckBox.setSelected( false );
            _sumView.setPresetEnabled( false );
        }
        _fourierSeries.setPreset( preset );
    }

    private void handleShowInfinite() {
        boolean enabled = _showInfiniteCheckBox.isSelected();
        _sumView.setPresetEnabled( enabled );
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

        Domain domain = (Domain) _domainComboBox.getSelectedKey();
        int harmonicOrder = _periodToolComboBox.getSelectedIndex();

        if ( domain == Domain.TIME ) {
            _periodTool.setVisible( _periodToolCheckBox.isEnabled() && _periodToolCheckBox.isSelected() );
        }
        else if ( domain == Domain.SPACE_AND_TIME ) {
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
        Preset preset = (Preset) _presetsComboBox.getSelectedKey();
        if ( _cosinesRadioButton.isSelected() && preset == Preset.SAWTOOTH ) {
            showSawtoothCosinesErrorDialog();
            _sinesRadioButton.setSelected( true );
            _fourierSeries.setWaveType( WaveType.SINES );
        }
        else {
            WaveType waveType = ( _sinesRadioButton.isSelected() ? WaveType.SINES : WaveType.COSINES );
            _fourierSeries.setWaveType( waveType );
        }
    }

    private void handleNumberOfHarmonics() {

        setWaitCursorEnabled( true );

        _animationCycleController.reset(); // do this first or preset animation will be out of sync!

        int numberOfHarmonics = (int) _numberOfHarmonicsSlider.getValue();

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
        _harmonicsView.setMathEnabled( isSelected );
        _sumView.setMathEnabled( isSelected );
        if ( !isSelected ) {
            _expandSumDialog.hide();
            _expandSumCheckBox.setSelected( false );
        }
    }

    private void handleMathForm() {
        Domain domain = (Domain) _domainComboBox.getSelectedKey();
        MathForm mathForm = (MathForm) _mathFormComboBox.getSelectedKey();
        _harmonicsView.setDomainAndMathForm( domain, mathForm );
        _sumView.setDomainAndMathForm( domain, mathForm );
        _expandSumDialog.setDomainAndMathForm( domain, mathForm );
        if ( domain == Domain.SPACE ) {
            _mathFormKeySpace = mathForm;
        }
        else if ( domain == Domain.TIME ) {
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

    private void handleSoundOnOff( boolean initialize ) {
        if ( _soundCheckBox.isEnabled() ) {
            if ( _soundPlayer == null && initialize ) {
                initializeSound();
            }
            if ( _soundPlayer != null ) {
                _soundPlayer.setSoundEnabled( _soundCheckBox.isSelected() );
            }
        }
    }

    private void handleSoundVolume() {
        if ( _soundPlayer != null ) {
            _soundPlayer.setVolume( _soundSlider.getValue() / 100F );
        }
    }

    /*
     * Displays a modal error dialog if the user attempts to select
     * sawtooth preset and cosines wave type.
     * You can't make a sawtooth wave out of cosines because it is asymmetric.
     */
    private void showSawtoothCosinesErrorDialog() {
        String message = FourierResources.getString( "SawtoothCosinesErrorDialog.message" );
        JOptionPane op = new JOptionPane( message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION );
        op.createDialog( this, null ).show();
    }

    //----------------------------------------------------------------------------
    // Sound
    //----------------------------------------------------------------------------

    /*
     * Initializes the sound features.
     */
    private void initializeSound() {
        if ( _soundPlayer == null ) {
            try {
                _soundPlayer = new FourierSoundPlayer( _fourierSeries );
            }
            catch ( Exception e ) {
                _soundPlayer = null;
                _soundCheckBox.setEnabled( false );
                _soundSlider.setEnabled( false );
                String message = FourierResources.getString( "sound.error.init" );
                handleSoundError( message, e );
            }
            _soundPlayer.addSoundErrorListener( this );
        }
    }

    /*
     * Is sound initialized?
     *
     * @return true or false
     */
    private boolean isSoundInitialized() {
        return ( _soundPlayer != null );
    }

    /*
     * Handles any type of error related to sound.
     */
    private void handleSoundError( String message, Exception exception ) {
        // Display the message in an Error dialog
        String title = FourierResources.getString( "sound.error.title" );
        JOptionPane.showMessageDialog( this, message, title, JOptionPane.ERROR_MESSAGE );
        // Disable the UI control for sound
        _soundCheckBox.setSelected( false );
        _soundCheckBox.setEnabled( false );
        // Disable the sound player
        if ( _soundPlayer != null ) {
            _soundPlayer.setSoundEnabled( false );
            _soundPlayer.cleanup();
            _soundPlayer = null;
        }
        // Print a stack trace of the exception
        exception.printStackTrace();
    }

    //----------------------------------------------------------------------------
    // SoundErrorListener implementation
    //----------------------------------------------------------------------------

    /**
     * Handles notification of a sound error.
     *
     * @param event a SoundErrorEvent
     */
    public void soundErrorOccurred( SoundErrorEvent event ) {
        handleSoundError( event.getMessage(), event.getException() );
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
            _presetsComboBox.setSelectedKey( Preset.CUSTOM );
        }
    }
}
