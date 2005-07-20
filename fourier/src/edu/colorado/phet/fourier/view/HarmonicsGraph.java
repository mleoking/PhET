/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.LabelTable;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.charts.HarmonicPlot;
import edu.colorado.phet.fourier.charts.HarmonicsChart;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.event.ZoomEvent;
import edu.colorado.phet.fourier.event.ZoomListener;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleEvent;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleListener;


/**
 * HarmonicsGraph graphs a collection of harmonic waveforms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsGraph extends GraphicLayerSet 
    implements SimpleObserver, ZoomListener, HarmonicFocusListener, AnimationCycleListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;
    private static final double MATH_LAYER = 5;

    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 200 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    // Chart parameters
    private static final double L = FourierConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 550, 130 );

    // Wave parameters
    private static final Stroke WAVE_NORMAL_STROKE = new BasicStroke( 1f );
    private static final Stroke WAVE_FOCUS_STROKE = new BasicStroke( 2f );
    private static final Stroke WAVE_DIMMED_STROKE = new BasicStroke( 0.5f );
    private static final Color WAVE_DIMMED_COLOR = Color.GRAY;
    private static final double[] PIXELS_PER_POINT = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _fourierSeries;
    private PhetImageGraphic _closeButton;
    private HarmonicsChart _chartGraphic;
    private ZoomControl _horizontalZoomControl;
    private HarmonicsEquation _mathGraphic;
    private ArrayList _harmonicPlots; // array of HarmonicPlot
    
    private int _xZoomLevel;
    private int _domain;
    private int _mathForm;

    private int _previousNumberOfHarmonics;
    private int _previousPreset;
    private int _previousWaveType;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component the parent Component
     * @param fourierSeries the Fourier series that this view displays
     */
    public HarmonicsGraph( Component component, FourierSeries fourierSeries ) {
        super( component );
        
        // Model
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        backgroundGraphic.setStroke( BACKGROUND_STROKE );
        backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        backgroundGraphic.setLocation( 0, 0 );
        
        // Title
        String title = SimStrings.get( "HarmonicsGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( 40, 115 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new HarmonicsChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, CHART_SIZE.height / 2 );  // at the chart's origin
            _chartGraphic.setLocation( 60, 50 + ( CHART_SIZE.height / 2 ) );
        }
        
        // Close button
        {
            _closeButton = new PhetImageGraphic( component, FourierConstants.CLOSE_BUTTON_IMAGE );
            addGraphic( _closeButton, CONTROLS_LAYER );
            _closeButton.setLocation( 10, 10 );
        }
        
        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            // Location is aligned with top-right edge of chart.
            int x = _chartGraphic.getX() + CHART_SIZE.width + 20;
            int y = _chartGraphic.getY() - ( CHART_SIZE.height / 2 );
            _horizontalZoomControl.setLocation( x, y );
        }
        
        // Math
        {
            _mathGraphic = new HarmonicsEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            // Location is above the center of the chart.
            int x = _chartGraphic.getX() + ( CHART_SIZE.width / 2 );
            int y = 28;
            _mathGraphic.setLocation( x, y );
        }

        // Interactivity
        {
            backgroundGraphic.setIgnoreMouse( true );
            titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );
            _mathGraphic.setIgnoreMouse( true );
            _horizontalZoomControl.addZoomListener( this );
            
            _closeButton.setCursorHand();
        }

        // Misc initialization
        {
            _harmonicPlots = new ArrayList();
            for ( int i = 0; i < FourierConfig.MAX_HARMONICS; i++ ) {
                _harmonicPlots.add( new HarmonicPlot( component, _chartGraphic ) );
            }
        }
        
        reset();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
        _horizontalZoomControl.removeAllZoomListeners();
    }

    //----------------------------------------------------------------------------
    // Reset
    //----------------------------------------------------------------------------
    
    /**
     * Resets to the initial state.
     */
    public void reset() {

        // Domain
        _domain = FourierConstants.DOMAIN_SPACE;
        
        // Chart
        {
            _xZoomLevel = 0;
            _chartGraphic.setRange( CHART_RANGE );
            updateLabelsAndLines();
            updateZoomButtons();
        }
        
        // Math Mode
        _mathForm = FourierConstants.MATH_FORM_WAVE_NUMBER;
        _mathGraphic.setVisible( false );
        updateMath();
        
        // Synchronize with model
        _previousNumberOfHarmonics = 0; // force an update
        _previousPreset = -1;
        _previousWaveType = -1;
        update();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the horizontal zoom control.
     * 
     * @return the horizontal zoom control
     */
    public ZoomControl getHorizontalZoomControl() {
        return _horizontalZoomControl;
    }
    
    /**
     * Enables things that are related to "math mode".
     * 
     * @param enabled true or false
     */
    public void setMathEnabled( boolean enabled ) {
        _mathGraphic.setVisible( enabled );
    }
    
    /**
     * Gets the chart associated with this graphic.
     * 
     * @return the chart
     */
    public Chart getChart() {
        return _chartGraphic;
    }
    
    /**
     * Sets the domain and math form.
     * Together, these values determines how the chart is 
     * labeled, and the format of the equation shown above the chart.
     * 
     * @param domain
     * @param mathForm
     */
    public void setDomainAndMathForm( int domain, int mathForm ) {
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        _domain = domain;
        _mathForm = mathForm;
        updateLabelsAndLines();
        updateMath();
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
            harmonicPlot.setStartX( 0 );
        }
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();
        
        if ( numberOfHarmonics != _previousNumberOfHarmonics ) {

            // Clear the chart.
            _chartGraphic.removeAllDataSetGraphics();

            // Re-populate the chart such that the fundamental's graphic is in the foreground.
            for ( int i = numberOfHarmonics-1; i >= 0; i-- ) {

                HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                harmonicPlot.setHarmonic( _fourierSeries.getHarmonic( i ) );
                harmonicPlot.setPeriod( L / ( i + 1 ) );
                harmonicPlot.setPixelsPerPoint( PIXELS_PER_POINT[i] );
                harmonicPlot.setStroke( WAVE_NORMAL_STROKE );
                harmonicPlot.setBorderColor( HarmonicColors.getInstance().getColor( i ) );
                harmonicPlot.setStartX( 0 );

                _chartGraphic.addDataSetGraphic( harmonicPlot );
            }
            
            _previousNumberOfHarmonics = numberOfHarmonics;
        }
        else {
            // When the preset or wave type changes, reset the cycle of the waves.
            int preset = _fourierSeries.getPreset();
            int waveType = _fourierSeries.getWaveType();
            if ( preset != _previousPreset || waveType != _previousWaveType ) {
                for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
                    HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                    harmonicPlot.setStartX( 0 );
                    harmonicPlot.setWaveType( waveType );
                }
                _previousPreset = preset;
                _previousWaveType = waveType;
            }
        }
    }

    //----------------------------------------------------------------------------
    // ZoomListener implementation
    //----------------------------------------------------------------------------

    /**
     * Invokes when a zoom of the chart has been performed.
     * 
     * @param event
     */
    public void zoomPerformed( ZoomEvent event ) {
        int zoomType = event.getZoomType();
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN || zoomType == ZoomEvent.HORIZONTAL_ZOOM_OUT ) {
            handleHorizontalZoom( zoomType );
        }
        else {
            throw new IllegalArgumentException( "unexpected event: " + event );
        }
    }
    
    //----------------------------------------------------------------------------
    // HarmonicFocusListener implementation
    //----------------------------------------------------------------------------

    /**
     * When a harmonic gains focus, grays out all harmonics except for the one with focus.
     */
    public void focusGained( HarmonicFocusEvent event ) {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicGraphic = (HarmonicPlot) _harmonicPlots.get( i );
            if ( harmonicGraphic.getHarmonic() != event.getHarmonic() ) {
                harmonicGraphic.setBorderColor( WAVE_DIMMED_COLOR );
                harmonicGraphic.setStroke( WAVE_DIMMED_STROKE );
            }
            else {
                Harmonic harmonic = harmonicGraphic.getHarmonic();
                if ( harmonic != null ) {
                    Color harmonicColor = HarmonicColors.getInstance().getColor( harmonic );
                    harmonicGraphic.setBorderColor( harmonicColor );
                }
                harmonicGraphic.setStroke( WAVE_FOCUS_STROKE );
            }
        }
    }
    
    /**
     * When a harmonic loses focus, sets all harmonics to their assigned color.
     */
    public void focusLost( HarmonicFocusEvent event ) {
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicGraphic = (HarmonicPlot) _harmonicPlots.get( i );
            Harmonic harmonic = harmonicGraphic.getHarmonic();
            if ( harmonic != null ) {
                Color harmonicColor = HarmonicColors.getInstance().getColor( harmonic );
                harmonicGraphic.setBorderColor( harmonicColor );
            }
            harmonicGraphic.setStroke( WAVE_NORMAL_STROKE );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    /*
     * Handles horizontal zooming.
     * 
     * @param zoomType indicates the type of zoom
     */
    private void handleHorizontalZoom( int zoomType ) {

        // Adjust the zoom level.
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN ) {
            _xZoomLevel++;
        }
        else {
            _xZoomLevel--;
        }
        
        // Obtuse sqrt(2) zoom factor, immune to numeric precision errors 
        double zoomFactor = Math.pow( 2, Math.abs( _xZoomLevel ) / 2.0 );
        
        // Adjust the chart's horizontal range.
        Range2D range = _chartGraphic.getRange();
        double xRange;
        if ( _xZoomLevel == 0 ) {
            xRange = ( L / 2 );
        }
        else if ( _xZoomLevel > 0 ) {
            xRange = ( L / 2 ) / zoomFactor; 
        }
        else {
            xRange = ( L / 2 ) * zoomFactor;
        }
        range.setMaxX( xRange );
        range.setMinX( -xRange );
        _chartGraphic.setRange( range );

        updateLabelsAndLines();
        updateZoomButtons();
    }
    
    /*
     * Adjusts labels, ticks and gridlines to match the chart range.
     */
    private void updateLabelsAndLines() {
        
        // X axis
        {
            LabelTable labelTable = null;
            if ( _domain == FourierConstants.DOMAIN_TIME ) {
                _chartGraphic.setXAxisTitle( MathStrings.C_TIME );
                if ( _xZoomLevel > -3 ) {
                    labelTable = _chartGraphic.getTimeLabels1();
                }
                else {
                    labelTable = _chartGraphic.getTimeLabels2();
                }
            }
            else { /* DOMAIN_SPACE or DOMAIN_SPACE_AND_TIME */
                _chartGraphic.setXAxisTitle( MathStrings.C_SPACE );
                if ( _xZoomLevel > -3 ) {
                    labelTable = _chartGraphic.getSpaceLabels1();
                }
                else {
                    labelTable = _chartGraphic.getSpaceLabels2();
                }
            }
            _chartGraphic.getHorizontalTicks().setMajorLabels( labelTable );
        }
    }
    
    /*
     * Enables and disables zoom buttons based on the current
     * zoom levels and range of the chart.
     */
    private void updateZoomButtons() {
        
        Range2D range = _chartGraphic.getRange();
        
        // Horizontal buttons
        if ( range.getMaxX() >= X_RANGE_MAX ) {
            _horizontalZoomControl.setZoomOutEnabled( false );
            _horizontalZoomControl.setZoomInEnabled( true );
        }
        else if ( range.getMaxX() <= X_RANGE_MIN ) {
            _horizontalZoomControl.setZoomOutEnabled( true );
            _horizontalZoomControl.setZoomInEnabled( false );
        }
        else {
            _horizontalZoomControl.setZoomOutEnabled( true );
            _horizontalZoomControl.setZoomInEnabled( true );
        }
    }
    
    /*
     * Updates the math equation that appears above the graph.
     */
    private void updateMath() {
        _mathGraphic.setForm( _domain, _mathForm );
        _mathGraphic.centerRegistrationPoint();
    }
    
    //----------------------------------------------------------------------------
    // AnimationCycleListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles animation events.
     * Animates the harmonics by adjusting their phase (aka, start times).
     * 
     * @param event
     */
    public void animate( AnimationCycleEvent event ) {
        if ( isVisible() && _domain == FourierConstants.DOMAIN_SPACE_AND_TIME ) {
            double cyclePoint = event.getCyclePoint();
            double startX = cyclePoint * L;
            for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
                HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                harmonicPlot.setStartX( startX  );
            }
        }
    }
}
