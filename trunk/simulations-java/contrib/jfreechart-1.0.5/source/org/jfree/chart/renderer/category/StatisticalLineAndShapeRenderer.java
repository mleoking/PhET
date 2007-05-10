/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 * 
 * ------------------------------------
 * StatisticalLineAndShapeRenderer.java
 * ------------------------------------
 * (C) Copyright 2005, 2006, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Mofeed Shahin;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: StatisticalLineAndShapeRenderer.java,v 1.4.2.7 2006/09/25 10:09:58 mungady Exp $
 *
 * Changes
 * -------
 * 01-Feb-2005 : Version 1, contributed by Mofeed Shahin (DG);
 * 16-Jun-2005 : Added errorIndicatorPaint to be consistent with 
 *               StatisticalBarRenderer (DG);
 * ------------- JFREECHART 1.0.0 ---------------------------------------------
 * 11-Apr-2006 : Fixed bug 1468794, error bars drawn incorrectly when rendering 
 *               plots with horizontal orientation (DG);
 * 25-Sep-2006 : Fixed bug 1562759, constructor ignoring arguments (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
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
    
    /** The paint used to show the error indicator. */
    private transient Paint errorIndicatorPaint;

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
        super(linesVisible, shapesVisible);
        this.errorIndicatorPaint = null;
    }

    /**
     * Returns the paint used for the error indicators.
     * 
     * @return The paint used for the error indicators (possibly 
     *         <code>null</code>).
     */
    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;   
    }

    /**
     * Sets the paint used for the error indicators (if <code>null</code>, 
     * the item outline paint is used instead)
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setErrorIndicatorPaint(Paint paint) {
        this.errorIndicatorPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
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
     * @param dataset  the dataset (a {@link StatisticalCategoryDataset} is 
     *   required).
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
        double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), 
                dataArea, plot.getDomainAxisEdge());

        double y1 = rangeAxis.valueToJava2D(meanValue.doubleValue(), dataArea, 
                plot.getRangeAxisEdge());

        Shape shape = getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
        }
        if (getItemShapeVisible(row, column)) {
            
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

        if (getItemLineVisible(row, column)) {
            if (column != 0) {

                Number previousValue = statData.getValue(row, column - 1);
                if (previousValue != null) {

                    // previous data point...
                    double previous = previousValue.doubleValue();
                    double x0 = domainAxis.getCategoryMiddle(column - 1, 
                            getColumnCount(), dataArea, 
                            plot.getDomainAxisEdge());
                    double y0 = rangeAxis.valueToJava2D(previous, dataArea, 
                            plot.getRangeAxisEdge());

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
        double rectX = domainAxis.getCategoryStart(column, getColumnCount(), 
                dataArea, xAxisLocation);
        
        rectX = rectX + row * state.getBarWidth();
        
        g2.setPaint(getItemPaint(row, column));

        //standard deviation lines
        double valueDelta = statData.getStdDevValue(row, column).doubleValue(); 

        double highVal, lowVal;
        if ((meanValue.doubleValue() + valueDelta) 
                > rangeAxis.getRange().getUpperBound()) {
            highVal = rangeAxis.valueToJava2D(
                    rangeAxis.getRange().getUpperBound(), dataArea, 
                    yAxisLocation);
        }
        else {
            highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() 
                    + valueDelta, dataArea, yAxisLocation);
        }
        
        if ((meanValue.doubleValue() + valueDelta) 
                < rangeAxis.getRange().getLowerBound()) {
            lowVal = rangeAxis.valueToJava2D(
                    rangeAxis.getRange().getLowerBound(), dataArea, 
                    yAxisLocation);
        }
        else {
            lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() 
                    - valueDelta, dataArea, yAxisLocation);
        }
        
        if (this.errorIndicatorPaint != null) {
            g2.setPaint(this.errorIndicatorPaint);  
        }
        else {
            g2.setPaint(getItemPaint(row, column));   
        }
        Line2D line = new Line2D.Double();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line.setLine(lowVal, x1, highVal, x1);
            g2.draw(line);
            line.setLine(lowVal, x1 - 5.0d, lowVal, x1 + 5.0d);
            g2.draw(line);
            line.setLine(highVal, x1 - 5.0d, highVal, x1 + 5.0d);
            g2.draw(line);
        }
        else {  // PlotOrientation.VERTICAL
            line.setLine(x1, lowVal, x1, highVal);
            g2.draw(line);
            line.setLine(x1 - 5.0d, highVal, x1 + 5.0d, highVal);
            g2.draw(line);
            line.setLine(x1 - 5.0d, lowVal, x1 + 5.0d, lowVal);
            g2.draw(line);
        }
        
        // draw the item label if there is one...
        if (isItemLabelVisible(row, column)) {
            if (orientation == PlotOrientation.HORIZONTAL) {
              drawItemLabel(g2, orientation, dataset, row, column, 
                  y1, x1, (meanValue.doubleValue() < 0.0));
            }
            else if (orientation == PlotOrientation.VERTICAL) {
              drawItemLabel(g2, orientation, dataset, row, column, 
                  x1, y1, (meanValue.doubleValue() < 0.0));                
            }
        }

        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null && shape != null) {
                String tip = null;
                CategoryToolTipGenerator tipster = getToolTipGenerator(row, 
                        column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(
                            dataset, row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(shape, tip, 
                        url, dataset, row, dataset.getColumnKey(column), 
                        column);
                entities.add(entity);

            }

        }

    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof StatisticalLineAndShapeRenderer)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;   
        }
        StatisticalLineAndShapeRenderer that 
            = (StatisticalLineAndShapeRenderer) obj;
        if (!PaintUtilities.equal(this.errorIndicatorPaint, 
                that.errorIndicatorPaint)) {
            return false;
        }
        return true;
    }
    
    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
    }

}
