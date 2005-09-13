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
 * CandlestickRenderer.java
 * ------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Authors:  David Gilbert (for Object Refinery Limited);
 *                    Sylvain Vieujot;
 * Contributor(s):    Richard Atkinson;
 *                    Christian W. Zuckschwerdt;
 *                    Jerome Fisher;
 *
 * $Id$
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1.  Based on code in the (now redundant) 
 *               CandlestickPlot class, written by Sylvain Vieujot (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem() method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers 
 *               no longer need to be immutable.  Added properties for up and 
 *               down colors (DG);
 * 04-Apr-2002 : Updated with new automatic width calculation and optional 
 *               volume display, contributed by Sylvain Vieujot (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem() method, and 
 *               changed the return type of the drawItem method to void, 
 *               reflecting a change in the XYItemRenderer interface.  Added 
 *               tooltip code to drawItem() method (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML 
 *               image maps (RA);
 * 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 30-Jun-2003 : Added support for PlotOrientation (for completeness, this 
 *               renderer is unlikely to be used with a HORIZONTAL 
 *               orientation) (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 29-Aug-2003 : Moved maxVolume calculation to initialise method (see bug 
 *               report 796619) (DG);
 * 02-Sep-2003 : Added maxCandleWidthInMilliseconds as workaround for bug 
 *               796621 (DG);
 * 08-Sep-2003 : Changed ValueAxis API (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 13-Oct-2003 : Applied patch from Jerome Fisher to improve auto width 
 *               calculations (DG);
 * 23-Dec-2003 : Fixed bug where up and down paint are used incorrectly (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 
 */

package org.jfree.chart.renderer.xy;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws candlesticks on an {@link XYPlot} (requires a 
 * {@link OHLCDataset}).
 * <P>
 * This renderer does not include code to calculate the crosshair point for the 
 * plot.
 *
 * @author Sylvain Vieujot
 */
public class CandlestickRenderer extends AbstractXYItemRenderer 
                                 implements XYItemRenderer, 
                                            Cloneable,
                                            PublicCloneable,
                                            Serializable {
            
    /** For serialization. */
    private static final long serialVersionUID = 50390395841817121L;
    
    /** The average width method. */                                          
    public static final int WIDTHMETHOD_AVERAGE = 0;
    
    /** The smallest width method. */
    public static final int WIDTHMETHOD_SMALLEST = 1;
    
    /** The interval data method. */
    public static final int WIDTHMETHOD_INTERVALDATA = 2;

    /** The method of automatically calculating the candle width. */
    private int autoWidthMethod = WIDTHMETHOD_AVERAGE;

    /** 
     * The number (generally between 0.0 and 1.0) by which the available space 
     * automatically calculated for the candles will be multiplied to determine
     * the actual width to use. 
     */
    private double autoWidthFactor = 4.5 / 7;

    /** The minimum gap between one candle and the next */
    private double autoWidthGap = 0.0;

    /** The candle width. */
    private double candleWidth;
    
    /** The maximum candlewidth in milliseconds. */
    private double maxCandleWidthInMilliseconds = 1000.0 * 60.0 * 60.0 * 20.0;
    
    /** Temporary storage for the maximum candle width. */
    private double maxCandleWidth;

    /** 
     * The paint used to fill the candle when the price moved up from open to 
     * close. 
     */
    private transient Paint upPaint;

    /** 
     * The paint used to fill the candle when the price moved down from open 
     * to close. 
     */
    private transient Paint downPaint;

    /** A flag controlling whether or not volume bars are drawn on the chart. */
    private boolean drawVolume;
    
    /** Temporary storage for the maximum volume. */
    private transient double maxVolume;

    /**
     * Creates a new renderer for candlestick charts.
     */
    public CandlestickRenderer() {
        this(-1.0);
    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated 
     * automatically.
     *
     * @param candleWidth  The candle width.
     */
    public CandlestickRenderer(double candleWidth) {
        this(candleWidth, true, new HighLowItemLabelGenerator());
    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated 
     * automatically.
     *
     * @param candleWidth  the candle width.
     * @param drawVolume  a flag indicating whether or not volume bars should 
     *                    be drawn.
     * @param toolTipGenerator  the tool tip generator. <code>null</code> is 
     *                          none.
     */
    public CandlestickRenderer(double candleWidth, boolean drawVolume,
                               XYToolTipGenerator toolTipGenerator) {

        super();
        setToolTipGenerator(toolTipGenerator);
        this.candleWidth = candleWidth;
        this.drawVolume = drawVolume;
        this.upPaint = Color.green;
        this.downPaint = Color.red;

    }

    /**
     * Returns the width of each candle.
     *
     * @return The candle width.
     * 
     * @see #setCandleWidth(double)
     */
    public double getCandleWidth() {
        return this.candleWidth;
    }

    /**
     * Sets the candle width.
     * <P>
     * If you set the width to a negative value, the renderer will calculate
     * the candle width automatically based on the space available on the chart.
     *
     * @param width  The width.
     * @see #setAutoWidthMethod(int)
     * @see #setAutoWidthGap(double)
     * @see #setAutoWidthFactor(double)
     * @see #setMaxCandleWidthInMilliseconds(double)
     */
    public void setCandleWidth(double width) {
        if (width != this.candleWidth) {
            this.candleWidth = width;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the maximum width (in milliseconds) of each candle.
     *
     * @return The maximum candle width in milliseconds.
     */
    public double getMaxCandleWidthInMilliseconds() {
        return this.maxCandleWidthInMilliseconds;
    }

    /**
     * Sets the maximum candle width (in milliseconds).  
     *
     * @param millis  The maximum width.
     * @see #setCandleWidth(double)
     * @see #setAutoWidthMethod(int)
     * @see #setAutoWidthGap(double)
     * @see #setAutoWidthFactor(double)
     */
    public void setMaxCandleWidthInMilliseconds(double millis) {
        this.maxCandleWidthInMilliseconds = millis;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the method of automatically calculating the candle width.
     *
     * @return The method of automatically calculating the candle width.
     */
    public int getAutoWidthMethod() {
        return this.autoWidthMethod;
    }

    /**
     * Sets the method of automatically calculating the candle width.
     * <p>
     * <code>WIDTHMETHOD_AVERAGE</code>: Divides the entire display (ignoring 
     * scale factor) by the number of items, and uses this as the available 
     * width.<br>
     * <code>WIDTHMETHOD_SMALLEST</code>: Checks the interval between each 
     * item, and uses the smallest as the available width.<br>
     * <code>WIDTHMETHOD_INTERVALDATA</code>: Assumes that the dataset supports
     * the IntervalXYDataset interface, and uses the startXValue - endXValue as 
     * the available width.
     * <br>
     *
     * @param autoWidthMethod  The method of automatically calculating the 
     * candle width.
     *
     * @see #WIDTHMETHOD_AVERAGE
     * @see #WIDTHMETHOD_SMALLEST
     * @see #WIDTHMETHOD_INTERVALDATA
     * @see #setCandleWidth(double)
     * @see #setAutoWidthGap(double)
     * @see #setAutoWidthFactor(double)
     * @see #setMaxCandleWidthInMilliseconds(double)
     */
    public void setAutoWidthMethod(int autoWidthMethod) {
        if (this.autoWidthMethod != autoWidthMethod) {
            this.autoWidthMethod = autoWidthMethod;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the factor by which the available space automatically 
     * calculated for the candles will be multiplied to determine the actual 
     * width to use.
     *
     * @return The width factor (generally between 0.0 and 1.0).
     */
    public double getAutoWidthFactor() {
        return this.autoWidthFactor;
    }

    /**
     * Sets the factor by which the available space automatically calculated 
     * for the candles will be multiplied to determine the actual width to use.
     *
     * @param autoWidthFactor The width factor (generally between 0.0 and 1.0).
     * @see #setCandleWidth(double)
     * @see #setAutoWidthMethod(int)
     * @see #setAutoWidthGap(double)
     * @see #setMaxCandleWidthInMilliseconds(double)
     */
    public void setAutoWidthFactor(double autoWidthFactor) {
        if (this.autoWidthFactor != autoWidthFactor) {
            this.autoWidthFactor = autoWidthFactor;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the amount of space to leave on the left and right of each 
     * candle when automatically calculating widths.
     *
     * @return The gap.
     */
    public double getAutoWidthGap() {
        return this.autoWidthGap;
    }

    /**
     * Sets the amount of space to leave on the left and right of each candle 
     * when automatically calculating widths.
     *
     * @param autoWidthGap The gap.
     * @see #setCandleWidth(double)
     * @see #setAutoWidthMethod(int)
     * @see #setAutoWidthFactor(double)
     * @see #setMaxCandleWidthInMilliseconds(double)
     */
    public void setAutoWidthGap(double autoWidthGap) {
        if (this.autoWidthGap != autoWidthGap) {
            this.autoWidthGap = autoWidthGap;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to fill candles when the price moves up from open
     * to close.
     *
     * @return The paint.
     */
    public Paint getUpPaint() {
        return this.upPaint;
    }

    /**
     * Sets the paint used to fill candles when the price moves up from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.upPaint" property has changed.
     *
     * @param paint The paint.
     */
    public void setUpPaint(Paint paint) {
        this.upPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the paint used to fill candles when the price moves down from
     * open to close.
     *
     * @return The paint.
     */
    public Paint getDownPaint() {
        return this.downPaint;
    }

    /**
     * Sets the paint used to fill candles when the price moves down from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.downPaint" property has changed.
     *
     * @param paint  The paint.
     */
    public void setDownPaint(Paint paint) {
        this.downPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a flag indicating whether or not volume bars are drawn on the
     * chart.
     *
     * @return <code>true</code> if volume bars are drawn on the chart.
     */
    public boolean drawVolume() {
        return this.drawVolume;
    }

    /**
     * Sets a flag that controls whether or not volume bars are drawn in the
     * background.
     *
     * @param flag The flag.
     */
    public void setDrawVolume(boolean flag) {
        if (this.drawVolume != flag) {
            this.drawVolume = flag;
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    /**
     * Initialises the renderer then returns the number of 'passes' through the
     * data that the renderer will require (usually just one).  This method 
     * will be called before the first item is rendered, giving the renderer 
     * an opportunity to initialise any state information it wants to maintain.
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param dataset  the data.
     * @param info  an optional info collection object to return data back to 
     *              the caller.
     *
     * @return The number of passes the renderer requires.
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset dataset,
                                          PlotRenderingInfo info) {
          
        // calculate the maximum allowed candle width from the axis...
        ValueAxis axis = plot.getDomainAxis();
        double x1 = axis.getLowerBound();
        double x2 = x1 + this.maxCandleWidthInMilliseconds;
        RectangleEdge edge = plot.getDomainAxisEdge();
        double xx1 = axis.valueToJava2D(x1, dataArea, edge);
        double xx2 = axis.valueToJava2D(x2, dataArea, edge);
        this.maxCandleWidth = Math.abs(xx2 - xx1); 
            // Absolute value, since the relative x 
            // positions are reversed for horizontal orientation
        
        // calculate the highest volume in the dataset... 
        if (this.drawVolume) {
            OHLCDataset highLowDataset = (OHLCDataset) dataset;
            this.maxVolume = 0.0;
            for (int series = 0; series < highLowDataset.getSeriesCount(); 
                 series++) {
                for (int item = 0; item < highLowDataset.getItemCount(series); 
                     item++) {
                    double volume = highLowDataset.getVolumeValue(series, item);
                    if (volume > this.maxVolume) {
                        this.maxVolume = volume;
                    }
                    
                }    
            }
        }
        
        return new XYItemRendererState(info);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, 
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot, 
                         ValueAxis domainAxis, 
                         ValueAxis rangeAxis,
                         XYDataset dataset, 
                         int series, 
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        boolean horiz;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            horiz = true;
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            horiz = false;
        }
        else {
            return;
        }
        
        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        OHLCDataset highLowData = (OHLCDataset) dataset;

        Number x = highLowData.getX(series, item);
        Number yHigh = highLowData.getHigh(series, item);
        Number yLow = highLowData.getLow(series, item);
        Number yOpen = highLowData.getOpen(series, item);
        Number yClose = highLowData.getClose(series, item);

        RectangleEdge domainEdge = plot.getDomainAxisEdge();
        double xx = domainAxis.valueToJava2D(x.doubleValue(), dataArea, 
                domainEdge);

        RectangleEdge edge = plot.getRangeAxisEdge();
        double yyHigh = rangeAxis.valueToJava2D(yHigh.doubleValue(), dataArea,
                edge);
        double yyLow = rangeAxis.valueToJava2D(yLow.doubleValue(), dataArea, 
                edge);
        double yyOpen = rangeAxis.valueToJava2D(yOpen.doubleValue(), dataArea, 
                edge);
        double yyClose = rangeAxis.valueToJava2D(yClose.doubleValue(), dataArea,
                edge);

        double volumeWidth;
        double stickWidth;
        if (this.candleWidth > 0) {
            // These are deliberately not bounded to minimums/maxCandleWidth to
            //  retain old behaviour.
            volumeWidth = this.candleWidth;
            stickWidth = this.candleWidth;
        }
        else {
            double xxWidth = 0;
            int itemCount;
            switch (this.autoWidthMethod) {
            
                case WIDTHMETHOD_AVERAGE:
                    itemCount = highLowData.getItemCount(series);
                    if (horiz) {
                        xxWidth = dataArea.getHeight() / itemCount;
                    }
                    else {
                        xxWidth = dataArea.getWidth() / itemCount;
                    }
                    break;
            
                case WIDTHMETHOD_SMALLEST:
                    // Note: It would be nice to pre-calculate this per series
                    itemCount = highLowData.getItemCount(series);
                    double lastPos = -1;
                    xxWidth = dataArea.getWidth();
                    for (int i = 0; i < itemCount; i++) {
                        double pos = domainAxis.valueToJava2D(
                            highLowData.getXValue(series, i), dataArea, 
                            domainEdge
                        );
                        if (lastPos != -1) {
                            xxWidth = Math.min(
                                xxWidth, Math.abs(pos - lastPos)
                            );
                        }
                        lastPos = pos;
                    }
                    break;
            
                case WIDTHMETHOD_INTERVALDATA:
                    IntervalXYDataset intervalXYData 
                        = (IntervalXYDataset) dataset;
                    double startPos = domainAxis.valueToJava2D(
                        intervalXYData.getStartXValue(series, item), dataArea, 
                        plot.getDomainAxisEdge()
                    );
                    double endPos = domainAxis.valueToJava2D(
                        intervalXYData.getEndXValue(series, item), dataArea, 
                        plot.getDomainAxisEdge()
                    );
                    xxWidth = Math.abs(endPos - startPos);
                    break;
                
            }
            xxWidth -= 2 * this.autoWidthGap;
            xxWidth *= this.autoWidthFactor;
            xxWidth = Math.min(xxWidth, this.maxCandleWidth);
            volumeWidth = Math.max(Math.min(1, this.maxCandleWidth), xxWidth);
            stickWidth = Math.max(Math.min(3, this.maxCandleWidth), xxWidth);
        }

        Paint p = getItemPaint(series, item);
        Stroke s = getItemStroke(series, item);

        g2.setStroke(s);

        if (this.drawVolume) {
            int volume = (int) highLowData.getVolumeValue(series, item);
            double volumeHeight = volume / this.maxVolume;

            double min, max;
            if (horiz) {
                min = dataArea.getMinX();
                max = dataArea.getMaxX();
            }
            else {
                min = dataArea.getMinY();
                max = dataArea.getMaxY();
            }

            double zzVolume = volumeHeight * (max - min);

            g2.setPaint(Color.gray);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)
            );

            if (horiz) {
                g2.fill(new Rectangle2D.Double(min,
                                               xx - volumeWidth / 2,
                                               zzVolume, volumeWidth));
            }
            else {
                g2.fill(
                    new Rectangle2D.Double(
                        xx - volumeWidth / 2,
                        max - zzVolume, volumeWidth, zzVolume
                    )
                );
            }

            g2.setComposite(originalComposite);
        }

        g2.setPaint(p);

        double yyMaxOpenClose = Math.max(yyOpen, yyClose);
        double yyMinOpenClose = Math.min(yyOpen, yyClose);
        double maxOpenClose = Math.max(yOpen.doubleValue(), 
                yClose.doubleValue());
        double minOpenClose = Math.min(yOpen.doubleValue(), 
                yClose.doubleValue());

        // draw the upper shadow
        if (yHigh.doubleValue() > maxOpenClose) {
            if (horiz) {
                g2.draw(new Line2D.Double(yyHigh, xx, yyMaxOpenClose, xx));
            }
            else {
                g2.draw(new Line2D.Double(xx, yyHigh, xx, yyMaxOpenClose));
            }
        }

        // draw the lower shadow
        if (yLow.doubleValue() < minOpenClose) {
            if (horiz) {
                g2.draw(new Line2D.Double(yyLow, xx, yyMinOpenClose, xx));
            }
            else {
                g2.draw(new Line2D.Double(xx, yyLow, xx, yyMinOpenClose));
            }
        }

        // draw the body
        Shape body = null;
        if (horiz) {
            body = new Rectangle2D.Double(
                yyMinOpenClose, xx - stickWidth / 2, 
                yyMaxOpenClose - yyMinOpenClose, stickWidth
            );
        } 
        else {
            body = new Rectangle2D.Double(
                xx - stickWidth / 2, yyMinOpenClose,
                stickWidth, yyMaxOpenClose - yyMinOpenClose
            );
        }
        if (yClose.doubleValue() > yOpen.doubleValue()) {
            if (this.upPaint != null) {
                g2.setPaint(this.upPaint);
                g2.fill(body);
            }
        }
        else {
            if (this.downPaint != null) {
                g2.setPaint(this.downPaint);
            }
            g2.fill(body);
        }
        g2.setPaint(p);
        g2.draw(body);

        // add an entity for the item...
        if (entities != null) {
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(body, dataset, series, item, 
                    tip, url);
            entities.add(entity);
        }

    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof CandlestickRenderer) {
            CandlestickRenderer renderer = (CandlestickRenderer) obj;
            boolean result = super.equals(obj);
            result = result && (this.candleWidth == renderer.getCandleWidth());
            result = result && (this.upPaint.equals(renderer.getUpPaint()));
            result = result && (this.downPaint.equals(renderer.getDownPaint()));
            result = result && (this.drawVolume == renderer.drawVolume);
            return result;
        }

        return false;

    }

    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        SerialUtilities.writePaint(this.upPaint, stream);
        SerialUtilities.writePaint(this.downPaint, stream);
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
        this.upPaint = SerialUtilities.readPaint(stream);
        this.downPaint = SerialUtilities.readPaint(stream);
    }
    
}
