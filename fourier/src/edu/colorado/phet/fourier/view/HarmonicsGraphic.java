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

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.model.HarmonicDataSet;
import edu.colorado.phet.fourier.util.FourierUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


/**
 * HarmonicsGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsGraphic extends GraphicLayerSet implements SimpleObserver {

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

    // X axis
    private static final double L = 1.0;
    private static final Color X_AXIS_COLOR = Color.BLACK;
    private static final Stroke X_AXIS_STROKE = new BasicStroke( 2f );
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final Stroke X_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font X_MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color X_MAJOR_TICK_COLOR = Color.BLACK;
    private static final Color X_MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke X_MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
    private static final double X_ZOOM_FACTOR = Math.sqrt( 2 );
    private static final int X_MIN_ZOOM_LEVEL = -4;
    private static final int X_MAX_ZOOM_LEVEL = 1;

    // Y axis
    private static final double Y_MIN = -FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAX = +FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final Color Y_AXIS_COLOR = Color.BLACK;
    private static final Stroke Y_AXIS_STROKE = new BasicStroke( 2f );
    private static final double Y_MAJOR_TICK_SPACING = 0.5;
    private static final double Y_MINOR_TICK_SPACING = 0.1;
    private static final Stroke Y_MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font Y_MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color Y_MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Color Y_MINOR_GRIDLINE_COLOR = new Color( 0, 0, 0, 60 );
    private static final Stroke Y_MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
    private static final Stroke Y_MINOR_GRIDLINE_STROKE = new BasicStroke( 0.5f );

    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( -L / 2, Y_MIN, L / 2, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 600, 130 );

    // Wave parameters
    private static final Stroke WAVE_STROKE = new BasicStroke( 1f );
    private static final int NUMBER_OF_DATA_POINTS = 1000;
    private static final int MAX_FUNDAMENTAL_CYCLES = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeriesModel;
    private Chart _chartGraphic;
    private ArrayList _dataSets; // array of HarmonicDataSet
    private int _previousNumberOfHarmonics;
    private int _waveType;
    private ZoomControl _horizontalZoomControl;
    private int _xZoomLevel;
    private LabelTable _spaceLabels1, _spaceLabels2;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     *
     * @param component          the parent Component
     * @param fourierSeriesModel the Fourier series that this view displays
     */
    public HarmonicsGraphic( Component component, FourierSeries fourierSeriesModel ) {
        super( component );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Model
        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Title
        String title = SimStrings.get( "HarmonicsGraphic.title" );
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
                _spaceLabels1.put( -1.00 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.75 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-3L/4", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.50 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L/2", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.25 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L/4", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( 0 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "0", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( 0.25 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L/4", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( 0.50 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L/2", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( 0.75 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "3L/4", X_MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( 1.00 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L", X_MAJOR_TICK_COLOR ) );

                _spaceLabels2 = new LabelTable();
                _spaceLabels2.put( -2.0 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-2L", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -1.5 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-3L/2", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -1.0 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -0.5 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "-L/2", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( 0 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "0", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( 0.5 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L/2", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( 1.0 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "L", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( 1.5 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "3L/2", X_MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( 2.0 * L, new PhetTextGraphic( component, X_MAJOR_TICK_FONT, "2L", X_MAJOR_TICK_COLOR ) );
            }
            
            // X axis
            {
                _chartGraphic.getXAxis().setStroke( X_AXIS_STROKE );
                _chartGraphic.getXAxis().setColor( X_AXIS_COLOR );

                // No ticks or labels on the axis
                _chartGraphic.getXAxis().setMajorTicksVisible( false );
                _chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getXAxis().setMinorTicksVisible( false );
                _chartGraphic.getXAxis().setMinorTickLabelsVisible( false );
                
                // Major ticks with labels below the chart
                _chartGraphic.getHorizontalTicks().setMajorTicksVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizontalTicks().setMajorTickStroke( X_MAJOR_TICK_STROKE );
                _chartGraphic.getHorizontalTicks().setMajorTickFont( X_MAJOR_TICK_FONT );
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );

                // Vertical gridlines for major ticks.
                _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesColor( X_MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesStroke( X_MAJOR_GRIDLINE_STROKE );
                
                // No vertical gridlines for minor ticks.
                _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( false );
            }
            
            // Y axis
            {
                _chartGraphic.getYAxis().setStroke( Y_AXIS_STROKE );
                _chartGraphic.getYAxis().setColor( Y_AXIS_COLOR );
                
                // No ticks or labels on the axis
                _chartGraphic.getYAxis().setMajorTicksVisible( false );
                _chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getYAxis().setMinorTicksVisible( false );
                _chartGraphic.getYAxis().setMinorTickLabelsVisible( false );
                
                // Major ticks with labels to the left of the chart
                _chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalTicks().setMajorTickStroke( Y_MAJOR_TICK_STROKE );
                _chartGraphic.getVerticalTicks().setMajorTickFont( Y_MAJOR_TICK_FONT );

                // Horizontal gridlines for major ticks
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( Y_MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( Y_MAJOR_GRIDLINE_STROKE );

                // Horizontal gridlines for minor ticks
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesColor( Y_MINOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesStroke( Y_MINOR_GRIDLINE_STROKE );
            }
        }
        
        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( CHART_SIZE.width + 10, -50 );
        }
        
        // Interactivity
        {
            titleGraphic.setIgnoreMouse( true );
            // XXX others setIgnoreMouse ?
            EventListener listener = new EventListener();
            _horizontalZoomControl.addActionListener( listener );
        }

        // Misc initialization
        {
            _xZoomLevel = 0;
            _dataSets = new ArrayList();
            _previousNumberOfHarmonics = -1; // force update
        }

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
        if( waveType != _waveType ) {
            _waveType = waveType;
            for( int i = 0; i < _dataSets.size(); i++ ) {
                HarmonicDataSet dataSet = (HarmonicDataSet)_dataSets.get( i );
                dataSet.setWaveType( _waveType );
            }
            repaint();
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
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {

        int numberOfHarmonics = _fourierSeriesModel.getNumberOfHarmonics();

        if( _previousNumberOfHarmonics != numberOfHarmonics ) {

            _dataSets.clear();
            _chartGraphic.removeAllDataSetGraphics();

            for( int i = 0; i < _fourierSeriesModel.getNumberOfHarmonics(); i++ ) {
                Harmonic harmonic = _fourierSeriesModel.getHarmonic( i );

                HarmonicDataSet dataSet = new HarmonicDataSet( harmonic, NUMBER_OF_DATA_POINTS, L, MAX_FUNDAMENTAL_CYCLES );
                _dataSets.add( dataSet );

                Color harmonicColor = FourierUtils.calculateHarmonicColor( i );
                DataSetGraphic dataSetGraphic = new LinePlot( getComponent(), _chartGraphic, dataSet, WAVE_STROKE, harmonicColor );
                _chartGraphic.addDataSetGraphic( dataSetGraphic );
            }

            _previousNumberOfHarmonics = numberOfHarmonics;
            setBoundsDirty();
            autorepaint();
//            repaint();
        }
//        repaint();///HACK Should be inside if block, but LinePlot is not updating itself when its DataSet changes.
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
            if( event.getSource() == _horizontalZoomControl ) {
                handleHorizontalZoom( event.getID() );
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

        // Adjust the chart's horizontal range.
        Range2D range = _chartGraphic.getRange();
        double maxX;
        if( actionID == ZoomControl.ACTION_ID_ZOOM_IN ) {
            maxX = range.getMaxX() / X_ZOOM_FACTOR;
            _xZoomLevel++;
        }
        else {
            maxX = range.getMaxX() * X_ZOOM_FACTOR;
            _xZoomLevel--;
        }
        range.setMaxX( maxX );
        range.setMinX( -maxX );
        _chartGraphic.setRange( range );
        
        // Adjust the labels to match the zoom level.
        if( _xZoomLevel > -3 ) {
            _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );
        }
        else {
            _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels2 );
        }
        
        // Disable a zoom button when we reach a max zoom level.
        switch( _xZoomLevel ) {
            case X_MIN_ZOOM_LEVEL:
                _horizontalZoomControl.setZoomOutEnabled( false );
                _horizontalZoomControl.setZoomInEnabled( true );
                break;
            case X_MAX_ZOOM_LEVEL:
                _horizontalZoomControl.setZoomOutEnabled( true );
                _horizontalZoomControl.setZoomInEnabled( false );
                break;
            default:
                _horizontalZoomControl.setZoomOutEnabled( true );
                _horizontalZoomControl.setZoomInEnabled( true );
        }
    }
}
