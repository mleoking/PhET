/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.plot;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampUtil;
import edu.colorado.phet.theramp.view.plot.TimePlotSuitePNode;
import edu.colorado.phet.timeseries.TimePoint;
import edu.colorado.phet.timeseries.TimeSeries;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

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
    private int strokeSize = 3;
    private BasicStroke s;
    private Color transparentColor;
    private boolean visible = true;
    private PText readoutGraphic;
    private DecimalFormat decimalFormat;

    public TimeSeriesPNode( TimePlotSuitePNode plotSuite, TimeSeries series, ValueAccessor valueAccessor, Color color,String justifyString) {
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
        s = new BasicStroke( strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
        transparentColor = RampUtil.transparify( color, 120 );
        readoutGraphic = new PText();
        readoutGraphic.setTextPaint( color );
        readoutGraphic.setFont( new Font( "Lucida Sans",Font.BOLD, 10) );
        decimalFormat = new DecimalFormat( "0.00");
        updateReadout();
    }

    private void updateReadout() {
        updateReadout( valueAccessor.getValue( getRampPhysicalModel() ) );
    }

    private RampPhysicalModel getRampPhysicalModel() {
        return plotSuite.getRampModule().getRampPhysicalModel();
    }

    private void updateReadout( double value ) {
        readoutGraphic.setText( "" + valueAccessor.getName() + " = " + decimalFormat.format( value)+ " " + valueAccessor.getUnits() );
    }

    private void dataAdded() {
        TimePoint pt = series.getLastPoint();
        addPoint( pt );
        updateReadout( pt.getValue() );
    }

    private void addPoint( TimePoint at ) {
        BufferedImage bufferedImage = plotSuite.getChartImage();
        Graphics2D graphics2D = bufferedImage.createGraphics();

        Point2D screenPoint = plotSuite.toImageLocation( at.getTime(), at.getValue() );

        if( lastScreenPoint != null ) {
            if( visible ) {
                Line2D.Double screenLine = new Line2D.Double( lastScreenPoint, screenPoint );
                graphics2D.setColor( transparentColor );
                graphics2D.setClip( plotSuite.getDataArea() );

                graphics2D.setStroke( s );
                graphics2D.draw( screenLine );

                Rectangle2D bounds = screenLine.getBounds2D();
                bounds = RectangleUtils.expand( bounds, strokeSize / 2 + 2, strokeSize / 2 + 2 );
                plotSuite.repaintImage( bounds );
            }
        }

        lastScreenPoint = new Point2D.Double( screenPoint.getX(), screenPoint.getY() );
    }

    public void reset() {
        lastScreenPoint = null;
        updateReadout( );
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        readoutGraphic.setVisible( visible );
    }

    public void repaintAll() {
        if( visible ) {
            lastScreenPoint = null;
            for( int i = 0; i < series.numPoints(); i++ ) {
                addPoint( series.pointAt( i ) );
            }
        }
    }

    public PNode getReadoutGraphic() {
        return readoutGraphic;
    }
}
