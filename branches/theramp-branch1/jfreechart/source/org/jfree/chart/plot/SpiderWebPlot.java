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
 * ------------------
 * SpiderWebPlot.java
 * ------------------
 * (C) Copyright 2005, by Heaps of Flavour Pty Ltd.
 *
 * Company Info:  http://www.i4-talent.com
 *
 * Original Author:  Don Elliott;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes (from 28-Jan-2005)
 * --------------------------
 * 28-Jan-2005 : First cut - missing a few features - still to do:
 *                           - needs tooltips/URL/label generator functions
 *                           - ticks on axes / background grid?
 * 31-Jan-2005 : Renamed SpiderWebPlot, added label generator support, and 
 *               reformatted for consistency with other source files in 
 *               JFreeChart (DG);
 * 20-Apr-2005 : Renamed CategoryLabelGenerator 
 *               --> CategoryItemLabelGenerator (DG);
 * 05-May-2005 : Updated draw() method parameters (DG);
 * 
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.PaintList;
import org.jfree.util.Rotation;
import org.jfree.util.StrokeList;
import org.jfree.util.TableOrder;

/**
 * A plot that displays data from a {@link CategoryDataset} in the form of a 
 * "spider web".  Multiple series can be plotted on the same axis to allow 
 * easy comparison.
 */
