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
 * PhaseRenderer renders a "phase view" data series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhaseRenderer extends AbstractXYItemRenderer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // number of point in our polygons
    private static int POLYGON_POINTS = 4;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // coordinates used to describe a polygon
    private int _xCoordinates[], _yCoordinates[];
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public PhaseRenderer() {
        super();
        _xCoordinates = new int[POLYGON_POINTS];
        _yCoordinates = new int[POLYGON_POINTS];
    }
    
    //----------------------------------------------------------------------------
    // AbstractXYItemRenderer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Draws an item in the dataset.
     * <p>
     * The dataset consists of interleaved magnitude and phase values
     * (in that order). When drawing a magnitude/phase pair (M1,P1), 
     * we look ahead at the next pair (M2,P2).  M1 and M2 are used to
     * construct a 4-sided polygon, which is filled using a color
     * based on P1.  (P2 is ignored.)  The color is created by performing
     * and HSV-to-RGB conversion using P1 as the hue component.
     */
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
    
    //----------------------------------------------------------------------------
    // Misc.
    //----------------------------------------------------------------------------
    
    /*
     * Determines if an item is visible.
     * Note that there are many levels of visibility, and JFreeChart's 
     * renderers are very inconsitent in their support of visibility.
     * 
     * @param series
     * @param item
     */
    private boolean isVisible( int series, int item ) {
        return ( getSeriesVisible().booleanValue() && isSeriesVisible( series ) && getItemVisible( series, item ) );
    }
    
    /*
     * Convert phase angle to RGB color.
     * 
     * @param phase phase angle, in radians
     */
    private Color phaseToRGB( double phase ) {
        float H = ( (float) Math.toDegrees( phase ) % 360f ) / 360f;
        return Color.getHSBColor( H, 1f, 1f );
    }
}
