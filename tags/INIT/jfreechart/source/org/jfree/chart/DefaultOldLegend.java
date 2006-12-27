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
 * ---------------------
 * DefaultOldLegend.java
 * ---------------------
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Luke Quinane;
 *                   Barak Naveh;
 *
 * $Id$
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend 
 *               placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 19-Oct-2001 : Moved some methods [getSeriesPaint() etc.] from JFreeChart to 
 *               Plot (DG);
 * 22-Jan-2002 : Fixed bug correlating legend labels with pie data (DG);
 * 06-Feb-2002 : Bug fix for legends in small areas (DG);
 * 23-Apr-2002 : Legend item labels are now obtained from the plot, not the 
 *               chart (DG);
 * 20-Jun-2002 : Added outline paint and stroke attributes for the key 
 *               boxes (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Sep-2002 : Changed the name of LegendItem --> DrawableLegendItem (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Adjusted vertical text position in legend item (DG);
 * 17-Oct-2002 : Fixed bug where legend items are not using the font that has 
 *               been set (DG);
 * 11-Feb-2003 : Added title code by Donald Mitchell, removed unnecessary 
 *               constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 22-Sep-2003 : Added nullpointer checks (TM);
 * 23-Sep-2003 : Fixed bug in equals() method (DG);
 * 08-Oct-2003 : Applied patch for displaying series line style, contributed 
 *               by Luke Quinane (DG);
 * 23-Dec-2003 : Added scale factors (x and y) for shapes displayed in 
 *               legend (DG);
 * 26-Mar-2004 : Added option to control item order, contributed by Angel (DG);
 * 26-Mar-2004 : Added support for 8 more anchor points (BN);
 * 27-Mar-2004 : Added support for round corners of bounding box (BN);
 * 07-Apr-2004 : Changed text bounds calculation (DG);
 * 21-Apr-2004 : Barak Naveh has contributed word-wrapping for legend 
 *               items (BN);
 * 25-Nov-2004 : Small update due to changes in LegendItem class (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for 1.0.0 release (DG);
 * 27-Jan-2005 : Legend now picks up more info from the renderer of each 
 *               series (DG);
 * 22-Feb-2005 : Renamed outerGap --> margin (DG);
 * 28-Mar-2005 : Renamed StandardLegend --> DefaultOldLegend (DG);
 * 20-Apr-2005 : Updated for changes in LegendItem (DG);
 * 26-Apr-2005 : Removed LOGGER (DG);
 *
 */

package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.event.LegendChangeEvent;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;

/**
 * A chart legend shows the names and visual representations of the series
 * that are plotted in a chart.  If possible, use 
 * {@link org.jfree.chart.title.LegendTitle} rather than this class.
 */
public class DefaultOldLegend extends OldLegend implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -5466149184220837922L;
    
    /** The default margin (space around the outside of the legend). */
    public static final RectangleInsets DEFAULT_MARGIN 
        = new RectangleInsets(3, 3, 3, 3);

    /** The default inner gap. */
    public static final RectangleInsets DEFAULT_PADDING 
        = new RectangleInsets(2, 2, 2, 2);

    /** The default outline stroke. */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke();

    /** The default outline paint. */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default background paint. */
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;

    /** The default title font. */
    public static final Font DEFAULT_TITLE_FONT 
        = new Font("SansSerif", Font.BOLD, 11);

    /** The default item font. */
    public static final Font DEFAULT_ITEM_FONT 
        = new Font("SansSerif", Font.PLAIN, 10);

    /** 
     * Used with {@link #setPreferredWidth(double)} to indicate that no 
     * preferred width is desired and defaults are to be used.
     */
    public static final double NO_PREFERRED_WIDTH = Double.MAX_VALUE;
    
    /** Reported when illegal legend is unexpectedly found. */
    private static final String UNEXPECTED_LEGEND_ANCHOR 
        = "Unexpected legend anchor";
    
    /** The amount of blank space around the legend. */
    private RectangleInsets margin;

    /** The stroke used to draw the outline of the legend. */
    private transient Stroke outlineStroke;

    /** The paint used to draw the outline of the legend. */
    private transient Paint outlinePaint;

    /** The paint used to draw the background of the legend. */
    private transient Paint backgroundPaint;

    /** The blank space inside the legend box. */
    private RectangleInsets padding;

    /** An optional title for the legend. */
    private String title;

    /** The font used to display the legend title. */
    private Font titleFont;

    /** The font used to display the legend item names. */
    private Font itemFont;

    /** The paint used to display the legend item names. */
    private transient Paint itemPaint;
  
    /** The x scale factor for shapes displayed in the legend. */
    private double shapeScaleX = 1.0;
    
    /** The y scale factor for shapes displayed in the legend. */
    private double shapeScaleY = 1.0;

    /** The order of the legend items. */
    private LegendRenderingOrder renderingOrder = LegendRenderingOrder.STANDARD;
    
    /** 
     * The width of the arc used to round off the corners of the bounding box. 
     */
    private int boundingBoxArcWidth = 0;

    /** 
     * The height of the arc used to round off the corners of the bounding box. 
     */
    private int boundingBoxArcHeight = 0;

    /** The preferred width of the legend bounding box. */
    private double preferredWidth = NO_PREFERRED_WIDTH;
    
    /**
     * Constructs a new legend with default settings.
     */
    public DefaultOldLegend() {
        this.margin = DEFAULT_MARGIN;
        this.padding = DEFAULT_PADDING;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.outlineStroke = DEFAULT_OUTLINE_STROKE;
        this.outlinePaint = DEFAULT_OUTLINE_PAINT;
        this.title = null;
        this.titleFont = DEFAULT_TITLE_FONT;
        this.itemFont = DEFAULT_ITEM_FONT;
        this.itemPaint = Color.black;
    }

    /**
     * Returns the margin for the legend.  This is the amount of blank space 
     * around the outside of the legend.
     *
     * @return The gap (never <code>null</code>).
     */
    public RectangleInsets getMargin() {
        return this.margin;
    }

    /**
     * Sets the margin for the legend and sends a {@link LegendChangeEvent} to 
     * all registered listeners.
     *
     * @param margin  the margin (<code>null</code> not permitted).
     */
    public void setMargin(RectangleInsets margin) {
        if (margin == null) {
            throw new NullPointerException("Null 'margin' argument.");
        }
        this.margin = margin;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the padding for the legend.  This is the amount of blank space 
     * around the inside of the legend.
     *
     * @return The gap (never <code>null</code>).
     */
    public RectangleInsets getPadding() {
        return this.padding;
    }

    /**
     * Sets the padding for the legend and sends a {@link LegendChangeEvent} 
     * to all registered listeners.
     *
     * @param padding  the padding (<code>null</code> not permitted).
     */
    public void setPadding(RectangleInsets padding) {
        if (padding == null) {
            throw new NullPointerException("Null 'padding' argument.");
        }
        this.padding = padding;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the background paint for the legend.
     *
     * @return The background paint (never <code>null</code>).
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background paint for the legend and sends a 
     * {@link LegendChangeEvent} to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setBackgroundPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.backgroundPaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (never <code>null</code>).
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke and sends a {@link LegendChangeEvent} to all 
     * registered listeners.
     *
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new NullPointerException("Null 'stroke' argument.");
        }
        this.outlineStroke = stroke;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (never <code>null</code>).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint and sends a {@link LegendChangeEvent} to all 
     * registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.outlinePaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Gets the title for the legend.
     *
     * @return The title (possibly <code>null</code>).
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title of the legend and sends a {@link LegendChangeEvent} to 
     * all registered listeners.
     *
     * @param title  the title (<code>null</code> permitted).
     */
    public void setTitle(String title) {
        this.title = title;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the title font.
     *
     * @return The font (never <code>null</code>).
     */
    public Font getTitleFont() {
        return this.titleFont;
    }

    /**
     * Sets the title font and sends a {@link LegendChangeEvent} to all 
     * registered listeners.
     *
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setTitleFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");   
        }
        this.titleFont = font;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the series label font.
     *
     * @return The font (never <code>null</code>).
     */
    public Font getItemFont() {
        return this.itemFont;
    }

    /**
     * Sets the series label font and sends a {@link LegendChangeEvent} to all 
     * registered listeners.
     *
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setItemFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");  
        }
        this.itemFont = font;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the series label paint.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemPaint() {
        return this.itemPaint;
    }

    /**
     * Sets the series label paint and sends a {@link LegendChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setItemPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");   
        }
        this.itemPaint = paint;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the x scale factor for shapes displayed in the legend.
     * 
     * @return The x scale factor.
     */
    public double getShapeScaleX() {
        return this.shapeScaleX;
    }
    
    /**
     * Sets the x scale factor for shapes displayed in the legend and sends a
     * {@link LegendChangeEvent} to all registered listeners.
     * 
     * @param factor  the factor.
     */
    public void setShapeScaleX(double factor) {
        this.shapeScaleX = factor;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the y scale factor for shapes displayed in the legend.
     * 
     * @return The y scale factor.
     */
    public double getShapeScaleY() {
        return this.shapeScaleY;
    }
    
    /**
     * Sets the y scale factor for shapes displayed in the legend and sends a
     * {@link LegendChangeEvent} to all registered listeners.
     * 
     * @param factor  the factor.
     */
    public void setShapeScaleY(double factor) {
        this.shapeScaleY = factor;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the legend rendering order. 
     * 
     * @return The order (never <code>null</code>).
     */
    public LegendRenderingOrder getRenderingOrder() {
        return this.renderingOrder;
    }

    /**
     * Sets the legend rendering order and sends a {@link LegendChangeEvent} to
     * all registered listeners.
     * 
     * @param order  the order (<code>null</code> not permitted).
     */
    public void setRenderingOrder(LegendRenderingOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument.");   
        }
        this.renderingOrder = order;
        notifyListeners(new LegendChangeEvent(this));
    }

    
    /**
     * Returns the width of the arc used to round off the corners of the
     * bounding box. 
     * 
     * @return The width of the arc used to round off the corners of the
     *         bounding box.
     */
    public int getBoundingBoxArcWidth() {
        return this.boundingBoxArcWidth;
    }
    
    /**
     * Sets the width of the arc used to round off the corners of the
     * bounding box. 
     * A {@link LegendChangeEvent} is sent to all registered listeners.
     * 
     * @param arcWidth the new arc width.
     */
    public void setBoundingBoxArcWidth(int arcWidth) {
        this.boundingBoxArcWidth = arcWidth;
        notifyListeners(new LegendChangeEvent(this));
    }
    
    /**
     * Returns the height of the arc used to round off the corners of the
     * bounding box. 
     * 
     * @return The height of the arc used to round off the corners of the
     *         bounding box.
     */
    public int getBoundingBoxArcHeight() {
        return this.boundingBoxArcHeight;
    }
    
    /**
     * Sets the height of the arc used to round off the corners of the
     * bounding box. 
     * A {@link LegendChangeEvent} is sent to all registered listeners.
     * 
     * @param arcHeight the new arc height.
     */
    public void setBoundingBoxArcHeight(int arcHeight) {
        this.boundingBoxArcHeight = arcHeight;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Returns the preferred width of the legend bounding box if such width
     * has been defined; otherwise returns <code>NO_PREFERRED_WIDTH</code>.
     * 
     * @return The preferred width of the legend bounding box if such width
     *         has been defined; otherwise returns 
     *         <code>NO_PREFERRED_WIDTH</code>.
     */
    public double getPreferredWidth() {
        return this.preferredWidth;
    }

    /**
     * Sets the preferred width of the legend bounding box. If a preferred
     * width is set, the legend text is word-wrapped in an attempt to fulfill 
     * the preferred width. If the preferred width cannot be fulfilled, the 
     * legend would be wider to the extent necessary.
     * 
     * <p>
     * The preferred width takes effect only when the legend's anchor is set to 
     * one of the three EAST anchors or to one of the three WEST anchors.
     *   
     * <p>  
     * A {@link LegendChangeEvent} is sent to all registered listeners.
     * 
     * @param width the new width.
     */
    public void setPreferredWidth(double width) {
        this.preferredWidth = width;
        notifyListeners(new LegendChangeEvent(this));
    }
    
    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a 
     * printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend, and afterwards the 
     *                   plot, should be drawn.
     * @param info  collects rendering info (optional).
     *
     * @return The area NOT used by the legend.
     */
    public Rectangle2D draw(Graphics2D g2, Rectangle2D available, 
                            ChartRenderingInfo info) {

        return draw(
            g2, available, (getAnchor() & HORIZONTAL) != 0, 
            (getAnchor() & INVERTED) != 0, info
        );

    }

    /**
     * Draws the legend.
     *
     * @param g2  the graphics device.
     * @param available  the area available for drawing the chart.
     * @param horizontal  a flag indicating whether the legend items are laid 
     *                    out horizontally.
     * @param inverted ???
     * @param info  collects rendering info (optional).
     *
     * @return The remaining available drawing area.
     */
    protected Rectangle2D draw(Graphics2D g2, Rectangle2D available,
                               boolean horizontal, boolean inverted,
                               ChartRenderingInfo info) {

        LegendItemCollection legendItems 
            = getChart().getPlot().getLegendItems();

        if (legendItems == null || legendItems.getItemCount() == 0) {
            return available; 
        }
        // else...

        DrawableLegendItem legendTitle = null;
        LegendItem titleItem = null; 

        if (this.title != null && !this.title.equals("")) {
            titleItem = new LegendItem(
                this.title, this.title, null, null, (Shape) null, Color.black
            );
        }

        RectangularShape legendArea;
        double availableWidth = available.getWidth();
        // the translation point for the origin of the drawing system
        Point2D translation;

        // Create buffer for individual items within the legend
        List items = new ArrayList();

        // Compute individual rectangles in the legend, translation point as 
        // well as the bounding box for the legend.
        if (horizontal) {
            double xstart = available.getX() 
                            + getMargin().calculateLeftOutset(availableWidth);
            double xlimit = available.getMaxX() 
                            - getMargin().calculateRightOutset(availableWidth);
            double maxRowWidth = 0;
            double xoffset = 0;
            double rowHeight = 0;
            double totalHeight = 0;
            boolean wrappingAllowed = true;

            if (titleItem != null) {
                g2.setFont(getTitleFont());

                legendTitle = createDrawableLegendItem(
                    g2, titleItem, xoffset, totalHeight
                );

                rowHeight = Math.max(0, legendTitle.getHeight());
                xoffset += legendTitle.getWidth();
            }

            g2.setFont(this.itemFont);
            for (int i = 0; i < legendItems.getItemCount(); i++) {
                DrawableLegendItem item;
                
                if (this.renderingOrder == LegendRenderingOrder.STANDARD) {
                    item = createDrawableLegendItem(
                        g2, legendItems.get(i), xoffset, totalHeight
                    );
                }
                else if (this.renderingOrder == LegendRenderingOrder.REVERSE) {
                    item = createDrawableLegendItem(
                        g2, legendItems.get(legendItems.getItemCount() - i - 1),
                        xoffset, totalHeight
                    );                        
                }
                else {
                    // we're not supposed to get here, will cause 
                    // NullPointerException
                    item = null;
                }

                if (item.getMaxX() + xstart > xlimit && wrappingAllowed) {
                    // start a new row
                    maxRowWidth = Math.max(maxRowWidth, xoffset);
                    xoffset = 0;
                    totalHeight += rowHeight;
                    i--; // redo this item in the next row
                    // if item to big to fit, we dont want to attempt wrapping 
                    // endlessly. 
                    // we therefore disable wrapping for at least one item.
                    wrappingAllowed = false;  
                }
                else {
                    // continue current row
                    rowHeight = Math.max(rowHeight, item.getHeight());
                    xoffset += item.getWidth();
                    // we placed an item in this row, re-allow wrapping for 
                    // next item.
                    wrappingAllowed = true; 
                    items.add(item);
                }
            }

            maxRowWidth = Math.max(maxRowWidth, xoffset);
            totalHeight += rowHeight;

            // Create the bounding box
            legendArea = new RoundRectangle2D.Double(
                0, 0, maxRowWidth, totalHeight, this.boundingBoxArcWidth, 
                this.boundingBoxArcHeight
            );

            translation = createTranslationPointForHorizontalDraw(
                available, inverted, maxRowWidth, totalHeight 
            );
        }
        else {  // vertical...
            double totalHeight = 0;
            double maxWidth = (this.preferredWidth == NO_PREFERRED_WIDTH) 
                ? 0 : this.preferredWidth;
            
            if (titleItem != null) {
                g2.setFont(getTitleFont());

                legendTitle = createDrawableLegendItem(
                    g2, titleItem, 0, totalHeight
                );

                totalHeight += legendTitle.getHeight();
                maxWidth = Math.max(maxWidth, legendTitle.getWidth());
            }

            g2.setFont(this.itemFont);
            
            int legendItemsLength = legendItems.getItemCount();
            for (int i = 0; i < legendItemsLength; i++) {
                List drawableParts;
                
                if (this.renderingOrder == LegendRenderingOrder.STANDARD) {
                    drawableParts = createAllDrawableLinesForItem(g2, 
                            legendItems.get(i), 0, totalHeight, maxWidth);
                }
                else if (this.renderingOrder == LegendRenderingOrder.REVERSE) {
                    drawableParts = createAllDrawableLinesForItem(
                        g2, legendItems.get(legendItemsLength - i - 1), 0, 
                        totalHeight, maxWidth
                    );
                }
                else {
                    // we're not supposed to get here, will cause 
                    // NullPointerException
                    drawableParts = null;
                }
                
                for (Iterator j = drawableParts.iterator(); j.hasNext();) {
                    DrawableLegendItem item = (DrawableLegendItem) j.next();
                    
                    totalHeight += item.getHeight();
                    maxWidth = Math.max(maxWidth, item.getWidth());
                    
                    items.add(item);
                }
            }

            // Create the bounding box
            legendArea = new RoundRectangle2D.Float(
                0, 0, (float) maxWidth, (float) totalHeight, 
                this.boundingBoxArcWidth, this.boundingBoxArcHeight
            );

            translation = createTranslationPointForVerticalDraw(
                available, inverted, totalHeight, maxWidth 
            );
        }

        // Move the origin of the drawing to the appropriate location
        g2.translate(translation.getX(), translation.getY());
        drawLegendBox(g2, legendArea);
        drawLegendTitle(g2, legendTitle);
        drawSeriesElements(g2, items, translation, info);

        // translate the origin back to what it was prior to drawing the legend
        g2.translate(-translation.getX(), -translation.getY());

        return calcRemainingDrawingArea(
            available, horizontal, inverted, legendArea
        );
    }

    /**
     * ???
     * 
     * @param available  the available area.
     * @param inverted  inverted?
     * @param maxRowWidth  the maximum row width.
     * @param totalHeight  the total height.
     * 
     * @return The translation point.
     */
    private Point2D createTranslationPointForHorizontalDraw(
            Rectangle2D available, boolean inverted, double maxRowWidth, 
            double totalHeight) {
        // The yloc point is the variable part of the translation point
        // for horizontal legends xloc can be: left, center or right.
        double yloc = (inverted)
            ? available.getMaxY() - totalHeight
              - getMargin().calculateBottomOutset(available.getHeight())
            : available.getY() 
              + getMargin().calculateTopOutset(available.getHeight());
        double xloc;
        if (isAnchoredToLeft()) {
            xloc = available.getX() 
                   + getMargin().calculateLeftOutset(available.getWidth());
        }
        else if (isAnchoredToCenter()) {
            xloc = available.getX() + available.getWidth() / 2 
                   - maxRowWidth / 2;
        }
        else if (isAnchoredToRight()) {
            xloc = available.getX() + available.getWidth()
                   - maxRowWidth - getChart().getPlot().getInsets().getLeft();
        }
        else {
            throw new IllegalStateException(UNEXPECTED_LEGEND_ANCHOR);
        }
        
        // Create the translation point
        return new Point2D.Double(xloc, yloc);
    }

    /**
     * ???
     * 
     * @param available  the available area.
     * @param inverted  inverted?
     * @param totalHeight  the total height.
     * @param maxWidth  the maximum width.
     * 
     * @return The translation point.
     */
    private Point2D createTranslationPointForVerticalDraw(Rectangle2D available,
            boolean inverted, double totalHeight, double maxWidth) {
        // The xloc point is the variable part of the translation point
        // for vertical legends yloc can be: top, middle or bottom.
        double xloc = (inverted)
            ? available.getMaxX() - maxWidth 
              - getMargin().calculateRightOutset(available.getWidth())
            : available.getX() 
              + getMargin().calculateLeftOutset(available.getWidth());
        double yloc;
        if (isAnchoredToTop()) {
            yloc = available.getY() + getChart().getPlot().getInsets().getTop();
        }
        else if (isAnchoredToMiddle()) {
            yloc = available.getY() + (available.getHeight() / 2) 
                   - (totalHeight / 2);
        }
        else if (isAnchoredToBottom()) {
            yloc = available.getY() + available.getHeight() 
                   - getChart().getPlot().getInsets().getBottom() - totalHeight;
        }
        else {
            throw new IllegalStateException(UNEXPECTED_LEGEND_ANCHOR);
        }
        // Create the translation point
        return new Point2D.Double(xloc, yloc);
    }

    /**
     * Draws the legend title.
     * 
     * @param g2  the graphics device (<code>null</code> not permitted).
     * @param legendTitle  the title (<code>null</code> permitted, in which 
     *                     case the method does nothing).
     */
    private void drawLegendTitle(Graphics2D g2, 
                                 DrawableLegendItem legendTitle) {
        if (legendTitle != null) {
            // XXX dsm - make title bold?
            g2.setPaint(legendTitle.getItem().getFillPaint());
            g2.setPaint(this.itemPaint);
            g2.setFont(getTitleFont());
            TextUtilities.drawAlignedString(
                legendTitle.getItem().getLabel(), g2,
                (float) legendTitle.getLabelPosition().getX(),
                (float) legendTitle.getLabelPosition().getY(), 
                TextAnchor.CENTER_LEFT
            );
        }
    }

    /**
     * Draws the bounding box for the legend.
     * 
     * @param g2  the graphics device.
     * @param legendArea  the legend area.
     */
    private void drawLegendBox(Graphics2D g2, RectangularShape legendArea) {
        // Draw the legend's bounding box
        g2.setPaint(this.backgroundPaint);
        g2.fill(legendArea);
        g2.setPaint(this.outlinePaint);
        g2.setStroke(this.outlineStroke);
        g2.draw(legendArea);
    }

    /**
     * Draws the series elements.
     * 
     * @param g2  the graphics device.
     * @param items  the items.
     * @param translation  the translation point.
     * @param info  optional carrier for rendering info.
     */
    private void drawSeriesElements(Graphics2D g2, List items, 
                                    Point2D translation, 
                                    ChartRenderingInfo info) {
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }
        // Draw individual series elements
        for (int i = 0; i < items.size(); i++) {
            DrawableLegendItem item = (DrawableLegendItem) items.get(i);
            g2.setPaint(item.getItem().getFillPaint());
            Shape keyBox = item.getMarker();
            if (item.getItem().isLineVisible()) {
                g2.setStroke(item.getItem().getLineStroke());
                g2.draw(item.getLine());

                if (item.getItem().isShapeVisible()) {
                    if (item.getItem().isShapeFilled()) {
                        g2.fill(keyBox);
                    }
                    else {
                        g2.draw(keyBox);
                    }
                }
            } 
            else {
                if (item.getItem().isShapeFilled()) {
                    g2.fill(keyBox);
                }
                if (item.getItem().isShapeOutlineVisible()) {
                    g2.setPaint(item.getItem().getOutlinePaint());
                    g2.setStroke(item.getItem().getOutlineStroke());
                    g2.draw(keyBox);
                }
            }
            g2.setPaint(this.itemPaint);
            g2.setFont(this.itemFont);
            TextUtilities.drawAlignedString(
                item.getItem().getLabel(), g2,
                (float) item.getLabelPosition().getX(), 
                (float) item.getLabelPosition().getY(),
                TextAnchor.CENTER_LEFT
            );

            if (entities != null) {
                Rectangle2D area = new Rectangle2D.Double(
                    translation.getX() + item.getX(),
                    translation.getY() + item.getY(),
                    item.getWidth(), item.getHeight()
                );
                LegendItemEntity entity = new LegendItemEntity(area);
                entity.setSeriesIndex(i);
                entities.add(entity);
            }
        }
    }

    /**
     * Calculates the remaining drawing area.
     * 
     * @param available  the available area.
     * @param horizontal  horizontal?
     * @param inverted  inverted?
     * @param legendArea  the legend area.
     * 
     * @return The remaining drawing area.
     */
    private Rectangle2D calcRemainingDrawingArea(Rectangle2D available, 
            boolean horizontal, boolean inverted, RectangularShape legendArea) {
        if (horizontal) {
            // The remaining drawing area bounding box will have the same
            // x origin, width and height independent of the anchor's
            // location. The variable is the y coordinate. If the anchor is
            // SOUTH, the y coordinate is simply the original y coordinate
            // of the available area. If it is NORTH, we adjust original y
            // by the total height of the legend and the initial gap.
            double yy = available.getY();
            double yloc = (inverted) ? yy
                : yy + legendArea.getHeight()
                + getMargin().calculateBottomOutset(available.getHeight());

            // return the remaining available drawing area
            return new Rectangle2D.Double(
                available.getX(), yloc, available.getWidth(),
                available.getHeight() - legendArea.getHeight()
                - getMargin().calculateTopOutset(available.getHeight())
                - getMargin().calculateBottomOutset(available.getHeight())
            );
        }
        else {
            // The remaining drawing area bounding box will have the same
            // y  origin, width and height independent of the anchor's
            // location. The variable is the x coordinate. If the anchor is
            // EAST, the x coordinate is simply the original x coordinate
            // of the available area. If it is WEST, we adjust original x
            // by the total width of the legend and the initial gap.
            double xloc = (inverted) ? available.getX()
                : available.getX()
                + legendArea.getWidth()
                + getMargin().calculateLeftOutset(available.getWidth())
                + getMargin().calculateRightOutset(available.getWidth());


            // return the remaining available drawing area
            return new Rectangle2D.Double(
                xloc, available.getY(),
                available.getWidth() - legendArea.getWidth()
                - getMargin().calculateLeftOutset(available.getWidth())
                - getMargin().calculateRightOutset(available.getWidth()),
                available.getHeight()
            );
        }
    }

    /**
     * Returns a list of drawable legend items for the specified legend item.
     * Word-wrapping is applied to the specified legend item and it is broken
     * into a few lines in order to fit into the specified 
     * <code>wordWrapWidth</code>.
     *
     * @param g2 the graphics context.
     * @param legendItem  the legend item.
     * @param x  the upper left x coordinate for the bounding box.
     * @param y  the upper left y coordinate for the bounding box.
     * @param wordWrapWidth  the word wrap width.
     *
     * @return A list of drawable legend items for the specified legend item.
     * 
     * @see #setPreferredWidth(double)
     */
    private List createAllDrawableLinesForItem(Graphics2D g2,
            LegendItem legendItem, double x, double y, double wordWrapWidth) {
        
        List drawableParts = new ArrayList();

        DrawableLegendItem line 
            = createDrawableLegendItem(g2, legendItem, x, y);

        if (line.getWidth() < wordWrapWidth) {
            // we don't need word-wrapping, return just a single line.
            drawableParts.add(line);
            return drawableParts;
        }
        
        // we need word-wrapping. start laying out the lines. add words to 
        // every line until it's full.
        
        boolean firstLine = true;
        double totalHeight = y;
        String prefix = "";
        String suffix = legendItem.getLabel().trim();
        
        LegendItem tmpItem = new LegendItem(
            prefix.trim(), 
            legendItem.getLabel(),
            legendItem.getToolTipText(),
            legendItem.getURLText(),
            legendItem.isShapeVisible(),
            legendItem.getShape(),
            legendItem.isShapeFilled(), 
            legendItem.getFillPaint(),
            legendItem.isShapeOutlineVisible(),
            legendItem.getOutlinePaint(), 
            legendItem.getOutlineStroke(),
            legendItem.isLineVisible(),
            legendItem.getLine(),
            legendItem.getLineStroke(),
            legendItem.getLinePaint()
        );

        line = createDrawableLegendItem(g2, tmpItem, x, totalHeight);
        
        DrawableLegendItem goodLine = null; // no good known line yet.

        do {
            // save the suffix, we might need to restore it.
            String prevSuffix = suffix; 

            // try to extend the prefix.
            int spacePos = suffix.indexOf(" ");
            if (spacePos < 0) {
                // no space found, append all the suffix to the prefix.
                prefix += suffix;
                suffix = "";
            }
            else {
                // move a word from suffix to prefix.
                prefix += suffix.substring(0, spacePos + 1);
                suffix = suffix.substring(spacePos + 1);
            }
            
            // Create a temporary legend item for the extended prefix.
            // If first line, make its marker visible.
            tmpItem = new LegendItem(
                prefix.trim(),
                legendItem.getLabel(), 
                legendItem.getToolTipText(),
                legendItem.getURLText(),
                firstLine && legendItem.isShapeVisible(),
                legendItem.getShape(),
                firstLine && legendItem.isShapeFilled(), 
                legendItem.getFillPaint(),
                firstLine && legendItem.isShapeOutlineVisible(), 
                legendItem.getOutlinePaint(),
                legendItem.getOutlineStroke(),
                firstLine && legendItem.isLineVisible(), 
                legendItem.getLine(),
                legendItem.getLineStroke(),
                legendItem.getLinePaint()
            );
            
            // and create a line for it as well.
            line = createDrawableLegendItem(g2, tmpItem, x, totalHeight);

            // now check if line fits in width.
            if (line.getWidth() < wordWrapWidth) {
                // fits! save it as the last good known line.
                goodLine = line;
            }
            else {
                // doesn't fit. do we have a saved good line? 
                if (goodLine == null) {
                    // nope. this means we will have to add it anyway and exceed
                    // the desired wordWrapWidth. life is tough sometimes...
                    drawableParts.add(line);
                    totalHeight += line.getHeight();
                }
                else {
                    // yep, we have a saved good line, and we intend to use it.
                    drawableParts.add(goodLine);
                    totalHeight += goodLine.getHeight();
                    // restore previous suffix.
                    suffix = prevSuffix;
                }
                // prepare to start a new line.
                firstLine = false;
                prefix = "";
                suffix = suffix.trim();
                line = null; // mark as used to avoid using twice.
                goodLine = null; // mark as used to avoid using twice.
            }
        } 
        while (!suffix.equals(""));
        
        // make sure not to forget last line.
        if (line != null) {
            drawableParts.add(line);
        }
        
        return drawableParts;
    }
    
    /**
     * Creates a drawable legend item.
     * <P>
     * The marker box for each entry will be positioned next to the name of the
     * specified series within the legend area.  The marker box will be square
     * and 70% of the height of current font.
     *
     * @param graphics  the graphics context (supplies font metrics etc.).
     * @param legendItem  the legend item.
     * @param x  the upper left x coordinate for the bounding box.
     * @param y  the upper left y coordinate for the bounding box.
     *
     * @return A legend item encapsulating all necessary info for drawing.
     */
    private DrawableLegendItem createDrawableLegendItem(Graphics2D graphics,
                                                        LegendItem legendItem,
                                                        double x, double y) {

        int insideGap = 2;
        FontMetrics fm = graphics.getFontMetrics();
        LineMetrics lm = fm.getLineMetrics(legendItem.getLabel(), graphics);
        float textAscent = lm.getAscent();
        float lineHeight = textAscent + lm.getDescent() + lm.getLeading();

        DrawableLegendItem item = new DrawableLegendItem(legendItem);

        float xLabelLoc = (float) (x + insideGap + 1.15f * lineHeight);
        float yLabelLoc = (float) (y + insideGap + 0.5f * lineHeight);

        item.setLabelPosition(new Point2D.Float(xLabelLoc, yLabelLoc));

        float width = (float) (item.getLabelPosition().getX() - x
            + fm.stringWidth(legendItem.getLabel()) + 0.5 * textAscent);

        float height = (2 * insideGap + lineHeight);
        item.setBounds(x, y, width, height);
        float boxDim = lineHeight * 0.70f;
        float xloc = (float) (x + insideGap + 0.15f * lineHeight);
        float yloc = (float) (y + insideGap + 0.15f * lineHeight);
        if (legendItem.isLineVisible()) {
            Line2D line = new Line2D.Float(
                xloc, yloc + boxDim / 2, xloc + boxDim * 3, yloc + boxDim / 2
            );
            item.setLine(line);
            // lengthen the bounds to accomodate the longer item
            item.setBounds(
                item.getX(), item.getY(), item.getWidth() + boxDim * 2, 
                item.getHeight()
            );
            item.setLabelPosition(
                new Point2D.Float(xLabelLoc + boxDim * 2, yLabelLoc)
            );
            if (item.getItem().isShapeVisible()) {
                Shape marker = legendItem.getShape();
                AffineTransform t1 = AffineTransform.getScaleInstance(
                    this.shapeScaleX, this.shapeScaleY
                );
                Shape s1 = t1.createTransformedShape(marker);
                AffineTransform transformer 
                    = AffineTransform.getTranslateInstance(
                        xloc + (boxDim * 1.5), yloc + boxDim / 2
                    );
                Shape s2 = transformer.createTransformedShape(s1);
                item.setMarker(s2);
           }

        } 
        else {
            if (item.getItem().isShapeVisible()) {
                Shape marker = legendItem.getShape();
                AffineTransform t1 = AffineTransform.getScaleInstance(
                        this.shapeScaleX, this.shapeScaleY
                );
                Shape s1 = t1.createTransformedShape(marker);
                AffineTransform transformer 
                    = AffineTransform.getTranslateInstance(
                        xloc + boxDim / 2, yloc + boxDim / 2
                    );
                Shape s2 = transformer.createTransformedShape(s1);
                item.setMarker(s2);
            }
            else {
                item.setMarker(
                    new Rectangle2D.Float(xloc, yloc, boxDim, boxDim)
                );
            }
        }
        return item;

    }

    /**
     * Tests an object for equality with this legend.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultOldLegend)) {
            return false;
        }
        DefaultOldLegend that = (DefaultOldLegend) obj;
        if (!super.equals(obj)) {
            return false;
        }

        if (!ObjectUtilities.equal(this.margin, that.margin)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.backgroundPaint, that.backgroundPaint
        )) {
            return false;
        }
        if (!ObjectUtilities.equal(this.padding, that.padding)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.title, that.title)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.titleFont, that.titleFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemFont, that.itemFont)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.itemPaint, that.itemPaint)) {
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
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.itemPaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.itemPaint = SerialUtilities.readPaint(stream);
    }

}
