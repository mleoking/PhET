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
 * --------------------
 * FastScatterPlot.java
 * --------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Arnaud Lelievre;
 *
 * $Id$
 *
 * Changes (from 29-Oct-2002)
 * --------------------------
 * 29-Oct-2002 : Added standard header (DG);
 * 07-Nov-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 19-Aug-2003 : Implemented Cloneable (DG);
 * 08-Sep-2003 : Added internationalization via use of properties 
 *               resourceBundle (RFE 690236) (AL); 
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 12-Nov-2003 : Implemented zooming (DG);
 * 21-Jan-2004 : Update for renamed method in ValueAxis (DG);
 * 26-Jan-2004 : Added domain and range grid lines (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 29-Sep-2004 : Removed hard-coded color (DG);
 * 04-Oct-2004 : Reworked equals() method and renamed ArrayUtils 
 *               --> ArrayUtilities (DG);
 * 12-Nov-2004 : Implemented the new Zoomable interface (DG);
 * 05-May-2005 : Updated draw() method parameters (DG);
 * 16-Jun-2005 : Added get/setData() methods (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ArrayUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;

/**
 * A fast scatter plot.
 */
public class FastScatterPlot extends Plot implements ValueAxisPlot, 
                                                     Zoomable, 
                                                     Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 7871545897358563521L;
    
    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL,
            0.0f,
            new float[] {2.0f, 2.0f},
            0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;

    /** The data. */
    private float[][] data;

    /** The x data range. */
    private Range xDataRange;

    /** The y data range. */
    private Range yDataRange;

    /** The domain axis (used for the x-values). */
    private ValueAxis domainAxis;

    /** The range axis (used for the y-values). */
    private ValueAxis rangeAxis;

    /** The paint used to plot data points. */
    private transient Paint paint;

    /** A flag that controls whether the domain grid-lines are visible. */
    private boolean domainGridlinesVisible;

    /** The stroke used to draw the domain grid-lines. */
    private transient Stroke domainGridlineStroke;

    /** The paint used to draw the domain grid-lines. */
    private transient Paint domainGridlinePaint;

    /** A flag that controls whether the range grid-lines are visible. */
    private boolean rangeGridlinesVisible;

    /** The stroke used to draw the range grid-lines. */
    private transient Stroke rangeGridlineStroke;

    /** The paint used to draw the range grid-lines. */
    private transient Paint rangeGridlinePaint;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources = 
        ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * Creates an empty plot.
     */
    public FastScatterPlot() {
        this(null, null, null);    
    }
    
    /**
     * Creates a new fast scatter plot.
     * <P>
     * The data is an array of x, y values:  data[0][i] = x, data[1][i] = y.
     *
     * @param data  the data.
     * @param domainAxis  the domain (x) axis.
     * @param rangeAxis  the range (y) axis.
     */
    public FastScatterPlot(float[][] data, 
                           ValueAxis domainAxis, ValueAxis rangeAxis) {

        super();

        this.data = data;
        this.xDataRange = calculateXDataRange(data);
        this.yDataRange = calculateYDataRange(data);
        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

        this.paint = Color.red;
        
        this.domainGridlinesVisible = true;
        this.domainGridlinePaint = FastScatterPlot.DEFAULT_GRIDLINE_PAINT;
        this.domainGridlineStroke = FastScatterPlot.DEFAULT_GRIDLINE_STROKE;

        this.rangeGridlinesVisible = true;
        this.rangeGridlinePaint = FastScatterPlot.DEFAULT_GRIDLINE_PAINT;
        this.rangeGridlineStroke = FastScatterPlot.DEFAULT_GRIDLINE_STROKE;
    
    }

    /**
     * Returns a short string describing the plot type.
     *
     * @return A short string describing the plot type.
     */
    public String getPlotType() {
        return localizationResources.getString("Fast_Scatter_Plot");
    }

    /**
     * Returns the data array used by the plot.
     * 
     * @return The data array (possibly <code>null</code>).
     */
    public float[][] getData() {
        return this.data;   
    }
    
    /**
     * Sets the data array used by the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     * 
     * @param data  the data array (<code>null</code> permitted).
     */
    public void setData(float[][] data) {
        this.data = data;
        notifyListeners(new PlotChangeEvent(this));
    }
    
    /**
     * Returns the orientation of the plot.
     * 
     * @return The orientation (always {@link PlotOrientation#VERTICAL}).
     */
    public PlotOrientation getOrientation() {
        return PlotOrientation.VERTICAL;    
    }
    
    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return The domain axis.
     */
    public ValueAxis getDomainAxis() {
        return this.domainAxis;
    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if
     * there is a parent plot).
     *
     * @return The range axis.
     */
    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    /**
     * Returns the paint used to plot data points.
     *
     * @return The paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the color for the data points and sends a {@link PlotChangeEvent} 
     * to all registered listeners.
     *
     * @param paint  the paint (<code>null</code> not permitted).
     */
    public void setPaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns <code>true</code> if the domain gridlines are visible, and 
     * <code>false<code> otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not the domain grid-lines are 
     * visible.  If the flag value is changed, a {@link PlotChangeEvent} is 
     * sent to all registered listeners.
     *
     * @param visible  the new value of the flag.
     */
    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the stroke for the grid-lines (if any) plotted against the 
     * domain axis.
     *
     * @return The stroke.
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the domain axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setDomainGridlineStroke(Stroke stroke) {
        this.domainGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the domain
     * axis.
     *
     * @return The paint.
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the domain axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setDomainGridlinePaint(Paint paint) {
        this.domainGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns <code>true</code> if the range axis grid is visible, and 
     * <code>false<code> otherwise.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether or not the range axis grid lines are
     * visible.  If the flag value is changed, a {@link PlotChangeEvent} is 
     * sent to all registered listeners.
     *
     * @param visible  the new value of the flag.
     */
    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the stroke for the grid lines (if any) plotted against the range
     * axis.
     *
     * @return The stroke.
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the range axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param stroke  the stroke (<code>null</code> permitted).
     */
    public void setRangeGridlineStroke(Stroke stroke) {
        this.rangeGridlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the range 
     * axis.
     *
     * @return The paint.
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the range axis.
     * <p>
     * If you set this to <code>null</code>, no grid lines will be drawn.
     *
     * @param paint  the paint (<code>null</code> permitted).
     */
    public void setRangeGridlinePaint(Paint paint) {
        this.rangeGridlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Draws the fast scatter plot on a Java 2D graphics device (such as the 
     * screen or a printer).
     *
     * @param g2  the graphics device.
     * @param area   the area within which the plot (including axis labels)
     *                   should be drawn.
     * @param anchor  the anchor point (<code>null</code> permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects chart drawing information (<code>null</code> 
     *              permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor,
                     PlotState parentState,
                     PlotRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(area);
        }

        // adjust the drawing area for plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);

        AxisSpace space = new AxisSpace();
        space = this.domainAxis.reserveSpace(
            g2, this, area, RectangleEdge.BOTTOM, space
        );
        space = this.rangeAxis.reserveSpace(
            g2, this, area, RectangleEdge.LEFT, space
        );
        Rectangle2D dataArea = space.shrink(area, null);

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw the plot background and axes...
        drawBackground(g2, dataArea);

        AxisState domainAxisState = null;
        AxisState rangeAxisState = null;
        if (this.domainAxis != null) {
            domainAxisState = this.domainAxis.draw(
                g2, dataArea.getMaxY(), area, dataArea, 
                RectangleEdge.BOTTOM, info
            );
        }
        if (this.rangeAxis != null) {
            rangeAxisState = this.rangeAxis.draw(
                g2, dataArea.getMinX(), area, dataArea, 
                RectangleEdge.LEFT, info
            );
        }
        drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
        drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
        
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();

        g2.clip(dataArea);
        g2.setComposite(
            AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, getForegroundAlpha()
            )
        );

        render(g2, dataArea, info, null);

        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);

    }

    /**
     * Draws a representation of the data within the dataArea region.  The 
     * <code>info</code> and <code>crosshairState</code> arguments may be 
     * <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  collects crosshair information (<code>null</code>
     *                        permitted).
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       PlotRenderingInfo info, CrosshairState crosshairState) {
    
 
        //long start = System.currentTimeMillis();
        //System.out.println("Start: " + start);
        g2.setPaint(this.paint);

        // if the axes use a linear scale, you can uncomment the code below and
        // switch to the alternative transX/transY calculation inside the loop 
        // that follows - it is a little bit faster then.
        // 
        // int xx = (int) dataArea.getMinX();
        // int ww = (int) dataArea.getWidth();
        // int yy = (int) dataArea.getMaxY();
        // int hh = (int) dataArea.getHeight();
        // double domainMin = this.domainAxis.getLowerBound();
        // double domainLength = this.domainAxis.getUpperBound() - domainMin;
        // double rangeMin = this.rangeAxis.getLowerBound();
        // double rangeLength = this.rangeAxis.getUpperBound() - rangeMin;

        if (this.data != null) {
            for (int i = 0; i < this.data[0].length; i++) {
                float x = this.data[0][i];
                float y = this.data[1][i];
                
                //int transX = (int) (xx + ww * (x - domainMin) / domainLength);
                //int transY = (int) (yy - hh * (y - rangeMin) / rangeLength); 
                int transX = (int) this.domainAxis.valueToJava2D(
                    x, dataArea, RectangleEdge.BOTTOM
                );
                int transY = (int) this.rangeAxis.valueToJava2D(
                    y, dataArea, RectangleEdge.LEFT
                );
                g2.fillRect(transX, transY, 1, 1);
            }
        }
        //long finish = System.currentTimeMillis();
        //System.out.println("Finish: " + finish);
        //System.out.println("Time: " + (finish - start));

    }

    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, 
                                       List ticks) {

        // draw the domain grid lines, if any...
        if (isDomainGridlinesVisible()) {
            Stroke gridStroke = getDomainGridlineStroke();
            Paint gridPaint = getDomainGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                Iterator iterator = ticks.iterator();
                while (iterator.hasNext()) {
                    ValueTick tick = (ValueTick) iterator.next();
                    double v = this.domainAxis.valueToJava2D(
                        tick.getValue(), dataArea, RectangleEdge.BOTTOM
                    );
                    Line2D line = new Line2D.Double(
                        v, dataArea.getMinY(), v, dataArea.getMaxY()
                    );
                    g2.setPaint(gridPaint);
                    g2.setStroke(gridStroke);
                    g2.draw(line);                
                }
            }
        }
    }
    
    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     */
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, 
                                      List ticks) {

        // draw the range grid lines, if any...
        if (isRangeGridlinesVisible()) {
            Stroke gridStroke = getRangeGridlineStroke();
            Paint gridPaint = getRangeGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                Iterator iterator = ticks.iterator();
                while (iterator.hasNext()) {
                    ValueTick tick = (ValueTick) iterator.next();
                    double v = this.rangeAxis.valueToJava2D(
                        tick.getValue(), dataArea, RectangleEdge.LEFT
                    );
                    Line2D line = new Line2D.Double(
                        dataArea.getMinX(), v, dataArea.getMaxX(), v
                    );
                    g2.setPaint(gridPaint);
                    g2.setStroke(gridStroke);
                    g2.draw(line);                
                }
            }
        }

    }

    /**
     * Returns the range of data values to be plotted along the axis.
     *
     * @param axis  the axis.
     *
     * @return The range.
     */
    public Range getDataRange(ValueAxis axis) {

        Range result = null;
        if (axis == this.domainAxis) {
            result = this.xDataRange;
        }
        else if (axis == this.rangeAxis) {
            result = this.yDataRange;
        }
        return result;
    }

    /**
     * Calculates the X data range.
     *
     * @param data  the data.
     *
     * @return The range.
     */
    private Range calculateXDataRange(float[][] data) {
        
        Range result = null;
        
        if (data != null) {
            float lowest = Float.POSITIVE_INFINITY;
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < data[0].length; i++) {
                float v = data[0][i];
                if (v < lowest) {
                    lowest = v;
                }
                if (v > highest) {
                    highest = v;
                }
            }
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        
        return result;
        
    }

    /**
     * Calculates the Y data range.
     *
     * @param data  the data.
     *
     * @return The range.
     */
    private Range calculateYDataRange(float[][] data) {
        
        Range result = null;
        
        if (data != null) {
            float lowest = Float.POSITIVE_INFINITY;
            float highest = Float.NEGATIVE_INFINITY;
            for (int i = 0; i < data[0].length; i++) {
                float v = data[1][i];
                if (v < lowest) {
                    lowest = v;
                }
                if (v > highest) {
                    highest = v;
                }
            }
            if (lowest <= highest) {
                result = new Range(lowest, highest);
            }
        }
        return result;
        
    }

    /**
     * Multiplies the range on the domain axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point.
     */
    public void zoomDomainAxes(double factor, PlotRenderingInfo info, 
                               Point2D source) {
        this.domainAxis.resizeRange(factor);
    }

    /**
     * Zooms in on the domain axes.
     * 
     * @param lowerPercent  the new lower bound as a percentage of the current 
     *                      range.
     * @param upperPercent  the new upper bound as a percentage of the current
     *                      range.
     * @param info  the plot rendering info.
     * @param source  the source point.
     */
    public void zoomDomainAxes(double lowerPercent, double upperPercent, 
                               PlotRenderingInfo info, Point2D source) {
        this.domainAxis.zoomRange(lowerPercent, upperPercent);
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point.
     */
    public void zoomRangeAxes(double factor,
                              PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.resizeRange(factor);
    }

    /**
     * Zooms in on the range axes.
     * 
     * @param lowerPercent  the new lower bound as a percentage of the current 
     *                      range.
     * @param upperPercent  the new upper bound as a percentage of the current 
     *                      range.
     * @param info  the plot rendering info.
     * @param source  the source point.
     */
    public void zoomRangeAxes(double lowerPercent, double upperPercent,
                              PlotRenderingInfo info, Point2D source) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }

    /**
     * Returns <code>true</code>.
     * 
     * @return A boolean.
     */
    public boolean isDomainZoomable() {
        return true;
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
     * Tests an object for equality with this instance.
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
        if (!(obj instanceof FastScatterPlot)) {
            return false;
        }
        FastScatterPlot that = (FastScatterPlot) obj;
        if (!ArrayUtilities.equal(this.data, that.data)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainAxis, that.domainAxis)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeAxis, that.rangeAxis)) {
            return false;
        }
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.domainGridlinePaint, 
                that.domainGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.domainGridlineStroke, 
                that.domainGridlineStroke)) {
            return false;
        }  
        if (!this.rangeGridlinesVisible == that.rangeGridlinesVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.rangeGridlinePaint, 
                that.rangeGridlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.rangeGridlineStroke, 
                that.rangeGridlineStroke)) {
            return false;
        }              
        return true;
    }
    
    /**
     * Returns a clone of the plot.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if some component of the plot does 
     *                                    not support cloning.
     */
    public Object clone() throws CloneNotSupportedException {
    
        FastScatterPlot clone = (FastScatterPlot) super.clone();    
        
        if (this.data != null) {
            clone.data = ArrayUtilities.clone(this.data);    
        }
        
        if (this.domainAxis != null) {
            clone.domainAxis = (ValueAxis) this.domainAxis.clone();
            clone.domainAxis.setPlot(clone);
            clone.domainAxis.addChangeListener(clone);
        }
        
        if (this.rangeAxis != null) {
            clone.rangeAxis = (ValueAxis) this.rangeAxis.clone();
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
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
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
        SerialUtilities.writePaint(this.domainGridlinePaint, stream);
        SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
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

        this.paint = SerialUtilities.readPaint(stream);
        this.domainGridlineStroke = SerialUtilities.readStroke(stream);
        this.domainGridlinePaint = SerialUtilities.readPaint(stream);

        this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
        this.rangeGridlinePaint = SerialUtilities.readPaint(stream);

        if (this.domainAxis != null) {
            this.domainAxis.addChangeListener(this);
        }

        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }
    
}
