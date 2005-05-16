/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.colorado.phet.timeseries.plot.PlotDeviceSeries;
import edu.colorado.phet.timeseries.plot.TimePlot;
import edu.colorado.phet.timeseries.plot.TimePlotSuite;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 10:49:44 AM
 * Copyright (c) May 16, 2005 by Sam Reid
 */

public class RampPlotSet {
    private RampModule module;
    public TimeSeries series;

    public RampPlotSet( RampModule module ) {
        this.module = module;
        init();
    }

    private void init() {

        TimePlot timePlot = new TimePlot( module.getApparatusPanel(), module.getTimeSeriesModel(), new Range2D( 0, -10, 20, 10 ), "name", "var", null, Color.blue );
        series = new TimeSeries();
        PlotDeviceSeries plotDeviceData = new PlotDeviceSeries( timePlot, series, Color.blue, "name", new BasicStroke(), new Font( "Lucida Sans", Font.BOLD, 12 ), "units", "justifyText" );
        timePlot.addPlotDeviceData( plotDeviceData );
        TimePlotSuite timePlotSuite = new TimePlotSuite( module.getTimeSeriesModel(), module.getRampPanel(), timePlot );
        module.getRampPanel().addGraphic( timePlotSuite, 10 );
        timePlotSuite.setPlotVisible( false );
//        timePlotSuite.setPlotVerticalParameters( 200, 200 );
        timePlotSuite.setSize( 900, 400 );
        timePlotSuite.setLocation( 10, 400 );
    }

    public void updatePlots( RampModel state, double recordTime ) {
        double val = state.getAppliedWork();
        series.addPoint( val / 10000, recordTime );
    }

    public void reset() {
        series.reset();
    }
}
