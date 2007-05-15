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

import edu.colorado.phet.common.jfreechartphet.PhetHistogramDataset;
import edu.colorado.phet.common.jfreechartphet.PhetHistogramSeries;
import edu.colorado.phet.opticaltweezers.OTConstants;

/**
 * PositionHistogramPlot is the plot for the position histogram chart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionHistogramPlot extends XYPlot {

    private static final String SERIES_KEY = "position";
    private static final Color BACKGROUND_COLOR = OTConstants.COLOR_TRANSPARENT;
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    
    private PhetHistogramDataset _dataset;
    private PhetHistogramSeries _series;
    private NumberAxis _xAxis, _yAxis;
    
    /**
     * Constructor.
     * 
     * @param minPosition
     * @param maxPosition
     * @param binWidth
     */
    public PositionHistogramPlot( double minPosition, double maxPosition, double binWidth ) {
        super();
        
        // dataset
        _dataset = new PhetHistogramDataset();
        setDataset( _dataset );
        
        // series
        final int numberOfBins = (int) ( ( maxPosition - minPosition ) / binWidth );
        _series = new PhetHistogramSeries( SERIES_KEY, minPosition, maxPosition, numberOfBins );
        _dataset.addSeries( _series );

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

    /**
     * Sets the position range for the chart and underlying series.
     * 
     * @param minPosition
     * @param maxPosition
     */
    public void setPositionRange( double minPosition, double maxPosition ) {
        // set the range for the series
        final double binWidth = _series.getBinWidth();
        final int numberOfBins = (int) ( ( maxPosition - minPosition ) / binWidth );
        _dataset.removeSeries( _series );
        _series = new PhetHistogramSeries( SERIES_KEY, minPosition, maxPosition, numberOfBins );
        _dataset.addSeries( _series );
        // set the range for the x axis
        _xAxis.setRange( minPosition, maxPosition );
    }
    
    /**
     * Adds a position observation.
     * 
     * @param position
     */
    public void addPosition( double position ) {
        _series.addObservation( position );
    }

    /**
     * Clears the plot.
     */
    public void clear() {
        _series.clear();
    }
}
