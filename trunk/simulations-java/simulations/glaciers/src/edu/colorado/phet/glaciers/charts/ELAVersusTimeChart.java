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
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.GlaciersClock;

/**
 * ELAVersusTimeChart displays a "Equilibrium Line Altitude versus Time" chart.
 * The chart updates when the climate changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ELAVersusTimeChart extends JDialog {
    
    private static final Range ELEVATION_RANGE = new Range( 2000, 6000 ); // meters
    private static final int MAX_NUMBER_OF_YEARS = 1000;
    
    private final Climate _climate;
    private final GlaciersClock _clock;
    private final ClockListener _clockListener;
    private final XYSeries _series;
    private final NumberAxis _domainAxis;
    
    public ELAVersusTimeChart( Frame owner, Dimension size, Climate climate, GlaciersClock clock ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _climate = climate;
        
        _clock = clock;
        _clockListener = new ClockAdapter() {
            public void simulationTimeReset( ClockEvent clockEvent ) {
                _series.clear();
            }
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                update();
            }
        };
        _clock.addClockListener( _clockListener );
        
        // series and dataset
        _series = new XYSeries( "equilibriumLineAltitudeVersusTime", false /* autoSort */ );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        
        // create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_ELA_VERSUS_TIME, // title
            GlaciersStrings.AXIS_TIME, // x axis label
            GlaciersStrings.AXIS_EQUILIBRIUM_LINE_ALTITUDE,  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        _domainAxis = (NumberAxis) plot.getDomainAxis();
        _domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        // x-axis (time) range will be set dynamically
        
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
        _clock.removeClockListener( _clockListener );
    }
    
    private void update() {
        final double t = _clock.getSimulationTime();
        final double ela = _climate.getELA();
        _series.add( t, ela );
        double tMin = _series.getDataItem( 0 ).getX().doubleValue();
        _domainAxis.setRange( new Range( tMin, tMin + MAX_NUMBER_OF_YEARS ) );
    }
}
