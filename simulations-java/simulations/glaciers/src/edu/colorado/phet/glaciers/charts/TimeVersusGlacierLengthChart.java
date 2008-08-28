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
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;

/**
 * GlacierLengthVersusTimeChart displays a "Glacier Length versus Time" chart.
 * The chart updates as the glacier evolves.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TimeVersusGlacierLengthChart extends JDialog {
    
    private static final Range LENGTH_RANGE = new Range( 0, 80E3 ); // meters
    private static final double Y_AXIS_TICK_SPACING = 10000; // meters
    private static final int MAX_NUMBER_OF_YEARS = 1000;
    
    private final Glacier _glacier;
    private final GlaciersClock _clock;
    private final ClockListener _clockListener;
    private final XYSeries _series;
    private final NumberAxis _domainAxis;
    
    public TimeVersusGlacierLengthChart( Frame owner, Dimension size, Glacier glacier, GlaciersClock clock ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _glacier = glacier;
        
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
        _series = new XYSeries( "glacierLengthVersusTime", false /* autoSort */ );
        _series.setMaximumItemCount( MAX_NUMBER_OF_YEARS );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        
        // create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_TIME_VERSUS_GLACIER_LENGTH, // title
            GlaciersStrings.AXIS_TIME, // x axis label
            GlaciersStrings.AXIS_GLACIER_LENGTH,  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        _domainAxis = (NumberAxis) plot.getDomainAxis();
        _domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange( LENGTH_RANGE );
        rangeAxis.setTickUnit( new NumberTickUnit( Y_AXIS_TICK_SPACING ) );
        
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
    
    public void clear() {
        _series.clear();
    }
    
    private void update() {
        final double t = _clock.getSimulationTime();
        final double length = _glacier.getLength();
        _series.add( t, length );
        double tMin = _series.getDataItem( 0 ).getX().doubleValue();
        _domainAxis.setRange( new Range( tMin, tMin + MAX_NUMBER_OF_YEARS ) );
    }
}
