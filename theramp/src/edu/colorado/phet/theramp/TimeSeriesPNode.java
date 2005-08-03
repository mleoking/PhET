/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampUtil;
import edu.colorado.phet.timeseries.TimePoint;
import edu.colorado.phet.timeseries.TimeSeries;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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
    private Point2D.Double lastScreenPoint;
    private int strokeSize;
    private BasicStroke s;
    private Color transparentColor;

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
        strokeSize = 4;
        s = new BasicStroke( strokeSize );
        transparentColor = RampUtil.transparify( color, 120 );
    }

    private void dataAdded() {
        TimePoint pt = series.getLastPoint();
        addPoint( pt );
    }

    private void addPoint( TimePoint at ) {
        BufferedImage bufferedImage = plotSuite.getChartImage();
        Graphics2D graphics2D = bufferedImage.createGraphics();

        Point2D screenPoint = plotSuite.toImageLocation( at.getTime(), at.getValue() );

        if( lastScreenPoint != null ) {
            Line2D.Double screenLine = new Line2D.Double( lastScreenPoint, screenPoint );
//            graphics2D.setComposite( AlphaComposite.Xor );
            graphics2D.setColor( transparentColor );

            graphics2D.setClip( plotSuite.getDataArea() );


            graphics2D.setStroke( s );
            graphics2D.draw( screenLine );

            Rectangle2D bounds = screenLine.getBounds2D();
            bounds = RectangleUtils.expand( bounds, strokeSize / 2 + 2, strokeSize / 2 + 2 );
            plotSuite.repaintImage( bounds );
        }

        lastScreenPoint = new Point2D.Double( screenPoint.getX(), screenPoint.getY() );
    }


    public void reset() {
        System.out.println( "TODO" );
    }
}
