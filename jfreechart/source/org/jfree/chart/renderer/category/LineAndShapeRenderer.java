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
 * LineAndShapeRenderer.java
 * -------------------------
 * (C) Copyright 2001-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jeremy Bowman;
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id$
 *
 * Changes
 * -------
 * 23-Oct-2001 : Version 1 (DG);
 * 15-Nov-2001 : Modified to allow for null data values (DG);
 * 16-Jan-2002 : Renamed HorizontalCategoryItemRenderer.java 
 *               --> CategoryItemRenderer.java (DG);
 * 05-Feb-2002 : Changed return type of the drawCategoryItem method from void 
 *               to Shape, as part of the tooltips implementation (DG);
 * 11-May-2002 : Support for value label drawing (JB);
 * 29-May-2002 : Now extends AbstractCategoryItemRenderer (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 05-Aug-2002 : Small modification to drawCategoryItem method to support URLs 
 *               for HTML image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 11-Oct-2002 : Added new constructor to incorporate tool tip and URL 
 *               generators (DG);
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and 
 *               CategoryToolTipGenerator interface (DG);
 * 05-Nov-2002 : Base dataset is now TableDataset not CategoryDataset (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem() --> drawItem() and now using axis 
 *               for category spacing (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 10-Apr-2003 : Changed CategoryDataset to KeyedValues2DDataset in drawItem()
 *               method (DG);
 * 12-May-2003 : Modified to take into account the plot orientation (DG);
 * 29-Jul-2003 : Amended code that doesn't compile with JDK 1.2.2 (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 22-Sep-2003 : Fixed cloning (DG);
 * 10-Feb-2004 : Small change to drawItem() method to make cut-and-paste 
 *               override easier (DG);
 * 16-Jun-2004 : Fixed bug (id=972454) with label positioning on horizontal 
 *               charts (DG);
 * 15-Oct-2004 : Updated equals() method (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities class to translate shapes (DG);
 * 27-Jan-2005 : Changed attribute names, modified constructor and removed 
 *               constants (DG);
 * 01-Feb-2005 : Removed unnecessary constants (DG);
 * 15-Mar-2005 : Fixed bug 1163897, concerning outlines for shapes (DG);
 * 13-Apr-2005 : Check flags that control series visibility (DG);
 * 20-Apr-2005 : Use generators for legend labels, tooltips and URLs (DG);
 *
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.BooleanList;
import org.jfree.util.BooleanUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer that draws shapes for each data item, and lines between data 
 * items (for use with the {@link CategoryPlot} class).
 */
public class LineAndShapeRenderer extends AbstractCategoryItemRenderer 
                                  implements Cloneable, PublicCloneable, 
                                             Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -197749519869226398L;
    
    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean linesVisible;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    private boolean shapesVisible;

    /** A flag that controls whether or not shapes are filled for ALL series. */
    private Boolean shapesFilled;
    
    /** 
     * A table of flags that control (per series) whether or not shapes are 
     * filled. 
     */
    private BooleanList seriesShapesFilled;
    
    /** The default value returned by the getShapeFilled() method. */
    private boolean defaultShapesFilled;
    
    /** 
     * A flag that controls whether the fill paint is used for filling 
     * shapes. 
     */
    private boolean useFillPaint;

    /** A flag that controls whether outlines are drawn for shapes. */
    private boolean drawOutlines;
        
    /** 
     * A flag that controls whether the outline paint is used for drawing shape 
     * outlines - if not, the regular series paint is used. 
     */
    private boolean useOutlinePaint;

    /**
     * Creates a renderer with both lines and shapes visible by default.
     */
    public LineAndShapeRenderer() {
        this(true, true);
    }

    /**
     * Creates a new renderer with lines and/or shapes visible.
     * 
     * @param linesVisible  draw lines?
     * @param shapesVisible  draw shapes?
     */
    public LineAndShapeRenderer(boolean linesVisible, boolean shapesVisible) {
        super();
        this.linesVisible = linesVisible;
        this.shapesVisible = shapesVisible;
        this.shapesFilled = null;
        this.seriesShapesFilled = new BooleanList();
        this.defaultShapesFilled = true;
        this.useFillPaint = false;
        this.drawOutlines = true;
        this.useOutlinePaint = false;
    }
    
    /**
     * Returns <code>true</code> if a line should be drawn from the previous 
     * to the current data point, and <code>false</code> otherwise.
     *
     * @return A boolean flag.
     */
    public boolean isLinesVisible() {
        return this.linesVisible;
    }

    /**
     * Sets the flag that controls whether or not lines are drawn between 
     * consecutive data points.
     *
     * @param visible  the new value of the flag.
     */
    public void setLinesVisible(boolean visible) {
        if (visible != this.linesVisible) {
            this.linesVisible = visible;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns <code>true</code> if a shape should be drawn to represent each 
     * data point, and <code>false</code> otherwise.
     *
     * @return A boolean flag.
     */
    public boolean isShapesVisible() {
        return this.shapesVisible;
    }

    /**
     * Sets the flag that controls whether or not a shape should be drawn to 
     * represent each data point.
     *
     * @param visible  the new value of the flag.
     */
    public void setShapesVisible(boolean visible) {
        if (visible != this.shapesVisible) {
            this.shapesVisible = visible;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns <code>true</code> if outlines should be drawn for shapes, and 
     * <code>false</code> otherwise.
     * 
     * @return A boolean.
     */
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }
    
    /**
     * Sets the flag that controls whether outlines are drawn for 
     * shapes, and sends a {@link RendererChangeEvent} to all registered 
     * listeners. 
     * <P>
     * In some cases, shapes look better if they do NOT have an outline, but 
     * this flag allows you to set your own preference.
     * 
     * @param flag  the flag.
     */
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the flag that controls whether the outline paint is used for 
     * shape outlines.  If not, the regular series paint is used.
     * 
     * @return A boolean.
     */
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;   
    }
    
    /**
     * Sets the flag that controls whether the outline paint is used for shape 
     * outlines.
     * 
     * @param use  the flag.
     */
    public void setUseOutlinePaint(boolean use) {
        this.useOutlinePaint = use;   
    }

    // SHAPES FILLED
    
    /**
     * Returns the flag used to control whether or not the shape for an item 
     * is filled. The default implementation passes control to the 
     * <code>getSeriesShapesFilled</code> method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        return getSeriesShapesFilled(series);
    }

    /**
     * Returns the flag used to control whether or not the shapes for a series 
     * are filled. 
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getSeriesShapesFilled(int series) {

        // return the overall setting, if there is one...
        if (this.shapesFilled != null) {
            return this.shapesFilled.booleanValue();
        }

        // otherwise look up the paint table
        Boolean flag = this.seriesShapesFilled.getBoolean(series);
        if (flag != null) {
            return flag.booleanValue();
        }
        else {
            return this.defaultShapesFilled;
        } 

    }
    
    /**
     * Returns the flag that controls whether or not shapes are filled for 
     * ALL series.
     * 
     * @return A Boolean.
     */
    public Boolean getShapesFilled() {
        return this.shapesFilled;
    }

    /**
     * Sets the 'shapes filled' for ALL series.
     * 
     * @param filled  the flag.
     */
    public void setShapesFilled(boolean filled) {
        if (filled) {
            setShapesFilled(Boolean.TRUE);
        }
        else {
            setShapesFilled(Boolean.FALSE);
        }
    }
    
    /**
     * Sets the 'shapes filled' for ALL series.
     * 
     * @param filled  the flag (<code>null</code> permitted).
     */
    public void setShapesFilled(Boolean filled) {
        this.shapesFilled = filled;
    }
    
    /**
     * Sets the 'shapes filled' flag for a series.
     *
     * @param series  the series index (zero-based).
     * @param filled  the flag.
     */
    public void setSeriesShapesFilled(int series, Boolean filled) {
        this.seriesShapesFilled.setBoolean(series, filled);
    }

    /**
     * Sets the 'shapes filled' flag for a series.
     *
     * @param series  the series index (zero-based).
     * @param filled  the flag.
     */
    public void setSeriesShapesFilled(int series, boolean filled) {
        this.seriesShapesFilled.setBoolean(
            series, BooleanUtilities.valueOf(filled)
        );
    }

    /**
     * Returns the default 'shape filled' attribute.
     *
     * @return The default flag.
     */
    public boolean getDefaultShapesFilled() {
        return this.defaultShapesFilled;
    }

    /**
     * Sets the default 'shapes filled' flag.
     *
     * @param flag  the flag.
     */
    public void setDefaultShapesFilled(boolean flag) {
        this.defaultShapesFilled = flag;
    }

    /**
     * Returns <code>true</code> if the renderer should use the fill paint 
     * setting to fill shapes, and <code>false</code> if it should just
     * use the regular paint.
     * 
     * @return A boolean.
     */
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }
    
    /**
     * Sets the flag that controls whether the fill paint is used to fill 
     * shapes, and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     * 
     * @param flag  the flag.
     */
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }

        if (isSeriesVisible(series) && isSeriesVisibleInLegend(series)) {
            CategoryDataset dataset;
            dataset = cp.getDataset(datasetIndex);
            String label = getLegendItemLabelGenerator().generateLabel(
                dataset, series
            );
            String description = label;
            String toolTipText = null; 
            if (getLegendItemToolTipGenerator() != null) {
                toolTipText = getLegendItemToolTipGenerator().generateLabel(
                    dataset, series
                );   
            }
            String urlText = null;
            if (getLegendItemURLGenerator() != null) {
                urlText = getLegendItemURLGenerator().generateLabel(
                    dataset, series
                );   
            }
            Shape shape = getSeriesShape(series);
            Paint paint = getSeriesPaint(series);
            Paint fillPaint = (this.useFillPaint 
                ? getItemFillPaint(series, 0) : paint);
            boolean shapeOutlineVisible = this.drawOutlines;
            Paint outlinePaint = (this.useOutlinePaint 
                ? getItemOutlinePaint(series, 0) : paint);
            Stroke outlineStroke = getSeriesOutlineStroke(series);

            return new LegendItem(
                label, description, toolTipText, urlText, 
                isShapesVisible(), shape, 
                getItemShapeFilled(series, 0),
                fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke,
                isLinesVisible(), new Line2D.Double(-7.0, 0.0, 7.0, 0.0),
                getItemStroke(series, 0), getItemPaint(series, 0)
            );
        }
        return null;

    }

    /**
     * This renderer uses two passes to draw the data.
     * 
     * @return The pass count (<code>2</code> for this renderer).
     */
    public int getPassCount() {
        return 2;   
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

        // do nothing if item is not visible
        if (!getItemVisible(row, column)) {
            return;   
        }

        // nothing is drawn for null...
        Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();

        // current data point...
        double x1 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double value = v.doubleValue();
        double y1 = rangeAxis.valueToJava2D(
            value, dataArea, plot.getRangeAxisEdge()
        );

        if (pass == 0 && isLinesVisible()) {
            if (column != 0) {
                Number previousValue = dataset.getValue(row, column - 1);
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

        if (pass == 1) {
            Shape shape = getItemShape(row, column);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
            }

            if (isShapesVisible()) {
                if (getItemShapeFilled(row, column)) {
                    if (this.useFillPaint) {
                        g2.setPaint(getItemFillPaint(row, column));
                    }
                    else {
                        g2.setPaint(getItemPaint(row, column));   
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (this.useOutlinePaint) {
                        g2.setPaint(getItemOutlinePaint(row, column));   
                    }
                    else {
                        g2.setPaint(getItemPaint(row, column));
                    }
                    g2.setStroke(getItemOutlineStroke(row, column));
                    g2.draw(shape);
                }
            }

            // draw the item label if there is one...
            if (isItemLabelVisible(row, column)) {
                if (orientation == PlotOrientation.HORIZONTAL) {
                    drawItemLabel(
                        g2, orientation, dataset, row, column, y1, x1, 
                        (value < 0.0)
                    );
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    drawItemLabel(
                        g2, orientation, dataset, row, column, x1, y1, 
                        (value < 0.0)
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
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof LineAndShapeRenderer)) {
            return false;
        }
        
        LineAndShapeRenderer that = (LineAndShapeRenderer) obj;
        if (this.linesVisible != that.linesVisible) {
            return false;
        }
        if (this.shapesVisible != that.shapesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shapesFilled, that.shapesFilled)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.seriesShapesFilled, that.seriesShapesFilled)
        ) {
            return false;
        }
        if (this.defaultShapesFilled != that.defaultShapesFilled) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
    
        return true;

    }

    /**
     * Returns an independent copy of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  should not happen.
     */
    public Object clone() throws CloneNotSupportedException {
        LineAndShapeRenderer clone = (LineAndShapeRenderer) super.clone();
        clone.seriesShapesFilled 
            = (BooleanList) this.seriesShapesFilled.clone();
        return clone;
    }
    
}
