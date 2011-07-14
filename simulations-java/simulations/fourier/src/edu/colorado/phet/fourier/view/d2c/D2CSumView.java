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
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.charts.Chart;
import edu.colorado.phet.common.charts.DataSet;
import edu.colorado.phet.common.charts.LinePlot;
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
import edu.colorado.phet.fourier.charts.FlattenedChart;
import edu.colorado.phet.fourier.charts.FourierSumPlot;
import edu.colorado.phet.fourier.charts.GaussianWavePacketPlot;
import edu.colorado.phet.fourier.charts.WavePacketXWidthPlot;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.GaussianWavePacket;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * D2CSumView is the "Sum" view in the "Discrete to Continuous" module.
 * It displays the Fourier sum of a Gaussian wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CSumView extends GraphicLayerSet implements SimpleObserver, ZoomListener {

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
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, MIN_HEIGHT );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;

    // Title parameters
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Point TITLE_LOCATION = new Point( 40, 115 );

    // Chart parameters
    private static final double X_RANGE_START = D2CComponentsView.X_RANGE_START;
    private static final double X_RANGE_MIN = D2CComponentsView.X_RANGE_MIN;
    private static final double X_RANGE_MAX = D2CComponentsView.X_RANGE_MAX;
    private static final double Y_RANGE_START = 1;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 100 );

    // Sum waveform
    private static final Stroke SUM_STROKE = new BasicStroke( 1f );
    private static final Color SUM_COLOR = Color.BLACK;
    private static final double SUM_PIXELS_PER_POINT = 0.25;

    // Gaussian wave packet waveform
    private static final Stroke WAVE_PACKET_STROKE = SUM_STROKE;
    private static final Color WAVE_PACKET_COLOR = SUM_COLOR;
    private static final double WAVE_PACKET_PIXELS_PER_POINT = SUM_PIXELS_PER_POINT;

    // Envelope waveform
    private static final double ENVELOPE_STEP = Math.PI / 10; // about one value for every 2 pixels
    private static final Color ENVELOPE_COLOR = Color.LIGHT_GRAY;
    private static final Stroke ENVELOPE_STROKE = new BasicStroke( 4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

    // Autoscaling
    private static final double AUTOSCALE_FACTOR = 1.25; // multiple max amplitude by this amount when autoscaling

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GaussianWavePacket _wavePacket;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private PhetZoomControl _horizontalZoomControl;
    private D2CSumChart _chartGraphic;
    private FlattenedChart _flattenedChart;
    private D2CSumEquation _mathGraphic;
    private Domain _domain;
    private WaveType _waveType;
    private int _xZoomLevel;
    private FourierSeries _fourierSeries;
    private FourierSumPlot _sumPlot;
    private GaussianWavePacketPlot _wavePacketPlot;
    private LinePlot _envelopeGraphic;
    private boolean _envelopeEnabled;
    private WavePacketXWidthPlot _xWidthPlot;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component  the parent Component
     * @param wavePacket the Gaussian wave packet being displayed
     */
    public D2CSumView( Component component, GaussianWavePacket wavePacket ) {
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
        String title = FourierResources.getString( "D2CSumView.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( 40, BACKGROUND_SIZE.height / 2 );
        addGraphic( _titleGraphic, TITLE_LAYER );

        // Chart
        {
            _chartGraphic = new D2CSumChart( component, CHART_RANGE, CHART_SIZE );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 0, 0 );

            _xWidthPlot = new WavePacketXWidthPlot( component, _chartGraphic, _wavePacket );
            _chartGraphic.addDataSetGraphic( _xWidthPlot );

            int xOffset = 25;
            int yOffset = 25;
            _flattenedChart = new FlattenedChart( component, _chartGraphic, xOffset, yOffset );
            addGraphic( _flattenedChart, CHART_LAYER );
            _flattenedChart.setRegistrationPoint( 0, 0 ); // upper left
            _flattenedChart.setLocation( 60 - xOffset, 50 - yOffset );
        }

        // Fourier series
        _fourierSeries = new FourierSeries( 1, FourierConstants.FUNDAMENTAL_FREQUENCY );
        _fourierSeries.setPreset( Preset.CUSTOM );

        // Fourier sum plot
        _sumPlot = new FourierSumPlot( component, _chartGraphic, _fourierSeries );
        _sumPlot.setPixelsPerPoint( SUM_PIXELS_PER_POINT );
        _sumPlot.setStroke( SUM_STROKE );
        _sumPlot.setStrokeColor( SUM_COLOR );

        // Gaussian wave packet plot
        _wavePacketPlot = new GaussianWavePacketPlot( component, _chartGraphic );
        _wavePacketPlot.setPixelsPerPoint( WAVE_PACKET_PIXELS_PER_POINT );
        _wavePacketPlot.setStroke( WAVE_PACKET_STROKE );
        _wavePacketPlot.setStrokeColor( WAVE_PACKET_COLOR );

        // Envelope waveform
        _envelopeGraphic = new LinePlot( component, _chartGraphic );
        _envelopeGraphic.setBorderColor( ENVELOPE_COLOR );
        _envelopeGraphic.setStroke( ENVELOPE_STROKE );
        _envelopeGraphic.setDataSet( new DataSet() );

        // Close button
        _minimizeButton = new PhetImageGraphic( component, FourierConstants.MINIMIZE_BUTTON_IMAGE );
        addGraphic( _minimizeButton, CONTROLS_LAYER );
        _minimizeButton.centerRegistrationPoint();
        _minimizeButton.setLocation( ( _minimizeButton.getWidth() / 2 ) + 10, _minimizeButton.getHeight() / 2 + 5 );

        // Zoom controls
        {
            _horizontalZoomControl = new PhetZoomControl( component, PhetZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( 620, 50 );
        }

        // Math
        {
            _mathGraphic = new D2CSumEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            _mathGraphic.setLocation( 330, 30 );
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
        _envelopeEnabled = false;
        _envelopeGraphic.setVisible( _envelopeEnabled );
        setDomain( Domain.SPACE );
        _waveType = WaveType.SINES;
        _xZoomLevel = 0;
        _chartGraphic.setRange( CHART_RANGE );
        refreshChart();
        updateZoomButtons();
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
     * Gets a reference to the chart.
     *
     * @return Chart
     */
    public Chart getChart() {
        return _chartGraphic;
    }

    /**
     * Sets the domain.
     * Changes various labels on the chart, tools, formulas, etc.
     *
     * @param domain DOMAIN_SPACE or DOMAIN_TIME
     */
    public void setDomain( Domain domain ) {
        _domain = domain;
        _xWidthPlot.setDomain( domain );
        updateMath();
        updateAxisTitles();
    }

    /**
     * Sets the wave type.
     *
     * @param waveType WAVE_TYPE_SINE or WAVE_TYPE_COSINE
     */
    public void setWaveType( WaveType waveType ) {
        if ( waveType != _waveType ) {
            _waveType = waveType;
            update();
            updateMath();
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
            _titleGraphic.setLocation( TITLE_LOCATION.x, height / 2 );

            _chartGraphic.setChartSize( CHART_SIZE.width, height - 75 );
            refreshChart();

            setBoundsDirty();
        }
    }

    /**
     * Turns the continuous waveform display on and off.
     *
     * @param enabled
     */
    public void setEnvelopeEnabled( boolean enabled ) {
        _envelopeEnabled = enabled;
        if ( enabled ) {
            updateEnvelope();
        }
        _envelopeGraphic.setVisible( enabled );
        refreshChart();
    }

    /**
     * Is the envelope waveform display enabled?
     *
     * @return true or false
     */
    public boolean isEnvelopeEnabled() {
        return _envelopeEnabled;
    }

    /**
     * Controls the visibility of the x-width plot on the chart.
     *
     * @param visible
     */
    public void setXWidthVisible( boolean visible ) {
        _xWidthPlot.setVisible( visible );
        refreshChart();
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

        updateEnvelope();
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

//        System.out.println( "D2CSumView.update" ); //XXX

        updateMath(); // ...in case the number of components has changed

        _chartGraphic.removeAllDataSetGraphics();

        // Envelope needs to be behind everything else, so add it first.
        _envelopeGraphic.setVisible( _envelopeEnabled );
        _chartGraphic.addDataSetGraphic( _envelopeGraphic );

        double k1 = _wavePacket.getK1();
        if ( k1 > 0 ) {
            addFourierSeriesPlot();
        }
        else {
            addContinuousPlot();
        }

        // Update the envelope last, so that the Fourier series is in-sync with the wave packet
        if ( _envelopeEnabled ) {
            updateEnvelope();
        }

        // Re-add the x width plot
        _xWidthPlot.update();
        _chartGraphic.addDataSetGraphic( _xWidthPlot );

        refreshChart();
    }

    //----------------------------------------------------------------------------
    // Methods that update graphics
    //----------------------------------------------------------------------------

    /*
     * Adds a plot of the Fourier series that corresponds to the wave packet.
     */
    private void addFourierSeriesPlot() {

        double k1 = _wavePacket.getK1();
        assert ( k1 > 0 );
        double dk = _wavePacket.getDeltaK();
        double k0 = _wavePacket.getK0();

        int numberOfHarmonics = _wavePacket.getNumberOfComponents();

        _fourierSeries.setNumberOfHarmonics( numberOfHarmonics );
        _fourierSeries.setWaveType( _waveType );

        // Adjust the Fourier series to match the wave packet
        for ( int i = 0; i < numberOfHarmonics; i++ ) {

            // Compute the ith amplitude
            double kn = ( i + 1 ) * k1;
            double An = k1 * GaussianWavePacket.getAmplitude( kn, k0, dk );

            // Adjust the ith harmonic
            _fourierSeries.getHarmonic( i ).setAmplitude( An );
        }

        _sumPlot.setPeriod( 2 * Math.PI / k1 );
        _sumPlot.updateDataSet();

        _chartGraphic.addDataSetGraphic( _sumPlot );
        _chartGraphic.autoscaleY( _sumPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
    }

    /*
     * Adds a continuous waveform that corresponds to the wave packet.
     */
    private void addContinuousPlot() {

        _wavePacketPlot.setK0( _wavePacket.getK0() );
        _wavePacketPlot.setDeltaX( _wavePacket.getDeltaX() );
        _wavePacketPlot.setWaveType( _waveType );

        _chartGraphic.addDataSetGraphic( _wavePacketPlot );
        _chartGraphic.autoscaleY( _wavePacketPlot.getMaxAmplitude() * AUTOSCALE_FACTOR );
    }

    /*
     * Populates a LinePlot with a set of points that approximates the envelope waveform.
     * We do this by using the data from 2 FourierPlots -- one for sine and one for cosine --
     * and computing the following function:
     * 
     * F(x) = sqrt( s^2 + c^2 )
     * where s is F(x) using sine, and c is F(x) using cosine
     */
    private void updateEnvelope() {

        // Clear the data set.
        DataSet dataSet = _envelopeGraphic.getDataSet();
        dataSet.clear();

        // Get data points for F(x) using sines and cosines
        Point2D[] points1;
        Point2D[] points2;
        if ( _wavePacket.getK1() > 0 ) {
            // Spacing is > 0, so use the Fourier sum data.

            // Create a copy of the fourier series that we already have, 
            // but invert the wave type (sines or cosines).
            FourierSeries fourierSeries = new FourierSeries();
            {
                fourierSeries.setPreset( Preset.CUSTOM );
                fourierSeries.setNumberOfHarmonics( _fourierSeries.getNumberOfHarmonics() );
                fourierSeries.setFundamentalFrequency( _fourierSeries.getFundamentalFrequency() );
                for ( int i = 0; i < fourierSeries.getNumberOfHarmonics(); i++ ) {
                    Harmonic harmonic = _fourierSeries.getHarmonic( i );
                    fourierSeries.getHarmonic( i ).setAmplitude( harmonic.getAmplitude() );
                }
                if ( _fourierSeries.getWaveType() == WaveType.SINES ) {
                    fourierSeries.setWaveType( WaveType.COSINES );
                }
                else {
                    fourierSeries.setWaveType( WaveType.SINES );
                }
            }

            // Compute the data for the new Fourier series using a FourierPlot.
            FourierSumPlot sumPlot = new FourierSumPlot( getComponent(), _chartGraphic, fourierSeries );
            sumPlot.setPeriod( _sumPlot.getPeriod() );
            sumPlot.setPixelsPerPoint( _sumPlot.getPixelsPerPoint() );
            sumPlot.updateDataSet();

            points1 = _sumPlot.getDataSet().getPoints();
            points2 = sumPlot.getDataSet().getPoints();
        }
        else {
            // Spacing is zero, so use the continuous data.

            // Create a wave packet plot with inverted wave type (sines or cosines)
            GaussianWavePacketPlot wavePacketPlot = new GaussianWavePacketPlot( getComponent(), _chartGraphic );
            wavePacketPlot.setPixelsPerPoint( _wavePacketPlot.getPixelsPerPoint() );
            wavePacketPlot.setK0( _wavePacket.getK0() );
            wavePacketPlot.setDeltaX( _wavePacket.getDeltaX() );
            if ( _wavePacketPlot.getWaveType() == WaveType.SINES ) {
                wavePacketPlot.setWaveType( WaveType.COSINES );
            }
            else {
                wavePacketPlot.setWaveType( WaveType.SINES );
            }

            points1 = _wavePacketPlot.getDataSet().getPoints();
            points2 = wavePacketPlot.getDataSet().getPoints();
        }
        assert ( points1.length == points2.length );

        // Compute the envelope
        ArrayList envelopePoints = new ArrayList();
        int numberOfPoints = Math.max( points1.length, points2.length );
        for ( int i = 0; i < numberOfPoints; i++ ) {
            double x = points1[i].getX();
            double y = Math.sqrt( ( points1[i].getY() * points1[i].getY() ) + ( points2[i].getY() * points2[i].getY() ) );
            envelopePoints.add( new Point2D.Double( x, y ) );
        }

        // Add the envelope points to the data set.
        dataSet.addPoints( (Point2D.Double[]) envelopePoints.toArray( new Point2D.Double[envelopePoints.size()] ) );
    }

    /*
     * Updates the math equation that appears above the graph.
     */
    private void updateMath() {
        boolean infinity = ( _wavePacket.getK1() == 0 );
        _mathGraphic.setForm( _domain, infinity, _waveType );
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

    /*
     * Refreshes the chart.
     * Call this after making any changes to the chart so that it is re-flattened.
     */
    private void refreshChart() {
        _flattenedChart.flatten();
    }
}
