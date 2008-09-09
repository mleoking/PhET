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

import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.util.UnitsConverter;

/**
 * TemperatureVersusElevationChart displays a "Temperature versus Elevation" chart.
 * The chart updates as climate is changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TemperatureVersusElevationChart extends JDialog {
    
    private static final Range TEMPERATURE_RANGE_METRIC = new Range( -20, 8 ); // degrees C
    private static final Range TEMPERATURE_RANGE_ENGLISH = new Range( 
            UnitsConverter.celsiusToFahrenheit( TEMPERATURE_RANGE_METRIC.getLowerBound() ), 
            UnitsConverter.celsiusToFahrenheit( TEMPERATURE_RANGE_METRIC.getUpperBound() ) ); // degrees F
    
    private static final Range ELEVATION_RANGE_METRIC = new Range( 2000, 5000 ); // meters
    private static final Range ELEVATION_RANGE_ENGLISH = new Range( 
            UnitsConverter.metersToFeet( ELEVATION_RANGE_METRIC.getLowerBound() ), 
            UnitsConverter.metersToFeet( ELEVATION_RANGE_METRIC.getUpperBound() ) ); // feet
    
    private static final double DELTA_ELEVATION = 100; // meters
    
    private final Climate _climate;
    private final ClimateListener _climateListener;
    private final XYSeries _series;
    private final boolean _englishUnits;
    
    public TemperatureVersusElevationChart( Frame owner, Dimension size, Climate climate, boolean englishUnits ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _englishUnits = englishUnits;
        
        _climate = climate;
        _climateListener = new ClimateAdapter() {
            public void temperatureChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        // series and dataset
        _series = new XYSeries( "temperatureVersusElevation", false /* autoSort */ );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        
        // create the chart
        String xAxisLabel = ( englishUnits ? GlaciersStrings.AXIS_TEMPERATURE_FAHRENHEIT : GlaciersStrings.AXIS_TEMPERATURE_CELSIUS );
        String yAxisLabel = ( englishUnits ? GlaciersStrings.AXIS_ELEVATION_ENGLISH : GlaciersStrings.AXIS_ELEVATION_METRIC );
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_TEMPERATURE_VERSUS_ELEVATION, // title
            xAxisLabel, 
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        if ( englishUnits ) {
            domainAxis.setRange( TEMPERATURE_RANGE_ENGLISH );
        }
        else {
            domainAxis.setRange( TEMPERATURE_RANGE_METRIC );       
        }
        
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
        _climate.removeClimateListener( _climateListener );
    }
    
    private void update() {
        _series.clear();
        double elevation = ELEVATION_RANGE_METRIC.getLowerBound();
        double temperature = 0;
        final double maxElevation = ELEVATION_RANGE_METRIC.getUpperBound();
        while ( elevation <= maxElevation ) {
            temperature = _climate.getTemperature( elevation );
            if ( _englishUnits ) {
                _series.add( UnitsConverter.celsiusToFahrenheit( temperature ), UnitsConverter.metersToFeet( elevation ) );
            }
            else {
                _series.add( temperature, elevation );
            }
            elevation += DELTA_ELEVATION;
        }
    }
}