public class SpiderWebPlot extends Plot implements Cloneable, Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = -5376340422031599463L;
    
    /** The default head radius percent (currently 1%). */
    public static final double DEFAULT_HEAD = 0.01;

    /** The default axis label gap (currently 10%). */
    public static final double DEFAULT_AXIS_LABEL_GAP = 0.10;
 
    /** The default interior gap. */
    public static final double DEFAULT_INTERIOR_GAP = 0.25;

    /** The maximum interior gap (currently 40%). */
    public static final double MAX_INTERIOR_GAP = 0.40;

    /** The default starting angle for the radar chart axes. */
    public static final double DEFAULT_START_ANGLE = 90.0;

    /** The default series label font. */
    public static final Font   DEFAULT_LABEL_FONT = new Font(
        "SansSerif", Font.PLAIN, 10
    );
    
    /** The default series label paint. */
    public static final Paint  DEFAULT_LABEL_PAINT = Color.black;

    /** The default series label background paint. */
    public static final Paint  DEFAULT_LABEL_BACKGROUND_PAINT 
        = new Color(255, 255, 192);

    /** The default series label outline paint. */
    public static final Paint  DEFAULT_LABEL_OUTLINE_PAINT = Color.black;

    /** The default series label outline stroke. */
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE 
        = new BasicStroke(0.5f);

    /** The default series label shadow paint. */
    public static final Paint  DEFAULT_LABEL_SHADOW_PAINT = Color.lightGray;

    /** 
     * The default maximum value plotted - forces the plot to evaluate
     *  the maximum from the data passed in
     */
    public static final double DEFAULT_MAX_VALUE = -1.0;

    /** The head radius as a percentage of the available drawing area. */
    protected double headPercent;

    /** The space left around the outside of the plot as a percentage. */
    private double interiorGap;

    /** The gap between the labels and the axes as a %age of the radius. */
    private double axisLabelGap;

    /** The dataset. */
    private CategoryDataset dataset;

    /** The maximum value we are plotting against on each category axis */
    private double maxValue;
  
    /** 
     * The data extract order (BY_ROW or BY_COLUMN). This denotes whether
     * the data series are stored in rows (in which case the category names are
     * derived from the column keys) or in columns (in which case the category
     * names are derived from the row keys).
     */
    private TableOrder dataExtractOrder;

    /** The starting angle. */
    private double startAngle;

    /** The direction for drawing the radar axis & plots. */
    private Rotation direction;

    /** The legend item shape. */
    private transient Shape legendItemShape;

    /** The paint for ALL series (overrides list). */
    private transient Paint seriesPaint;

    /** The series paint list. */
    private PaintList seriesPaintList;

    /** The base series paint (fallback). */
    private transient Paint baseSeriesPaint;

    /** The outline paint for ALL series (overrides list). */
    private transient Paint seriesOutlinePaint;

    /** The series outline paint list. */
    private PaintList seriesOutlinePaintList;

    /** The base series outline paint (fallback). */
    private transient Paint baseSeriesOutlinePaint;

    /** The outline stroke for ALL series (overrides list). */
    private transient Stroke seriesOutlineStroke;

    /** The series outline stroke list. */
    private StrokeList seriesOutlineStrokeList;

    /** The base series outline stroke (fallback). */
    private transient Stroke baseSeriesOutlineStroke;

    /** The font used to display the category labels. */
    private Font labelFont;

    /** The color used to draw the category labels. */
    private transient Paint labelPaint;
    
    /** The label generator. */
    private CategoryItemLabelGenerator labelGenerator;

    /** controls if the web polygons are filled or not */
    private boolean webFilled = true;
  
    /**
     * Creates a new radar plot with default attributes.
     * 
     * @param dataset  the dataset.
     */
    public SpiderWebPlot(CategoryDataset dataset) {
        super();
        this.dataset = dataset;
    
        if (dataset != null) {
            dataset.addChangeListener(this);
        }

        this.dataExtractOrder = TableOrder.BY_ROW;
        this.headPercent = DEFAULT_HEAD;
        this.axisLabelGap = DEFAULT_AXIS_LABEL_GAP;

        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.maxValue = DEFAULT_MAX_VALUE;

        this.seriesPaint = null;
        this.seriesPaintList = new PaintList();
        this.baseSeriesPaint = null;

        this.seriesOutlinePaint = null;
        this.seriesOutlinePaintList = new PaintList();
        this.baseSeriesOutlinePaint = DEFAULT_OUTLINE_PAINT;

        this.seriesOutlineStroke = null;
        this.seriesOutlineStrokeList = new StrokeList();
        this.baseSeriesOutlineStroke = DEFAULT_OUTLINE_STROKE;

        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelGenerator = new StandardCategoryItemLabelGenerator();
        
        this.legendItemShape = DEFAULT_LEGEND_ITEM_CIRCLE;
    }

    /**
     * Creates a new radar plot overriding the data extraction order.
     * 
     * @param data  the data.
     * @param type  controls how radar data is extracted (BY_ROW or BY_COLUMN).
     */
    public SpiderWebPlot(CategoryDataset data, TableOrder type) {
        this(data);
        this.dataExtractOrder = type;
    }

    /**
     * Returns a short string describing the type of plot.
     * 
     * @return The plot type.
     */
    public String getPlotType() {
        // return localizationResources.getString("Radar_Plot");
        return ("Radar Plot");
    }
    
    /**
     * Method to determine if the web chart is to be filled.
     * 
     * @return A boolean.
     */
    public boolean isWebFilled() {
        return this.webFilled;
    }

    /**
     * Sets the webFilled flag and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param flag  the flag.
     */
    public void setWebFilled(boolean flag) {
        this.webFilled = flag;
        notifyListeners(new PlotChangeEvent(this));
    }
  
    /**
     * Returns the data extract order (by row or by column).
     * 
     * @return The data extract order (never <code>null</code>).
     */
    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }

    /**
     * Sets the data extract order (by row or by column) and sends a
     * {@link PlotChangeEvent}to all registered listeners.
     * 
     * @param order the order (<code>null</code> not permitted).
     */
    public void setDataExtractOrder(TableOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument");
        }
        this.dataExtractOrder = order;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the start angle for the first radar axis.
     * <BR>
     * This is measured in degrees starting from 3 o'clock (Java Arc2D default)
     * and measuring anti-clockwise.
     * 
     * @return The start angle.
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * <P>
     * The initial default value is 90 degrees, which corresponds to 12 o'clock.
     * A value of zero corresponds to 3 o'clock... this is the encoding used by
     * Java's Arc2D class.
     * 
     * @param angle  the angle (in degrees).
     */
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the maximum value any category axis can take.
     * 
     * @return The maximum value.
     */
    public double getMaxValue() {
        return this.maxValue;
    }

    /**
     * Sets the maximum value any category axis can take and sends 
     * a {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param value  the maximum value.
     */
    public void setMaxValue(double value) {
        this.maxValue = value;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the direction in which the radar axes are drawn
     * (clockwise or anti-clockwise).
     * 
     * @return The direction (never <code>null</code>).
     */
    public Rotation getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction in which the radar axes are drawn and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param direction  the direction (<code>null</code> not permitted).
     */
    public void setDirection(Rotation direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Null 'direction' argument.");
        }
        this.direction = direction;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the interior gap, measured as a percentage of the available 
     * drawing space.
     * 
     * @return The gap (as a percentage of the available drawing space).
     */
    public double getInteriorGap() {
        return this.interiorGap;
    }

    /**
     * Sets the interior gap and sends a {@link PlotChangeEvent} to all 
     * registered listeners. This controls the space between the edges of the 
     * plot and the plot area itself (the region where the axis labels appear).
     * 
     * @param percent  the gap (as a percentage of the available drawing space).
     */
    public void setInteriorGap(double percent) {
        if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException(
                "Percentage outside valid range."
            );
        }
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    //// SERIES PAINT /////////////////////////

    /**
     * Returns the paint for ALL series in the plot.
     * 
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getSeriesPaint() {
        return this.seriesPaint;
    }

    /**
     * Sets the paint for ALL series in the plot. If this is set to</code> null
     * </code>, then a list of paints is used instead (to allow different colors
     * to be used for each series of the radar group).
     * 
     * @param paint the paint (<code>null</code> permitted).
     */
    public void setSeriesPaint(Paint paint) {
        this.seriesPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the specified series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getSeriesPaint(int series) {

        // return the override, if there is one...
        if (this.seriesPaint != null) {
            return this.seriesPaint;
        }

        // otherwise look up the paint list
        Paint result = this.seriesPaintList.getPaint(series);
        if (result == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                Paint p = supplier.getNextPaint();
                this.seriesPaintList.setPaint(series, p);
                result = p;
            }
            else {
                result = this.baseSeriesPaint;
            }
        }
        return result;

    }

    /**
     * Sets the paint used to fill a series of the radar and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesPaint(int series, Paint paint) {
        this.seriesPaintList.setPaint(series, paint);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the base series paint. This is used when no other paint is
     * available.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getBaseSeriesPaint() {
      return this.baseSeriesPaint;
    }

    /**
     * Sets the base series paint.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseSeriesPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSeriesPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    //// SERIES OUTLINE PAINT ////////////////////////////

    /**
     * Returns the outline paint for ALL series in the plot.
     * 
     * @return The paint (possibly <code>null</code>).
     */
    public Paint getSeriesOutlinePaint() {
        return this.seriesOutlinePaint;
    }

    /**
     * Sets the outline paint for ALL series in the plot. If this is set to
     * </code> null</code>, then a list of paints is used instead (to allow
     * different colors to be used for each series).
     * 
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesOutlinePaint(Paint paint) {
        this.seriesOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the specified series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getSeriesOutlinePaint(int series) {
        // return the override, if there is one...
        if (this.seriesOutlinePaint != null) {
            return this.seriesOutlinePaint;
        }
        // otherwise look up the paint list
        Paint result = this.seriesOutlinePaintList.getPaint(series);
        if (result == null) {
            result = this.baseSeriesOutlinePaint;
        }
        return result;
    }

    /**
     * Sets the paint used to fill a series of the radar and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.seriesOutlinePaintList.setPaint(series, paint);
        notifyListeners(new PlotChangeEvent(this));  
    }

    /**
     * Returns the base series paint. This is used when no other paint is
     * available.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getBaseSeriesOutlinePaint() {
        return this.baseSeriesOutlinePaint;
    }

    /**
     * Sets the base series paint.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBaseSeriesOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSeriesOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    //// SERIES OUTLINE STROKE /////////////////////

    /**
     * Returns the outline stroke for ALL series in the plot.
     * 
     * @return The stroke (possibly <code>null</code>).
     */
    public Stroke getSeriesOutlineStroke() {
        return this.seriesOutlineStroke;
    }

    /**
     * Sets the outline stroke for ALL series in the plot. If this is set to
     * </code> null</code>, then a list of paints is used instead (to allow
     * different colors to be used for each series).
     * 
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSeriesOutlineStroke(Stroke stroke) {
        this.seriesOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the stroke for the specified series.
     * 
     * @param series  the series index (zero-based).
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getSeriesOutlineStroke(int series) {

        // return the override, if there is one...
        if (this.seriesOutlineStroke != null) {
            return this.seriesOutlineStroke;
        }

        // otherwise look up the paint list
        Stroke result = this.seriesOutlineStrokeList.getStroke(series);
        if (result == null) {
            result = this.baseSeriesOutlineStroke;
        }
        return result;

    }

    /**
     * Sets the stroke used to fill a series of the radar and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param series  the series index (zero-based).
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.seriesOutlineStrokeList.setStroke(series, stroke);
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the base series stroke. This is used when no other stroke is
     * available.
     * 
     * @return The stroke (never <code>null</code>).
     */
    public Stroke getBaseSeriesOutlineStroke() {
        return this.baseSeriesOutlineStroke;
    }

    /**
     * Sets the base series stroke.
     * 
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setBaseSeriesOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseSeriesOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the shape used for legend items.
     * 
     * @return The shape.
     */
    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    /**
     * Sets the shape used for legend items.
     * 
     * @param shape  the shape (<code>null</code> not permitted).
     */
    public void setLegendItemShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendItemShape = shape;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the series label font.
     * 
     * @return The font (never <code>null</code>).
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the series label font and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.labelFont = font;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the series label paint.
     * 
     * @return The paint (never <code>null</code>).
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the series label paint and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setLabelPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the label generator.
     * 
     * @return The label generator (never <code>null</code>).
     */
    public CategoryItemLabelGenerator getLabelGenerator() {
        return this.labelGenerator;   
    }
    
    /**
     * Sets the label generator and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * 
     * @param generator  the generator (<code>null</code> not permitted).
     */
    public void setLabelGenerator(CategoryItemLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");   
        }
        this.labelGenerator = generator;    
    }
    
    /**
     * Returns a collection of legend items for the radar chart.
     * 
     * @return The legend items.
     */
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();

        List keys = null;

        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            keys = this.dataset.getRowKeys();
        }
        else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            keys = this.dataset.getColumnKeys();
        }

        if (keys != null) {
            int series = 0;
            Iterator iterator = keys.iterator();
            Shape shape = getLegendItemShape();

            while (iterator.hasNext()) {
                String label = iterator.next().toString();
                String description = label;

                Paint paint = getSeriesPaint(series);
                Paint outlinePaint = getSeriesOutlinePaint(series);
                Stroke stroke = getSeriesOutlineStroke(series);
                LegendItem item = new LegendItem(
                    label, description, null, null, 
                    shape, paint, stroke, outlinePaint
                );
                result.add(item);
                series++;
            }
        }

        return result;
    }

    /**
     * Returns a cartesian point from a polar angle, length and bounding box
     * 
     * @param bounds  the area inside which the point needs to be.
     * @param angle  the polar angle, in degrees.
     * @param length  the relative length. Given in percent of maximum extend.
     * 
     * @return The cartesian point.
     */
    protected Point2D getWebPoint(Rectangle2D bounds, 
                                  double angle, double length) {
        
        double angrad = Math.toRadians(angle);
        double x = Math.cos(angrad) * length * bounds.getWidth() / 2;
        double y = -Math.sin(angrad) * length * bounds.getHeight() / 2;

        return new Point2D.Double(
            bounds.getX() + x + bounds.getWidth() / 2, 
            bounds.getY() + y + bounds.getHeight() / 2
        );
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * 
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info)
    {
        // adjust for insets...
        RectangleInsets insets = getInsets();
        insets.trim(area);

        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }

        drawBackground(g2, area);
        drawOutline(g2, area);

        Shape savedClip = g2.getClip();

        g2.clip(area);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(
            AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getForegroundAlpha()
            )
        );

        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            int seriesCount = 0, catCount = 0;

            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                seriesCount = this.dataset.getRowCount();
                catCount = this.dataset.getColumnCount();
            }
            else {
                seriesCount = this.dataset.getColumnCount();
                catCount = this.dataset.getRowCount();
            }

            // ensure we have a maximum value to use on the axes
            if (this.maxValue == DEFAULT_MAX_VALUE)
                calculateMaxValue(seriesCount, catCount);

            // Next, setup the plot area 
      
            // adjust the plot area by the interior spacing value

            double gapHorizontal = area.getWidth() * getInteriorGap();
            double gapVertical = area.getHeight() * getInteriorGap();

            double X = area.getX() + gapHorizontal / 2;
            double Y = area.getY() + gapVertical / 2;
            double W = area.getWidth() - gapHorizontal;
            double H = area.getHeight() - gapVertical;

            double headW = area.getWidth() * this.headPercent;
            double headH = area.getHeight() * this.headPercent;

            // make the chart area a square
            double min = Math.min(W, H) / 2;
            X = (X + X + W) / 2 - min;
            Y = (Y + Y + H) / 2 - min;
            W = 2 * min;
            H = 2 * min;

            Point2D  centre = new Point2D.Double(X + W / 2, Y + H / 2);
            Rectangle2D radarArea = new Rectangle2D.Double(X, Y, W, H);

            // Now actually plot each of the series polygons..

            for (int series = 0; series < seriesCount; series++) {
                drawRadarPoly(
                    g2, radarArea, centre, info, series, catCount, headH, headW
                );
            }
        }
        else { 
            drawNoDataMessage(g2, area);
        }
        g2.clip(savedClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, area);
    }

    /**
     * loop through each of the series to get the maximum value
     * on each category axis
     *
     * @param seriesCount  the number of series
     * @param catCount  the number of categories
     */
    private void calculateMaxValue(int seriesCount, int catCount) {
        double v = 0;
        Number nV = null;

        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            for (int catIndex = 0; catIndex < catCount; catIndex++) {
                nV = getPlotValue(seriesIndex, catIndex);
                if (nV != null) {
                    v = nV.doubleValue();
                    if (v > this.maxValue) { 
                        this.maxValue = v;
                    }   
                }
            }
        }
    }

    /**
     * Draws a radar plot polygon.
     * 
     * @param g2 the graphics device.
     * @param plotArea the area we are plotting in (already adjusted).
     * @param centre the centre point of the radar axes
     * @param info chart rendering info.
     * @param series the series within the dataset we are plotting
     * @param catCount the number of categories per radar plot
     * @param headH the data point height
     * @param headW the data point width
     */
    protected void drawRadarPoly(Graphics2D g2, 
                                 Rectangle2D plotArea,
                                 Point2D centre,
                                 PlotRenderingInfo info,
                                 int series, int catCount,
                                 double headH, double headW) {

        Polygon polygon = new Polygon();

        // plot the data...
        for (int cat = 0; cat < catCount; cat++) {
            Number dataValue = getPlotValue(series, cat);

            if (dataValue != null) {
                double value = dataValue.doubleValue();
  
                if (value > 0) { // draw the polygon series...
              
                    // Finds our starting angle from the centre for this axis

                    double angle = getStartAngle()
                        + (getDirection().getFactor() * cat * 360 / catCount);

                    // The following angle calc will ensure there isn't a top 
                    // vertical axis - this may be useful if you don't want any 
                    // given criteria to 'appear' move important than the 
                    // others..
                    //  + (getDirection().getFactor() 
                    //        * (cat + 0.5) * 360 / catCount);

                    // find the point at the appropriate distance end point 
                    // along the axis/angle identified above and add it to the
                    // polygon

                    Point2D point = getWebPoint(
                        plotArea, angle, value / this.maxValue
                    );
                    polygon.addPoint((int) point.getX(), (int) point.getY());

                    // put an elipse at the point being plotted..
          
                    // TODO add tooltip/URL capability to this elipse
          
                    Paint paint = getSeriesPaint(series);
                    Paint outlinePaint = getSeriesOutlinePaint(series);
                    Stroke outlineStroke = getSeriesOutlineStroke(series);

                    Ellipse2D head = new Ellipse2D.Double(
                        point.getX() - headW / 2, 
                        point.getY() - headH / 2,
                        headW, headH
                    );
                    g2.setPaint(paint);
                    g2.fill(head);
                    g2.setStroke(outlineStroke);
                    g2.setPaint(outlinePaint);
                    g2.draw(head);

                    // then draw the axis and category label, but only on the 
                    // first time through.....

                    if (series == 0) {
                        Point2D endPoint = getWebPoint(plotArea, angle, 1); 
                                                             // 1 = end of axis
                        Line2D  line = new Line2D.Double(centre, endPoint);
                        g2.draw(line);
                        drawLabel(
                            g2, plotArea, value, cat, angle, 360.0 / catCount
                        );
                    }
                }
            }
        }
        // Plot the polygon
    
        Paint paint = getSeriesPaint(series);
        g2.setPaint(paint);
        g2.draw(polygon);

        // Lastly, fill the web polygon if this is required
    
        if (this.webFilled) {
            g2.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f)
            );
            g2.fill(polygon);
            g2.setComposite(
                AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, getForegroundAlpha()
                )
            );
        }
    }

    /**
     * Returns the value to be plotted at the interseries of the 
     * series and the category.  This allows us to plot
     * BY_ROW or BY_COLUMN which basically is just reversing the
     * definition of the categories and data series being plotted
     * 
     * @param series the series to be plotted 
     * @param cat the category within the series to be plotted
     * 
     * @return The value to be plotted
     */
    Number getPlotValue(int series, int cat) {
        Number value = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            value = this.dataset.getValue(series, cat);
        }
        else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            value = this.dataset.getValue(cat, series);
        }
        return value;
    }

    /**
     * Draws the label for one axis.
     * 
     * @param g2  the graphics device.
     * @param plotArea  the plot area
     * @param value  the value of the label.
     * @param cat  the category (zero-based index).
     * @param startAngle  the starting angle.
     * @param extent  the extent of the arc.
     */
    protected void drawLabel(Graphics2D g2, Rectangle2D plotArea, double value, 
                             int cat, double startAngle, double extent) {
        FontRenderContext frc = g2.getFontRenderContext();
 
        String label = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            // if series are in rows, then the categories are the column keys
            label = this.labelGenerator.generateColumnLabel(this.dataset, cat);
        }
        else {
            // if series are in columns, then the categories are the row keys
            label = this.labelGenerator.generateRowLabel(this.dataset, cat);
        }
 
        Rectangle2D labelBounds = getLabelFont().getStringBounds(label, frc);
        LineMetrics lm = getLabelFont().getLineMetrics(label, frc);
        double ascent = lm.getAscent();

        Point2D labelLocation = calculateLabelLocation(
            labelBounds, ascent, plotArea, startAngle
        );

        Composite saveComposite = g2.getComposite();
    
        g2.setComposite(
            AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
        );
        g2.setPaint(getLabelPaint());
        g2.setFont(getLabelFont());
        g2.drawString(
            label, (float) labelLocation.getX(), (float) labelLocation.getY()
        );
        g2.setComposite(saveComposite);
    }

    /**
     * Returns the location for a label
     * 
     * @param labelBounds the label bounds.
     * @param ascent the ascent (height of font).
     * @param plotArea the plot area
     * @param startAngle the start angle for the pie series.
     * 
     * @return The location for a label.
     */
    protected Point2D calculateLabelLocation(Rectangle2D labelBounds, 
                                             double ascent,
                                             Rectangle2D plotArea, 
                                             double startAngle)
    {
        Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);
        Point2D point1 = arc1.getEndPoint();

        double deltaX = -(point1.getX() - plotArea.getCenterX()) 
                        * this.axisLabelGap;
        double deltaY = -(point1.getY() - plotArea.getCenterY()) 
                        * this.axisLabelGap;

        double labelX = point1.getX() - deltaX;
        double labelY = point1.getY() - deltaY;

        if (labelX < plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth();
        }
    
        if (labelX == plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth() / 2;
        }

        if (labelY > plotArea.getCenterY()) {
            labelY += ascent;
        }

        return new Point2D.Double(labelX, labelY);
    }

}
