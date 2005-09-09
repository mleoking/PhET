/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class BufferedLinePlotV2 {
    private boolean visible = true;
    private GeneralPath generalPath;
    private Stroke stroke;
    private Paint paint;
    private BufferedImage bufferedImage;
    private Chart chart;
    private boolean autorepaint;
    private int dx = 0;
    private int dy = 0;
//    private LinkedList rawData = new LinkedList();

    public BufferedLinePlotV2( Chart chart, BufferedImage bufferedImage ) {
        this( chart, new BasicStroke( 1 ), Color.black, bufferedImage );
    }

    public BufferedLinePlotV2( Chart chart, Stroke stroke, Paint paint, BufferedImage bufferedImage ) {
        this.chart = chart;
        this.bufferedImage = bufferedImage;
        this.stroke = stroke;
        this.paint = paint;
        setVisible( true );
    }

    public void setOffset( int dx, int dy ) {
        this.dx = dx;
        this.dy = dy;
    }

    public Rectangle lineTo( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
//        rawData.add( point );
        Point viewLocation = chart.transform( point );
//        System.out.println( "viewLocation = " + viewLocation );
        viewLocation.x += dx;
        viewLocation.y += dy;
//        System.out.println( "viewLocation @dx= " + viewLocation );
        if( generalPath == null ) {
            generalPath = new GeneralPath();
            generalPath.moveTo( viewLocation.x, viewLocation.y );
            return new Rectangle( viewLocation.x, viewLocation.y, 0, 0 );
        }
        else {
            Rectangle bounds = null;
            if( isVisible() && autorepaint ) {
                //Determine the exact region for repaint.
                Line2D line = new Line2D.Double( generalPath.getCurrentPoint(), viewLocation );

                bounds = stroke.createStrokedShape( line ).getBounds();
                drawToBuffer( line );
            }
            generalPath.lineTo( (float)viewLocation.getX(), (float)viewLocation.getY() );

            return bounds;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    private void drawToBuffer( Line2D line ) {
        if( isVisible() ) {
            Graphics2D g2 = bufferedImage.createGraphics();
//            g2.transform( chart.getNetTransform() );
            g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            g2.setStroke( stroke );
            g2.setPaint( paint );
            Shape origClip = g2.getClip();

            g2.setClip( getClip() );
            g2.draw( line );
            g2.setClip( origClip );
        }
    }

    private Shape getClip() {
        Rectangle chartBounds = new Rectangle( chart.getChartBounds() );
        chartBounds.x += dx;
        chartBounds.y += dy;
        return chartBounds;
    }

    public void cleared() {
        if( generalPath != null ) {
            Rectangle shape = stroke.createStrokedShape( generalPath ).getBounds();
            chart.getComponent().repaint( shape.x, shape.y, shape.width, shape.height );
        }
        generalPath = null;
    }

    public void repaintAll() {

        Graphics2D graphics2D = bufferedImage.createGraphics();
        Shape origClip = graphics2D.getClip();
        if( generalPath != null ) {
            Stroke oldStroke = graphics2D.getStroke();
            Paint oldPaint = graphics2D.getPaint();
            graphics2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
            graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            graphics2D.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            graphics2D.setStroke( stroke );
            graphics2D.setPaint( paint );
            graphics2D.setClip( getClip() );
            graphics2D.draw( generalPath );
            graphics2D.setStroke( oldStroke );
            graphics2D.setPaint( oldPaint );
            graphics2D.setClip( origClip );
        }
    }

    public void clear() {
        generalPath = null;
//        rawData.clear();
    }

    public void setAutoRepaint( boolean autorepaint ) {
        this.autorepaint = autorepaint;
    }

    public void setBufferedImage( BufferedImage image ) {
        this.bufferedImage = image;
    }
}