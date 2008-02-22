package edu.colorado.phet.glaciers.charts;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;


public class TemperatureVersusElevationChart extends JPanel {
    
    private static final double MIN_ELEVATION = 0; // meters
    private static final double MAX_ELEVATION = 10E3; // meters
    private static final double DELTA_ELEVATION = 100; // meters

    private Climate _climate;
    private ClimateListener _climateListener;
    private XYSeries _series;
    
    public TemperatureVersusElevationChart( Climate climate ) {
        super();
        
        _climate = climate;
        _climateListener = new ClimateAdapter() {
            public void temperatureChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        // create the chart
        _series = new XYSeries( "temperatureVersusElevation" );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Temperature v. Elevation", // title
            "temperature (C)", // x axis label
            "elevation (m)",  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        add( chartPanel );
        
        update();
    }
    
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    private void update() {
        _series.clear();
        double elevation = MIN_ELEVATION;
        double temperature = 0;
        while ( elevation <= MAX_ELEVATION ) {
            temperature = _climate.getTemperature( elevation );
            _series.add( temperature, elevation );
            elevation += DELTA_ELEVATION;
        }
    }
}
