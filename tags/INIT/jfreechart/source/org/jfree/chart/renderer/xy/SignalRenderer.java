/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * SignalRenderer.java
 * -------------------
 * (C) Copyright 2001-2004, by Sylvain Viuejot and Contributors.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id$
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1.  Based on code in the SignalsPlot class, written by
 *               Sylvain Vieujot (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem() method (DG);
 * 14-Feb-2002 : Added small fix from Sylvain (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers 
 *               no longer need to be immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem() method, and 
 *               changed the return type of the drawItem method to void, 
 *               reflecting a change in the XYItemRenderer interface.  Added 
 *               tooltip code to drawItem() method (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML
 *               image maps (RA);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 
 */

package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.SignalsDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws signals on an {@link XYPlot}.
 *
 * @author Sylvain Vieujot
 */
public class SignalRenderer extends AbstractXYItemRenderer 
                            implements XYItemRenderer, 
                                       Cloneable,
                                       PublicCloneable,
                                       Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 1161092564576638268L;
    
    /** The mark offset. */
    private double markOffset = 5;

    /** The shape width. */
    private double shapeWidth = 15;

    /** The shape height. */
    private double shapeHeight = 25;

    /**
     * Creates a new renderer.
     */
    public SignalRenderer() {
        super();
    }

    /**
     * Returns the mark offset.
     *
     * @return The mark offset.
     */
    public double getMarkOffset() {
        return this.markOffset;
    }

    /**
     * Sets the mark offset.
     *
     * @param offset  the mark offset.
     */
    public void setMarkOffset(double offset) {
        this.markOffset = offset;
    }

    /**
     * Returns the shape width.
     *
     * @return The shape width.
     */
    public double getShapeWidth() {
        return this.shapeWidth;
    }

    /**
     * Sets the shape width.
     *
     * @param width  the shape width.
     */
    public void setShapeWidth(double width) {
        this.shapeWidth = width;
    }

    /**
     * Returns the shape height.
     *
     * @return The shape height.
     */
    public double getShapeHeight() {
        return this.shapeHeight;
    }

    /**
     * Sets the shape height.
     *
     * @param height  the shape height.
     */
    public void setShapeHeight(double height) {
        this.shapeHeight = height;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, 
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot, 
                         ValueAxis horizontalAxis, 
                         ValueAxis verticalAxis,
                         XYDataset dataset, 
                         int series, 
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        SignalsDataset signalData = (SignalsDataset) dataset;

        Number x = signalData.getX(series, item);
        Number y = signalData.getY(series, item);
        int type = signalData.getType(series, item);
        //double level = signalData.getLevel(series, item);

        double xx = horizontalAxis.valueToJava2D(
            x.doubleValue(), dataArea, plot.getDomainAxisEdge()
        );
        double yy = verticalAxis.valueToJava2D(
            y.doubleValue(), dataArea, plot.getRangeAxisEdge()
        );

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);
        g2.setPaint(p);
        g2.setStroke(s);

        int direction = 1;
        if ((type == SignalsDataset.ENTER_LONG) 
                || (type == SignalsDataset.EXIT_SHORT)) {
            yy = yy + this.markOffset;
            direction = -1;
        }
        else {
            yy = yy - this.markOffset;
        }

        GeneralPath path = new GeneralPath();
        if ((type == SignalsDataset.ENTER_LONG) 
                || (type == SignalsDataset.ENTER_SHORT)) {
            path.moveTo((float) xx, (float) yy);
            path.lineTo(
                (float) (xx + this.shapeWidth / 2), 
                (float) (yy - direction * this.shapeHeight / 3)
            );
            path.lineTo(
                (float) (xx + this.shapeWidth / 6), 
                (float) (yy - direction * this.shapeHeight / 3)
            );
            path.lineTo(
                (float) (xx + this.shapeWidth / 6), 
                (float) (yy - direction * this.shapeHeight)
            );
            path.lineTo(
                (float) (xx - this.shapeWidth / 6), 
                (float) (yy - direction * this.shapeHeight)
            );
            path.lineTo(
                (float) (xx - this.shapeWidth / 6), 
                (float) (yy - direction * this.shapeHeight / 3)
            );
            path.lineTo(
                (float) (xx - this.shapeWidth / 2), 
                (float) (yy - direction * this.shapeHeight / 3)
            );
            path.lineTo((float) xx, (float) yy);
        }
        else {
            path.moveTo((float) xx, (float) yy);
            path.lineTo(
                (float) xx, (float) (yy - direction * this.shapeHeight)
            );
            Ellipse2D.Double ellipse = new Ellipse2D.Double(
                xx - this.shapeWidth / 2,
                yy + (direction == 1 ? -this.shapeHeight 
                        : this.shapeHeight - this.shapeWidth),
                this.shapeWidth, 
                this.shapeWidth
            );
            path.append(ellipse, false);
        }

        g2.fill(path);
        g2.setPaint(Color.black);
        g2.draw(path);

        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(
                path, dataset, series, item, tip, url
            );
            entities.add(entity);
        }

    }

    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
