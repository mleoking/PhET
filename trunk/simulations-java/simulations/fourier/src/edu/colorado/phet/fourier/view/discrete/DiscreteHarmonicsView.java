// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier.view.discrete;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.LabelTable;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl.ZoomEvent;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl.ZoomListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.charts.HarmonicPlot;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleEvent;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleListener;
import edu.colorado.phet.fourier.view.HarmonicColors;
import edu.colorado.phet.fourier.view.HarmonicsEquation;


/**
 * DiscreteHarmonicsView is the "Harmonics" view in the "Discrete" module.
 * It displays a collection of harmonic waveforms.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiscreteHarmonicsView extends GraphicLayerSet implements SimpleObserver, ZoomListener, HarmonicFocusListener, AnimationCycleListener {

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
    private static final int MIN_HEIGHT = 150;
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 216 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;

    // Title parameters
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 115 );

    // Chart parameters
    private static final double L = FourierConstants.L;
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = FourierConstants.MAX_HARMONIC_AMPLITUDE + ( 0.05 * FourierConstants.MAX_HARMONIC_AMPLITUDE );
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 135 );

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
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private DiscreteHarmonicsChart _chartGraphic;
    private PhetZoomControl _horizontalZoomControl;
    private HarmonicsEquation _mathGraphic;
    private ArrayList _harmonicPlots; // array of HarmonicPlot

    private int _xZoomLevel;
    private Domain _domain;
    private MathForm _mathForm;

    private int _previousNumberOfHarmonics;
    private Preset _previousPreset;
    private WaveType _previousWaveType;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component     the parent Component
     * @param fourierSeries the Fourier series that this view displays
     */
    public DiscreteHarmonicsView( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Model
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );

        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );
        _backgroundGraphic.setLocation( 0, 0 );

        // Title
        String title = FourierResources.getString( "DiscreteHarmonicsView.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( TITLE_LOCATION );
        addGraphic( _titleGraphic, TITLE_LAYER );

        // Chart
        {
            _chartGraphic = new DiscreteHarmonicsChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 60, 50 );
        }

        // Close button
        _minimizeButton = new PhetImageGraphic( component, FourierConstants.MINIMIZE_BUTTON_IMAGE );
        addGraphic( _minimizeButton, CONTROLS_LAYER );
        _minimizeButton.centerRegistrationPoint();
        _minimizeButton.setLocation( ( _minimizeButton.getWidth() / 2 ) + 10, _minimizeButton.getHeight() / 2 + 5 );

        // Zoom controls
        {
            _horizontalZoomControl = new PhetZoomControl( component, PhetZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            // Location is aligned with top-right edge of chart.
            int x = _chartGraphic.getX() + CHART_SIZE.width + 20;
            int y = _chartGraphic.getY();
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
            _backgroundGraphic.setIgnoreMouse( true );
            _titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );
            _mathGraphic.setIgnoreMouse( true );
            _horizontalZoomControl.addZoomListener( this );

            _minimizeButton.setCursorHand();
        }

        // Misc initialization
        {
            _harmonicPlots = new ArrayList();
            for ( int i = 0; i < FourierConstants.MAX_HARMONICS; i++ ) {
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
        _domain = Domain.SPACE;

        // Chart
        {
            _xZoomLevel = 0;
            _chartGraphic.setRange( CHART_RANGE );
            updateLabelsAndLines();
            updateZoomButtons();
        }

        // Math Mode
        _mathForm = MathForm.WAVE_NUMBER;
        _mathGraphic.setVisible( false );
        updateMath();

        // Synchronize with model
        _previousNumberOfHarmonics = 0; // force an update
        _previousPreset = Preset.UNDEFINED;
        _previousWaveType = WaveType.UNDEFINED;
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
    public PhetZoomControl getHorizontalZoomControl() {
        return _horizontalZoomControl;
    }

    /**
     * Enables things that are related to "math mode".
     *
     * @param enabled true or false
     */
    public void setMathEnabled( boolean enabled ) {
        _mathGraphic.setVisible( enabled );
        updateLabelsAndLines();
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
    public void setDomainAndMathForm( Domain domain, MathForm mathForm ) {
        _domain = domain;
        _mathForm = mathForm;
        updateLabelsAndLines();
        updateMath();
        for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
            HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
            harmonicPlot.setStartX( 0 );
        }
    }

    /**
     * Gets a reference to the "minimize" button.
     *
     * @return minimize button
     */
    public PhetImageGraphic getMinimizeButton() {
        return _minimizeButton;
    }

    /**
     * Sets the height of this graphic.
     *
     * @param height
     */
    public void setHeight( int height ) {
        if ( height >= MIN_HEIGHT ) {
            _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, height ) );
            _chartGraphic.setChartSize( CHART_SIZE.width, height - 75 );
            _titleGraphic.setLocation( TITLE_LOCATION.x, height / 2 );
            setBoundsDirty();
        }
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {

        if ( isVisible() ) {

            int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();

            if ( numberOfHarmonics != _previousNumberOfHarmonics ) {

                // Clear the chart.
                _chartGraphic.removeAllDataSetGraphics();

                // Re-populate the chart such that the fundamental's graphic is in the foreground.
                for ( int i = numberOfHarmonics - 1; i >= 0; i-- ) {

                    HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                    harmonicPlot.setHarmonic( _fourierSeries.getHarmonic( i ) );
                    harmonicPlot.setWaveType( WaveType.SINES );
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
                Preset preset = _fourierSeries.getPreset();
                WaveType waveType = _fourierSeries.getWaveType();
                if ( preset != _previousPreset || waveType != _previousWaveType ) {
                    for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
                        HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                        harmonicPlot.setStartX( 0 );
                        harmonicPlot.setWaveType( waveType );
                    }
                    _previousPreset = preset;
                    _previousWaveType = waveType;
                }
                updateMath();
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
            xRange = X_RANGE_START;
        }
        else if ( _xZoomLevel > 0 ) {
            xRange = X_RANGE_START / zoomFactor;
        }
        else {
            xRange = X_RANGE_START * zoomFactor;
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

        /// X axis labels
        final boolean mathMode = _mathGraphic.isVisible();
        LabelTable labelTable;
        if ( mathMode ) {
            if ( _xZoomLevel > -3 ) {
                labelTable = ( _domain == Domain.TIME ) ? _chartGraphic.getSymbolicTimeLabels1() : _chartGraphic.getSymbolicSpaceLabels1();
            }
            else {
                labelTable = ( _domain == Domain.TIME ) ? _chartGraphic.getSymbolicTimeLabels2() : _chartGraphic.getSymbolicSpaceLabels2();
            }
        }
        else {
            if ( _xZoomLevel > 0 ) {
                labelTable = ( _domain == Domain.TIME ) ? _chartGraphic.getActualTimeLabels0() : _chartGraphic.getActualSpaceLabels0();
            }
            else if ( _xZoomLevel == 0 || _xZoomLevel == -1 ) {
                labelTable = ( _domain == Domain.TIME ) ? _chartGraphic.getActualTimeLabels0() : _chartGraphic.getActualSpaceLabels1();
            }
            else if ( _xZoomLevel == -2 || _xZoomLevel == -3 ) {
                labelTable = ( _domain == Domain.TIME ) ? _chartGraphic.getActualTimeLabels0() : _chartGraphic.getActualSpaceLabels2();
            }
            else {
                labelTable = ( _domain == Domain.TIME ) ? _chartGraphic.getActualTimeLabels0() : _chartGraphic.getActualSpaceLabels3();
            }
        }
        _chartGraphic.getHorizontalTicks().setMajorLabels( labelTable );

        // X axis title
        if ( _domain == Domain.TIME ) {
            if ( _mathGraphic.isVisible() ) {
                _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.t" ) );
            }
            else {
                _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.t.units" ) );
            }
        }
        else { /* DOMAIN_SPACE or DOMAIN_SPACE_AND_TIME */
            if ( _mathGraphic.isVisible() ) {
                _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.x" ) );
            }
            else {
                _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.x.units" ) );
            }
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
        _mathGraphic.setForm( _domain, _mathForm, _fourierSeries.getWaveType() );
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
        if ( _domain == Domain.SPACE_AND_TIME ) {
            double cyclePoint = event.getCyclePoint();
            double startX = cyclePoint * L;
            for ( int i = 0; i < _harmonicPlots.size(); i++ ) {
                HarmonicPlot harmonicPlot = (HarmonicPlot) _harmonicPlots.get( i );
                harmonicPlot.setStartX( startX );
            }
        }
    }
}
