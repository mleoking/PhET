/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.Color;
import java.awt.geom.Point2D;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
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
    private NumberAxis _xAxis;
    
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
        setDomainAxis( _xAxis );
        
        // y-axis, no label, no ticks
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel( null );
        yAxis.setTickLabelsVisible( false );
        yAxis.setTickMarksVisible( false );
        yAxis.setAutoRange( true ); // adjust range to fit data, so data appears normalized
        setRangeAxis( yAxis );

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
     * 
     * @param minPosition
     * @param maxPosition
     */
    public void setPositionRange( double minPosition, double maxPosition ) {
        // add 2 extra bins to the series, see applyEmptySeriesWorkaround
        setSeriesRange( minPosition, maxPosition + ( 2 * _series.getBinWidth() ) );
        // set the range for the x axis
        _xAxis.setRange( minPosition, maxPosition );
    }
    
    /*
     * Sets the range for the series.
     * A series' range is not mutable, so this involved creating a new series.
     * The data from the existing series is lost, so the plot appears to clear.
     */
    private void setSeriesRange( double minPosition, double maxPosition ) {
        // set the range for the series
        final double binWidth = _series.getBinWidth();
        final int numberOfBins = (int) ( ( maxPosition - minPosition ) / binWidth );
        _dataset.removeSeries( _series );
        _series = new PhetHistogramSeries( SERIES_KEY, minPosition, maxPosition, numberOfBins );
        _dataset.addSeries( _series );
        applyEmptySeriesWorkaround();
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
     * I couldn't figure out how to get rid of this line, and as soon as the series contains 
     * data, the line disappears. So this workaround ensures that the series is never empty.
     * setSeriesRange adds 2 extra bins to the upper end of the series range. These bins 
     * will not be visible, since they are above the upper bound of the x-axis range.
     * This workaround places one observation in the upper bin, ensuring that the series
     * is not empty and the horizontal line is not visible.
     */
    private void applyEmptySeriesWorkaround() {
        _series.addObservation( _series.getMaximum() );
    }
}
