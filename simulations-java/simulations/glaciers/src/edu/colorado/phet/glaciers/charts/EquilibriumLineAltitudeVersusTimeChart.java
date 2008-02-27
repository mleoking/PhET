/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.charts;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * EquilibriumLineAltitudeVersusTimeChart displays a "Equilibrium Line Altitude versus Time" chart.
 * The chart updates when the climate changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquilibriumLineAltitudeVersusTimeChart extends JDialog {
    
    private Climate _climate;
    private ClimateListener _climateListener;
    private XYSeries _series;
    
    public EquilibriumLineAltitudeVersusTimeChart( Frame owner, Dimension size, Climate climate ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _climate = climate;
        _climateListener = new ClimateAdapter() {
            public void snowfallChanged() {
                update();
            }
            public void snowfallReferenceElevationChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        // create the chart
        _series = new XYSeries( "equilibriumLineAltitudeVersusTime" );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_EQUILIBRIUM_LINE_ALTITUDE_VERSUS_TIME, // title
            GlaciersStrings.AXIS_TIME, // x axis label
            GlaciersStrings.AXIS_EQUILIBRIUM_LINE_ALTITUDE,  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        
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
        System.out.println( "EquilibriumLineAltitudeVersusTimeChart.cleanup" );//XXX
        _climate.removeClimateListener( _climateListener );
    }
    
    public void clear() {
        _series.clear();
    }
    
    private void update() {
        //XXX add a data point every time the climate changes
    }
}
