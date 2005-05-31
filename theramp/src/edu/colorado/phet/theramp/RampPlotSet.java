/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampLookAndFeel;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.colorado.phet.timeseries.plot.PlotDeviceSeries;
import edu.colorado.phet.timeseries.plot.TimePlot;
import edu.colorado.phet.timeseries.plot.TimePlotSuite;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 10:49:44 AM
 * Copyright (c) May 16, 2005 by Sam Reid
 */

public class RampPlotSet {
    private RampModule module;
    private ArrayList dataUnits = new ArrayList();

    public RampPlotSet( RampModule module ) {
        this.module = module;
        initTest();
    }

    public void repaintBackground() {
        for( int i = 0; i < dataUnits.size(); i++ ) {
            DataUnit dataUnit = (DataUnit)dataUnits.get( i );
            dataUnit.repaintBackground();
        }
    }

    private static class DataUnit {
        private ValueAccessor valueAccessor;
        private TimeSeries timeSeries;
        private PlotDeviceSeries plotDeviceSeries;
        private TimePlotSuite timePlotSuite;

        public DataUnit( ValueAccessor valueAccessor, TimeSeries timeSeries, PlotDeviceSeries plotDeviceSeries, TimePlotSuite timePlotSuite ) {
            this.valueAccessor = valueAccessor;
            this.timeSeries = timeSeries;
            this.plotDeviceSeries = plotDeviceSeries;
            this.timePlotSuite = timePlotSuite;
        }

        public void reset() {
            timeSeries.reset();
            plotDeviceSeries.reset();
            timePlotSuite.reset();
        }

        public void updatePlot( RampModel state, double recordTime ) {
            double value = valueAccessor.getValue( state );
            timeSeries.addPoint( value, recordTime );
        }

        public void repaintBackground() {
//            timePlotSuite.getPlotDevice().reset();
        }
    }

    public void addTimeSeries( TimePlotSuite timePlotSuite, ValueAccessor valueAccessor, Color color, String justifyString ) {
        TimeSeries series = new TimeSeries();
        PlotDeviceSeries plotDeviceSeries = new PlotDeviceSeries( timePlotSuite.getPlotDevice(), series, color, valueAccessor.getName(), getStroke(), getFont(), valueAccessor.getUnits(), justifyString );
        timePlotSuite.addPlotDeviceData( plotDeviceSeries );
        dataUnits.add( new DataUnit( valueAccessor, series, plotDeviceSeries, timePlotSuite ) );
    }

    private Font getFont() {
        return new Font( "Lucida Sans", Font.BOLD, 12 );
    }

    private BasicStroke getStroke() {
        return new BasicStroke( 1 );
    }

    private void initTest() {
        TimePlotSuite energyPlot = createTimePlotSuite( new Range2D( 0, -20000, 20, 20000 ), "Energy", 400, 200 );
        module.getRampPanel().addGraphic( energyPlot, 10 );

        addTimeSeries( energyPlot, new ValueAccessor.TotalEnergy( getLookAndFeel() ), Color.black, "10000.00" );
        addTimeSeries( energyPlot, new ValueAccessor.ThermalEnergy( getLookAndFeel() ), Color.red, "10000.00" );
        addTimeSeries( energyPlot, new ValueAccessor.PotentialEnergy( getLookAndFeel() ), Color.green, "10000.00" );
        addTimeSeries( energyPlot, new ValueAccessor.KineticEnergy( getLookAndFeel() ), Color.blue, "10000.00" );

        TimePlotSuite workPlot = createTimePlotSuite( new Range2D( 0, -20000, 20, 20000 ), "Work", 620, 200 );
        module.getRampPanel().addGraphic( workPlot, 11 );
        addTimeSeries( workPlot, new ValueAccessor.AppliedWork( getLookAndFeel() ), Color.red, "10000.00" );
        addTimeSeries( workPlot, new ValueAccessor.FrictiveWork( getLookAndFeel() ), Color.blue, "10000.00" );
    }

    private RampLookAndFeel getLookAndFeel() {
        return module.getRampPanel().getLookAndFeel();
    }

    private TimePlotSuite createTimePlotSuite( Range2D range, String name, int y, int height ) {
        TimePlot timePlot = new TimePlot( module.getApparatusPanel(), module.getTimeSeriesModel(), range, name, "var", null, Color.blue );
        TimePlotSuite timePlotSuite = new TimePlotSuite( module.getTimeSeriesModel(), module.getRampPanel(), timePlot );
        timePlotSuite.setPlotVisible( false );
        timePlotSuite.setSize( 900, height );
        timePlotSuite.setLocation( 10, y );
        return timePlotSuite;
    }

    public void updatePlots( RampModel state, double recordTime ) {
        for( int i = 0; i < dataUnits.size(); i++ ) {
            DataUnit dataUnit = (DataUnit)dataUnits.get( i );
            dataUnit.updatePlot( state, recordTime );
        }
    }

    public void reset() {
        for( int i = 0; i < dataUnits.size(); i++ ) {
            dataUnitAt( i ).reset();
        }
    }

    private DataUnit dataUnitAt( int i ) {
        return (DataUnit)dataUnits.get( i );
    }
}
