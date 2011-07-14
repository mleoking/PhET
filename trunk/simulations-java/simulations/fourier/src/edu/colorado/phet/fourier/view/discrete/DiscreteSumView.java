// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.discrete;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.charts.*;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl.ZoomEvent;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetZoomControl.ZoomListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.*;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.FourierResources;
import edu.colorado.phet.fourier.charts.FourierSumPlot;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.Preset;
import edu.colorado.phet.fourier.enums.WaveType;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleEvent;
import edu.colorado.phet.fourier.view.AnimationCycleController.AnimationCycleListener;


/**
 * DiscreteSumView is the "Sum" view in the "Discrete" module.
 * It displays the sum of a Fourier series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteSumView extends GraphicLayerSet implements SimpleObserver, ZoomListener, AnimationCycleListener {

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
    private static final Point TITLE_LOCATION = new Point( 40, 135 );

    // Chart parameters
    private static final double L = FourierConstants.L;
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double Y_RANGE_START = FourierConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MIN = FourierConstants.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 540, 135 );

    // Zoom parameters
    private static final int Y_ZOOM_STEP = 2;

    // Wave parameters
    private static final Stroke SUM_STROKE = new BasicStroke( 1f );
    private static final Color SUM_COLOR = Color.BLACK;
    private static final double SUM_PIXELS_PER_POINT = 1;
    private static final Stroke PRESET_STROKE = new BasicStroke( 4f );
    private static final Color PRESET_COLOR = Color.LIGHT_GRAY;

    // Math parameters
    private static final Font MATH_FONT = new PhetFont( Font.PLAIN, 18 );
    private static final Color MATH_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _fourierSeries;
    private PhetShapeGraphic _backgroundGraphic;
    private PhetTextGraphic _titleGraphic;
    private PhetImageGraphic _minimizeButton;
    private DiscreteSumChart _chartGraphic;
    private DiscreteSumEquation _mathGraphic;
    private FourierSumPlot _sumPlot;
    private LinePlot _presetPlot;
    private SinePlot _sineCosinePresetPlot;
    private PhetZoomControl _horizontalZoomControl, _verticalZoomControl;
    private JCheckBox _autoScaleCheckBox;
    private PhetGraphic _autoScaleGraphic;

    private int _xZoomLevel;
    private Domain _domain;
    private MathForm _mathForm;
    private boolean _presetEnabled;

    private int _previousNumberOfHarmonics;
    private Preset _previousPreset;
    private WaveType _previousWaveType;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    public DiscreteSumView( Component component, FourierSeries fourierSeries ) {
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
        String title = FourierResources.getString( "DiscreteSumView.title" );
        _titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        _titleGraphic.centerRegistrationPoint();
        _titleGraphic.rotate( -( Math.PI / 2 ) );
        _titleGraphic.setLocation( TITLE_LOCATION );
        addGraphic( _titleGraphic, TITLE_LAYER );

        // Chart
        {
            _chartGraphic = new DiscreteSumChart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            _chartGraphic.setRegistrationPoint( 0, 0 );
            _chartGraphic.setLocation( 60, 50 );

            // Sine/cosine preset plot
            _sineCosinePresetPlot = new SinePlot( getComponent(), _chartGraphic );
            _sineCosinePresetPlot.setAmplitude( 1 );
            _sineCosinePresetPlot.setPeriod( L );
            _sineCosinePresetPlot.setPixelsPerPoint( SUM_PIXELS_PER_POINT ); // same as Sum plot!
            _sineCosinePresetPlot.setStroke( PRESET_STROKE );
            _sineCosinePresetPlot.setBorderColor( PRESET_COLOR );
            _sineCosinePresetPlot.setStartX( 0 );
            _chartGraphic.addDataSetGraphic( _sineCosinePresetPlot );

            // Plot for all other presets
            _presetPlot = new LinePlot( getComponent(), _chartGraphic, new DataSet(), PRESET_STROKE, PRESET_COLOR );
            _chartGraphic.addDataSetGraphic( _presetPlot );

            // Sum plot
            _sumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _fourierSeries );
            _sumPlot.setPeriod( L );
            _sumPlot.setPixelsPerPoint( SUM_PIXELS_PER_POINT );
            _sumPlot.setStroke( SUM_STROKE );
            _sumPlot.setBorderColor( SUM_COLOR );
            _chartGraphic.addDataSetGraphic( _sumPlot );
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

            _autoScaleCheckBox = new JCheckBox( FourierResources.getString( "DiscreteSumView.autoScale" ) );
            _autoScaleCheckBox.setBackground( new Color( 255, 255, 255, 0 ) );
            _autoScaleGraphic = PhetJComponent.newInstance( component, _autoScaleCheckBox );
            addGraphic( _autoScaleGraphic, CONTROLS_LAYER );
            // Aligned with the bottom of the chart.
            _autoScaleGraphic.setLocation( _horizontalZoomControl.getX(),
                                           _chartGraphic.getY() + _chartGraphic.getHeight() - _autoScaleGraphic.getHeight() );

            _verticalZoomControl = new PhetZoomControl( component, PhetZoomControl.VERTICAL );
            addGraphic( _verticalZoomControl, CONTROLS_LAYER );
            // Just above the autoscale check box.
            _verticalZoomControl.setLocation( _horizontalZoomControl.getX(),
                                              _autoScaleGraphic.getY() - _verticalZoomControl.getHeight() - 5 );
        }

        // Math
        {
            _mathGraphic = new DiscreteSumEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            // Location is above the center of the chart.
            int x = _chartGraphic.getX() + ( CHART_SIZE.width / 2 );
            int y = 30;
            _mathGraphic.setLocation( x, y );
        }

        // Interactivity
        {
            _backgroundGraphic.setIgnoreMouse( true );
            _titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );
            _mathGraphic.setIgnoreMouse( true );

            _minimizeButton.setCursorHand();

            _horizontalZoomControl.addZoomListener( this );
            _verticalZoomControl.addZoomListener( this );

            _autoScaleCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    updateZoomButtons();
                    update();
                }
            } );
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
        _verticalZoomControl.removeAllZoomListeners();
    }

    //----------------------------------------------------------------------------
    // Reset
    //----------------------------------------------------------------------------

    /**
     * Resets to the initial state.
     */
    public void reset() {

        // Chart
        {
            _xZoomLevel = 0;
            _chartGraphic.setRange( CHART_RANGE );
            _autoScaleCheckBox.setSelected( false );
            updateLabelsAndLines();
            updateZoomButtons();

            _presetPlot.setVisible( false );
            _sineCosinePresetPlot.setVisible( false );
        }

        _domain = Domain.SPACE;

        // Math Mode
        _mathForm = MathForm.WAVE_NUMBER;
        _mathGraphic.setVisible( false );
        updateMath();

        // Synchronize with model
        _previousNumberOfHarmonics = 0; // force an update
        _previousPreset = Preset.UNDEFINED;
        _previousWaveType = WaveType.UNDEFINED;
        _presetEnabled = false;
        update();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the horizontal zoom control.
     * Uses to set up linking of zoom controls on different user interfaces.
     *
     * @return the horizontal zoom control
     */
    public PhetZoomControl getHorizontalZoomControl() {
        return _horizontalZoomControl;
    }

    /**
     * Gets the vertical zoom control.
     *
     * @return the vertical zoom control
     */
    public PhetZoomControl getVerticalZoomControl() {
        return _verticalZoomControl;
    }

    /**
     * Gets the auto-scale control.
     *
     * @return the auto-scale control
     */
    public PhetGraphic getAutoScaleControl() {
        return _autoScaleGraphic;
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
        _previousPreset = Preset.UNDEFINED; // force update
        update();
    }

    /**
     * Turns visibility of the infinite preset waveform on/off.
     *
     * @param enabled
     */
    public void setPresetEnabled( boolean enabled ) {
        _presetEnabled = enabled;
        _sineCosinePresetPlot.setVisible( false );
        _presetPlot.setVisible( false );
        if ( _presetEnabled ) {
            if ( _fourierSeries.getPreset() == Preset.SINE_COSINE ) {
                _sineCosinePresetPlot.setVisible( true );
                _sineCosinePresetPlot.setCosineEnabled( _fourierSeries.getWaveType() == WaveType.COSINES );
            }
            else {
                _presetPlot.setVisible( true );
            }
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
            _autoScaleGraphic.setLocation( _horizontalZoomControl.getX(),
                                           _chartGraphic.getY() + (int) _chartGraphic.getChartSize().getHeight() - _autoScaleGraphic.getHeight() + 15 );
            _verticalZoomControl.setLocation( _horizontalZoomControl.getX(),
                                              _autoScaleGraphic.getY() - _verticalZoomControl.getHeight() - 5 );
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

            _sumPlot.updateDataSet();

            // If auto scaling is enabled, adjust the vertical scale to fit the curve.
            if ( _autoScaleCheckBox.isSelected() ) {
                Range2D range = _chartGraphic.getRange();
                double maxAmplitude = _sumPlot.getMaxAmplitude() * FourierConstants.AUTOSCALE_PERCENTAGE;
                if ( maxAmplitude < FourierConstants.MAX_HARMONIC_AMPLITUDE ) {
                    maxAmplitude = FourierConstants.MAX_HARMONIC_AMPLITUDE;
                }
                if ( maxAmplitude != range.getMaxY() ) {
                    range.setMinY( -maxAmplitude );
                    range.setMaxY( +maxAmplitude );
                    _chartGraphic.setRange( range );
                    updateLabelsAndLines();
                    updateZoomButtons();
                }
            }

            // Reset the sum waveform, and update the preset waveform.
            int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();
            Preset preset = _fourierSeries.getPreset();
            WaveType waveType = _fourierSeries.getWaveType();

            if ( numberOfHarmonics != _previousNumberOfHarmonics || preset != _previousPreset || waveType != _previousWaveType ) {

                // Sum
                _sumPlot.setStartX( 0 );

                // Sine/cosine preset
                _sineCosinePresetPlot.setStartX( 0 );

                // Other preset
                _presetPlot.getDataSet().clear();
                Point2D[] points = Preset.getPresetPoints( preset, waveType );
                if ( points != null ) {
                    Point2D[] copyPoints = new Point2D[points.length];
                    for ( int i = 0; i < points.length; i++ ) {
                        copyPoints[i] = new Point2D.Double( points[i].getX(), points[i].getY() );
                    }
                    _presetPlot.getDataSet().addAllPoints( copyPoints );
                }

                // Preset visibility
                _sineCosinePresetPlot.setVisible( false );
                _presetPlot.setVisible( false );
                if ( _presetEnabled ) {
                    if ( _fourierSeries.getPreset() == Preset.SINE_COSINE ) {
                        _sineCosinePresetPlot.setVisible( true );
                        _sineCosinePresetPlot.setCosineEnabled( _fourierSeries.getWaveType() == WaveType.COSINES );
                    }
                    else {
                        _presetPlot.setVisible( true );
                    }
                }

                // Math
                updateMath();

                _previousNumberOfHarmonics = numberOfHarmonics;
                _previousPreset = preset;
                _previousWaveType = waveType;
            }
        }
    }

    //----------------------------------------------------------------------------
    // ZoomListener implementation
    //----------------------------------------------------------------------------

    public void zoomPerformed( ZoomEvent event ) {
        int zoomType = event.getZoomType();
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN || zoomType == ZoomEvent.HORIZONTAL_ZOOM_OUT ) {
            handleHorizontalZoom( zoomType );
        }
        else if ( zoomType == ZoomEvent.VERTICAL_ZOOM_IN || zoomType == ZoomEvent.VERTICAL_ZOOM_OUT ) {
            handleVerticalZoom( zoomType );
        }
        else {
            throw new IllegalArgumentException( "unexpected event: " + event );
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    /*
    * Handles horizontal zooming.
    *
    * @param actionID indicates the type of zoom
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
    * Handles vertical zooming.
    *
    * @param actionID indicates the type of zoom
    */
    private void handleVerticalZoom( int zoomType ) {

        // Get the chart's vertical range.
        Range2D range = _chartGraphic.getRange();
        double yRange = range.getMaxY();

        // Round to an integral multiple of Y_ZOOM_STEP.
        if ( yRange % Y_ZOOM_STEP > 0 ) {
            yRange = ( (int) ( yRange / Y_ZOOM_STEP ) ) * Y_ZOOM_STEP;
        }

        // Adjust the scale.
        if ( zoomType == ZoomEvent.VERTICAL_ZOOM_IN ) {
            yRange -= Y_ZOOM_STEP;
        }
        else {
            yRange += Y_ZOOM_STEP;
        }

        // Constrain the scale's range.
        if ( yRange < Y_RANGE_MIN ) {
            yRange = Y_RANGE_MIN;
        }
        else if ( yRange > Y_RANGE_MAX ) {
            yRange = Y_RANGE_MAX;
        }

        // Change the chart's vertical range.
        range.setMaxY( yRange );
        range.setMinY( -yRange );
        _chartGraphic.setRange( range );

        updateLabelsAndLines();
        updateZoomButtons();
    }

    /*
    * Adjusts labels, ticks and gridlines to match the chart range.
    */
    private void updateLabelsAndLines() {

        // X axis labels
        if ( _mathGraphic.isVisible() ) {
            // If math mode is enabled, use symbolic labels.
            LabelTable labelTable = null;
            if ( _domain == Domain.TIME ) {
                if ( _xZoomLevel > -3 ) {
                    labelTable = _chartGraphic.getTimeLabels1();
                }
                else {
                    labelTable = _chartGraphic.getTimeLabels2();
                }
            }
            else { /* DOMAIN_SPACE or DOMAIN_SPACE_AND_TIME */
                if ( _xZoomLevel > -3 ) {
                    labelTable = _chartGraphic.getSpaceLabels1();
                }
                else {
                    labelTable = _chartGraphic.getSpaceLabels2();
                }
            }
            _chartGraphic.getHorizontalTicks().setMajorLabels( labelTable );
        }
        else {
            // If math mode is disabled, use numeric labels.
            _chartGraphic.getHorizontalTicks().setMajorLabels( null );
        }

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

        // Y axis ticks and gridlines
        {
            Range2D range = _chartGraphic.getRange();
            double tickSpacing;
            if ( range.getMaxY() < 2 ) {
                tickSpacing = 0.5;
            }
            else if ( range.getMaxY() < 5 ) {
                tickSpacing = 1.0;
            }
            else {
                tickSpacing = 5.0;
            }
            _chartGraphic.getVerticalTicks().setMajorTickSpacing( tickSpacing );
            _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( tickSpacing );
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

        // Vertical buttons
        if ( _autoScaleCheckBox.isSelected() ) {
            _verticalZoomControl.setZoomOutEnabled( false );
            _verticalZoomControl.setZoomInEnabled( false );
        }
        else if ( range.getMaxY() >= Y_RANGE_MAX ) {
            _verticalZoomControl.setZoomOutEnabled( false );
            _verticalZoomControl.setZoomInEnabled( true );
        }
        else if ( range.getMaxY() <= Y_RANGE_MIN ) {
            _verticalZoomControl.setZoomOutEnabled( true );
            _verticalZoomControl.setZoomInEnabled( false );
        }
        else {
            _verticalZoomControl.setZoomOutEnabled( true );
            _verticalZoomControl.setZoomInEnabled( true );
        }
    }

    /*
    * Updates the math equation that appears above the chart.
    */
    private void updateMath() {
        _mathGraphic.setForm( _domain, _mathForm, _fourierSeries.getNumberOfHarmonics(), _fourierSeries.getWaveType() );
        _mathGraphic.centerRegistrationPoint();
    }

    //----------------------------------------------------------------------------
    // AnimationCycleListener implementation
    //----------------------------------------------------------------------------

    /**
     * Handles animation events.
     * Animates the sum waveform by adjusting its phase (aka, start times).
     * Animates the infinite waveform by moving each point by some delta.
     *
     * @param event
     */
    public void animate( AnimationCycleEvent event ) {
        if ( _domain == Domain.SPACE_AND_TIME ) {

            /*
            * To animate the sum plot, shift its phase based on
            * where we are in the animation cycle.
            */
            double startX = event.getCyclePoint() * L;
            _sumPlot.setStartX( startX );

            // Animate the preset.
            if ( _fourierSeries.getPreset() == Preset.SINE_COSINE ) {
                // To animate sine/cosine preset, set the phase to be the same as the sum.
                _sineCosinePresetPlot.setStartX( startX );
            }
            else {
                // To animate non-sine/cosine presets, advance their data points by delta X.
                double deltaX = event.getDelta() * L;
                Point2D[] points = _presetPlot.getDataSet().getPoints();
                if ( points != null ) {
                    _presetPlot.getDataSet().clear();
                    for ( int i = 0; i < points.length; i++ ) {
                        points[i].setLocation( points[i].getX() + deltaX, points[i].getY() );
                    }
                    _presetPlot.getDataSet().addAllPoints( points );
                }
            }
        }
    }
}
