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
 * ------------------------
 * StackedAreaRenderer.java
 * ------------------------
 * (C) Copyright 2002-2005, by Dan Rivett (d.rivett@ukonline.co.uk) and 
 *                          Contributors.
 *
 * Original Author:  Dan Rivett (adapted from AreaCategoryItemRenderer);
 * Contributor(s):   Jon Iles;
 *                   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *
 * $Id$
 *
 * Changes:
 * --------
 * 20-Sep-2002 : Version 1, contributed by Dan Rivett;
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and 
 *               CategoryToolTipGenerator interface (DG);
 * 01-Nov-2002 : Added tooltips (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem() --> drawItem() and now using axis 
 *               for category spacing. Renamed StackedAreaCategoryItemRenderer 
 *               --> StackedAreaRenderer (DG);
 * 26-Nov-2002 : Switched CategoryDataset --> TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 13-May-2003 : Modified to take into account the plot orientation (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 07-Oct-2003 : Added renderer state (DG);
 * 29-Apr-2004 : Added getRangeExtent() override (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 07-Jan-2005 : Renamed getRangeExtent() --> findRangeBounds() (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws stacked area charts for a 
 * {@link org.jfree.chart.plot.CategoryPlot}.
 *
 * @author Dan Rivett
 */
public class StackedAreaRenderer extends AreaRenderer 
                                 implements Cloneable, PublicCloneable, 
                                            Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -3595635038460823663L;
     
    /**
     * Creates a new renderer.
     */
    public StackedAreaRenderer() {
        super();
    }

    /**
     * Returns the range of values the renderer requires to display all the 
     * items from the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * 
     * @return The range (or <code>null</code> if the dataset is empty).
     */
    public Range findRangeBounds(CategoryDataset dataset) {
        return DatasetUtilities.findStackedRangeBounds(dataset);   
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
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

        // plot non-null values...
        Number value = dataset.getValue(row, column);
        if (value == null) {
            return;
        }

        // leave the y values (y1, y0) untranslated as it is going to be be 
        // stacked up later by previous series values, after this it will be 
        // translated.
        double x1 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double y1 = 0.0;  // calculate later
        double y1Untranslated = value.doubleValue();

        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));

        if (column != 0) {

            Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {

                double x0 = domainAxis.getCategoryMiddle(
                    column - 1, getColumnCount(), dataArea, 
                    plot.getDomainAxisEdge()
                );
                double y0Untranslated = previousValue.doubleValue();

                // Get the previous height, but this will be different for both
                // y0 and y1 as the previous series values could differ.
                double previousHeightx0Untranslated 
                    = getPreviousHeight(dataset, row, column - 1);
                double previousHeightx1Untranslated 
                    = getPreviousHeight(dataset, row, column);

                // Now stack the current y values on top of the previous values.
                y0Untranslated += previousHeightx0Untranslated;
                y1Untranslated += previousHeightx1Untranslated;

                // Now translate the previous heights
                RectangleEdge location = plot.getRangeAxisEdge();
                double previousHeightx0 = rangeAxis.valueToJava2D(
                    previousHeightx0Untranslated, dataArea, location
                );
                double previousHeightx1 = rangeAxis.valueToJava2D(
                    previousHeightx1Untranslated, dataArea, location
                );

                // Now translate the current y values.
                double y0 = rangeAxis.valueToJava2D(
                    y0Untranslated, dataArea, location
                );
                y1 = rangeAxis.valueToJava2D(
                    y1Untranslated, dataArea, location
                );

                Polygon p = null;
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    p = new Polygon();
                    p.addPoint((int) y0, (int) x0);
                    p.addPoint((int) y1, (int) x1);
                    p.addPoint((int) previousHeightx1, (int) x1);
                    p.addPoint((int) previousHeightx0, (int) x0);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    p = new Polygon();
                    p.addPoint((int) x0, (int) y0);
                    p.addPoint((int) x1, (int) y1);
                    p.addPoint((int) x1, (int) previousHeightx1);
                    p.addPoint((int) x0, (int) previousHeightx0);
                }
                g2.setPaint(getItemPaint(row, column));
                g2.setStroke(getItemStroke(row, column));
                g2.fill(p);
            }

        }

        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities 
                = state.getInfo().getOwner().getEntityCollection();
            Shape shape = new Rectangle2D.Double(x1 - 3.0, y1 - 3.0, 6.0, 6.0);
            if (entities != null && shape != null) {
                String tip = null;
                CategoryToolTipGenerator tipster 
                    = getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(dataset,
                            row, column);
                }
                CategoryItemEntity entity = new CategoryItemEntity(
                    shape, tip, url, dataset, row, 
                    dataset.getColumnKey(column), column
                );
                entities.add(entity);
            }
        }

    }

    /**
     * Calculates the stacked value of the all series up to, but not including 
     * <code>series</code> for the specified category, <code>category</code>.  
     * It returns 0.0 if <code>series</code> is the first series, i.e. 0.
     *
     * @param data  the data.
     * @param series  the series.
     * @param category  the category.
     *
     * @return double returns a cumulative value for all series' values up to 
     *         but excluding <code>series</code> for Object 
     *         <code>category</code>.
     */
    protected double getPreviousHeight(CategoryDataset data, 
                                       int series, int category) {

        double result = 0.0;
        Number tmp;
        for (int i = 0; i < series; i++) {
            tmp = data.getValue(i, category);
            if (tmp != null) {
                result += tmp.doubleValue();
            }
        }
        return result;

    }

}
