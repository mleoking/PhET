/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphics2D;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class BufferedLinePlot {
    private boolean visible = true;
    private boolean autorepaint = true;
    private GeneralPath generalPath;
    private Stroke stroke;
    private Paint paint;
    private BufferedChart bufferedChart;

    public BufferedLinePlot( BufferedChart chart ) {
        this( chart, new BasicStroke( 1 ), Color.black );
    }

    public BufferedLinePlot( BufferedChart bufferedChart, Stroke stroke, Paint paint ) {
        this.bufferedChart = bufferedChart;
        this.stroke = stroke;
        this.paint = paint;
    }

    public Rectangle lineTo( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        Point viewLocation = bufferedChart.toBufferCoordinates( point );
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

    private void drawToBuffer( Line2D line ) {
        if( isVisible() ) {
            PhetGraphics2D g2 = createGraphics();
            g2.pushState();
            g2.setAntialias( true );
            g2.setStrokePure();
            g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            g2.setStroke( stroke );
            g2.setPaint( paint );
            Shape origClip = g2.getClip();

            g2.setClip( getClip() );
            g2.draw( line );
            g2.setClip( origClip );
            g2.popState();
        }
    }

    private Shape getClip() {
        return bufferedChart.getChartArea();
    }

    private PhetGraphics2D createGraphics() {
        return new PhetGraphics2D( bufferedChart.createGraphics() );
    }

    public void cleared() {
        if( generalPath != null ) {
            Rectangle shape = stroke.createStrokedShape( generalPath ).getBounds();
            bufferedChart.getComponent().repaint( shape.x, shape.y, shape.width, shape.height );
        }
        generalPath = null;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void repaintAll() {
        PhetGraphics2D graphics2D = createGraphics();
        if( generalPath != null ) {
            graphics2D.pushState();
            graphics2D.setStrokePure();
            graphics2D.setAntialias( true );
            graphics2D.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            graphics2D.setStroke( stroke );
            graphics2D.setPaint( paint );
            graphics2D.setClip( getClip() );
            graphics2D.draw( generalPath );
            graphics2D.popState();
        }
    }

    public void clear() {
        generalPath = null;
    }

    public void setAutoRepaint( boolean autorepaint ) {
        this.autorepaint = autorepaint;
    }

    public void setBufferedChart( BufferedChart bufferedChart ) {
        this.bufferedChart = bufferedChart;
    }
}