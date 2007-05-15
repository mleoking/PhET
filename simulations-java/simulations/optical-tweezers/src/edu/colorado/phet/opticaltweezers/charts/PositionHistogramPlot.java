/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.Color;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.jfreechartphet.PhetHistogramDataset;
import edu.colorado.phet.common.jfreechartphet.PhetHistogramSeries;
import edu.colorado.phet.opticaltweezers.OTConstants;

/**
 * PositionHistogramPlot is the plot for the position histogram chart.
 * The position histogram is normalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositionHistogramPlot extends XYPlot {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String SERIES_KEY = "position";
    private static final Color BACKGROUND_COLOR = OTConstants.COLOR_TRANSPARENT;
    private static final Color BAR_FILL_COLOR = Color.YELLOW;
    private static final Color BAR_OUTLINE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetHistogramDataset _dataset;
    private PhetHistogramSeries _series;
    private NumberAxis _xAxis, _yAxis;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
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
        // Range will be adjusted via setPositionRange.
        _xAxis = new NumberAxis();
        _xAxis.setLabel( null );
        _xAxis.setTickLabelsVisible( false );
        _xAxis.setTickMarksVisible( false );
        _xAxis.setAxisLineVisible( false );
        _xAxis.setAxisLinePaint( Color.RED );
        _xAxis.setVisible( false );
        setDomainAxis( _xAxis );
        
        // y-axis, no label, no ticks
        _yAxis = new NumberAxis();
        _yAxis.setLabel( null );
        _yAxis.setTickLabelsVisible( false );
        _yAxis.setTickMarksVisible( false );
        _yAxis.setAxisLineVisible( false );
        _yAxis.setAutoRange( true ); // adjust range to fit data, so data appears normalized
        setRangeAxis( _yAxis );

        // plot configuration
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( BACKGROUND_COLOR );
        setDomainGridlinesVisible( false );
        setRangeGridlinesVisible( false );
        setInsets( new RectangleInsets( 0, 0, 0, 0 ) );

        applyEmptySeriesWorkaround();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Sets the position range for the x axis and series.
     * A series' range is not mutable, so this involves creating a new series.
     * The data from the existing series is lost, so the plot appears to clear.
     * 
     * @param minPosition
     * @param maxPosition
     */
    public void setPositionRange( double minPosition, double maxPosition ) {
        
        // set the range for the x axis
        _xAxis.setRange( minPosition, maxPosition );
        
        // set the range for the series
        final double binWidth = _series.getBinWidth();
        final int numberOfBins = (int) ( ( maxPosition - minPosition ) / binWidth );
        _dataset.removeSeries( _series );
        _series = new PhetHistogramSeries( SERIES_KEY, minPosition, maxPosition, numberOfBins );
        _dataset.addSeries( _series );
    }
    
    //----------------------------------------------------------------------------
    // Data management
    //----------------------------------------------------------------------------
    
    /**
     * Adds a position observation.
     * 
     * @param position
     */
    public void addPosition( double position ) {
        _series.addObservation( position );
        applyEmptySeriesWorkaround();
    }

    /**
     * Clears the plot.
     */
    public void clear() {
        _series.clear();
        applyEmptySeriesWorkaround();
    }
    
    /*
     * WORKAROUND:
     * When the series is empty, a horizontal black line is drawn across the center of the plot.
     * I couldn't figure out how to get rid of this line, but manipulating the y axis range
     * makes it disappear.  When the series is empty, use a range that starts with zero.
     * When the series contains data, make the y axis adjust is range automatically, so
     * that the data appears to be normalized.
     */
    private void applyEmptySeriesWorkaround() {
        if ( _series.getNumberOfObservations() == 0 ) {
            _yAxis.setRange( 0, 1 );
        }
        else {
            _yAxis.setAutoRange( true );
        }
    }
}
