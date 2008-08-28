/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.charts;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Stroke;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * GlacialBudgetVersusElevationChart charts glacial budget, accumulation and ablation versus elevation.
 * The chart updates as climate is changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacialBudgetVersusElevationChart extends JDialog {
    
    private static final boolean SHOW_NEGATIVE_ABLATION = true;
    
    private static final Range METERS_PER_YEAR_RANGE = new Range( -20, ( SHOW_NEGATIVE_ABLATION ? 5 : 20 ) ); // meters
    private static final Range ELEVATION_RANGE = new Range( 2000, 5000 ); // meters
    private static final double DELTA_ELEVATION = 100; // meters
    private static final Stroke GLACIAL_BUDGET_STROKE = new BasicStroke( 2f );
    private static final Stroke ACCUMULATION_STROKE = new BasicStroke( 1f );
    private static final Stroke ABLATION_STROKE = new BasicStroke( 1f );
    
    private final Climate _climate;
    private final ClimateListener _climateListener;
    private final XYSeries _glacialBudgetSeries, _accumulationSeries, _ablationSeries;
    
    public GlacialBudgetVersusElevationChart( Frame owner, Dimension size, Climate climate ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _climate = climate;
        _climateListener = new ClimateListener() {
            public void snowfallChanged() {
                update();
            }

            public void temperatureChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        // series and dataset
        _accumulationSeries = new XYSeries( GlaciersStrings.LABEL_ACCUMULATION, false /* autoSort */ );
        String ablationLabel = GlaciersStrings.LABEL_ABLATION;
        if ( SHOW_NEGATIVE_ABLATION ) {
            ablationLabel = "-" + ablationLabel;
        }
        _ablationSeries = new XYSeries( ablationLabel, false /* autoSort */ );
        _glacialBudgetSeries = new XYSeries( GlaciersStrings.LABEL_GLACIAL_BUDGET, false /* autoSort */ );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _accumulationSeries );
        dataset.addSeries( _ablationSeries );
        dataset.addSeries( _glacialBudgetSeries );

        // create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_GLACIAL_BUDGET_VERSUS_ELEVATION, // title
            GlaciersStrings.AXIS_METERS_PER_YEAR, // x axis label
            GlaciersStrings.AXIS_ELEVATION,  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            true, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        // NOTE! Indicies depend on the order that the series were added to the dataset above.
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint( 0, GlaciersConstants.ACCUMULATION_COLOR );
        renderer.setSeriesStroke( 0, ACCUMULATION_STROKE );
        renderer.setSeriesPaint( 1, GlaciersConstants.ABLATION_COLOR );
        renderer.setSeriesStroke( 1, ABLATION_STROKE );
        renderer.setSeriesPaint( 2, GlaciersConstants.GLACIAL_BUDGET_COLOR );
        renderer.setSeriesStroke( 2, GLACIAL_BUDGET_STROKE );
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        domainAxis.setRange( METERS_PER_YEAR_RANGE );
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        rangeAxis.setRange( ELEVATION_RANGE );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable( false );
        setContentPane( chartPanel );
        
        addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                cleanup();
            }
            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                cleanup();
            }
        });
        
        update();
    }
    
    private void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    private void update() {
        
        _glacialBudgetSeries.clear();
        _accumulationSeries.clear();
        _ablationSeries.clear();
        
        double elevation = ELEVATION_RANGE.getLowerBound();
        double glacialBudget = 0;
        double accumulation = 0;
        double ablation = 0;
        final double maxElevation = ELEVATION_RANGE.getUpperBound();
        
        while ( elevation <=  maxElevation ) {
            
            glacialBudget = _climate.getGlacialBudget( elevation );
            accumulation = _climate.getAccumulation( elevation );
            ablation = _climate.getAblation( elevation );
            
            _glacialBudgetSeries.add( glacialBudget, elevation );
            _accumulationSeries.add( accumulation, elevation );
            if ( SHOW_NEGATIVE_ABLATION ) {
                _ablationSeries.add( -ablation, elevation );
            }
            else {
                _ablationSeries.add( ablation, elevation );  
            }

            elevation += DELTA_ELEVATION;
        }
    }
}
