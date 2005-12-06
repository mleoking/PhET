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

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * GradientBandRenderer
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GradientBandRenderer extends AbstractXYItemRenderer {

    private Rectangle2D _previousDataArea;
    private double _bandWidth;  // in model coordinates
    private BufferedImage _image;

    public GradientBandRenderer( double bandWidth ) {
        super();
        if ( bandWidth <= 0 ) {
            throw new IllegalArgumentException( "bandWidth must be > 0: " + bandWidth );
        }
        _bandWidth = bandWidth;
        _previousDataArea = new Rectangle2D.Double();
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

        if ( !isSeriesVisible( series ) || series != 0 ) {
            return;
        }

        if ( !dataArea.equals( _previousDataArea ) || _image == null ) {
            updateBand( dataArea, plot, domainAxis, rangeAxis, dataset, series, item );
            _previousDataArea.setRect( dataArea );
        }
        
        if ( _image != null ) {
            // Axis (model) coordinates
            double aMinX = domainAxis.getLowerBound();
            double aCenterY = dataset.getYValue( series, 0 );

            // Java2D coordinates
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            double jMinX = domainAxis.valueToJava2D( aMinX, dataArea, domainAxisLocation );
            double jCenterY = rangeAxis.valueToJava2D( aCenterY, dataArea, rangeAxisLocation );
            double jMinY = jCenterY - ( _image.getHeight() / 2 );

            AffineTransform transform = new AffineTransform();
            transform.translate( jMinX, jMinY );
            g2.drawRenderedImage( _image, transform );
        }
    }

    private void updateBand(
            Rectangle2D dataArea,             
            XYPlot plot, 
            ValueAxis domainAxis, 
            ValueAxis rangeAxis, 
            XYDataset dataset, 
            int series, 
            int item ) {

        // Axis (model) coordinates
        double aMinX = domainAxis.getLowerBound();
        double aMaxX = domainAxis.getUpperBound();
        double aCenterY = dataset.getYValue( series, 0 );
        double aMinY = aCenterY - ( _bandWidth / 2 );
        double aMaxY = aCenterY + ( _bandWidth / 2 );

        // Java2D coorinates
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        double jMinX = domainAxis.valueToJava2D( aMinX, dataArea, domainAxisLocation );
        double jMaxX = domainAxis.valueToJava2D( aMaxX, dataArea, domainAxisLocation );
        double jMinY = rangeAxis.valueToJava2D( aMaxY, dataArea, rangeAxisLocation ); // +y is down
        double jMaxY = rangeAxis.valueToJava2D( aMinY, dataArea, rangeAxisLocation ); // +y is down
        double jWidth = jMaxX - jMinX;
        double jHeight = jMaxY - jMinY;
       
        if ( jWidth > 0 && jHeight > 0 ) {
            
            final double overlap = .05 * jHeight;
            Shape shape1 = new Rectangle2D.Double( 0, 0, jWidth, ( jHeight / 2 ) + overlap );
            Shape shape2 = new Rectangle2D.Double( 0, ( jHeight / 2 ) - overlap, jWidth, (jHeight / 2 ) + overlap );

            Color color1 = QTConstants.TOTAL_ENERGY_COLOR;
            Color color2 = new Color( color1.getRed(), color1.getGreen(), color1.getBlue(), 0 );

            GradientPaint gradient1 = new GradientPaint( 0f, 0f, color2, 0f, (float) jHeight / 2f, color1 );
            GradientPaint gradient2 = new GradientPaint( 0f, (float) jHeight / 2, color1, 0f, (float) jHeight, color2 );

            _image = new BufferedImage( (int)jWidth, (int)jHeight, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g2 = _image.createGraphics();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setPaint( gradient1 );
            g2.fill( shape1 );
            g2.setPaint( gradient2 );
            g2.fill( shape2 );
        }
    }
}
