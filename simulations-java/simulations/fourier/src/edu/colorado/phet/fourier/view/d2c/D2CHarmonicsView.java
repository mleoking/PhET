// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.d2c;

import java.awt.*;

import edu.colorado.phet.common.charts.DataSetGraphic;
import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl.ZoomEvent;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl.ZoomListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.*;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.charts.FlattenedChart;
import edu.colorado.phet.fourier.charts.HarmonicPlot;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.view.HarmonicsEquation;


/**
 * D2CHarmonicsView is the "Components" (or Harmonics) view in the "Discrete to Continuous" module.
 * It displays the harmonics of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CHarmonicsView extends GraphicLayerSet implements SimpleObserver, ZoomListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double MESSAGE_LAYER = 3;
    private static final double CHART_LAYER = 4;
    private static final double CONTROLS_LAYER = 5;
    private static final double MATH_LAYER = 6;

    // Background parameters
    private static final int MIN_HEIGHT = 150;
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, MIN_HEIGHT );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;

    // Title parameters
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 115 );

    // Message parameters
    private static final Font CANNOT_SHOW_MESSAGE_FONT = new PhetFont( Font.PLAIN, 16 );
    private static final Color CANNOT_SHOW_MESSAGE_COLOR = Color.RED;
    private static final Font MINIMIZE_MESSAGE_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final Color MINIMIZE_MESSAGE_COLOR = Color.RED;

    // Chart parameters
    public static final double X_RANGE_START = 2;
    public static final double X_RANGE_MIN = 0.5;
    public static final double X_RANGE_MAX = 8;
    private static final double Y_RANGE_START = 1;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 100 );

    // Harmonics in the chart
    private static final int HARMONIC_DARKEST_GRAY = 0; //dark gray
    private static final int HARMONIC_LIGHTEST_GRAY = 230;  // light gray
    private static final Stroke HARMONIC_STROKE = new BasicStroke( 1f );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GaussianWavePacket _wavePacket;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private PhetZoomControl _horizontalZoomControl;
    private D2CHarmonicsChart _chartGraphic;
    private HarmonicsEquation _mathGraphic;
    private Domain _domain;
    private WaveType _waveType;
    private int _xZoomLevel;
    private HTMLGraphic _cannotShowGraphic;
    private FlattenedChart _flattenedChart;
    private boolean _updateRequired;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component  the parent Component
     * @param wavePacket the Gaussian wave packet being displayed
     */
    public D2CHarmonicsView( Component component, GaussianWavePacket wavePacket ) {
        super( component );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Model
        _wavePacket = wavePacket;
        _wavePacket.addObserver( this );

        // Background
        _backgroundGraphic = new PhetShapeGraphic( component );
        _backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        _backgroundGraphic.setPaint( BACKGROUND_COLOR );
        _backgroundGraphic.setStroke( BACKGROUND_STROKE );
        _backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        _backgroundGraphic.setLocation( 0, 0 );
        addGraphic( _backgroundGraphic, BACKGROUND_LAYER );

        // Title
        String title = FourierResources.getString( "D2CHarmonicsView.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( 40, BACKGROUND_SIZE.height / 2 );
        addGraphic( _titleGraphic, TITLE_LAYER );

        // Flattened Chart
        {
            _chartGraphic = new D2CHarmonicsChart( component, CHART_RANGE, CHART_SIZE );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 0, 0 );

            int xOffset = 25; // distance between the left edge and the chart's origin.
            int yOffset = 25;
            _flattenedChart = new FlattenedChart( component, _chartGraphic, xOffset, yOffset );
            addGraphic( _flattenedChart, CHART_LAYER );
            _flattenedChart.setRegistrationPoint( 0, 0 ); // upper left
            _flattenedChart.setLocation( 60 - xOffset, 50 - yOffset );
        }

        // "Cannot show" message 
        String cannotShowMessage = FourierResources.getString( "D2CHarmonicsView.cannotShow" );
        _cannotShowGraphic = new HTMLGraphic( component, CANNOT_SHOW_MESSAGE_FONT, cannotShowMessage, CANNOT_SHOW_MESSAGE_COLOR );
        addGraphic( _cannotShowGraphic, MESSAGE_LAYER );
        _cannotShowGraphic.setRegistrationPoint( 0, _cannotShowGraphic.getHeight() / 2 ); // left center
        _cannotShowGraphic.setLocation( 125, BACKGROUND_SIZE.height / 2 );

        // Close button
        _minimizeButton = new PhetImageGraphic( component, FourierConstants.MINIMIZE_BUTTON_IMAGE );
        addGraphic( _minimizeButton, CONTROLS_LAYER );
        _minimizeButton.centerRegistrationPoint();
        _minimizeButton.setLocation( ( _minimizeButton.getWidth() / 2 ) + 10, _minimizeButton.getHeight() / 2 + 5 );

        // "Minimize" message
        String minimizeMessage = FourierResources.getString( "D2CHarmonicsView.minimize" );
        HTMLGraphic minimizeGraphic = new HTMLGraphic( component, MINIMIZE_MESSAGE_FONT, minimizeMessage, MINIMIZE_MESSAGE_COLOR );
        addGraphic( minimizeGraphic, MESSAGE_LAYER );
        minimizeGraphic.setRegistrationPoint( 0, minimizeGraphic.getHeight() / 2 ); // left center
        minimizeGraphic.setLocation( _minimizeButton.getX() + _minimizeButton.getWidth() / 2 + 10, _minimizeButton.getY() + 5 );

        // Zoom controls
        {
            _horizontalZoomControl = new PhetZoomControl( component, PhetZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( 620, 50 );
        }

        // Math
        {
            _mathGraphic = new HarmonicsEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            _mathGraphic.setLocation( 330, 28 );
        }

        // Interactivity
        {
            _backgroundGraphic.setIgnoreMouse( true );
            _titleGraphic.setIgnoreMouse( true );
            _flattenedChart.setIgnoreMouse( true );
            _mathGraphic.setIgnoreMouse( true );
            _cannotShowGraphic.setIgnoreMouse( true );
            minimizeGraphic.setIgnoreMouse( true );

            _horizontalZoomControl.addZoomListener( this );

            _minimizeButton.setCursorHand();
        }

        reset();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _wavePacket.removeObserver( this );
        _wavePacket = null;
    }

    //----------------------------------------------------------------------------
    // Reset
    //----------------------------------------------------------------------------

    /**
     * Resets to the initial state.
     */
    public void reset() {
        setDomain( Domain.SPACE );
        _waveType = WaveType.SINES;
        _xZoomLevel = 0;
        _chartGraphic.setRange( CHART_RANGE );
        refreshChart();
        updateZoomButtons();
        update();
        _updateRequired = false;
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
     * Sets the domain.
     * Changes various labels on the chart, tools, formulas, etc.
     *
     * @param domain DOMAIN_SPACE or DOMAIN_TIME
     */
    public void setDomain( Domain domain ) {
        _domain = domain;
        updateMath();
        updateAxisTitles();
    }

    /**
     * Sets the wave type.
     *
     * @param waveType WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public void setWaveType( WaveType waveType ) {
        _waveType = waveType;
        update();
        updateMath();
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
            _titleGraphic.setLocation( TITLE_LOCATION.x, height / 2 );
            _cannotShowGraphic.setLocation( 125, height / 2 );

            _chartGraphic.setChartSize( CHART_SIZE.width, height - 75 );
            refreshChart();

            setBoundsDirty();
        }
    }

    //----------------------------------------------------------------------------
    // PhetGraphic overrides
    //----------------------------------------------------------------------------

    /**
     * When this graphic becomes visible, update it.
     *
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( visible != super.isVisible() ) {
            super.setVisible( visible );
            if ( visible && _updateRequired ) {
                update();
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

        /*
         * The order in which we change the tick marks is important.  
         * If we're not careful, we may end up generating a huge 
         * number of ticks based on old/new settings.
         */
        if ( xRange > 3 ) {
            _chartGraphic.getHorizontalTicks().setMinorTicksVisible( false );
            _chartGraphic.getHorizontalTicks().setMajorTickSpacing( 1 );

            range.setMaxX( xRange );
            range.setMinX( -xRange );
            _chartGraphic.setRange( range );
        }
        else {
            range.setMaxX( xRange );
            range.setMinX( -xRange );
            _chartGraphic.setRange( range );

            _chartGraphic.getHorizontalTicks().setMinorTicksVisible( true );
            _chartGraphic.getHorizontalTicks().setMajorTickSpacing( 0.5 );
        }

        refreshChart();
        updateZoomButtons();
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

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Synchronizes the view with the model.
     * Called when the wave packet notifies us that it has been changed.
     */
    public void update() {

//        System.out.println( "D2CHarmonicsView.update" ); //XXX

        _updateRequired = true;

        if ( isVisible() ) {

            _updateRequired = false;

            // Clean up any existing HarmonicPlots
            DataSetGraphic[] dataSetGraphics = _chartGraphic.getDataSetGraphics();
            for ( int i = 0; i < dataSetGraphics.length; i++ ) {
                if ( dataSetGraphics[i] instanceof HarmonicPlot ) {
                    dataSetGraphics[i].cleanup();
                }
            }

            // Remove all plots from the chart.
            _chartGraphic.removeAllDataSetGraphics();

            double k1 = _wavePacket.getK1();
            double k0 = _wavePacket.getK0();
            double dk = _wavePacket.getDeltaK();

            int numberOfHarmonics = _wavePacket.getNumberOfComponents();

            if ( numberOfHarmonics < Integer.MAX_VALUE ) {

                _flattenedChart.setVisible( true );
                _horizontalZoomControl.setVisible( true );
                _mathGraphic.setVisible( true );
                _cannotShowGraphic.setVisible( false );

                // Change in grayscale value between bars.
                int deltaColor = ( HARMONIC_DARKEST_GRAY - HARMONIC_LIGHTEST_GRAY ) / numberOfHarmonics;

                double maxAmplitude = 0;

                double fundamentalPeriod = 2 * Math.PI / k1;

                // Re-populate the chart such that the fundamental's graphic is in the foreground.
                for ( int i = numberOfHarmonics - 1; i >= 0; i-- ) {

                    // Compute the amplitude
                    double kn = ( i + 1 ) * k1;
                    double An = k1 * GaussianWavePacket.getAmplitude( kn, k0, dk );
                    Harmonic harmonic = new Harmonic( i );
                    harmonic.setAmplitude( An );
                    if ( Math.abs( An ) > maxAmplitude ) {
                        maxAmplitude = Math.abs( An );
                    }

                    // Compute the bar color (grayscale).
                    int r = HARMONIC_DARKEST_GRAY - ( i * deltaColor );
                    int g = HARMONIC_DARKEST_GRAY - ( i * deltaColor );
                    int b = HARMONIC_DARKEST_GRAY - ( i * deltaColor );
                    Color harmonicColor = new Color( r, g, b );

                    // Harmonic waveform graphic
                    HarmonicPlot harmonicPlot = new HarmonicPlot( getComponent(), _chartGraphic );
                    harmonicPlot.setHarmonic( harmonic );
                    harmonicPlot.setPeriod( fundamentalPeriod / ( i + 1 ) );
                    harmonicPlot.setWaveType( _waveType );
                    harmonicPlot.setPixelsPerPoint( 1 );
                    harmonicPlot.setStroke( HARMONIC_STROKE );
                    harmonicPlot.setBorderColor( harmonicColor );
                    harmonicPlot.setStartX( 0 );

                    _chartGraphic.addDataSetGraphic( harmonicPlot );
                }

                // Autoscale the vertical axis.
                _chartGraphic.autoscaleY( maxAmplitude * FourierConstants.AUTOSCALE_PERCENTAGE );

                refreshChart();
            }
            else {
                _flattenedChart.setVisible( false );
                _horizontalZoomControl.setVisible( false );
                _mathGraphic.setVisible( false );
                _cannotShowGraphic.setVisible( true );
            }
        }
    }

    /*
    * Updates the math equation that appears above the graph.
    */
    private void updateMath() {
        int numberOfHarmonics = _wavePacket.getNumberOfComponents();
        if ( _domain == Domain.SPACE ) {
            _mathGraphic.setForm( _domain, MathForm.WAVE_NUMBER, _waveType );
        }
        else if ( _domain == Domain.TIME ) {
            _mathGraphic.setForm( _domain, MathForm.ANGULAR_FREQUENCY, _waveType );
        }
        _mathGraphic.centerRegistrationPoint();
    }

    /*
    * Update the titles on the axes.
    */
    private void updateAxisTitles() {
        if ( _domain == Domain.SPACE ) {
            _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.x.units" ) );
        }
        else if ( _domain == Domain.TIME ) {
            _chartGraphic.setXAxisTitle( FourierResources.getString( "axis.t.units" ) );
        }
        refreshChart();
    }

    private void refreshChart() {
        _flattenedChart.flatten();
    }
}
