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
    private static final Color BACKGROUND_COLOR = OTConstants.COLOR_TRANSPARENT;
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    
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
        
        // x-axis, no label, no ticks
        _xAxis = new NumberAxis();
        _xAxis.setLabel( "" );
        _xAxis.setTickLabelsVisible( false );
        _xAxis.setTickMarksVisible( false );
        
        // y-axis, no label, no ticks
        _yAxis = new NumberAxis();
        _yAxis.setLabel( "" );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        // If we don't set a range for the y-axis, the data will appear to be normalized.
        
        // plot configuration
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        setDomainAxis( _xAxis );
        setRangeAxis( _yAxis );
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
