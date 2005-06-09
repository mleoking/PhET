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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JCheckBox;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * SumGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SumGraphic extends GraphicLayerSet implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double TITLE_LAYER = 1;
    private static final double CHART_LAYER = 2;
    private static final double CONTROLS_LAYER = 3;

    // Title parameters
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin
    
    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    
    // Tick Mark parameter
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;
    private static final Color MINOR_TICK_COLOR = MAJOR_TICK_COLOR;
    
    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
    private static final Color MINOR_GRIDLINE_COLOR = new Color( 0, 0, 0, 100 );
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.5f );
    
    // X axis
    private static final double L = 1.0; // arbitrary value for the symbol L (length of the string)
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

    // Y axis
    private static final double Y_RANGE_START = 3.0;
    private static final double Y_RANGE_MIN = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final double Y_MAJOR_TICK_SPACING = 5.0;
    private static final double Y_MINOR_TICK_SPACING = 1.0;
    private static final int Y_ZOOM_STEP = 2;
    
    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 600, 130 );
    
    // Wave parameters
    private static final Stroke WAVE_STROKE = new BasicStroke( 1f );
    private static final int NUMBER_OF_DATA_POINTS = 1000;
    private static final int MAX_FUNDAMENTAL_CYCLES = 4;
    private static final Color SUM_COLOR = Color.BLACK; 
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private Chart _chartGraphic;
    private DataSet _sumDataSet;
    private DataSet _presetDataSet;
    private int _waveType;
    private ZoomControl _horizontalZoomControl, _verticalZoomControl;
    private JCheckBox _autoScaleCheckBox;
    private int _xZoomLevel;
    private LabelTable _spaceLabels1, _spaceLabels2;
    private boolean _autoScaleEnabled;
    private Point2D[] _points;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public SumGraphic( Component component, FourierSeries fourierSeriesModel ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Model
        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Title
        String title = SimStrings.get( "SumGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );
        
        // Chart
        {
            _chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );
            
            _chartGraphic.setLocation( 0, -( CHART_SIZE.height / 2 ) );

            // Symbolic labels for the X axis
            {
                _spaceLabels1 = new LabelTable();
                _spaceLabels1.put( -1.00 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.75 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.50 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.25 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put(     0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put(  0.25 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put(  0.50 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put(  0.75 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put(  1.00 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L", MAJOR_TICK_COLOR ) );
                
                _spaceLabels2 = new LabelTable();
                _spaceLabels2.put( -2.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-2L", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -1.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -1.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -0.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put(    0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put(  0.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put(  1.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put(  1.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put(  2.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "2L", MAJOR_TICK_COLOR ) );
            }
            
            // X axis
            {
                _chartGraphic.getXAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getXAxis().setColor( AXIS_COLOR );

                // No ticks or labels on the axis
                _chartGraphic.getXAxis().setMajorTicksVisible( false );
                _chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getXAxis().setMinorTicksVisible( false );
                _chartGraphic.getXAxis().setMinorTickLabelsVisible( false );
                
                // Major ticks with labels below the chart
                _chartGraphic.getHorizontalTicks().setMajorTicksVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizontalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                _chartGraphic.getHorizontalTicks().setMajorTickFont( MAJOR_TICK_FONT );
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );

                // Vertical gridlines for major ticks.
                _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );
                
                // Vertical gridlines for minor ticks.
                _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMinorTickSpacing( X_MINOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
            }
            
            // Y axis
            {
                _chartGraphic.getYAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getYAxis().setColor( AXIS_COLOR );
                
                // No ticks or labels on the axis
                _chartGraphic.getYAxis().setMajorTicksVisible( false );
                _chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getYAxis().setMinorTicksVisible( false );
                _chartGraphic.getYAxis().setMinorTickLabelsVisible( false );
                
                // Major ticks with labels to the left of the chart
                _chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                _chartGraphic.getVerticalTicks().setMajorTickFont( MAJOR_TICK_FONT );

                // Horizontal gridlines for major ticks
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

                // Horizontal gridlines for minor ticks
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
            }
        }
        
        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( CHART_SIZE.width + 10, -50 );
            
            _verticalZoomControl = new ZoomControl( component, ZoomControl.VERTICAL );
            addGraphic( _verticalZoomControl, CONTROLS_LAYER );
            _verticalZoomControl.setLocation( _horizontalZoomControl.getX(), 
                    _horizontalZoomControl.getY() + _horizontalZoomControl.getHeight() + 5 );
        }
        
        // Auto Scale control
        {
            _autoScaleCheckBox = new JCheckBox( "Auto scale" );//XXX i18n
            _autoScaleCheckBox.setOpaque( false );
            PhetGraphic graphic = PhetJComponent.newInstance( component, _autoScaleCheckBox );
            addGraphic( graphic, CONTROLS_LAYER );
            graphic.setLocation( _verticalZoomControl.getX(), 
                    _verticalZoomControl.getY() + _verticalZoomControl.getHeight() + 5 );
            graphic.scale( 0.8 );
        }
        
        // Sum data set
        _sumDataSet = new DataSet();
        DataSetGraphic sumDataSetGraphic = new LinePlot( getComponent(), _chartGraphic, _sumDataSet, WAVE_STROKE, SUM_COLOR );
        _chartGraphic.addDataSetGraphic( sumDataSetGraphic );
        
        // Preset data set
        _presetDataSet = new DataSet();
        DataSetGraphic presetDataSetGraphic = new LinePlot( getComponent(), _chartGraphic, _presetDataSet, WAVE_STROKE, SUM_COLOR );
        _chartGraphic.addDataSetGraphic( presetDataSetGraphic );
        
        // Interactivity
        {
            titleGraphic.setIgnoreMouse( true );
            // XXX others setIgnoreMouse ?
            EventListener listener = new EventListener();
            _horizontalZoomControl.addActionListener( listener );
            _verticalZoomControl.addActionListener( listener );
            _autoScaleCheckBox.addActionListener( listener );
        }
        
        // Misc initialization
        {
            _xZoomLevel = 0;
            _autoScaleEnabled = false;
            
            _points = new Point2D[ NUMBER_OF_DATA_POINTS + 1 ];
            for ( int i = 0; i < _points.length; i++ ) {
                _points[ i ] = new Point2D.Double();
            }
        }
        
        updateZoomButtons();
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _fourierSeriesModel.removeObserver( this );
        _fourierSeriesModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the wave type, sine or cosine.
     * 
     * @param waveType FourierConstants.WAVE_TYPE_SINE or FourierConstants.WAVE_TYPE_COSINE
     */
    public void setWaveType( int waveType ) {
       if ( waveType != _waveType ) {
           _waveType = waveType;
           update();
       }
    }
    
    /**
     * Gets the wave type.
     * 
     * @return FourierConstants.WAVE_TYPE_SINE or FourierConstants.WAVE_TYPE_COSINE
     */
    public int getWaveType() {
        return _waveType;
    }
    
    public void setAutoScaleEnabled( boolean autoRescaleEnabled ) {
        if ( autoRescaleEnabled != _autoScaleEnabled ) {
            _autoScaleEnabled = autoRescaleEnabled;
            updateZoomButtons();
            update();
        }
    }
    
    public boolean isAutoScaleEnabled() {
        return _autoScaleEnabled;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {

        _sumDataSet.clear();
        _presetDataSet.clear();

        final int numberOfHarmonics = _fourierSeriesModel.getNumberOfHarmonics();
        double maxSum = FourierConfig.MAX_HARMONIC_AMPLITUDE;
        final double deltaX = ( MAX_FUNDAMENTAL_CYCLES * L ) / NUMBER_OF_DATA_POINTS;
        final double startX = -2 * L;
        final double startAngle = 0.0;
        
        for ( int harmonicIndex = 0; harmonicIndex < numberOfHarmonics; harmonicIndex++ ) {

            Harmonic harmonic = (Harmonic) _fourierSeriesModel.getHarmonic( harmonicIndex );
            final double amplitude = harmonic.getAmplitude();
            final int numberOfCycles = MAX_FUNDAMENTAL_CYCLES * ( harmonic.getOrder() + 1 );
            final double pointsPerCycle = NUMBER_OF_DATA_POINTS / (double) numberOfCycles;
            final double deltaAngle = ( 2.0 * Math.PI ) / pointsPerCycle;

            for ( int pointIndex = 0; pointIndex < _points.length; pointIndex++ ) {

                // Clear the points the first time through.
                if ( harmonicIndex == 0 ) {
                    final double x = startX + ( pointIndex * deltaX );
                    final double y = 0;
                    _points[pointIndex].setLocation( x, y );
                }

                // Add the Y contribution for harmonics with non-zero amplitude.
                if ( amplitude != 0 ) {
                    final double angle = startAngle + ( pointIndex * deltaAngle );
                    double radians;
                    if ( _waveType == FourierConstants.WAVE_TYPE_SINE ) {
                        radians = FourierUtils.sin( angle );
                    }
                    else {
                        radians = FourierUtils.cos( angle );
                    }

                    final double x = startX + ( pointIndex * deltaX );
                    final double y = _points[pointIndex].getY() + ( amplitude * radians );
                    _points[pointIndex].setLocation( x, y );
                }

                // Determine the max Y value the last time through.
                if ( harmonicIndex == numberOfHarmonics - 1 ) {
                    final double absoluteY = Math.abs( _points[pointIndex].getY() );
                    if ( absoluteY > maxSum ) {
                        maxSum = absoluteY;
                    }
                }
            }
        }
        
        // Add the points the the data set.
        _sumDataSet.addAllPoints( _points );

        // If auto scaling is enabled, adjust the vertical scale to fit the curve.
        if ( _autoScaleEnabled ) {
            Range2D range = _chartGraphic.getRange();
            if ( maxSum != range.getMaxY() ) {
                range.setMinY( -maxSum );
                range.setMaxY( +maxSum );
                _chartGraphic.setRange( range );
                updateTicksAndGridlines();
                updateZoomButtons();
            }
        }

        repaint();
    }     
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /* 
     * EventListener handles events related to this view.
     */
    private class EventListener implements ActionListener {
        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _horizontalZoomControl ) {
                handleHorizontalZoom( event.getID() );
            }
            else if ( event.getSource() == _verticalZoomControl ) {
                handleVerticalZoom( event.getID() );
            }
            else if ( event.getSource() == _autoScaleCheckBox ) {
                setAutoScaleEnabled( _autoScaleCheckBox.isSelected() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
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
    private void handleHorizontalZoom( int actionID ) {

        // Adjust the zoom level.
        if ( actionID == ZoomControl.ACTION_ID_ZOOM_IN ) {
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

        updateTicksAndGridlines();
        updateZoomButtons();
    }
    
    /*
     * Handles vertical zooming.
     * 
     * @param actionID indicates the type of zoom
     */
    private void handleVerticalZoom( int actionID ) {
        
        // Get the chart's vertical range.
        Range2D range = _chartGraphic.getRange();
        double yRange = range.getMaxY();

        // Round to an integral multiple of Y_ZOOM_STEP.
        if ( yRange % Y_ZOOM_STEP > 0 ) {
            yRange = ( (int) ( yRange / Y_ZOOM_STEP ) ) * Y_ZOOM_STEP;
        }
        
        // Adjust the scale.
        if ( actionID == ZoomControl.ACTION_ID_ZOOM_IN ) {
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
        
        updateTicksAndGridlines();
        updateZoomButtons();
    }
    
    /*
     * Adjusts ticks and gridlines to match the chart range.
     */
    private void updateTicksAndGridlines() {
        
        Range2D range = _chartGraphic.getRange();
        
        // X axis ticks and gridlines
        {
            if ( _xZoomLevel > -3 ) {
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );
            }
            else {
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels2 );
            }
        }
        
        // Y axis ticks and gridlines
        {
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
     * Enables and disabled zoom buttons based on the current
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
        if ( _autoScaleEnabled ) {
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
}
