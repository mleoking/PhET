/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import edu.colorado.phet.opticaltweezers.OTConstants;


public class PositionHistogramPlot extends XYPlot {

    private static final String SERIES_KEY = "position";
    private static final Color BACKGROUND_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    private static final Color GRIDLINES_COLOR = Color.BLACK;
    private static final Font AXIS_LABEL_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.PLAIN, 14 );
    private static final Stroke AXIS_STROKE = new BasicStroke( 1f );
    private static final Color AXIS_COLOR = Color.BLACK;
    
    //XXX this stuff should be in constructor
    private static final double MIN_POSITION = 0; // nm
    private static final double MAX_POSITION = 10000; // nm
    private static final double BIN_WIDTH = 20; // nm
    
    private PhetHistogramDataset _dataset;
    private int _seriesIndex;
    private NumberAxis _xAxis, _yAxis;
    
    public PositionHistogramPlot() {
        super();
        
        // dataset
        _dataset = new PhetHistogramDataset();
        setDataset( _dataset );
        
        // series
        int numberOfBins = (int) ( ( MAX_POSITION - MIN_POSITION ) / BIN_WIDTH );
        _seriesIndex = _dataset.addSeries( SERIES_KEY, numberOfBins, MIN_POSITION, MAX_POSITION );
        
        // renderer
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setPaint( BAR_FILL_COLOR );
        renderer.setOutlinePaint( BAR_OUTLINE_COLOR );
        renderer.setDrawBarOutline( true );
        setRenderer( renderer );
        
        // axis labels
        String positonLabel = "";
        String frequencyLabel = "";
        
        // x-axis configuration
        _xAxis = new NumberAxis();
        _xAxis.setLabel( positonLabel );
        _xAxis.setLabelFont( AXIS_LABEL_FONT );
        _xAxis.setTickLabelsVisible( false );
        _xAxis.setTickMarksVisible( false );
        _xAxis.setAxisLineStroke( AXIS_STROKE );
        _xAxis.setAxisLinePaint( AXIS_COLOR );
        
        // y-axis configuration
        _yAxis = new NumberAxis();
        _yAxis.setLabel( frequencyLabel );
        _yAxis.setLabelFont( AXIS_LABEL_FONT );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        _yAxis.setAxisLineStroke( AXIS_STROKE );
        _yAxis.setAxisLinePaint( AXIS_COLOR );
        // If we don't set a range for the y-axis, the data will appear to be normalized.
        
        // plot configuration
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        setDomainGridlinePaint( GRIDLINES_COLOR );
        setRangeGridlinePaint( GRIDLINES_COLOR );
        setDomainAxis( _xAxis );
        setRangeAxis( _yAxis );
//        setDomainZeroBaselineVisible( false ); // 1.0.5 feature
//        setRangeZeroBaselineVisible( false ); // 1.0.5 feature
    }

    public void setPositionRange( double minPosition, double maxPosition ) {
        _xAxis.setRange( minPosition, maxPosition );
    }
    
    public void addPosition( double position ) {
        _dataset.addObservation( _seriesIndex, position );
    }

    public void clear() {
        _dataset.clearObservations( _seriesIndex );
    }
}
