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
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;


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
        
        // every other item is a phase angle...
        if ( item % 2 == 1 ) {
            return;
        }
        
        // there is no corresponding polygon for the last 2 items...
        if ( item == dataset.getItemCount( series ) - 2 ) {
            return;
        }
        
        // get 2 adjacent data points and phase...
        double x1 = dataset.getXValue( series, item );
        double y1 = dataset.getYValue( series, item );
        double x2 = dataset.getXValue( series, item + 2 );
        double y2 = dataset.getYValue( series, item + 2 );
        double phase = dataset.getYValue( series, item + 1 );

        // translate the points to screen coordinates...
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double tx1 = domainAxis.valueToJava2D( x1, dataArea, xAxisLocation );
        double tx2 = domainAxis.valueToJava2D( x2, dataArea, xAxisLocation );
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double ty0 = rangeAxis.valueToJava2D( 0, dataArea, yAxisLocation );
        double ty1 = rangeAxis.valueToJava2D( y1, dataArea, yAxisLocation );
        double ty2 = rangeAxis.valueToJava2D( y2, dataArea, yAxisLocation );

        // determine the color that corresponds to the phase...
        Color color = phaseToRGB( phase );
        g2.setPaint( color );
        
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
    
    private boolean isVisible( int series, int item ) {
        return ( getSeriesVisible().booleanValue() && isSeriesVisible( series ) && getItemVisible( series, item ) );
    }
    
    /*
     * Convert phase angle to RGB color.
     * 
     * @param phase phase angle, in radians
     */
    private Color phaseToRGB( double phase ) {
        double H = Math.toDegrees( phase ) % 360.0; // H range: 0-360
        if ( H < 0 ) {
            H = 360.0 - ( Math.abs( H ) % 360.0 );
        }
        return HSVtoRGB( H, 1.0 /* S */, 1.0 /* V */);
    }
    
    /*
     * HSV to RGB colorspace conversion algorithm, 
     * from http://en.wikipedia.org/wiki/HSV_color_space.
     * 
     * @param H hue, 0.0 inclusive to 360.0 exclusive
     * @param S saturation. 0.0 to 1.0
     * @param V brightness, 0.0 to 1.0
     * @return RGB color
     */
    private Color HSVtoRGB( double H, double S, double V ) {
        assert( H >= 0 && H < 360 );
        assert( S >= 0 && S <= 1 );
        assert( V >= 0 && V <= 1 );
        
        double R, G, B;
        if ( S == 0 ) {
            R = G = B = V;
        }
        else {
            final int Hi = (int) ( Math.floor( H / 60 ) % 6 );
            final double f = ( H / 60 ) - Hi;
            final double p = V * ( 1 - S );
            final double q = V * ( 1 - ( f * S ) );
            final double t = V * ( 1 - ( ( 1 - f ) * S ) );
            switch ( Hi ) {
            case 0:
                R = V;
                G = t;
                B = p;
                break;
            case 1:
                R = q;
                G = V;
                B = p;
                break;
            case 2:
                R = p;
                G = V;
                B = t;
                break;
            case 3:
                R = p;
                G = q;
                B = V;
                break;
            case 4:
                R = t;
                G = p;
                B = V;
                break;
            case 5:
                R = V;
                G = p;
                B = q;
                break;
            default:
                throw new IllegalStateException( "Hi is out of range: " + Hi );
            }
        }
        return new Color( (float)R, (float)G, (float)B );
    }
}
