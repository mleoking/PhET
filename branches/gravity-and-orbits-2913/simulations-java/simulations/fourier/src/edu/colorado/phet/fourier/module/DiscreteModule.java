// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel3;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.help.HelpBubble;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.persistence.FourierConfig;
import edu.colorado.phet.fourier.view.AnimationCycleController;
import edu.colorado.phet.fourier.view.MinimizedView;
import edu.colorado.phet.fourier.view.discrete.DiscreteAmplitudesView;
import edu.colorado.phet.fourier.view.discrete.DiscreteHarmonicsView;
import edu.colorado.phet.fourier.view.discrete.DiscreteSumView;
import edu.colorado.phet.fourier.view.tools.HarmonicPeriodDisplay;
import edu.colorado.phet.fourier.view.tools.HarmonicPeriodTool;
import edu.colorado.phet.fourier.view.tools.HarmonicWavelengthTool;


/**
 * DiscreteModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteModule extends FourierAbstractModule implements ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double AMPLITUDES_LAYER = 1;
    private static final double HARMONICS_LAYER = 2;
    private static final double SUM_LAYER = 3;
    private static final double HARMONICS_CLOSED_LAYER = 4;
    private static final double SUM_CLOSED_LAYER = 5;
    private static final double TOOLS_LAYER = 6;

    // Locations
    private static final Point WAVELENGTH_TOOL_LOCATION = new Point( 440, 236 );
    private static final Point PERIOD_TOOL_LOCATION = WAVELENGTH_TOOL_LOCATION;
    private static final Point PERIOD_DISPLAY_LOCATION = new Point( 655, 355 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 115, 153 );

    // Colors
    private static final Color APPARATUS_PANEL_BACKGROUND = new Color( 240, 255, 255 );
    private static final Color WIGGLE_ME_COLOR = Color.RED;

    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = FourierConstants.MAX_HARMONICS;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _fourierSeries;
    private DiscreteAmplitudesView _amplitudesView;
    private DiscreteHarmonicsView _harmonicsView;
    private MinimizedView _harmonicsMinimizedView;
    private DiscreteSumView _sumView;
    private MinimizedView _sumMinimizedView;
    private HarmonicWavelengthTool _wavelengthTool;
    private HarmonicPeriodTool _periodTool;
    private HarmonicPeriodDisplay _periodDisplay;
    private DiscreteControlPanel _controlPanel;
    private AnimationCycleController _animationCycleController;
    private Dimension _canvasSize;
    private boolean _soundEnabled;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public DiscreteModule() {

        super( FourierResources.getString( "DiscreteModule.title" ) );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );

        // Fourier Series
        _fourierSeries = new FourierSeries( NUMBER_OF_HARMONICS, FUNDAMENTAL_FREQUENCY );

        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        // Use ApparatusPanel 3 to improve support for low resolution screens.  The size was sampled at runtime by using ApparatusPanel2 with TransformManager.DEBUG_OUTPUT_ENABLED=true on large screen size, see #2860
        ApparatusPanel2 apparatusPanel = new ApparatusPanel3( getClock(), 710, 630 );
        _canvasSize = apparatusPanel.getSize();
        apparatusPanel.setBackground( APPARATUS_PANEL_BACKGROUND );
        setApparatusPanel( apparatusPanel );
        apparatusPanel.addChangeListener( this );

        // Amplitudes view
        _amplitudesView = new DiscreteAmplitudesView( apparatusPanel, _fourierSeries );
        _amplitudesView.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesView, AMPLITUDES_LAYER );

        // Harmonics view
        _harmonicsView = new DiscreteHarmonicsView( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _harmonicsView, HARMONICS_LAYER );

        // Harmonics view (minimized)
        _harmonicsMinimizedView = new MinimizedView( apparatusPanel, FourierResources.getString( "DiscreteHarmonicsView.title" ) );
        apparatusPanel.addGraphic( _harmonicsMinimizedView, HARMONICS_CLOSED_LAYER );

        // Sum view
        _sumView = new DiscreteSumView( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _sumView, SUM_LAYER );

        // Sum view (minimized)
        _sumMinimizedView = new MinimizedView( apparatusPanel, FourierResources.getString( "DiscreteSumView.title" ) );
        apparatusPanel.addGraphic( _sumMinimizedView, SUM_CLOSED_LAYER );

        // Wavelength Tool
        _wavelengthTool = new HarmonicWavelengthTool( apparatusPanel,
                _fourierSeries.getHarmonic(0), _harmonicsView.getChart() );
        apparatusPanel.addGraphic( _wavelengthTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _wavelengthTool );

        // Period Tool
        _periodTool = new HarmonicPeriodTool( apparatusPanel,
                _fourierSeries.getHarmonic(0), _harmonicsView.getChart() );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodTool );

        // Period Display
        _periodDisplay = new HarmonicPeriodDisplay( apparatusPanel, _fourierSeries.getHarmonic(0) );
        apparatusPanel.addGraphic( _periodDisplay, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodDisplay );

        // Animation controller
        _animationCycleController = new AnimationCycleController( FourierConstants.ANIMATION_STEPS_PER_CYCLE );
        getClock().addClockListener( _animationCycleController );
        _animationCycleController.addAnimationCycleListener( _harmonicsView );
        _animationCycleController.addAnimationCycleListener( _sumView );
        _animationCycleController.addAnimationCycleListener( _periodDisplay );

        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new DiscreteControlPanel( this,
                _fourierSeries, _harmonicsView, _sumView,
                _wavelengthTool, _periodTool, _periodDisplay,
                _animationCycleController );
        _controlPanel.addVerticalSpace( 5 );
        _controlPanel.addResetAllButton( this );
        setControlPanel( _controlPanel );

        // Link horizontal zoom controls
        _harmonicsView.getHorizontalZoomControl().addZoomListener( _sumView );
        _sumView.getHorizontalZoomControl().addZoomListener( _harmonicsView );

        // Harmonic hightlighting
        _amplitudesView.addHarmonicFocusListener( _harmonicsView );
        _wavelengthTool.addHarmonicFocusListener( _harmonicsView );
        _periodTool.addHarmonicFocusListener( _harmonicsView );
        _periodDisplay.addHarmonicFocusListener( _harmonicsView );

        // Slider movement by the user
        _amplitudesView.addChangeListener( _controlPanel );

        // Minimize/maximize buttons on views
        {
            // Harmonics minimize
            _harmonicsView.getMinimizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsView.setVisible( false );
                    _harmonicsMinimizedView.setVisible( true );
                    layoutViews();
                }
             } );

            // Harmonics maximize
            _harmonicsMinimizedView.getMaximizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsView.setVisible( true );
                    _harmonicsMinimizedView.setVisible( false );
                    setWaitCursorEnabled( true );
                    layoutViews();
                    setWaitCursorEnabled( false );
                }
             } );

            // Sum minimize
            _sumView.getMinimizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumView.setVisible( false );
                    _sumMinimizedView.setVisible( true );
                    layoutViews();
                }
             } );

            // Sum maximize
            _sumMinimizedView.getMaximizeButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumView.setVisible( true );
                    _sumMinimizedView.setVisible( false );
                    setWaitCursorEnabled( true );
                    layoutViews();
                    setWaitCursorEnabled( false );
                }
             } );
        }

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------

        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, getClock() );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
        wiggleMe.setEnabled( true );

        // Help Items
        HelpBubble slidersToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "DiscreteModule.help.sliders" ) );
        slidersToolHelp.pointAt( new Point( 94, 117 ), HelpBubble.TOP_LEFT, 30 );
        addHelpItem( slidersToolHelp );

        HelpBubble textfieldsToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "DiscreteModule.help.textfields" ) );
        textfieldsToolHelp.pointAt( new Point( 94, 44 ), HelpBubble.TOP_LEFT, 15 );
        addHelpItem( textfieldsToolHelp );

        HelpBubble harmonicsMinimizeButtonHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "DiscreteModule.help.minimize" ) );
        harmonicsMinimizeButtonHelp.pointAt( _harmonicsView.getMinimizeButton(), HelpBubble.LEFT_CENTER, 15 );
        addHelpItem( harmonicsMinimizeButtonHelp );

        HelpBubble wavelengthToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "DiscreteModule.help.wavelengthTool" ) );
        wavelengthToolHelp.pointAt( _wavelengthTool, HelpBubble.TOP_CENTER, 15 );
        wavelengthToolHelp.setVisible( false );
        addHelpItem( wavelengthToolHelp );

        HelpBubble periodToolHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "DiscreteModule.help.periodTool" ) );
        periodToolHelp.pointAt( _periodTool, HelpBubble.TOP_CENTER, 15 );
        periodToolHelp.setVisible( false );
        addHelpItem( periodToolHelp );

        HelpBubble periodDisplayHelp = new HelpBubble( apparatusPanel, FourierResources.getString( "DiscreteModule.help.periodDisplay" ) );
        periodDisplayHelp.pointAt( _periodDisplay, HelpBubble.RIGHT_CENTER, 15 );
        periodDisplayHelp.setVisible( false );
        addHelpItem( periodDisplayHelp );

        //----------------------------------------------------------------------------
        // Initialze the module state
        //----------------------------------------------------------------------------

        reset();

        // Add the wiggle me observer after everything has been initialized.
        _fourierSeries.addObserver( wiggleMe );
    }

    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------

    /**
     * Resets everything to the initial state.
     */
    public void reset() {

        _controlPanel.setSoundEnabled( false );

        _fourierSeries.setNumberOfHarmonics( NUMBER_OF_HARMONICS );
        _fourierSeries.setFundamentalFrequency( FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setPreset( Preset.SINE_COSINE );
        _fourierSeries.setWaveType( WaveType.SINES );

        _amplitudesView.reset();
        _harmonicsView.reset();
        _sumView.reset();

        _harmonicsView.setVisible( true );
        _harmonicsMinimizedView.setVisible( !_harmonicsView.isVisible() );
        _sumView.setVisible( true );
        _sumMinimizedView.setVisible( !_sumView.isVisible() );
        layoutViews();

        _wavelengthTool.setVisible( false );
        _wavelengthTool.setLocation( WAVELENGTH_TOOL_LOCATION );

        _periodTool.setVisible( false );
        _periodTool.setLocation( PERIOD_TOOL_LOCATION );

        _periodDisplay.setVisible( false );
        _periodDisplay.setLocation( PERIOD_DISPLAY_LOCATION );

        _controlPanel.reset();
        _soundEnabled = _controlPanel.isSoundEnabled();
    }

    //----------------------------------------------------------------------------
    // EventHandling
    //----------------------------------------------------------------------------

    /*
     * Resizes and repositions the views based on which ones are visible.
     *
     * @param event
     */
    private void layoutViews() {

        int canvasHeight = _canvasSize.height;
        int availableHeight = canvasHeight - _amplitudesView.getHeight();

        _harmonicsMinimizedView.setVisible( !_harmonicsView.isVisible() );
        _sumMinimizedView.setVisible( !_sumView.isVisible() );

        if ( _harmonicsView.isVisible() && _sumView.isVisible() ) {
            // Both maximized
            _harmonicsView.setHeight( availableHeight/2 );
            _harmonicsView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumView.setHeight( availableHeight/2 );
            _sumView.setLocation( _amplitudesView.getX(), _harmonicsView.getY() + _harmonicsView.getHeight() );
        }
        else if ( _harmonicsView.isVisible() ) {
            // Harmonics maximized
            _harmonicsView.setHeight( availableHeight - _sumMinimizedView.getHeight() );
            _harmonicsView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumMinimizedView.setLocation( _amplitudesView.getX(), _harmonicsView.getY() + _harmonicsView.getHeight() );
        }
        else if ( _sumView.isVisible() ) {
            // Sum maximized
            _harmonicsMinimizedView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumView.setHeight( availableHeight - _harmonicsMinimizedView.getHeight() );
            _sumView.setLocation( _amplitudesView.getX(), _harmonicsMinimizedView.getY() + _harmonicsMinimizedView.getHeight() );
        }
        else {
            // Both minimized
            _harmonicsMinimizedView.setLocation( _amplitudesView.getX(), _amplitudesView.getY() + _amplitudesView.getHeight() );
            _sumMinimizedView.setLocation( _amplitudesView.getX(), _harmonicsMinimizedView.getY() + _harmonicsMinimizedView.getHeight() );
        }
    }

    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------

    /**
     * Redoes the layout whenever the apparatus panel's canvas size changes.
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _canvasSize.setSize( event.getCanvasSize() );
        layoutViews();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Restore the state of sound when switching to this module.
     */
    public void activate() {
        super.activate();
        _controlPanel.setSoundEnabled( _soundEnabled );
    }

    /**
     * Mute the sound when switching to another module.
     */
    public void deactivate() {
        super.deactivate();
        _soundEnabled = _controlPanel.isSoundEnabled();
        _controlPanel.setSoundEnabled( false );
    }

    //----------------------------------------------------------------------------
    // Save & Load configurations
    //----------------------------------------------------------------------------

    /**
     * Saves the module's configuration.
     *
     * @return
     */
    public FourierConfig.DiscreteConfig save() {

        FourierConfig.DiscreteConfig config = new FourierConfig.DiscreteConfig();

        // Save control panel config
        config.setPresetName( _controlPanel.getPreset().getName() );
        config.setShowInfiniteEnabled( _controlPanel.isShowInfiniteEnabled() );
        config.setDomainName( _controlPanel.getDomain().getName() );
        config.setWaveTypeName( _controlPanel.getWaveType().getName() );
        config.setWavelengthToolEnabled( _controlPanel.isWavelengthToolEnabled() );
        config.setPeriodToolEnabled( _controlPanel.isPeriodToolEnabled() );
        config.setShowMathEnabled( _controlPanel.isShowMathEnabled() );
        config.setMathFormName( _controlPanel.getMathForm().getName() );
        config.setExpandSumEnabled( _controlPanel.isExpandSumEnabled() );
        config.setSoundEnabled( _controlPanel.isSoundEnabled() );
        config.setSoundVolume( _controlPanel.getSoundVolume() );

        // Save view config
        config.setHarmonicsViewMaximized( _harmonicsView.isVisible() );
        config.setSumViewMaximized( _sumView.isVisible() );

        // Save Fourier series config
        config.setNumberOfHarmonics( _fourierSeries.getNumberOfHarmonics() );
        double[] amplitudes = new double[ _fourierSeries.getNumberOfHarmonics() ];
        for ( int i = 0; i < amplitudes.length; i++ ) {
            amplitudes[i] = _fourierSeries.getHarmonic(i).getAmplitude();
        }
        config.setAmplitudes( amplitudes );

        return config;
    }

    /**
     * Loads the module's configuration.
     *
     * @param config
     */
    public void load( FourierConfig.DiscreteConfig config ) {

        // Load control panel config
        _controlPanel.setPreset( Preset.getByName( config.getPresetName() ) );
        _controlPanel.setNumberOfHarmonics( config.getNumberOfHarmonics() );
        _controlPanel.setShowInfiniteEnabled( config.isShowInfiniteEnabled() );
        _controlPanel.setDomain( Domain.getByName( config.getDomainName() ) );
        _controlPanel.setWaveType( WaveType.getByName( config.getWaveTypeName() ) );
        _controlPanel.setWavelengthToolEnabled( config.isWavelengthToolEnabled() );
        _controlPanel.setPeriodToolEnabled( config.isPeriodToolEnabled() );
        _controlPanel.setShowMathEnabled( config.isShowMathEnabled() );
        _controlPanel.setMathForm( MathForm.getByName( config.getMathFormName() ) );
        _controlPanel.setExpandSumEnabled( config.isExpandSumEnabled() );
        _controlPanel.setSoundEnabled( config.isSoundEnabled() );
        _controlPanel.setSoundVolume( config.getSoundVolume() );

        // Load view config
        _harmonicsView.setVisible( config.isHarmonicsViewMaximized() );
        _sumView.setVisible( config.isSumViewMaximized() );
        layoutViews();

        // Load Fourier series config
        _fourierSeries.setNumberOfHarmonics( config.getNumberOfHarmonics() );
        double[] amplitudes = config.getAmplitudes();
        for ( int i = 0; i < amplitudes.length; i++ ) {
            _fourierSeries.getHarmonic(i).setAmplitude( amplitudes[i] );
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private MouseInputAdapter _mouseListener;

        /**
         * Sole constructor.
         *
         * @param component
         * @param model
         */
        public ThisWiggleMeGraphic( Component component, IClock clock ) {
            super( component, clock );

            setText( FourierResources.getString( "DiscreteModule.wiggleMe" ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.TOP_CENTER, new Vector2D( 0, -30 ), WIGGLE_ME_COLOR );
            setRange( 0, 10 );
            setCycleDuration( 5 );
            setEnabled( true );

            _mouseListener = new MouseInputAdapter() {
                // Disable the wiggle me when the mouse is pressed in the apparatus panel.
                public void mousePressed( MouseEvent event ) {
                    // Disable
                    setEnabled( false );
                    // Unwire
                    getComponent().removeMouseListener( this );
                }
            };

            component.addMouseListener( _mouseListener );
        }

        /*
         * Disable the wiggle me when the FourierSeries model changes.
         */
        public void update() {
            // Disable
            setEnabled( false );
            // Unwire
            getComponent().removeMouseListener( _mouseListener );
        }
    }
}
