/* Copyright 2004, Sam Reid */
package edu.colorado.phet.jfreechart.tests;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Avoids using AffineTransform.createTransformedShape().
 * Instead, translation is assumed, and done manually.
 */
public class FastXYLineAndShapeRenderer extends XYLineAndShapeRenderer {
    public FastXYLineAndShapeRenderer( boolean lines, boolean shapes ) {
        super( lines, shapes );
    }

    protected void drawSecondaryPass( Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, CrosshairState crosshairState, EntityCollection entities ) {

        Shape entityArea = null;

        // get the data point...
        double x1 = dataset.getXValue( series, item );
        double y1 = dataset.getYValue( series, item );
        if( Double.isNaN( y1 ) || Double.isNaN( x1 ) ) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D( x1, dataArea, xAxisLocation );
        double transY1 = rangeAxis.valueToJava2D( y1, dataArea, yAxisLocation );

        if( getItemShapeVisible( series, item ) ) {
            Rectangle2D.Double shape = (Rectangle2D.Double)getItemShape( series, item );
            shape = new Rectangle2D.Double( shape.x, shape.y, shape.width, shape.height );
            PlotOrientation orientation = plot.getOrientation();
            if( orientation == PlotOrientation.HORIZONTAL ) {
                shape.x += transY1;
                shape.y += transX1;
            }
//                else if( orientation == PlotOrientation.VERTICAL ) {
//                    shape = ShapeUtilities.createTranslatedShape(
//                            shape, transX1, transY1
//                    );
//                }
            entityArea = shape;
            if( shape.intersects( dataArea ) ) {
                if( getItemShapeFilled( series, item ) ) {
                    if( this.getUseFillPaint() ) {
                        g2.setPaint( getItemFillPaint( series, item ) );
                    }
                    else {
                        g2.setPaint( getItemPaint( series, item ) );
                    }
                    g2.fill( shape );
                }
//                    if( this.getDrawOutlines() ) {
//                        if( getUseOutlinePaint() ) {
//                            g2.setPaint( getItemOutlinePaint( series, item ) );
//                        }
//                        else {
//                            g2.setPaint( getItemPaint( series, item ) );
//                        }
//                        g2.setStroke( getItemOutlineStroke( series, item ) );
//                        g2.draw( shape );
//                    }
            }
        }

        updateCrosshairValues(
                crosshairState, x1, y1, transX1, transY1, plot.getOrientation()
        );

        // add an entity for the item...
        if( entities != null ) {
            addEntity(
                    entities, entityArea, dataset, series, item, transX1, transY1
            );
        }
    }
}
