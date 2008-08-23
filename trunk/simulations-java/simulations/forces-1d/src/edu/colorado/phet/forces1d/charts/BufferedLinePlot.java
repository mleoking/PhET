/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.forces1d.charts;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.BufferedPhetGraphic;

public class BufferedLinePlot implements DataSet.Observer {
    private boolean visible = true;
    private GeneralPath generalPath;
    private Stroke stroke;
    private Paint paint;
    private BufferedPhetGraphic bufferedPhetGraphic;
    private Chart chart;
    private boolean autorepaint;

    public BufferedLinePlot( Chart chart, DataSet dataSet, BufferedPhetGraphic bufferedPhetGraphic ) {
        this( chart, dataSet, new BasicStroke( 1 ), Color.black, bufferedPhetGraphic );
    }

    public BufferedLinePlot( Chart chart, DataSet dataSet, Stroke stroke, Paint paint, BufferedPhetGraphic bufferedPhetGraphic ) {
        this.chart = chart;
        dataSet.addObserver( this );
        this.stroke = stroke;
        this.paint = paint;
        this.bufferedPhetGraphic = bufferedPhetGraphic;
    }

    public void pointAdded( Point2D point ) {
        if ( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        Point viewLocation = chart.transform( point );
        if ( generalPath == null ) {
            generalPath = new GeneralPath();
            generalPath.moveTo( viewLocation.x, viewLocation.y );
        }
        else {

            if ( isVisible() && autorepaint ) {
                //Determine the exact region for repaint.
                Line2D line = new Line2D.Double( generalPath.getCurrentPoint(), viewLocation );

                Rectangle bounds = stroke.createStrokedShape( line ).getBounds();
                drawToBuffer( line );
                JComponent jc = (JComponent) chart.getComponent();

//                jc.paintImmediately( bounds.x, bounds.y, bounds.width, bounds.height );
                jc.repaint( bounds.x, bounds.y, bounds.width, bounds.height );//We could pass a flag (or call a method) that this rectangle not be unioned with the rest of the crowd.
            }
            generalPath.lineTo( (float) viewLocation.getX(), (float) viewLocation.getY() );
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void pointRemoved( Point2D point ) {
    }

    private void drawToBuffer( Line2D line ) {
        if ( isVisible() ) {
            Graphics2D g2 = bufferedPhetGraphic.getBuffer().createGraphics();
            g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            g2.setStroke( stroke );
            g2.setPaint( paint );
            Shape origClip = g2.getClip();
            g2.setClip( chart.getViewBounds() );
            g2.draw( line );
            g2.setClip( origClip );
        }
    }

    public void cleared() {
        if ( generalPath != null ) {
            Rectangle shape = stroke.createStrokedShape( generalPath ).getBounds();
            chart.getComponent().repaint( shape.x, shape.y, shape.width, shape.height );
        }
        generalPath = null;
    }

    public void repaintAll() {
        Graphics2D graphics2D = bufferedPhetGraphic.getBuffer().createGraphics();
        Shape origClip = graphics2D.getClip();
        if ( generalPath != null ) {
            Stroke oldStroke = graphics2D.getStroke();
            Paint oldPaint = graphics2D.getPaint();
            graphics2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
            graphics2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            graphics2D.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY );
            graphics2D.setStroke( stroke );
            graphics2D.setPaint( paint );
            graphics2D.setClip( chart.getViewBounds() );
            graphics2D.draw( generalPath );
            graphics2D.setStroke( oldStroke );
            graphics2D.setPaint( oldPaint );
            graphics2D.setClip( origClip );
        }
    }

    public void clear() {
        generalPath = null;
    }

    public void setAutoRepaint( boolean autorepaint ) {
        this.autorepaint = autorepaint;
    }
}