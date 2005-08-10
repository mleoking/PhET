/*PhET, 2004.*/
package edu.colorado.phet.chart.controllers;

import edu.colorado.phet.chart.BufferedChart;
import edu.colorado.phet.chart.Chart;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 9:02:00 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class BufferedChartCursor extends ChartCursor {
    private BufferedChart bufferedChart;

    public BufferedChartCursor( Component component, Chart chart, int width, BufferedChart bufferedChart ) {
        super( component, chart, width );
        this.bufferedChart = bufferedChart;
    }

    public BufferedChartCursor( Component component, Chart chart, Color fill, Color outline, int width, BufferedChart bufferedChart ) {
        super( component, chart, fill, outline, width );
        this.bufferedChart = bufferedChart;
    }

    protected double toModelCoordinate( MouseEvent e, final Chart chart ) {
        try {
            int screenX = e.getX();
            double locInBuffer = bufferedChart.getNetTransform().inverseTransform( new Point2D.Double( screenX, 0 ), null ).getX();
            double locInChart = bufferedChart.getTransform2D().viewToModel( (int)locInBuffer, 0 ).getX();
            return locInChart;
        }
        catch( NoninvertibleTransformException e1 ) {
            e1.printStackTrace();
            throw new RuntimeException( e1 );
        }
    }

    public void setBufferedChart( BufferedChart bufferedChart ) {
        this.bufferedChart = bufferedChart;
    }
}
