/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.nodes.ShadowHTMLGraphic;
import edu.colorado.phet.piccolo.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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
    private Stroke stroke;
    private Color transparentColor;
    private boolean visible = true;
    //    private ShadowHTMLGraphic readoutGraphic;
    private DecimalFormat decimalFormat;
    private HTMLLabel htmlLabel;
    private double lastUpdateValue = Double.NaN;
    private Object model;

    public TimeSeriesPNode( TimePlotSuitePNode plotSuite, ValueAccessor valueAccessor, String justifyString, Object model ) {
        this.plotSuite = plotSuite;
        this.series = new TimeSeries();
        this.valueAccessor = valueAccessor;
        this.color = valueAccessor.getColor();
        this.justifyString = justifyString;
        this.model = model;
        series.addObserver( new TimeSeries.Observer() {
            public void dataAdded( TimeSeries timeSeries ) {
                TimeSeriesPNode.this.dataAdded();
            }

            public void cleared( TimeSeries timeSeries ) {
                reset();
            }
        } );
        stroke = new BasicStroke( strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
        transparentColor = new Color( color.getRGB() );
//        readoutGraphic = new ShadowHTMLGraphic( "" );
//        readoutGraphic = new ShadowPText();
//        readoutGraphic.setShadowOffset( 1, 1 );
//        readoutGraphic.setShadowColor( Color.darkGray );
//        readoutGraphic.setColor( color );
//        readoutGraphic.setFont( createDefaultFont() );
        decimalFormat = new DecimalFormat( "0.00" );

        String html = "<html>";
        html += valueAccessor.getHTML() + " = ";
//        html += " = " + decimalFormat.format( value );
//        html += " " + valueAccessor.getUnitsAbbreviation();
        html += "</html>";
        this.htmlLabel = new HTMLLabel( html, color, createDefaultFont(), 2 );

        updateReadout();

//        readoutGraphic.setPickable( false );
//        readoutGraphic.setChildrenPickable( false );


    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public static Font createDefaultFont() {
        return new Font( "Lucida Sans", Font.BOLD, 13 );
    }

    void updateReadout() {
        updateReadout( valueAccessor.getValue( getModel() ) );

    }

    private Object getModel() {
        return model;
    }

    public ValueAccessor getValueAccessor() {
        return valueAccessor;
    }

    public TimeSeries getTimeSeries() {
        return series;
    }

    public TimePlotSuitePNode getPlot() {
        return plotSuite;
    }

//    private RampPhysicalModel getRampPhysicalModel() {
//        return plotSuite.getRampModule().getRampPhysicalModel();
//    }

    private class HTMLLabel extends PNode {
        private ShadowHTMLGraphic htmlGraphic;
        private ShadowPText valueGraphic;
        private PImage labelAsImage;

        public HTMLLabel( String html, Color color, Font defaultFont, int textInsetDY ) {
            htmlGraphic = new ShadowHTMLGraphic( html );
            valueGraphic = new ShadowPText();
            setPickable( false );
            setChildrenPickable( false );
            htmlGraphic.setShadowOffset( 1, 1 );
            htmlGraphic.setShadowColor( Color.darkGray );
            htmlGraphic.setColor( color );
            valueGraphic.setTextPaint( color );
            htmlGraphic.setFont( defaultFont );
            valueGraphic.setFont( defaultFont );

//            addChild( htmlGraphic );
            addChild( valueGraphic );
            valueGraphic.setOffset( htmlGraphic.getFullBounds().getWidth(), textInsetDY );

            Image im = htmlGraphic.toImage();
            labelAsImage = new PImage( im );
            addChild( labelAsImage );
        }

        public void setValue( String value ) {
            valueGraphic.setText( value );
        }

        public void setFont( Font font ) {
            htmlGraphic.setFont( font );
            valueGraphic.setFont( font );
            System.out.println( "TimeSeriesPNode$HTMLLabel.setFont" );
            System.err.println( "Not supported" );
        }

        public void setShadowOffset( int dx, int dy ) {
            htmlGraphic.setShadowOffset( dx, dy );
        }
    }

    private void updateReadout( double value ) {
        if( lastUpdateValue != value ) {
//            System.out.println( "TimeSeriesPNode.updateReadout, " + valueAccessor.getName() + ", lastValue=" + lastUpdateValue + ", vaule=" + value );
            this.lastUpdateValue = value;

//        readoutGraphic.setText( "" + valueAccessor.getName() + " = " + decimalFormat.format( value ) + " " + valueAccessor.getUnitsAbbreviation() );
//        String html = "<html>";
//        html += valueAccessor.getHTML();
//        html += " = " + decimalFormat.format( value );
//        html += " " + valueAccessor.getUnitsAbbreviation();
//        html += "</html>";
            //"" + valueAccessor.getName() + " = " + decimalFormat.format( value ) + " " + valueAccessor.getUnitsAbbreviation() );;
            htmlLabel.setValue( decimalFormat.format( value ) + " " + valueAccessor.getUnitsAbbreviation() );
//        readoutGraphic.setHtml( html );
//        readoutGraphic.setText( "" + valueAccessor.getA() + " = " + decimalFormat.format( value ) + " " + valueAccessor.getUnitsAbbreviation() );
        }
    }

    private void dataAdded() {
        TimePoint pt = series.getLastPoint();
        addPoint( pt );
        updateReadout( pt.getValue() );
    }

    private void addPoint( TimePoint at ) {
        BufferedImage bufferedImage = plotSuite.getChartImage();
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        Point2D screenPoint = plotSuite.toImageLocation( at.getTime(), at.getValue() );

        if( lastScreenPoint != null ) {
            if( visible ) {
                Line2D.Double screenLine = new Line2D.Double( lastScreenPoint, screenPoint );
                graphics2D.setColor( transparentColor );
                graphics2D.setClip( plotSuite.getDataArea() );

                graphics2D.setStroke( stroke );
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
        updateReadout();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        htmlLabel.setVisible( visible );
//        readoutGraphic.setVisible( visible );
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
        return htmlLabel;
    }

    public void setFont( Font font ) {
        htmlLabel.setFont( font );
    }

    public void setShadowOffset( int dx, int dy ) {
//        readoutGraphic.setShadowOffset( dx, dy );
        htmlLabel.setShadowOffset( dx, dy );
    }
}
