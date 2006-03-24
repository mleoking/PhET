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
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
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
 * PhaseRenderer renders a "phase view" of an XYDataset.
 * See drawItem for a description of how the dataset is organized
 * and what is rendered.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhaseRenderer extends AbstractXYItemRenderer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // shape used to describe a polygon
    private GeneralPath _polygonPath;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public PhaseRenderer() {
        super();
        _polygonPath = new GeneralPath();
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
        if ( !getItemVisible( series, item ) ) {
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

        // Enable antialiasing...
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        
        // determine the color that corresponds to the phase...
        Color color = phaseToRGB( phase );
        g2.setPaint( color );
        
        // fill the area under the curve between the two data points...
        //
        //     (tx1,ty1) +--------+ (tx2,ty2)
        //               |        |
        //               |        |
        //               |        |
        //     (tx1,ty0) +--------+ (tx2, ty0)
        //
        _polygonPath.reset();
        _polygonPath.moveTo( (float) tx1, (float) ty0 );
        _polygonPath.lineTo( (float) tx1, (float) ty1 );
        _polygonPath.lineTo( (float) tx2, (float) ty2 );
        _polygonPath.lineTo( (float) tx2, (float) ty0 );
        _polygonPath.closePath();
        g2.fill( _polygonPath );
    }
    
    //----------------------------------------------------------------------------
    // Color
    //----------------------------------------------------------------------------
    
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
