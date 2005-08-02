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
 * XYStepRenderer.java
 * -------------------
 * (C) Copyright 2002-2005, by Roger Studner and Contributors.
 *
 * Original Author:  Roger Studner;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Matthias Rose;
 *
 * $Id$
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1, contributed by Roger Studner (DG);
 * 25-Jun-2002 : Updated import statements (DG);
 * 22-Jul-2002 : Added check for null data items (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 28-Oct-2003 : Added tooltips, code contributed by Matthias Rose 
 *               (RFE 824857) (DG);
 * 10-Feb-2004 : Removed working line (use line from state object instead) (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState.  Renamed 
 *               XYToolTipGenerator --> XYItemLabelGenerator (DG);
 * 19-Jan-2005 : Now accesses only primitives from dataset (DG);
 * 15-Mar-2005 : Fix silly bug in drawItem() method (DG);
 *
 */

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * Line/Step item renderer for an {@link XYPlot}.  This class draws lines 
 * between data points, only allowing horizontal or vertical lines (steps).
 *
 * @author Roger Studner
 */
public class XYStepRenderer extends AbstractXYItemRenderer 
                            implements XYItemRenderer, 
                                       Cloneable,
                                       PublicCloneable,
                                       Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -8918141928884796108L;
    
    /**
     * Constructs a new renderer with no tooltip or URL generation.
     */
    public XYStepRenderer() {
        super();
    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the item label generator.
     * @param urlGenerator  the URL generator.
     */
    public XYStepRenderer(XYToolTipGenerator toolTipGenerator,
                          XYURLGenerator urlGenerator) {

        super();
        setBaseToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index (ignored here).
     */
    public void drawItem(Graphics2D g2, 
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
                         int pass) {

        PlotOrientation orientation = plot.getOrientation();
        
        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            return;
        }

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);

        if (item > 0) {
            // get the previous data point...
            double x0 = dataset.getXValue(series, item - 1);
            double y0 = dataset.getYValue(series, item - 1);
            if (!Double.isNaN(y0)) {
                double transX0 = domainAxis.valueToJava2D(
                    x0, dataArea, xAxisLocation
                );
                double transY0 = rangeAxis.valueToJava2D(
                    y0, dataArea, yAxisLocation
                );

                Line2D line = state.workingLine;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    if (transY0 == transY1) { //this represents the situation 
                                              // for drawing a horizontal bar.
                        line.setLine(transY0, transX0, transY1, transX1);
                        g2.draw(line);
                    }
                    else {  //this handles the need to perform a 'step'.
                        line.setLine(transY0, transX0, transY1, transX0);
                        g2.draw(line);
                        line.setLine(transY1, transX0, transY1, transX1);
                        g2.draw(line);
                    }
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    if (transY0 == transY1) { // this represents the situation 
                                              // for drawing a horizontal bar.
                        line.setLine(transX0, transY0, transX1, transY1);
                        g2.draw(line);
                    }
                    else {  //this handles the need to perform a 'step'.
                        line.setLine(transX0, transY0, transX1, transY0);
                        g2.draw(line);
                        line.setLine(transX1, transY0, transX1, transY1);
                        g2.draw(line);
                    }
                }

            }
        }

        updateCrosshairValues(
            crosshairState, x1, y1, transX1, transY1, orientation
        );
        
        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities 
                = state.getInfo().getOwner().getEntityCollection();
            if (entities != null) {
                Shape shape = orientation == PlotOrientation.VERTICAL
                    ? new Rectangle2D.Double(transX1 - 2, transY1 - 2, 4.0, 4.0)
                    : new Rectangle2D.Double(
                        transY1 - 2, transX1 - 2, 4.0, 4.0
                    );           
                if (shape != null) {
                    String tip = null;
                    XYToolTipGenerator generator 
                        = getToolTipGenerator(series, item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(
                            dataset, series, item
                        );
                    }
                    XYItemEntity entity = new XYItemEntity(
                        shape, dataset, series, item, tip, url
                    );
                    entities.add(entity);
                }
            }
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
