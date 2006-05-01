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
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------
 * PolarPlot.java
 * --------------
 * (C) Copyright 2004, 2005, by Solution Engineering, Inc. and Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes
 * -------
 * 19-Jan-2004 : Version 1, contributed by DB with minor changes by DG (DG);
 * 07-Apr-2004 : Changed text bounds calculation (DG);
 * 05-May-2005 : Updated draw() method parameters (DG);
 * 09-Jun-2005 : Fixed getDataRange() and equals() methods (DG);
 * 25-Oct-2005 : Implemented Zoomable (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;


/**
 * Plots data that is in (theta, radius) pairs where
 * theta equal to zero is due north and and increases clockwise.
 *
 * @author Daniel Bridenbecker, Solution Engineering, Inc.
 */
public class PolarPlot extends Plot implements ValueAxisPlot, 
                                               Zoomable,
                                               RendererChangeListener, 
                                               Cloneable, 
                                               Serializable {
   
    /** For serialization. */
    private static final long serialVersionUID = 3794383185924179525L;
    
    /** The default margin. */
    private static final int MARGIN = 20;
   
    /** The annotation margin. */
    private static final double ANNOTATION_MARGIN = 7.0;
   
    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRIDLINE_STROKE 
        = new BasicStroke(
            0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 
            0.0f, new float[]{2.0f, 2.0f}, 0.0f
          );
   
    /** The default grid line paint. */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.gray;
   
    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
   
    // ------------------------
    // --- Member Variables ---
    // ------------------------
    /** The angles that are marked with gridlines. */
    private List angleTicks;
    
    /** The axis (used for the y-values). */
    private ValueAxis axis;
    
    /** The dataset. */
    private XYDataset dataset;
   
    /** 
     * Object responsible for drawing the visual representation of each point 
     * on the plot. 
     */
    private PolarItemRenderer renderer;
   
    /** A flag that controls whether or not the angle labels are visible. */
    private boolean angleLabelsVisible = true;
    
    /** The font used to display the angle labels - never null. */
    private Font angleLabelFont = new Font("SansSerif", Font.PLAIN, 12);
    
    /** The paint used to display the angle labels. */
    private Paint angleLabelPaint = Color.black;
    
    /** A flag that controls whether the angular grid-lines are visible. */
    private boolean angleGridlinesVisible;
   
    /** The stroke used to draw the angular grid-lines. */
    private transient Stroke angleGridlineStroke;
   
    /** The paint used to draw the angular grid-lines. */
    private transient Paint angleGridlinePaint;
   
    /** A flag that controls whether the radius grid-lines are visible. */
    private boolean radiusGridlinesVisible;
   
    /** The stroke used to draw the radius grid-lines. */
    private transient Stroke radiusGridlineStroke;
   
    /** The paint used to draw the radius grid-lines. */
    private transient Paint radiusGridlinePaint;
   
    /** The annotations for the plot. */
    private List cornerTextItems = new ArrayList();
   
    // --------------------
    // --- Constructors ---
    // --------------------
    /**
     * Default constructor.
     */
    public PolarPlot() {
        this(null, null, null);
    }
   
   /**
     * Creates a new plot.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     * @param radiusAxis  the radius axis (<code>null</code> permitted).
     * @param renderer  the renderer (<code>null</code> permitted).
     */
    public PolarPlot(XYDataset dataset, 
                     ValueAxis radiusAxis,
                     PolarItemRenderer renderer) {
      
        super();
            
        this.dataset = dataset;
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
      
        this.angleTicks = new java.util.ArrayList();
        this.angleTicks.add(new NumberTick(new Double(0.0), "0", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(45.0), "45", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(90.0), "90", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(135.0), "135", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(180.0), "180", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(225.0), "225", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(270.0), "270", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(315.0), "315", 
                TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        
        this.axis = radiusAxis;
        if (this.axis != null) {
            this.axis.setPlot(this);
            this.axis.addChangeListener(this);
        }
      
        this.renderer = renderer;
        if (this.renderer != null) {
            this.renderer.setPlot(this);
            this.renderer.addChangeListener(this);
        }
      
        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = DEFAULT_GRIDLINE_PAINT;
      
        this.radiusGridlinesVisible = true;
        this.radiusGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = DEFAULT_GRIDLINE_PAINT;      
    }
   
    /**
     * Add text to be displayed in the lower right hand corner.
     * 
     * @param text  the text to display (<code>null</code> not permitted).
     */
    public void addCornerTextItem(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.cornerTextItems.add(text);
        this.notifyListeners(new PlotChangeEvent(this));
    }
   
    /**
     * Remove the given text from the list of corner text items.
     * 
     * @param text  the text to remove (<code>null</code> ignored).
     */
    public void removeCornerTextItem(String text) {
        boolean removed = this.cornerTextItems.remove(text);
        if (removed) {
            this.notifyListeners(new PlotChangeEvent(this));        
        }
    }
   
    /**
     * Clear the list of corner text items.
     */
    public void clearCornerTextItems() {
        if (this.cornerTextItems.size() > 0) {
            this.cornerTextItems.clear();
            this.notifyListeners(new PlotChangeEvent(this));        
        }
    }
   
    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    public String getPlotType() {
       return PolarPlot.localizationResources.getString("Polar_Plot");
    }
    
    /**
     * Returns the axis for the plot.
     *
     * @return The radius axis.
     */
    public ValueAxis getAxis() {
        return this.axis;
    }
   
    /**
     * Sets the axis for the plot and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param axis  the new axis (<code>null</code> permitted).
     */
    public void setAxis(ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
        }
       
        // plot is likely registered as a listener with the existing axis...
        if (this.axis != null) {
            this.axis.removeChangeListener(this);
        }
       
        this.axis = axis;
        if (this.axis != null) {
            this.axis.configure();
            this.axis.addChangeListener(this);
        }
        notifyListeners(new PlotChangeEvent(this));
    }
   
    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly <code>null</code>).
     */
    public XYDataset getDataset() {
        return this.dataset;
    }
    
    /**
     * Sets the dataset for the plot, replacing the existing dataset if there 
     * is one.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public void setDataset(XYDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of 
        // change listeners...
        XYDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
       
        // set the new m_Dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (this.dataset != null) {
            setDatasetGroup(this.dataset.getGroup());
            this.dataset.addChangeListener(this);
        }
       
        // send a m_Dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, this.dataset);
        datasetChanged(event);
    }
   
    /**
     * Returns the item renderer.
     *
     * @return The renderer (possibly <code>null</code>).
     */
    public PolarItemRenderer getRenderer() {
        return this.renderer;
    }
   
    /**
     * Sets the item renderer, and notifies all listeners of a change to the 
     * plot.
     * <P>
     * If the renderer is set to <code>null</code>, no chart will be drawn.
     *
     * @param renderer  the new renderer (<code>null</code> permitted).
     */
    public void setRenderer(PolarItemRenderer renderer) {
        if (this.renderer != null) {
            this.renderer.removeChangeListener(this);
        }
       
        this.renderer = renderer;
        if (this.renderer != null) {
            this.renderer.setPlot(this);
        }
       
        notifyListeners(new PlotChangeEvent(this));
    }
   
    /**
     * Returns a flag that controls whether or not the angle labels are visible.
     * 
     * @return A boolean.
     */
    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }
    
    /**
     * Sets the flag that controls whether or not the angle labels are visible,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param visible  the flag.
     */
    public void setAngleLabelsVisible(boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    /**
     * Returns the font used to display the angle labels.
     * 
     * @return A font (never <code>null</code>).
     */
    public Font getAngleLabelFont() {
        return this.angleLabelFont;
    }
    
    /**
     * Sets the font used to display the angle labels and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param font  the font (<code>null</code> not permitted).
     */
    public void setAngleLabelFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");   
        }
        this.angleLabelFont = font;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the paint used to display the angle labels.
     * 
     * @return A paint.
     */
    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }
    
    /**
     * Sets the paint used to display the angle labels and sends a 
     * {@link PlotChangeEvent} to all registered listeners.
     * 
     * @param paint  the paint.
     */
    public void setAngleLabelPaint(Paint paint) {
        this.angleLabelPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns <code>true</code> if the angular gridlines are visible, and 
     * <code>false<code> otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }
    
    /**
     * Sets the flag that controls whether or not the angular grid-lines are 
     * visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all 
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     */
    public void setAngleGridlinesVisible(boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }
   
    /**
     * Returns the stroke for the grid-lines (if any) plotted against the 
     * angular axis.
     *
     * @return The stroke.
     */
    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }
    
    /**
     * Sets the stroke for the grid lines plotted against the angular axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setAngleGridlineStroke(Stroke stroke) {
        this.angleGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the paint for the grid lines (if any) plotted against the 
     * angular axis.
     *
     * @return The paint.
     */
    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }
   
    /**
     * Sets the paint for the grid lines plotted against the angular axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setAngleGridlinePaint(Paint paint) {
        this.angleGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns <code>true</code> if the radius axis grid is visible, and 
     * <code>false<code> otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }
    
    /**
     * Sets the flag that controls whether or not the radius axis grid lines 
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all 
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     */
    public void setRadiusGridlinesVisible(boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }
   
    /**
     * Returns the stroke for the grid lines (if any) plotted against the 
     * radius axis.
     *
     * @return The stroke.
     */
    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }
    
    /**
     * Sets the stroke for the grid lines plotted against the radius axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setRadiusGridlineStroke(Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the paint for the grid lines (if any) plotted against the radius
     * axis.
     *
     * @return The paint.
     */
    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }
    
    /**
     * Sets the paint for the grid lines plotted against the radius axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setRadiusGridlinePaint(Paint paint) {
        this.radiusGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a 
     * printer).
     * <P>
     * This plot relies on an 
     * {@link org.jfree.chart.renderer.DefaultPolarItemRenderer} to draw each 
     * item in the plot.  This allows the visual representation of the data to 
     * be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in 
     * <code>null</code> if you do not need this information.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot (including axes and 
     *              labels) should be drawn.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  ignored.
     * @param info  collects chart drawing information (<code>null</code> 
     *              permitted).
     */
    public void draw(Graphics2D g2, 
                     Rectangle2D area, 
                     Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {
       
        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }
       
        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }
       
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
      
        Rectangle2D dataArea = area;
        if (info != null) {
            info.setDataArea(dataArea);
        }
       
        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        double h = Math.min(
            dataArea.getWidth() / 2.0, dataArea.getHeight() / 2.0
        ) - MARGIN;
        Rectangle2D quadrant = new Rectangle2D.Double(
            dataArea.getCenterX(), dataArea.getCenterY(), h, h
        );
        AxisState state = drawAxis(g2, area, quadrant);
        if (this.renderer != null) {
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();
          
            g2.clip(dataArea);
            g2.setComposite(
                AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, getForegroundAlpha()
                )
            );
          
            drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
          
            // draw...
            render(g2, dataArea, info);
          
            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }
        drawOutline(g2, dataArea);
        drawCornerTextItems(g2, dataArea);
    }
   
    /**
     * Draws the corner text items.
     * 
     * @param g2  the drawing surface.
     * @param area  the area.
     */
    protected void drawCornerTextItems(Graphics2D g2, Rectangle2D area) {
        if (this.cornerTextItems.isEmpty()) {
            return;
        }
       
        g2.setColor(Color.black);
        double width = 0.0;
        double height = 0.0;
        for (Iterator it = this.cornerTextItems.iterator(); it.hasNext();) {
            String msg = (String) it.next();
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D bounds = TextUtilities.getTextBounds(msg, g2, fm);
            width = Math.max(width, bounds.getWidth());
            height += bounds.getHeight();
        }
        
        double xadj = ANNOTATION_MARGIN * 2.0;
        double yadj = ANNOTATION_MARGIN;
        width += xadj;
        height += yadj;
       
        double x = area.getMaxX() - width;
        double y = area.getMaxY() - height;
        g2.drawRect((int) x, (int) y, (int) width, (int) height);
        x += ANNOTATION_MARGIN;
        for (Iterator it = this.cornerTextItems.iterator(); it.hasNext();) {
            String msg = (String) it.next();
            Rectangle2D bounds = TextUtilities.getTextBounds(
                msg, g2, g2.getFontMetrics()
            );
            y += bounds.getHeight();
            g2.drawString(msg, (int) x, (int) y);
        }
    }
   
    /**
     * A utility method for drawing the axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * 
     * @return A map containing the axis states.
     */
    protected AxisState drawAxis(Graphics2D g2, Rectangle2D plotArea, 
                                 Rectangle2D dataArea) {
        return this.axis.draw(
            g2, dataArea.getMinY(), plotArea, dataArea, RectangleEdge.TOP, null
        );
    }
   
    /**
     * Draws a representation of the data within the dataArea region, using the
     * current m_Renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension 
     *              information (<code>null</code> permitted).
     */
    protected void render(Graphics2D g2,
                       Rectangle2D dataArea,
                       PlotRenderingInfo info) {
      
        // now get the data and plot it (the visual representation will depend
        // on the m_Renderer that has been set)...
        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            int seriesCount = this.dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                this.renderer.drawSeries(
                    g2, dataArea, info, this, this.dataset, series
                );
            }
        }
        else {
            drawNoDataMessage(g2, dataArea);
        }
    }
   
    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param angularTicks  the ticks for the angular axis.
     * @param radialTicks  the ticks for the radial axis.
     */
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea, 
                                 List angularTicks, List radialTicks) {

        // no renderer, no gridlines...
        if (this.renderer == null) {
            return;
        }
       
        // draw the domain grid lines, if any...
        if (isAngleGridlinesVisible()) {
            Stroke gridStroke = getAngleGridlineStroke();
            Paint gridPaint = getAngleGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                this.renderer.drawAngularGridLines(
                    g2, this, angularTicks, dataArea
                );
            }
        }
       
        // draw the radius grid lines, if any...
        if (isRadiusGridlinesVisible()) {
            Stroke gridStroke = getRadiusGridlineStroke();
            Paint gridPaint = getRadiusGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                this.renderer.drawRadialGridLines(
                    g2, this, this.axis, radialTicks, dataArea
                );
            }
        }      
    }
   
    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  the amount of the zoom.
     */
    public void zoom(double percent) {
        if (percent > 0.0) {
            double radius = getMaxRadius();
            double scaledRadius = radius * percent;
            this.axis.setUpperBound(scaledRadius);
            getAxis().setAutoRange(false);
        } 
        else {
            getAxis().setAutoRange(true);
        }
    }
   
    /**
     * Returns the range for the specified axis.
     *
     * @param axis  the axis.
     *
     * @return The range.
     */
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        if (this.dataset != null) {
            result = Range.combine(result, 
                    DatasetUtilities.findRangeBounds(this.dataset));
        }
        return result;
    }
   
    /**
     * Receives notification of a change to the plot's m_Dataset.
     * <P>
     * The axis ranges are updated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.axis != null) {
            this.axis.configure();
        }
       
        if (getParent() != null) {
            getParent().datasetChanged(event);
        }
        else {
            super.datasetChanged(event);
        }
    }
   
    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's m_Renderer.
     *
     * @param event  information about the property change.
     */
    public void rendererChanged(RendererChangeEvent event) {
        notifyListeners(new PlotChangeEvent(this));
    }
   
    /**
     * Returns the number of series in the dataset for this plot.  If the 
     * dataset is <code>null</code>, the method returns 0.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        int result = 0;
       
        if (this.dataset != null) {
            result = this.dataset.getSeriesCount();
        }
        return result;
    }
   
    /**
     * Returns the legend items for the plot.  Each legend item is generated by
     * the plot's m_Renderer, since the m_Renderer is responsible for the visual
     * representation of the data.
     *
     * @return The legend items.
     */
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
       
        // get the legend items for the main m_Dataset...
        if (this.dataset != null) {
            if (this.renderer != null) {
                int seriesCount = this.dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = this.renderer.getLegendItem(i);
                    result.add(item);
                }
            }
        }      
        return result;
    }
   
    /**
     * Tests this plot for equality with another object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PolarPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        PolarPlot that = (PolarPlot) obj;
        if (!ObjectUtilities.equal(this.axis, that.axis)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.renderer, that.renderer)) {
            return false;
        }
        if (this.angleGridlinesVisible != that.angleGridlinesVisible) {
            return false;
        }
        if (this.angleLabelsVisible != that.angleLabelsVisible) {
            return false;   
        }
        if (!this.angleLabelFont.equals(that.angleLabelFont)) {
            return false;   
        }
        if (!PaintUtilities.equal(this.angleLabelPaint, that.angleLabelPaint)) {
            return false;   
        }
        if (!ObjectUtilities.equal(
            this.angleGridlineStroke, that.angleGridlineStroke
        )) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.angleGridlinePaint, that.angleGridlinePaint
        )) {
            return false;
        }
        if (this.radiusGridlinesVisible != that.radiusGridlinesVisible) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.radiusGridlineStroke, that.radiusGridlineStroke
        )) {
            return false;
        }
        if (!PaintUtilities.equal(
            this.radiusGridlinePaint, that.radiusGridlinePaint
        )) {
            return false;
        }
        return true;
    }
   
    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  this can occur if some component of 
     *         the plot cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
      
        PolarPlot clone = (PolarPlot) super.clone();
        if (this.axis != null) {
            clone.axis = (ValueAxis) ObjectUtilities.clone(this.axis);
            clone.axis.setPlot(clone);
            clone.axis.addChangeListener(clone);
        }
      
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
      
        if (this.renderer != null) {
            clone.renderer 
                = (PolarItemRenderer) ObjectUtilities.clone(this.renderer);
        }
       
        return clone;
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
        SerialUtilities.writeStroke(this.angleGridlineStroke, stream);
        SerialUtilities.writePaint(this.angleGridlinePaint, stream);
        SerialUtilities.writeStroke(this.radiusGridlineStroke, stream);
        SerialUtilities.writePaint(this.radiusGridlinePaint, stream);
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
        this.angleGridlineStroke = SerialUtilities.readStroke(stream);
        this.angleGridlinePaint = SerialUtilities.readPaint(stream);
        this.radiusGridlineStroke = SerialUtilities.readStroke(stream);
        this.radiusGridlinePaint = SerialUtilities.readPaint(stream);
      
        if (this.axis != null) {
            this.axis.setPlot(this);
            this.axis.addChangeListener(this);
        }
      
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
    }
   
    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, 
                               Point2D source) {
        // do nothing
    }
   
    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     * 
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    public void zoomDomainAxes(double lowerPercent, double upperPercent, 
                               PlotRenderingInfo state, Point2D source) {
        // do nothing
    }
   
    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, 
                              Point2D source) {
        zoom(factor);
    }
   
    /**
     * Zooms in on the range axes.
     * 
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    public void zoomRangeAxes(double lowerPercent, double upperPercent, 
                              PlotRenderingInfo state, Point2D source) {
        zoom((upperPercent + lowerPercent) / 2.0);
    }   

    /**
     * Returns <code>true</code>.
     * 
     * @return A boolean.
     */
    public boolean isDomainZoomable() {
        return false;
    }
    
    /**
     * Returns <code>true</code>.
     * 
     * @return A boolean.
     */
    public boolean isRangeZoomable() {
        return true;
    }
    
    /**
     * Returns the orientation of the plot.
     * 
     * @return The orientation.
     */
    public PlotOrientation getOrientation() {
        return PlotOrientation.HORIZONTAL;
    }


    // ----------------------
    // --- Public Methods ---
    // ----------------------

    /**
     * Returns the upper bound of the radius axis.
     * 
     * @return The upper bound.
     */
    public double getMaxRadius() {
        return this.axis.getUpperBound();
    }

    /**
     * Translates a (theta, radius) pair into Java2D coordinates.
     * 
     * @param angleDegrees  the angle in degrees.
     * @param radius  the radius.
     * @param dataArea  the data area.
     * 
     * @return A point in Java2D space.
     */   
    public Point translateValueThetaRadiusToJava2D(double angleDegrees, 
                                                   double radius,
                                                   Rectangle2D dataArea) {
       
        double radians = Math.toRadians(angleDegrees - 90.0);
      
        double minx = dataArea.getMinX() + MARGIN;
        double maxx = dataArea.getMaxX() - MARGIN;
        double miny = dataArea.getMinY() + MARGIN;
        double maxy = dataArea.getMaxY() - MARGIN;
      
        double lengthX = maxx - minx;
        double lengthY = maxy - miny;
        double length = Math.min(lengthX, lengthY);
      
        double midX = minx + lengthX / 2.0;
        double midY = miny + lengthY / 2.0;
      
        double axisMin = this.axis.getLowerBound();
        double axisMax =  getMaxRadius();

        double xv = length / 2.0 * Math.cos(radians);
        double yv = length / 2.0 * Math.sin(radians);

        float x = (float) (midX + (xv * (radius - axisMin) 
                / (axisMax - axisMin)));
        float y = (float) (midY + (yv * (radius - axisMin) 
                / (axisMax - axisMin)));
      
        int ix = Math.round(x);
        int iy = Math.round(y);
      
        Point p = new Point(ix, iy);
        return p;
        
    }
    
}
