/* Copyright 2004, University of Colorado */

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

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.help.FourierHelpItem;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.Vector2D;
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
public class DiscreteModule extends FourierModule implements ApparatusPanel2.ChangeListener {

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
    private static final Point WAVELENGTH_TOOL_LOCATION = new Point( 450, 236 );
    private static final Point PERIOD_TOOL_LOCATION = WAVELENGTH_TOOL_LOCATION;
    private static final Point PERIOD_DISPLAY_LOCATION = new Point( 655, 345 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 115, 153 );
    
    // Colors
    private static final Color APPARATUS_PANEL_BACKGROUND = new Color( 240, 255, 255 );
    private static final Color WIGGLE_ME_COLOR = Color.RED;
    
    // Fourier Components
    private static final double FUNDAMENTAL_FREQUENCY = 440.0; // Hz
    private static final int NUMBER_OF_HARMONICS = FourierConfig.MAX_HARMONICS;
  
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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public DiscreteModule( AbstractClock clock ) {
        
        super( SimStrings.get( "DiscreteModule.title" ), clock );

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
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
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
        _harmonicsMinimizedView = new MinimizedView( apparatusPanel, SimStrings.get( "DiscreteHarmonicsView.title" ) );
        apparatusPanel.addGraphic( _harmonicsMinimizedView, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumView = new DiscreteSumView( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _sumView, SUM_LAYER );
        
        // Sum view (minimized)
        _sumMinimizedView = new MinimizedView( apparatusPanel, SimStrings.get( "DiscreteSumView.title" ) );
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
        _animationCycleController = new AnimationCycleController( FourierConfig.ANIMATION_STEPS_PER_CYCLE );
        clock.addClockTickListener( _animationCycleController );
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
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
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
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, clock );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
        wiggleMe.setEnabled( true );
        
        // Help Items
        FourierHelpItem slidersToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.sliders" ) );
        slidersToolHelp.pointAt( new Point( 252, 117 ), FourierHelpItem.UP, 30 );
        addHelpItem( slidersToolHelp );
        
        FourierHelpItem textfieldsToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.textfields" ) );
        textfieldsToolHelp.pointAt( new Point( 205, 44 ), FourierHelpItem.UP, 15 );
        addHelpItem( textfieldsToolHelp );
        
        FourierHelpItem harmonicsMinimizeButtonHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.minimize" ) );
        harmonicsMinimizeButtonHelp.pointAt( _harmonicsView.getMinimizeButton(), FourierHelpItem.LEFT, 15 );
        addHelpItem( harmonicsMinimizeButtonHelp );
        
        FourierHelpItem wavelengthToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.wavelengthTool" ) );
        wavelengthToolHelp.pointAt( _wavelengthTool, FourierHelpItem.UP, 15 );
        wavelengthToolHelp.setVisible( false );
        addHelpItem( wavelengthToolHelp );
          
        FourierHelpItem periodToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.periodTool" ) );
        periodToolHelp.pointAt( _periodTool, FourierHelpItem.UP, 15 );
        periodToolHelp.setVisible( false );
        addHelpItem( periodToolHelp );
        
        FourierHelpItem periodDisplayHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.periodDisplay" ) );
        periodDisplayHelp.pointAt( _periodDisplay, FourierHelpItem.RIGHT, 15 );
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
        
        _fourierSeries.setNumberOfHarmonics( NUMBER_OF_HARMONICS );
        _fourierSeries.setFundamentalFrequency( FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setPreset( FourierConstants.PRESET_SINE_COSINE );
        _fourierSeries.setWaveType( FourierConstants.WAVE_TYPE_SINE );
        
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
        public ThisWiggleMeGraphic( Component component, AbstractClock clock ) {
            super( component, clock );

            setText( SimStrings.get( "DiscreteModule.wiggleMe" ), WIGGLE_ME_COLOR );
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
