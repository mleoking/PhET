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
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.control.DiscreteControlPanel;
import edu.colorado.phet.fourier.help.FourierHelpItem;
import edu.colorado.phet.fourier.help.WiggleMeGraphic;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.Vector2D;
import edu.colorado.phet.fourier.view.*;
import edu.colorado.phet.fourier.view.tools.AbstractHarmonicMeasurementTool;
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
    private static final int NUMBER_OF_HARMONICS = 7;
  
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private AmplitudesGraph _amplitudesGraph;
    private HarmonicsGraph _harmonicsGraph;
    private GraphClosed _harmonicsGraphClosed;
    private SumGraph _sumGraph;
    private GraphClosed _sumGraphClosed;
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
        _amplitudesGraph = new AmplitudesGraph( apparatusPanel, _fourierSeries );
        _amplitudesGraph.setLocation( 0, 0 );
        apparatusPanel.addGraphic( _amplitudesGraph, AMPLITUDES_LAYER );
        
        // Harmonics view
        _harmonicsGraph = new HarmonicsGraph( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _harmonicsGraph, HARMONICS_LAYER );
        
        // Harmonics view (collapsed)
        _harmonicsGraphClosed = new GraphClosed( apparatusPanel, SimStrings.get( "HarmonicsGraph.title" ) );
        apparatusPanel.addGraphic( _harmonicsGraphClosed, HARMONICS_CLOSED_LAYER );
        
        // Sum view
        _sumGraph = new SumGraph( apparatusPanel, _fourierSeries );
        apparatusPanel.addGraphic( _sumGraph, SUM_LAYER );
        
        // Sum view (collapsed)
        _sumGraphClosed = new GraphClosed( apparatusPanel, SimStrings.get( "SumGraph.title" ) );
        apparatusPanel.addGraphic( _sumGraphClosed, SUM_CLOSED_LAYER );
        
        // Wavelength Tool
        _wavelengthTool = new HarmonicWavelengthTool( apparatusPanel,
                _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _wavelengthTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _wavelengthTool );
        
        // Period Tool
        _periodTool = new HarmonicPeriodTool( apparatusPanel,
                _fourierSeries.getHarmonic(0), _harmonicsGraph.getChart() );
        apparatusPanel.addGraphic( _periodTool, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodTool );
        
        // Period Display
        _periodDisplay = new HarmonicPeriodDisplay( apparatusPanel, _fourierSeries.getHarmonic(0) );
        apparatusPanel.addGraphic( _periodDisplay, TOOLS_LAYER );
        apparatusPanel.addChangeListener( _periodDisplay );
        
        // Animation controller
        _animationCycleController = new AnimationCycleController( FourierConfig.ANIMATION_STEPS_PER_CYCLE );
        clock.addClockTickListener( _animationCycleController );
        _animationCycleController.addAnimationCycleListener( _harmonicsGraph );
        _animationCycleController.addAnimationCycleListener( _sumGraph );
        _animationCycleController.addAnimationCycleListener( _periodDisplay );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new DiscreteControlPanel( this, 
                _fourierSeries, _harmonicsGraph, _sumGraph, 
                _wavelengthTool, _periodTool, _periodDisplay,
                _animationCycleController );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );
        
        // Link horizontal zoom controls
        _harmonicsGraph.getHorizontalZoomControl().addZoomListener( _sumGraph );
        _sumGraph.getHorizontalZoomControl().addZoomListener( _harmonicsGraph );
        
        // Harmonic hightlighting
        _amplitudesGraph.addHarmonicFocusListener( _harmonicsGraph );
        _wavelengthTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodTool.addHarmonicFocusListener( _harmonicsGraph );
        _periodDisplay.addHarmonicFocusListener( _harmonicsGraph );
        
        // Slider movement by the user
        _amplitudesGraph.addChangeListener( _controlPanel );
        
        // Open/close buttons on graphs
        {
            // Harmonics close
            _harmonicsGraph.getCloseButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsGraph.setVisible( false );
                    _harmonicsGraphClosed.setVisible( true );
                    resizeGraphs();
                }
             } );
            
            // Harmonics open
            _harmonicsGraphClosed.getOpenButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _harmonicsGraph.setVisible( true );
                    _harmonicsGraphClosed.setVisible( false );
                    resizeGraphs();
                }
             } );
            
            // Sum close
            _sumGraph.getCloseButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumGraph.setVisible( false );
                    _sumGraphClosed.setVisible( true );
                    resizeGraphs();
                }
             } );
            
            // Sum open
            _sumGraphClosed.getOpenButton().addMouseInputListener( new MouseInputAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    _sumGraph.setVisible( true );
                    _sumGraphClosed.setVisible( false );
                    resizeGraphs();
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
        slidersToolHelp.pointAt( new Point( 75, 155 ), FourierHelpItem.UP, 30 );
        addHelpItem( slidersToolHelp );
        
        FourierHelpItem textfieldsToolHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.textfields" ) );
        textfieldsToolHelp.pointAt( new Point( 195, 24 ), FourierHelpItem.LEFT, 15 );
        addHelpItem( textfieldsToolHelp );
        
        FourierHelpItem harmonicsCloseButtonHelp = new FourierHelpItem( apparatusPanel, SimStrings.get( "DiscreteModule.help.closeHarmonics" ) );
        harmonicsCloseButtonHelp.pointAt( _harmonicsGraph.getCloseButton(), FourierHelpItem.LEFT, 15 );
        addHelpItem( harmonicsCloseButtonHelp );
        
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
        
        _amplitudesGraph.reset();
        _harmonicsGraph.reset();
        _sumGraph.reset();
        
        _harmonicsGraph.setVisible( true );
        _harmonicsGraphClosed.setVisible( false );
        _sumGraph.setVisible( true );
        _sumGraphClosed.setVisible( false ); 
        resizeGraphs();
        
        _harmonicsGraph.setLocation( _amplitudesGraph.getX(), _amplitudesGraph.getY() + _amplitudesGraph.getHeight() );
        _harmonicsGraphClosed.setLocation( _amplitudesGraph.getX(), _amplitudesGraph.getY() + _amplitudesGraph.getHeight() );
        _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        
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
     * Resizes and repositions the graphs based on which ones are visible.
     * 
     * @param event
     */
    private void resizeGraphs() {

        int canvasHeight = _canvasSize.height;
        int availableHeight = canvasHeight - _amplitudesGraph.getHeight();
        
        if (  _harmonicsGraph.isVisible() && _sumGraph.isVisible() ) {
            _harmonicsGraph.setHeight( availableHeight/2 );
            _sumGraph.setHeight( availableHeight/2 );
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        }
        else if ( _harmonicsGraph.isVisible() ) {
            _harmonicsGraph.setHeight( availableHeight - _sumGraphClosed.getHeight() );
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraph.getY() + _harmonicsGraph.getHeight() );
        }
        else if ( _sumGraph.isVisible() ) {
            _sumGraph.setHeight( availableHeight - _harmonicsGraphClosed.getHeight() );
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() );
        }
        else {
            _sumGraph.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() );
            _sumGraphClosed.setLocation( _amplitudesGraph.getX(), _harmonicsGraphClosed.getY() + _harmonicsGraphClosed.getHeight() ); 
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
        resizeGraphs();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic {

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         */
        public ThisWiggleMeGraphic( final Component component, AbstractClock clock ) {
            super( component, clock );

            setText( SimStrings.get( "DiscreteModule.wiggleMe" ), WIGGLE_ME_COLOR );
            addArrow( WiggleMeGraphic.TOP_CENTER, new Vector2D( 0, -30 ), WIGGLE_ME_COLOR );
            setRange( 0, 10 );
            setCycleDuration( 5 );
            setEnabled( true );
            
            // Disable the wiggle me when the mouse is pressed in the apparatus panel.
            component.addMouseListener( new MouseInputAdapter() { 
                public void mousePressed( MouseEvent event ) {
                    // Disable
                    setEnabled( false );
                    // Unwire
                    component.removeMouseListener( this );
                }
             } );
        }
    }
}
