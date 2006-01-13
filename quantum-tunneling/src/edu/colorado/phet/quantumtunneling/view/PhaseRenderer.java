/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.quantumtunneling.util.Complex;


/**
 * PhaseRenderer
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhaseRenderer extends AbstractXYItemRenderer {

    private static int POLYGON_POINTS = 4;
    
    private int _xCoordinates[], _yCoordinates[];
    
    public PhaseRenderer() {
        super();
        _xCoordinates = new int[POLYGON_POINTS];
        _yCoordinates = new int[POLYGON_POINTS];
    }
    
    public void drawItem( 
            Graphics2D g2, 
            XYItemRendererState state, 
            Rectangle2D dataArea, 
            PlotRenderingInfo info, 
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item, 
            CrosshairState crosshairState,
            int pass ) {
        
        // draw only if visible...
        if ( !isVisible( series, item ) ) {
            return;
        }
        
        // there is no corresponding polygon for the last data point...
        if ( item == dataset.getItemCount( series ) - 1 ) {
            return;
        }
        
        // get 2 adjacent data points...
        double x1 = dataset.getXValue( series, item );
        double y1 = dataset.getYValue( series, item );
        double x2 = dataset.getXValue( series, item + 1 );
        double y2 = dataset.getYValue( series, item + 1 );

        // translate the points to screen coordinates...
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double tx1 = domainAxis.valueToJava2D( x1, dataArea, xAxisLocation );
        double tx2 = domainAxis.valueToJava2D( x2, dataArea, xAxisLocation );
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double ty0 = rangeAxis.valueToJava2D( 0, dataArea, yAxisLocation );
        double ty1 = rangeAxis.valueToJava2D( y1, dataArea, yAxisLocation );
        double ty2 = rangeAxis.valueToJava2D( y2, dataArea, yAxisLocation );

        // determine the color that corresponds to the phase...
//        g2.setPaint( VisZ( Psi[item] ) );
        g2.setPaint( Color.BLUE );//XXX
        
        // fill the area under the curve between the two data points...
        _xCoordinates[0] = (int) tx1;
        _yCoordinates[0] = (int) ty0;
        _xCoordinates[1] = (int) tx1;
        _yCoordinates[1] = (int) ty1;
        _xCoordinates[2] = (int) tx2;
        _yCoordinates[2] = (int) ty2;
        _xCoordinates[3] = (int) tx2;
        _yCoordinates[3] = (int) ty0;
        g2.fillPolygon( _xCoordinates, _yCoordinates, POLYGON_POINTS );
    }

    /**
     * This method was lifted from Brock University's modified Richardson program at
     * http://www.physics.brocku.ca/faculty/sternin/teaching/mirrors/qm/packet/wave-map.html
     * 
     * @param z
     * @return
     */
    private Color VisZ( Complex z ) {

        final double x = z.getReal();
        final double y = z.getImaginary();
        final double r = Math.sqrt( x * x + y * y );
        final double a = 0.40824829046386301636 * x;
        final double b = 0.70710678118654752440 * y;
        
        double red, green, blue, d;
        d = 1.0 / ( 1. + r * r );
        red = 0.5 + 0.81649658092772603273 * x * d;
        green = 0.5 - d * ( a - b );
        blue = 0.5 - d * ( a + b );
        d = 0.5 - r * d;
        if ( r < 1 ) {
            d = -d;
        }
        red += d;
        green += d;
        blue += d;
        
        return ( new Color( (float) red, (float) green, (float) blue ) );
    }
    
    private boolean isVisible( int series, int item ) {
        return ( getSeriesVisible().booleanValue() && isSeriesVisible( series ) && getItemVisible( series, item ) );
    }
}
