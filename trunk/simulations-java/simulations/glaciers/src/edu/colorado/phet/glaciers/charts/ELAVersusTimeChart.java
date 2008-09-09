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
import edu.colorado.phet.glaciers.util.UnitsConverter;

/**
 * ELAVersusTimeChart displays a "Equilibrium Line Altitude versus Time" chart.
 * The chart updates when the climate changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ELAVersusTimeChart extends JDialog {
    
    private static final Range ELEVATION_RANGE_METRIC = new Range( 2000, 6000 ); // meters
    private static final Range ELEVATION_RANGE_ENGLISH = new Range( 
            UnitsConverter.metersToFeet( ELEVATION_RANGE_METRIC.getLowerBound() ), 
            UnitsConverter.metersToFeet( ELEVATION_RANGE_METRIC.getUpperBound() ) ); // feet
    
    private static final int MAX_NUMBER_OF_YEARS = 1000;
    
    private final Climate _climate;
    private final GlaciersClock _clock;
    private final ClockListener _clockListener;
    private final XYSeries _series;
    private final NumberAxis _domainAxis;
    private final boolean _englishUnits;
    
    public ELAVersusTimeChart( Frame owner, Dimension size, Climate climate, GlaciersClock clock, boolean englishUnits ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _climate = climate;
        _englishUnits = englishUnits;
        
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
        String xAxisLabel = GlaciersStrings.AXIS_TIME;
        String yAxisLabel = ( englishUnits ? GlaciersStrings.AXIS_ELA_ENGLISH : GlaciersStrings.AXIS_ELA_METRIC );
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_ELA_VERSUS_TIME, // title
            xAxisLabel,
            yAxisLabel,
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
        if ( englishUnits ) {
            rangeAxis.setRange( ELEVATION_RANGE_ENGLISH );
        }
        else {
            rangeAxis.setRange( ELEVATION_RANGE_METRIC );
        }
        
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
        double ela = _climate.getELA();
        if ( _englishUnits ) {
            ela = UnitsConverter.metersToFeet( ela );
        }
        _series.add( t, ela );
        double tMin = _series.getDataItem( 0 ).getX().doubleValue();
        _domainAxis.setRange( new Range( tMin, tMin + MAX_NUMBER_OF_YEARS ) );
    }
}
