/* Copyright 2004, Sam Reid */
package edu.colorado.phet.jfreechart.tests;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * 'Split' refers to the ability to draw just the chart, just the data or both.
 */
public class SplitXYPlot extends XYPlot {
    private boolean renderData = true;
    private boolean backgroundIsBuffered = false;
    private BufferedImage buffer;

    public SplitXYPlot() {
    }

    public SplitXYPlot( XYDataset dataset, NumberAxis xAxis, NumberAxis yAxis, XYItemRenderer o ) {
        super( dataset, xAxis, yAxis, o );
    }

    public void updateBuffer( JFreeChart jFreeChart, int width, int height ) {
        if( width > 0 && height > 0 ) {
            boolean origRenderData = renderData;
            boolean origBNB = backgroundIsBuffered;
            renderData = false;
            backgroundIsBuffered = false;
            buffer = jFreeChart.createBufferedImage( width, height );
            System.out.println( "new Buffer Size=" + new Dimension( width, height ) + ", image.getWidth=" + buffer.getWidth() + ", image.getHeight=" + buffer.getHeight() );
            renderData = origRenderData;
            backgroundIsBuffered = origBNB;
        }
    }

    //draw everything.
    public void draw( Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info ) {
        if( !backgroundIsBuffered ) {
            super.draw( g2, area, anchor, parentState, info );
        }
        else if( renderData ) {
            drawDataOnly( g2, area, anchor, info );
        }
    }

    //draw the data
    public boolean render( Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState ) {
        if( renderData ) {
            return super.render( g2, dataArea, index, info, crosshairState );
        }
        else {
            return false;
        }
    }

    private void drawDataOnly( Graphics2D g2, Rectangle2D area, Point2D anchor, PlotRenderingInfo info ) {
        if( backgroundIsBuffered ) {
            if( buffer != null ) {
//                System.out.println( "rendering buffer: image.getWidth=" + buffer.getWidth() + ", image.getHeight=" + buffer.getHeight() );
                g2.drawRenderedImage( buffer, new AffineTransform() );
            }
        }
        // if the plot area is too small, just return...
        boolean b1 = ( area.getWidth() <= MINIMUM_WIDTH_TO_DRAW );
        boolean b2 = ( area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW );
        if( b1 || b2 ) {
            return;
        }

        // record the plot area...
        if( info != null ) {
            info.setPlotArea( area );
        }

        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim( area );

        AxisSpace space = calculateAxisSpace( g2, area );
        Rectangle2D dataArea = space.shrink( area, null );
        super.getAxisOffset().trim( dataArea );

        if( info != null ) {
            info.setDataArea( dataArea );
        }

        if( anchor != null && !dataArea.contains( anchor ) ) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance( Double.POSITIVE_INFINITY );
        crosshairState.setAnchor( anchor );
        crosshairState.setCrosshairX( getDomainCrosshairValue() );
        crosshairState.setCrosshairY( getRangeCrosshairValue() );
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip( dataArea );
        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, getForegroundAlpha()
                )
        );

        // now draw annotations and render data items...
        boolean foundData = false;

        for( int i = getDatasetCount() - 1; i >= 0; i-- ) {
            foundData = render( g2, dataArea, i, info, crosshairState )
                        || foundData;
        }

        g2.setClip( originalClip );
        g2.setComposite( originalComposite );
    }

    public void setBackgroundIsBuffered( boolean b ) {
        this.backgroundIsBuffered = b;
    }

    public void setRenderData( boolean renderData ) {
        this.renderData = renderData;
    }
}
