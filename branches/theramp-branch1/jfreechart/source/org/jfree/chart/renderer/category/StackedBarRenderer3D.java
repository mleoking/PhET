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
 * -------------------------
 * StackedBarRenderer3D.java
 * -------------------------
 * (C) Copyright 2000-2005, by Serge V. Grachov and Contributors.
 *
 * Original Author:  Serge V. Grachov;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id$
 *
 * Changes
 * -------
 * 31-Oct-2001 : Version 1, contributed by Serge V. Grachov (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 15-Feb-2002 : Added isStacked() method (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 19-Jun-2002 : Added check for null info in drawCategoryItem method (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 26-Jun-2002 : Small change to entity (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs 
 *               for HTML image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and 
 *               CategoryToolTipGenerator interface (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Added default constructor (bug 726235) and fixed bug 
 *               726260) (DG);
 * 13-May-2003 : Renamed StackedVerticalBarRenderer3D 
 *               --> StackedBarRenderer3D (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 07-Oct-2003 : Added renderer state (DG);
 * 21-Nov-2003 : Added a new constructor (DG);
 * 27-Nov-2003 : Modified code to respect maxBarWidth setting (DG);
 * 11-Aug-2004 : Fixed bug where isDrawBarOutline() was ignored (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 07-Jan-2005 : Renamed getRangeExtent() --> findRangeBounds (DG);
 * 18-Mar-2005 : Override for getPassCount() method (DG);
 * 20-Apr-2005 : Renamed CategoryLabelGenerator 
 *               --> CategoryItemLabelGenerator (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * Renders stacked bars with 3D-effect, for use with the 
 * {@link org.jfree.chart.plot.CategoryPlot} class.
 */
public class StackedBarRenderer3D extends BarRenderer3D 
                                  implements Cloneable, PublicCloneable, 
                                             Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -5832945916493247123L;
    
    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to 
     * minimise the processing required to generate a default chart.  If you 
     * require tool tips or URLs, then you can easily add the required 
     * generators.
     */
    public StackedBarRenderer3D() {
        super();
    }

    /**
     * Constructs a new renderer with the specified '3D effect'.
     *
     * @param xOffset  the x-offset for the 3D effect.
     * @param yOffset  the y-offset for the 3D effect.
     */
    public StackedBarRenderer3D(double xOffset, double yOffset) {
        super(xOffset, yOffset);
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
     * Calculates the bar width and stores it in the renderer state.
     * 
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param rendererIndex  the renderer index.
     * @param state  the renderer state.
     */
    protected void calculateBarWidth(CategoryPlot plot, 
                                     Rectangle2D dataArea, 
                                     int rendererIndex,
                                     CategoryItemRendererState state) {

        // calculate the bar width
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            PlotOrientation orientation = plot.getOrientation();
            double space = 0.0;
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaxBarWidth();
            int columns = data.getColumnCount();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }

            double used = space * (1 - domainAxis.getLowerMargin() 
                                     - domainAxis.getUpperMargin()
                                     - categoryMargin);
            if (columns > 0) {
                state.setBarWidth(Math.min(used / columns, maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }

    }
    
    /**
     * Draws a stacked bar (with 3D-effect) for a specific item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
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

        // check the value we are plotting...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        
        double value = dataValue.doubleValue();
        
        Rectangle2D adjusted = new Rectangle2D.Double(
            dataArea.getX(), dataArea.getY() + getYOffset(),
            dataArea.getWidth() - getXOffset(), 
            dataArea.getHeight() - getYOffset()
        );

        PlotOrientation orientation = plot.getOrientation();

        double barW0 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), adjusted, plot.getDomainAxisEdge()
        ) - state.getBarWidth() / 2.0;

        double positiveBase = 0.0;
        double negativeBase = 0.0;
        for (int i = 0; i < row; i++) {
            Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }

        double translatedBase;
        double translatedValue;
        RectangleEdge location = plot.getRangeAxisEdge();
        if (value > 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, adjusted, 
                    location);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, 
                    adjusted, location);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, adjusted, 
                    location);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, 
                    adjusted, location);
        }
        double barL0 = Math.min(translatedBase, translatedValue);
        double barLength = Math.max(
            Math.abs(translatedValue - translatedBase), getMinimumBarLength()
        );

        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, 
                    state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), 
                    barLength);
        }
        Paint itemPaint = getItemPaint(row, column);
        g2.setPaint(itemPaint);
        g2.fill(bar);

        if (pass == 0) {
            double x0 = bar.getMinX();
            double x1 = x0 + getXOffset();
            double x2 = bar.getMaxX();
            double x3 = x2 + getXOffset();
        
            double y0 = bar.getMinY() - getYOffset();
            double y1 = bar.getMinY();
            double y2 = bar.getMaxY() - getYOffset();
            double y3 = bar.getMaxY();
        
            GeneralPath bar3dRight = null;
            GeneralPath bar3dTop = null;
            if (value > 0.0 || orientation == PlotOrientation.VERTICAL) {
                bar3dRight = new GeneralPath();
                bar3dRight.moveTo((float) x2, (float) y3);
                bar3dRight.lineTo((float) x2, (float) y1);
                bar3dRight.lineTo((float) x3, (float) y0);
                bar3dRight.lineTo((float) x3, (float) y2);
                bar3dRight.closePath();

                if (itemPaint instanceof Color) {
                    g2.setPaint(((Color) itemPaint).darker());
                }
                g2.fill(bar3dRight);
            }

            if (value > 0.0 || orientation == PlotOrientation.HORIZONTAL) {
                bar3dTop = new GeneralPath();
                bar3dTop.moveTo((float) x0, (float) y1);
                bar3dTop.lineTo((float) x1, (float) y0);
                bar3dTop.lineTo((float) x3, (float) y0);
                bar3dTop.lineTo((float) x2, (float) y1);
                bar3dTop.closePath();
                g2.fill(bar3dTop);
            }

            if (isDrawBarOutline() && state.getBarWidth() > 3) {
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
                if (bar3dRight != null) {
                    g2.draw(bar3dRight);
                }
                if (bar3dTop != null) {
                    g2.draw(bar3dTop);
                }
            }

            // collect entity and tool tip information...
            if (state.getInfo() != null) {
                EntityCollection entities 
                    = state.getInfo().getOwner().getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    CategoryToolTipGenerator tipster 
                        = getToolTipGenerator(row, column);
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
                        bar, tip, url, dataset, row, 
                        dataset.getColumnKey(column), column
                    );
                    entities.add(entity);
                }
            }
        }
        else if (pass == 1) {
            CategoryItemLabelGenerator generator 
                = getItemLabelGenerator(row, column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(
                    g2, dataset, row, column, plot, generator, bar, 
                    (value < 0.0)
                );
            }
        }

    }
    
    /**
     * Returns the number of passes through the dataset required by the 
     * renderer.  This method returns <code>2</code>, the second pass is used
     * to draw the item labels.
     * 
     * @return The pass count.
     */
    public int getPassCount() {
        return 2;
    }

}
