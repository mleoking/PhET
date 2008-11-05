/*  */
package edu.colorado.phet.theramp.view.plot;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.timeseries.TimePoint;
import edu.colorado.phet.theramp.timeseries.TimeSeries;
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

    public TimeSeriesPNode( TimePlotSuitePNode plotSuite, TimeSeries series, ValueAccessor valueAccessor, Color color, String justifyString ) {
        this.plotSuite = plotSuite;
        this.series = series;
        this.valueAccessor = valueAccessor;
        this.color = color;
        this.justifyString = justifyString;
        series.addObserver( new TimeSeries.Observer() {
            public void dataAdded() {
                TimeSeriesPNode.this.dataAdded();
            }

            public void cleared() {
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
        return new Font( PhetFont.getDefaultFontName(), Font.BOLD, 13 );
    }

    void updateReadout() {
        updateReadout( valueAccessor.getValue( getRampPhysicalModel() ) );
    }

    private RampPhysicalModel getRampPhysicalModel() {
        return plotSuite.getRampModule().getRampPhysicalModel();
    }

    private class HTMLLabel extends PNode {
        private ShadowHTMLNode htmlNode;
        private ShadowPText valueGraphic;
        private PImage labelAsImage;

        public HTMLLabel( String html, Color color, Font defaultFont, int textInsetDY ) {
            htmlNode = new ShadowHTMLNode( html );
            valueGraphic = new ShadowPText();
            setPickable( false );
            setChildrenPickable( false );
            htmlNode.setShadowOffset( 1, 1 );
            htmlNode.setShadowColor( Color.darkGray );
            htmlNode.setColor( color );
            valueGraphic.setTextPaint( color );
            htmlNode.setFont( defaultFont );
            valueGraphic.setFont( defaultFont );

//            addChild( htmlGraphic );
            addChild( valueGraphic );
            valueGraphic.setOffset( htmlNode.getFullBounds().getWidth(), textInsetDY );

            Image im = htmlNode.toImage();
            labelAsImage = new PImage( im );
            addChild( labelAsImage );
        }

        public void setValue( String value ) {
            valueGraphic.setText( value );
        }

        public void setFont( Font font ) {
            htmlNode.setFont( font );
            valueGraphic.setFont( font );
            System.out.println( "TimeSeriesPNode$HTMLLabel.setFont" );
            System.err.println( "Not supported" );
        }

        public void setShadowOffset( int dx, int dy ) {
            htmlNode.setShadowOffset( dx, dy );
        }
    }

    private void updateReadout( double value ) {
        if( lastUpdateValue != value ) {
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
