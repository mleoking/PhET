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
 * ------------------------------------
 * StatisticalLineAndShapeRenderer.java
 * ------------------------------------
 * (C) Copyright 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Mofeed Shahin;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes
 * -------
 * 01-Feb-2005 : Version 1, contributed by Mofeed Shahin (DG);
 *  
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer that draws shapes for each data item, and lines between data 
 * items.  Each point has a mean value and a standard deviation line. For use 
 * with the {@link CategoryPlot} class.
 */
public class StatisticalLineAndShapeRenderer extends LineAndShapeRenderer 
    implements Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -3557517173697777579L;
    
    /**
     * Constructs a default renderer (draws shapes and lines).
     */
    public StatisticalLineAndShapeRenderer() {
        this(true, true);
    }

    /**
     * Constructs a new renderer.
     * 
     * @param linesVisible  draw lines?
     * @param shapesVisible  draw shapes?
     */
    public StatisticalLineAndShapeRenderer(boolean linesVisible, 
                                           boolean shapesVisible) {
        super(true, true);
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass.
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {

        // nothing is drawn for null...
        Number v = dataset.getValue(row, column);
        if (v == null) {
          return;
        }

        StatisticalCategoryDataset statData 
            = (StatisticalCategoryDataset) dataset;

        Number meanValue = statData.getMeanValue(row, column);

        PlotOrientation orientation = plot.getOrientation();

        // current data point...
        double x1 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );

        double y1 = rangeAxis.valueToJava2D(
            meanValue.doubleValue(), dataArea, plot.getRangeAxisEdge()
        );


        Shape shape = getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
        }
        if (isShapesVisible()) {
            
            if (getItemShapeFilled(row, column)) {
                g2.setPaint(getItemPaint(row, column));
                g2.fill(shape);
            }
            else {
                if (getUseOutlinePaint()) {
                    g2.setPaint(getItemOutlinePaint(row, column));   
                }
                else {
                    g2.setPaint(getItemPaint(row, column));
                }
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.draw(shape);
            }
        }

        if (isLinesVisible()) {
            if (column != 0) {

                Number previousValue = statData.getValue(row, column - 1);
                if (previousValue != null) {

                    // previous data point...
                    double previous = previousValue.doubleValue();
                    double x0 = domainAxis.getCategoryMiddle(
                        column - 1, getColumnCount(), dataArea, 
                        plot.getDomainAxisEdge()
                    );
                    double y0 = rangeAxis.valueToJava2D(
                        previous, dataArea, plot.getRangeAxisEdge()
                    );

                    Line2D line = null;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        line = new Line2D.Double(y0, x0, y1, x1);
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        line = new Line2D.Double(x0, y0, x1, y1);
                    }
                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));
                    g2.draw(line);
                }
            }
        }

        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double rectX = domainAxis.getCategoryStart(
            column, getColumnCount(), dataArea, xAxisLocation
        );
        
        rectX = rectX + row * state.getBarWidth();
        
        g2.setPaint(getItemPaint(row, column));
        //standard deviation lines
        double valueDelta = statData.getStdDevValue(row, column).doubleValue(); 

        double highVal, lowVal;
        if ((meanValue.doubleValue() + valueDelta) 
                > rangeAxis.getRange().getUpperBound()) {
            highVal = rangeAxis.valueToJava2D(
                rangeAxis.getRange().getUpperBound(), dataArea, yAxisLocation
            );
        }
        else {
            highVal = rangeAxis.valueToJava2D(
                meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation
            );
        }
        
        if ((meanValue.doubleValue() + valueDelta) 
                < rangeAxis.getRange().getLowerBound()) {
            lowVal = rangeAxis.valueToJava2D(
                rangeAxis.getRange().getLowerBound(), dataArea, yAxisLocation
            );
        }
        else {
            lowVal = rangeAxis.valueToJava2D(
                meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation
            );
        }
        
        Line2D line = null;
        line = new Line2D.Double(x1, lowVal, x1, highVal);
        g2.draw(line);
        line = new Line2D.Double(x1 - 5.0d, highVal, x1 + 5.0d, highVal);
        g2.draw(line);
        line = new Line2D.Double(x1 - 5.0d, lowVal, x1 + 5.0d, lowVal);
        g2.draw(line);
        
        
        // draw the item label if there is one...
        if (isItemLabelVisible(row, column)) {
            if (orientation == PlotOrientation.HORIZONTAL) {
              drawItemLabel(
                  g2, orientation, dataset, row, column, 
                  y1, x1, (meanValue.doubleValue() < 0.0)
              );
            }
            else if (orientation == PlotOrientation.VERTICAL) {
              drawItemLabel(
                  g2, orientation, dataset, row, column, 
                  x1, y1, (meanValue.doubleValue() < 0.0)
              );                
            }
        }

        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities 
                = state.getInfo().getOwner().getEntityCollection();
            if (entities != null && shape != null) {
                String tip = null;
                CategoryToolTipGenerator tipster = getToolTipGenerator(
                    row, column
                );
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(
                        dataset, row, column
                    );
                }
                CategoryItemEntity entity = new CategoryItemEntity(
                    shape, tip, url, dataset, row, 
                    dataset.getColumnKey(column), column
                );
                entities.add(entity);

            }

        }

    }
    
}
