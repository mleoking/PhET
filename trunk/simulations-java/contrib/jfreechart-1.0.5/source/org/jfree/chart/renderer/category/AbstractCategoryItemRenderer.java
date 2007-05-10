/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * ---------------------------------
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002-2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: AbstractCategoryItemRenderer.java,v 1.17.2.17 2007/03/15 16:41:34 mungady Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (DG);
 * 06-Jun-2002 : Added accessor methods for the tool tip generator (DG);
 * 11-Jun-2002 : Made constructors protected (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 05-Aug-2002 : Added urlGenerator member variable plus accessors (RA);
 * 22-Aug-2002 : Added categoriesPaint attribute, based on code submitted by
 *               Janet Banks.  This can be used when there is only one series,
 *               and you want each category item to have a different color (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 29-Oct-2002 : Fixed bug where background image for plot was not being
 *               drawn (DG);
 * 05-Nov-2002 : Replaced references to CategoryDataset with TableDataset (DG);
 * 26-Nov 2002 : Replaced the isStacked() method with getRangeType() (DG);
 * 09-Jan-2003 : Renamed grid-line methods (DG);
 * 17-Jan-2003 : Moved plot classes into separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 12-May-2003 : Modified to take into account the plot orientation (DG);
 * 12-Aug-2003 : Very minor javadoc corrections (DB)
 * 13-Aug-2003 : Implemented Cloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 05-Nov-2003 : Fixed marker rendering bug (833623) (DG);
 * 21-Jan-2004 : Update for renamed method in ValueAxis (DG);
 * 11-Feb-2004 : Modified labelling for markers (DG);
 * 12-Feb-2004 : Updated clone() method (DG);
 * 15-Apr-2004 : Created a new CategoryToolTipGenerator interface (DG);
 * 05-May-2004 : Fixed bug (948310) where interval markers extend outside axis
 *               range (DG);
 * 14-Jun-2004 : Fixed bug in drawRangeMarker() method - now uses 'paint' and
 *               'stroke' rather than 'outlinePaint' and 'outlineStroke' (DG);
 * 15-Jun-2004 : Interval markers can now use GradientPaint (DG);
 * 30-Sep-2004 : Moved drawRotatedString() from RefineryUtilities
 *               --> TextUtilities (DG);
 * 01-Oct-2004 : Fixed bug 1029697, problem with label alignment in
 *               drawRangeMarker() method (DG);
 * 07-Jan-2005 : Renamed getRangeExtent() --> findRangeBounds() (DG);
 * 21-Jan-2005 : Modified return type of calculateRangeMarkerTextAnchorPoint()
 *               method (DG);
 * 08-Mar-2005 : Fixed positioning of marker labels (DG);
 * 20-Apr-2005 : Added legend label, tooltip and URL generators (DG);
 * 01-Jun-2005 : Handle one dimension of the marker label adjustment
 *               automatically (DG);
 * 09-Jun-2005 : Added utility method for adding an item entity (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 01-Mar-2006 : Updated getLegendItems() to check seriesVisibleInLegend
 *               flags (DG);
 * 20-Jul-2006 : Set dataset and series indices in LegendItem (DG);
 * 23-Oct-2006 : Draw outlines for interval markers (DG);
 * 24-Oct-2006 : Respect alpha setting in markers, as suggested by Sergei
 *               Ivanov in patch 1567843 (DG);
 * 30-Nov-2006 : Added a check for series visibility in the getLegendItem()
 *               method (DG);
 * 07-Dec-2006 : Fix for equals() method (DG);
 * 22-Feb-2007 : Added createState() method (DG);
 * 01-Mar-2007 : Fixed interval marker drawing (patch 1670686 thanks to 
 *               Sergei Ivanov) (DG);
 *
 */

package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectList;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

/**
 * An abstract base class that you can use to implement a new
 * {@link CategoryItemRenderer}.  When you create a new
 * {@link CategoryItemRenderer} you are not required to extend this class,
 * but it makes the job easier.
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer
    implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 1247553218442497391L;

    /** The plot that the renderer is assigned to. */
    private CategoryPlot plot;

    /** The item label generator for ALL series. */
    private CategoryItemLabelGenerator itemLabelGenerator;

    /** A list of item label generators (one per series). */
    private ObjectList itemLabelGeneratorList;

    /** The base item label generator. */
    private CategoryItemLabelGenerator baseItemLabelGenerator;

    /** The tool tip generator for ALL series. */
    private CategoryToolTipGenerator toolTipGenerator;

    /** A list of tool tip generators (one per series). */
    private ObjectList toolTipGeneratorList;

    /** The base tool tip generator. */
    private CategoryToolTipGenerator baseToolTipGenerator;

    /** The URL generator. */
    private CategoryURLGenerator itemURLGenerator;

    /** A list of item label generators (one per series). */
    private ObjectList itemURLGeneratorList;

    /** The base item label generator. */
    private CategoryURLGenerator baseItemURLGenerator;

    /** The legend item label generator. */
    private CategorySeriesLabelGenerator legendItemLabelGenerator;

    /** The legend item tool tip generator. */
    private CategorySeriesLabelGenerator legendItemToolTipGenerator;

    /** The legend item URL generator. */
    private CategorySeriesLabelGenerator legendItemURLGenerator;

    /** The number of rows in the dataset (temporary record). */
    private transient int rowCount;

    /** The number of columns in the dataset (temporary record). */
    private transient int columnCount;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * The defaults (no tool tip or URL generators) have been chosen to
     * minimise the processing required to generate a default chart.  If you
     * require tool tips or URLs, then you can easily add the required
     * generators.
     */
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorList = new ObjectList();
        this.toolTipGenerator = null;
        this.toolTipGeneratorList = new ObjectList();
        this.itemURLGenerator = null;
        this.itemURLGeneratorList = new ObjectList();
        this.legendItemLabelGenerator
            = new StandardCategorySeriesLabelGenerator();
    }

    /**
     * Returns the number of passes through the dataset required by the
     * renderer.  This method returns <code>1</code>, subclasses should
     * override if they need more passes.
     *
     * @return The pass count.
     */
    public int getPassCount() {
        return 1;
    }

    /**
     * Returns the plot that the renderer has been assigned to (where
     * <code>null</code> indicates that the renderer is not currently assigned
     * to a plot).
     *
     * @return The plot (possibly <code>null</code>).
     *
     * @see #setPlot(CategoryPlot)
     */
    public CategoryPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that the renderer has been assigned to.  This method is
     * usually called by the {@link CategoryPlot}, in normal usage you
     * shouldn't need to call this method directly.
     *
     * @param plot  the plot (<code>null</code> not permitted).
     *
     * @see #getPlot()
     */
    public void setPlot(CategoryPlot plot) {
        if (plot == null) {
            throw new IllegalArgumentException("Null 'plot' argument.");
        }
        this.plot = plot;
    }

    // ITEM LABEL GENERATOR

    /**
     * Returns the item label generator for a data item.  This implementation
     * simply passes control to the {@link #getSeriesItemLabelGenerator(int)}
     * method.  If, for some reason, you want a different generator for
     * individual items, you can override this method.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public CategoryItemLabelGenerator getItemLabelGenerator(int row,
            int column) {
        return getSeriesItemLabelGenerator(row);
    }

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly <code>null</code>).
     *
     * @see #setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)
     */
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {

        // return the generator for ALL series, if there is one...
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }

        // otherwise look up the generator table
        CategoryItemLabelGenerator generator = (CategoryItemLabelGenerator)
            this.itemLabelGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemLabelGenerator;
        }
        return generator;

    }

    // TODO: there should probably be a getItemLabelGenerator() method

    /**
     * Sets the item label generator for ALL series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     */
    public void setItemLabelGenerator(CategoryItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    public void setSeriesItemLabelGenerator(int series,
                                        CategoryItemLabelGenerator generator) {
        this.itemLabelGeneratorList.set(series, generator);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the base item label generator.
     *
     * @return The generator (possibly <code>null</code>).
     *
     * @see #setBaseItemLabelGenerator(CategoryItemLabelGenerator)
     */
    public CategoryItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }

    /**
     * Sets the base item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getBaseItemLabelGenerator()
     */
    public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator)
    {
        this.baseItemLabelGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    // TOOL TIP GENERATOR

    /**
     * Returns the tool tip generator that should be used for the specified
     * item.  This method looks up the generator using the "three-layer"
     * approach outlined in the general description of this interface.  You
     * can override this method if you want to return a different generator per
     * item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The generator (possibly <code>null</code>).
     */
    public CategoryToolTipGenerator getToolTipGenerator(int row, int column) {

        CategoryToolTipGenerator result = null;
        if (this.toolTipGenerator != null) {
            result = this.toolTipGenerator;
        }
        else {
            result = getSeriesToolTipGenerator(row);
            if (result == null) {
                result = this.baseToolTipGenerator;
            }
        }
        return result;
    }

    /**
     * Returns the tool tip generator that will be used for ALL items in the
     * dataset (the "layer 0" generator).
     *
     * @return A tool tip generator (possibly <code>null</code>).
     *
     * @see #setToolTipGenerator(CategoryToolTipGenerator)
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator for ALL series and sends a
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered
     * listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getToolTipGenerator()
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the tool tip generator for the specified series (a "layer 1"
     * generator).
     *
     * @param series  the series index (zero-based).
     *
     * @return The tool tip generator (possibly <code>null</code>).
     *
     * @see #setSeriesToolTipGenerator(int, CategoryToolTipGenerator)
     */
    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series) {
        return (CategoryToolTipGenerator) this.toolTipGeneratorList.get(series);
    }

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    public void setSeriesToolTipGenerator(int series,
                                          CategoryToolTipGenerator generator) {
        this.toolTipGeneratorList.set(series, generator);
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the base tool tip generator (the "layer 2" generator).
     *
     * @return The tool tip generator (possibly <code>null</code>).
     *
     * @see #setBaseToolTipGenerator(CategoryToolTipGenerator)
     */
    public CategoryToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }

    /**
     * Sets the base tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getBaseToolTipGenerator()
     */
    public void setBaseToolTipGenerator(CategoryToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    // URL GENERATOR

    /**
     * Returns the URL generator for a data item.  This method just calls the
     * getSeriesItemURLGenerator method, but you can override this behaviour if
     * you want to.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The URL generator.
     */
    public CategoryURLGenerator getItemURLGenerator(int row, int column) {
        return getSeriesItemURLGenerator(row);
    }

    /**
     * Returns the URL generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The URL generator for the series.
     *
     * @see #setSeriesItemURLGenerator(int, CategoryURLGenerator)
     */
    public CategoryURLGenerator getSeriesItemURLGenerator(int series) {

        // return the generator for ALL series, if there is one...
        if (this.itemURLGenerator != null) {
            return this.itemURLGenerator;
        }

        // otherwise look up the generator table
        CategoryURLGenerator generator
            = (CategoryURLGenerator) this.itemURLGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemURLGenerator;
        }
        return generator;

    }

    // TODO: there should probably be a getItemURLGenerator() method

    /**
     * Sets the item URL generator for ALL series.
     *
     * @param generator  the generator.
     */
    public void setItemURLGenerator(CategoryURLGenerator generator) {
        this.itemURLGenerator = generator;
        // TODO: this should fire an event
    }

    /**
     * Sets the URL generator for a series.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    public void setSeriesItemURLGenerator(int series,
                                          CategoryURLGenerator generator) {
        this.itemURLGeneratorList.set(series, generator);
        // TODO: this should fire an event
    }

    /**
     * Returns the base item URL generator.
     *
     * @return The item URL generator.
     *
     * @see #setBaseItemURLGenerator(CategoryURLGenerator)
     */
    public CategoryURLGenerator getBaseItemURLGenerator() {
        return this.baseItemURLGenerator;
    }

    /**
     * Sets the base item URL generator.
     *
     * @param generator  the item URL generator.
     *
     * @see #getBaseItemURLGenerator()
     */
    public void setBaseItemURLGenerator(CategoryURLGenerator generator) {
        this.baseItemURLGenerator = generator;
        // TODO: this should generate an event
    }

    /**
     * Returns the number of rows in the dataset.  This value is updated in the
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return The row count.
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Returns the number of columns in the dataset.  This value is updated in
     * the {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return The column count.
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Creates a new state instance---this method is called from the
     * {@link #initialise(Graphics2D, Rectangle2D, CategoryPlot, int,
     * PlotRenderingInfo)} method.  Subclasses can override this method if
     * they need to use a subclass of {@link CategoryItemRendererState}.
     *
     * @param info  collects plot rendering info (<code>null</code> permitted).
     *
     * @return The new state instance (never <code>null</code>).
     *
     * @since 1.0.5
     */
    protected CategoryItemRendererState createState(PlotRenderingInfo info) {
        return new CategoryItemRendererState(info);
    }

    /**
     * Initialises the renderer and returns a state object that will be used
     * for the remainder of the drawing process for a single chart.  The state
     * object allows for the fact that the renderer may be used simultaneously
     * by multiple threads (each thread will work with a separate state object).
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  an object for returning information about the structure of
     *              the plot (<code>null</code> permitted).
     *
     * @return The renderer state.
     */
    public CategoryItemRendererState initialise(Graphics2D g2,
                                                Rectangle2D dataArea,
                                                CategoryPlot plot,
                                                int rendererIndex,
                                                PlotRenderingInfo info) {

        setPlot(plot);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        }
        else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        return createState(info);

    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The range (or <code>null</code> if the dataset is
     *         <code>null</code> or empty).
     */
    public Range findRangeBounds(CategoryDataset dataset) {
        return DatasetUtilities.findRangeBounds(dataset);
    }

    /**
     * Draws a background for the data area.  The default implementation just
     * gets the plot to draw the outline, but some renderers will override this
     * behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawBackground(Graphics2D g2,
                               CategoryPlot plot,
                               Rectangle2D dataArea) {

        plot.drawBackground(g2, dataArea);

    }

    /**
     * Draws an outline for the data area.  The default implementation just
     * gets the plot to draw the outline, but some renderers will override this
     * behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    public void drawOutline(Graphics2D g2,
                            CategoryPlot plot,
                            Rectangle2D dataArea) {

        plot.drawOutline(g2, dataArea);

    }

    /**
     * Draws a grid line against the domain axis.
     * <P>
     * Note that this default implementation assumes that the horizontal axis
     * is the domain axis. If this is not the case, you will need to override
     * this method.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any
     *                  3D effect).
     * @param value  the Java2D value at which the grid line should be drawn.
     *
     * @see #drawRangeGridline(Graphics2D, CategoryPlot, ValueAxis,
     *     Rectangle2D, double)
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        Line2D line = null;
        PlotOrientation orientation = plot.getOrientation();

        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), value,
                    dataArea.getMaxX(), value);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(value, dataArea.getMinY(), value,
                    dataArea.getMaxY());
        }

        Paint paint = plot.getDomainGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);

        Stroke stroke = plot.getDomainGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);

        g2.draw(line);

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any
     *                  3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
     * @see #drawDomainGridline(Graphics2D, CategoryPlot, Rectangle2D, double)
     *
     */
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        PlotOrientation orientation = plot.getOrientation();
        double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v,
                    dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v,
                    dataArea.getMaxX(), v);
        }

        Paint paint = plot.getRangeGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);

        Stroke stroke = plot.getRangeGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);

        g2.draw(line);

    }

    /**
     * Draws a marker for the domain axis.
     *
     * @param g2  the graphics device (not <code>null</code>).
     * @param plot  the plot (not <code>null</code>).
     * @param axis  the range axis (not <code>null</code>).
     * @param marker  the marker to be drawn (not <code>null</code>).
     * @param dataArea  the area inside the axes (not <code>null</code>).
     *
     * @see #drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker,
     *     Rectangle2D)
     */
    public void drawDomainMarker(Graphics2D g2,
                                 CategoryPlot plot,
                                 CategoryAxis axis,
                                 CategoryMarker marker,
                                 Rectangle2D dataArea) {

        Comparable category = marker.getKey();
        CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
        int columnIndex = dataset.getColumnIndex(category);
        if (columnIndex < 0) {
            return;
        }

        final Composite savedComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, marker.getAlpha()));

        PlotOrientation orientation = plot.getOrientation();
        Rectangle2D bounds = null;
        if (marker.getDrawAsLine()) {
            double v = axis.getCategoryMiddle(columnIndex,
                    dataset.getColumnCount(), dataArea,
                    plot.getDomainAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v,
                        dataArea.getMaxX(), v);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v,
                        dataArea.getMaxY());
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            bounds = line.getBounds2D();
        }
        else {
            double v0 = axis.getCategoryStart(columnIndex,
                    dataset.getColumnCount(), dataArea,
                    plot.getDomainAxisEdge());
            double v1 = axis.getCategoryEnd(columnIndex,
                    dataset.getColumnCount(), dataArea,
                    plot.getDomainAxisEdge());
            Rectangle2D area = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                area = new Rectangle2D.Double(dataArea.getMinX(), v0,
                        dataArea.getWidth(), (v1 - v0));
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                area = new Rectangle2D.Double(v0, dataArea.getMinY(),
                        (v1 - v0), dataArea.getHeight());
            }
            g2.setPaint(marker.getPaint());
            g2.fill(area);
            bounds = area;
        }

        String label = marker.getLabel();
        RectangleAnchor anchor = marker.getLabelAnchor();
        if (label != null) {
            Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            Point2D coordinates = calculateDomainMarkerTextAnchorPoint(
                    g2, orientation, dataArea, bounds, marker.getLabelOffset(),
                    marker.getLabelOffsetType(), anchor);
            TextUtilities.drawAlignedString(label, g2,
                    (float) coordinates.getX(), (float) coordinates.getY(),
                    marker.getLabelTextAnchor());
        }
        g2.setComposite(savedComposite);
    }

    /**
     * Draws a marker for the range axis.
     *
     * @param g2  the graphics device (not <code>null</code>).
     * @param plot  the plot (not <code>null</code>).
     * @param axis  the range axis (not <code>null</code>).
     * @param marker  the marker to be drawn (not <code>null</code>).
     * @param dataArea  the area inside the axes (not <code>null</code>).
     *
     * @see #drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis,
     *     CategoryMarker, Rectangle2D)
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = axis.getRange();

            if (!range.contains(value)) {
                return;
            }

            final Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, marker.getAlpha()));

            PlotOrientation orientation = plot.getOrientation();
            double v = axis.valueToJava2D(value, dataArea,
                    plot.getRangeAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v,
                        dataArea.getMaxY());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(dataArea.getMinX(), v,
                        dataArea.getMaxX(), v);
            }

            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(
                        g2, orientation, dataArea, line.getBounds2D(),
                        marker.getLabelOffset(), LengthAdjustmentType.EXPAND,
                        anchor);
                TextUtilities.drawAlignedString(label, g2,
                        (float) coordinates.getX(), (float) coordinates.getY(),
                        marker.getLabelTextAnchor());
            }
            g2.setComposite(savedComposite);
        }
        else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = axis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }

            final Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, marker.getAlpha()));

            double start2d = axis.valueToJava2D(start, dataArea,
                    plot.getRangeAxisEdge());
            double end2d = axis.valueToJava2D(end, dataArea,
                    plot.getRangeAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);

            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                // clip left and right bounds to data area
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low,
                        dataArea.getMinY(), high - low,
                        dataArea.getHeight());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                // clip top and bottom bounds to data area
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(),
                        low, dataArea.getWidth(),
                        high - low);
            }
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            }
            else {
                g2.setPaint(p);
            }
            g2.fill(rect);

            // now draw the outlines, if visible...
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                if (orientation == PlotOrientation.VERTICAL) {
                    Line2D line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                }
                else { // PlotOrientation.HORIZONTAL
                    Line2D line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                }
            }

            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(
                        g2, orientation, dataArea, rect,
                        marker.getLabelOffset(), marker.getLabelOffsetType(),
                        anchor);
                TextUtilities.drawAlignedString(label, g2,
                        (float) coordinates.getX(), (float) coordinates.getY(),
                        marker.getLabelTextAnchor());
            }
            g2.setComposite(savedComposite);
        }
    }

    /**
     * Calculates the (x, y) coordinates for drawing the label for a marker on
     * the range axis.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker.
     * @param markerOffset  the marker offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2,
                                      PlotOrientation orientation,
                                      Rectangle2D dataArea,
                                      Rectangle2D markerArea,
                                      RectangleInsets markerOffset,
                                      LengthAdjustmentType labelOffsetType,
                                      RectangleAnchor anchor) {

        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);

    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker.
     * @param markerOffset  the marker offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2,
                                      PlotOrientation orientation,
                                      Rectangle2D dataArea,
                                      Rectangle2D markerArea,
                                      RectangleInsets markerOffset,
                                      LengthAdjustmentType labelOffsetType,
                                      RectangleAnchor anchor) {

        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea,
                    LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);

    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item.
     *
     * @see #getLegendItems()
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {

        CategoryPlot p = getPlot();
        if (p == null) {
            return null;
        }

        // check that a legend item needs to be displayed...
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }

        CategoryDataset dataset;
        dataset = p.getDataset(datasetIndex);
        String label = this.legendItemLabelGenerator.generateLabel(dataset,
                series);
        String description = label;
        String toolTipText = null;
        if (this.legendItemToolTipGenerator != null) {
            toolTipText = this.legendItemToolTipGenerator.generateLabel(
                    dataset, series);
        }
        String urlText = null;
        if (this.legendItemURLGenerator != null) {
            urlText = this.legendItemURLGenerator.generateLabel(dataset,
                    series);
        }
        Shape shape = getSeriesShape(series);
        Paint paint = getSeriesPaint(series);
        Paint outlinePaint = getSeriesOutlinePaint(series);
        Stroke outlineStroke = getSeriesOutlineStroke(series);

        LegendItem item = new LegendItem(label, description, toolTipText,
                urlText, shape, paint, outlineStroke, outlinePaint);
        item.setSeriesIndex(series);
        item.setDatasetIndex(datasetIndex);
        return item;
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemRenderer)) {
            return false;
        }
        AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer) obj;

        if (!ObjectUtilities.equal(this.itemLabelGenerator,
                that.itemLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemLabelGeneratorList,
                that.itemLabelGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemLabelGenerator,
                that.baseItemLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGenerator,
                that.toolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.toolTipGeneratorList,
                that.toolTipGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseToolTipGenerator,
                that.baseToolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemURLGenerator,
                that.itemURLGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemURLGeneratorList,
                that.itemURLGeneratorList)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.baseItemURLGenerator,
                that.baseItemURLGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemLabelGenerator,
                that.legendItemLabelGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemToolTipGenerator,
                that.legendItemToolTipGenerator)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.legendItemURLGenerator,
                that.legendItemURLGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for the renderer.
     *
     * @return The hash code.
     */
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier (possibly <code>null</code>).
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        CategoryPlot cp = getPlot();
        if (cp != null) {
            result = cp.getDrawingSupplier();
        }
        return result;
    }

    /**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param row  the row.
     * @param column  the column.
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param negative  indicates a negative value (which affects the item
     *                  label position).
     */
    protected void drawItemLabel(Graphics2D g2,
                                 PlotOrientation orientation,
                                 CategoryDataset dataset,
                                 int row, int column,
                                 double x, double y,
                                 boolean negative) {

        CategoryItemLabelGenerator generator
            = getItemLabelGenerator(row, column);
        if (generator != null) {
            Font labelFont = getItemLabelFont(row, column);
            Paint paint = getItemLabelPaint(row, column);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, row, column);
            ItemLabelPosition position = null;
            if (!negative) {
                position = getPositiveItemLabelPosition(row, column);
            }
            else {
                position = getNegativeItemLabelPosition(row, column);
            }
            Point2D anchorPoint = calculateLabelAnchorPoint(
                    position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2,
                    (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                    position.getTextAnchor(),
                    position.getAngle(), position.getRotationAnchor());
        }

    }

    /**
     * Returns an independent copy of the renderer.  The <code>plot</code>
     * reference is shallow copied.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  can be thrown if one of the objects
     *         belonging to the renderer does not support cloning (for example,
     *         an item label generator).
     */
    public Object clone() throws CloneNotSupportedException {

        AbstractCategoryItemRenderer clone
            = (AbstractCategoryItemRenderer) super.clone();

        if (this.itemLabelGenerator != null) {
            if (this.itemLabelGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.itemLabelGenerator;
                clone.itemLabelGenerator
                        = (CategoryItemLabelGenerator) pc.clone();
            }
            else {
                throw new CloneNotSupportedException(
                        "ItemLabelGenerator not cloneable.");
            }
        }

        if (this.itemLabelGeneratorList != null) {
            clone.itemLabelGeneratorList
                    = (ObjectList) this.itemLabelGeneratorList.clone();
        }

        if (this.baseItemLabelGenerator != null) {
            if (this.baseItemLabelGenerator instanceof PublicCloneable) {
                PublicCloneable pc
                        = (PublicCloneable) this.baseItemLabelGenerator;
                clone.baseItemLabelGenerator
                        = (CategoryItemLabelGenerator) pc.clone();
            }
            else {
                throw new CloneNotSupportedException(
                        "ItemLabelGenerator not cloneable.");
            }
        }

        if (this.toolTipGenerator != null) {
            if (this.toolTipGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.toolTipGenerator;
                clone.toolTipGenerator = (CategoryToolTipGenerator) pc.clone();
            }
            else {
                throw new CloneNotSupportedException(
                        "Tool tip generator not cloneable.");
            }
        }

        if (this.toolTipGeneratorList != null) {
            clone.toolTipGeneratorList
                    = (ObjectList) this.toolTipGeneratorList.clone();
        }

        if (this.baseToolTipGenerator != null) {
            if (this.baseToolTipGenerator instanceof PublicCloneable) {
                PublicCloneable pc
                        = (PublicCloneable) this.baseToolTipGenerator;
                clone.baseToolTipGenerator
                        = (CategoryToolTipGenerator) pc.clone();
            }
            else {
                throw new CloneNotSupportedException(
                        "Base tool tip generator not cloneable.");
            }
        }

        if (this.itemURLGenerator != null) {
            if (this.itemURLGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.itemURLGenerator;
                clone.itemURLGenerator = (CategoryURLGenerator) pc.clone();
            }
            else {
                throw new CloneNotSupportedException(
                        "Item URL generator not cloneable.");
            }
        }

        if (this.itemURLGeneratorList != null) {
            clone.itemURLGeneratorList
                    = (ObjectList) this.itemURLGeneratorList.clone();
        }

        if (this.baseItemURLGenerator != null) {
            if (this.baseItemURLGenerator instanceof PublicCloneable) {
                PublicCloneable pc
                        = (PublicCloneable) this.baseItemURLGenerator;
                clone.baseItemURLGenerator = (CategoryURLGenerator) pc.clone();
            }
            else {
                throw new CloneNotSupportedException(
                        "Base item URL generator not cloneable.");
            }
        }

        if (this.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (CategorySeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemLabelGenerator);
        }
        if (this.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (CategorySeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemToolTipGenerator);
        }
        if (this.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (CategorySeriesLabelGenerator)
                    ObjectUtilities.clone(this.legendItemURLGenerator);
        }
        return clone;
    }

    /**
     * Returns a domain axis for a plot.
     *
     * @param plot  the plot.
     * @param index  the axis index.
     *
     * @return A domain axis.
     */
    protected CategoryAxis getDomainAxis(CategoryPlot plot, int index) {
        CategoryAxis result = plot.getDomainAxis(index);
        if (result == null) {
            result = plot.getDomainAxis();
        }
        return result;
    }

    /**
     * Returns a range axis for a plot.
     *
     * @param plot  the plot.
     * @param index  the axis index.
     *
     * @return A range axis.
     */
    protected ValueAxis getRangeAxis(CategoryPlot plot, int index) {
        ValueAxis result = plot.getRangeAxis(index);
        if (result == null) {
            result = plot.getRangeAxis();
        }
        return result;
    }

    /**
     * Returns a (possibly empty) collection of legend items for the series
     * that this renderer is responsible for drawing.
     *
     * @return The legend item collection (never <code>null</code>).
     *
     * @see #getLegendItem(int, int)
     */
    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        LegendItemCollection result = new LegendItemCollection();
        int index = this.plot.getIndexOf(this);
        CategoryDataset dataset = this.plot.getDataset(index);
        if (dataset != null) {
            int seriesCount = dataset.getRowCount();
            for (int i = 0; i < seriesCount; i++) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }

        }
        return result;
    }

    /**
     * Returns the legend item label generator.
     *
     * @return The label generator (never <code>null</code>).
     *
     * @see #setLegendItemLabelGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    /**
     * Sets the legend item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> not permitted).
     *
     * @see #getLegendItemLabelGenerator()
     */
    public void setLegendItemLabelGenerator(
            CategorySeriesLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendItemLabelGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the legend item tool tip generator.
     *
     * @return The tool tip generator (possibly <code>null</code>).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    /**
     * Sets the legend item tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     */
    public void setLegendItemToolTipGenerator(
            CategorySeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the legend item URL generator.
     *
     * @return The URL generator (possibly <code>null</code>).
     *
     * @see #setLegendItemURLGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    /**
     * Sets the legend item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator (<code>null</code> permitted).
     *
     * @see #getLegendItemURLGenerator()
     */
    public void setLegendItemURLGenerator(
            CategorySeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Adds an entity with the specified hotspot.
     *
     * @param entities  the entity collection.
     * @param dataset  the dataset.
     * @param row  the row index.
     * @param column  the column index.
     * @param hotspot  the hotspot.
     */
    protected void addItemEntity(EntityCollection entities,
                                 CategoryDataset dataset, int row, int column,
                                 Shape hotspot) {

        String tip = null;
        CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
        if (tipster != null) {
            tip = tipster.generateToolTip(dataset, row, column);
        }
        String url = null;
        CategoryURLGenerator urlster = getItemURLGenerator(row, column);
        if (urlster != null) {
            url = urlster.generateURL(dataset, row, column);
        }
        CategoryItemEntity entity = new CategoryItemEntity(hotspot, tip, url,
                dataset, row, dataset.getColumnKey(column), column);
        entities.add(entity);

    }

}
