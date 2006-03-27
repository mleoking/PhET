/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.jfreechart.FastPathRenderer;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;


/**
 * ProbabilityDensityPlot is the plot that displays probability density.
 * Its data series is managed by WaveFunctionPlot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ProbabilityDensityPlot extends QTXYPlot {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ProbabilityDensityPlot( XYSeries probabilityDensitySeries ) {
        super();
        
        // Labels (localized)
        String probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
        
        // Dataset
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries( probabilityDensitySeries );
        
        // Renderer
        XYItemRenderer renderer = new FastPathRenderer();
        renderer.setSeriesPaint( 0, QTConstants.COLOR_SCHEME.getProbabilityDensityColor() );
        renderer.setSeriesStroke( 0, QTConstants.PROBABILITY_DENSITY_STROKE );
        
        // X axis 
        PositionAxis xAxis = new PositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( probabilityDensityLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.DEFAULT_PROBABILITY_DENSITY_RANGE );
        yAxis.setTickLabelPaint( QTConstants.COLOR_SCHEME.getTickColor() );
        yAxis.setTickMarkPaint( QTConstants.COLOR_SCHEME.getTickColor() );
        
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.COLOR_SCHEME.getChartColor() );
        setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( QTConstants.COLOR_SCHEME.getGridlineColor() );
        setRangeGridlinePaint( QTConstants.COLOR_SCHEME.getGridlineColor() );
        setDataset( data );
        setRenderer( renderer );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
    }
    
    /**
     * Sets the color scheme for this plot.
     * 
     * @param scheme
     */
    public void setColorScheme( QTColorScheme scheme ) {
        // Background
        setBackgroundPaint( scheme.getChartColor() );
        // Ticks
        getDomainAxis().setTickLabelPaint( scheme.getTickColor() );
        getDomainAxis().setTickMarkPaint( scheme.getTickColor() );
        getRangeAxis().setTickLabelPaint( scheme.getTickColor() );
        getRangeAxis().setTickMarkPaint( scheme.getTickColor() );
        // Gridlines
        setDomainGridlinePaint( scheme.getGridlineColor() );
        setRangeGridlinePaint( scheme.getGridlineColor() );
        // Series
        getRenderer().setSeriesPaint( 0, scheme.getProbabilityDensityColor() );
    }
}
