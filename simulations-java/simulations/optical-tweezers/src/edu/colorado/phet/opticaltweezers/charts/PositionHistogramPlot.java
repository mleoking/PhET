/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
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
    
    private final double _binWidth;
    private PhetHistogramDataset _dataset;
    private PhetHistogramSeries _series;
    private NumberAxis _xAxis;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param binWidth
     */
    public PositionHistogramPlot( double binWidth ) {
        super();
        
        _binWidth = binWidth;
        
        // dataset
        _dataset = new PhetHistogramDataset();
        setDataset( _dataset );
        
        // series will be create when setPositionRange is called
        _series = null;

        // renderer
        XYBarRenderer renderer = new PositionHistogramRenderer();
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
        _xAxis.setVisible( false );
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
        
        // Default
        setPositionRange( 0, binWidth );
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
        final int numberOfBins = (int) ( ( maxPosition - minPosition ) / _binWidth );
        if ( _series != null ) {
            _dataset.removeSeries( _series );
        }
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
    }

    /**
     * Clears the plot.
     */
    public void clear() {
        _series.clear();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * If the series is empty, don't render anything.
     * When rendering an empty series, it appears as a horizontal line across the middle of the plot.
     */
    private static class PositionHistogramRenderer extends XYBarRenderer {
        public void drawItem(Graphics2D g2,
                XYItemRendererState state,
                Rectangle2D dataArea,
                PlotRenderingInfo info,
                XYPlot plot,
                ValueAxis domainAxis,
                ValueAxis rangeAxis,
                XYDataset dataset,
                int seriesIndex,
                int itemIndex,
                CrosshairState crosshairState,
                int pass) {
            PhetHistogramSeries series = ((PhetHistogramDataset)dataset).getSeries( seriesIndex );
            if ( series.getNumberOfObservations() == 0 ) {
                return;
            }
            super.drawItem( g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, seriesIndex, itemIndex, crosshairState, pass );
        }
    }
}
