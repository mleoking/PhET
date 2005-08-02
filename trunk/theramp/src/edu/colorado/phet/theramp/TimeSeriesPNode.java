/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.timeseries.TimePoint;
import edu.colorado.phet.timeseries.TimeSeries;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 2:41:09 PM
 * Copyright (c) Aug 2, 2005 by Sam Reid
 */

public class TimeSeriesPNode {
    private TimePlotSuitePNode plotSuite;
    private ValueAccessor valueAccessor;
    private Color color;
    private String justifyString;
    private TimeSeries series;

    public TimeSeriesPNode( TimePlotSuitePNode plotSuite, TimeSeries series, ValueAccessor valueAccessor, Color color, String justifyString ) {
        this.plotSuite = plotSuite;
        this.series = series;
        this.valueAccessor = valueAccessor;
        this.color = color;
        this.justifyString = justifyString;
        series.addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                TimeSeriesPNode.this.dataAdded();
            }

            public void cleared( TimeSeries timeSeries ) {
                reset();
            }
        } );
    }

    private void dataAdded() {
        TimePoint pt = series.getLastPoint();
        //draw into the buffered image

//        plotSuite.
    }

    public void reset() {
        System.out.println( "TODO" );
    }
}
