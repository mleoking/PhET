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
 * ------------------
 * XYBarRenderer.java
 * ------------------
 * (C) Copyright 2001-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *                   Bill Kelemen;
 *
 * $Id$
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1, makes VerticalXYBarPlot class redundant (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem() method (DG);
 * 09-Apr-2002 : Removed the translated zero from the drawItem method. Override 
 *               the initialise() method to calculate it (DG);
 * 24-May-2002 : Incorporated tooltips into chart entities (DG);
 * 25-Jun-2002 : Removed redundant import (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML 
 *               image maps (RA);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 01-May-2003 : Modified drawItem() method signature (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 24-Aug-2003 : Added null checks in drawItem (BK);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 07-Oct-2003 : Added renderer state (DG);
 * 05-Dec-2003 : Changed call to obtain outline paint (DG);
 * 10-Feb-2004 : Added state class, updated drawItem() method to make 
 *               cut-and-paste overriding easier, and replaced property change 
 *               with RendererChangeEvent (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 26-Apr-2004 : Added gradient paint transformer (DG);
 * 19-May-2004 : Fixed bug (879709) with bar zero value for secondary axis (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 01-Sep-2004 : Added a flag to control whether or not the bar outlines are 
 *               drawn (DG);
 * 03-Sep-2004 : Added option to use y-interval from dataset to determine the 
 *               length of the bars (DG);
 * 08-Sep-2004 : Added equals() method and updated clone() method (DG);
 * 26-Jan-2005 : Added override for getLegendItem() method (DG);
 * 20-Apr-2005 : Use generators for label tooltips and URLs (DG);
 * 19-May-2005 : Added minimal item label implementation - needs improving (DG);
 * 14-Oct-2005 : Fixed rendering problem with inverted axes (DG);
 * 
 */

package org.jfree.chart.renderer.xy;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer that draws bars on an {@link XYPlot} (requires an 
 * {@link IntervalXYDataset}).
 * <P>
 * This renderer does not include any code for calculating the crosshair point.
 */
public class XYBarRenderer extends AbstractXYItemRenderer 
                           implements XYItemRenderer,
                                      Cloneable,
                                      PublicCloneable,
                                      Serializable {
    
    /** For serialization. */
    private static final long serialVersionUID = 770559577251370036L;

    /**
     * The state class used by this renderer.
     */
    protected class XYBarRendererState extends XYItemRendererState {
        
        /** Base for bars against the range axis, in Java 2D space. */
        private double g2Base;
        
        /**
         * Creates a new state object.
         * 
         * @param info  the plot rendering info.
         */
        public XYBarRendererState(PlotRenderingInfo info) {
            super(info);
        }
        
        /**
         * Returns the base (range) value in Java 2D space.
         * 
         * @return The base value.
         */
        public double getG2Base() {
            return this.g2Base;
        }
        
        /**
         * Sets the range axis base in Java2D space.
         * 
         * @param value  the value.
         */
        public void setG2Base(double value) {
            this.g2Base = value;
        }
    }

    /** The default base value for the bars. */
    private double base;
    
    /** 
     * A flag that controls whether the bars use the y-interval supplied by the 
     * dataset. 
     */
    private boolean useYInterval;
    
    /** Percentage margin (to reduce the width of bars). */
    private double margin;

    /** A flag that controls whether or not bar outlines are drawn. */
    private boolean drawBarOutline;
    
    /** 
     * An optional class used to transform gradient paint objects to fit each 
     * bar. 
     */
    private GradientPaintTransformer gradientPaintTransformer; 
    
    /** 
     * The shape used to represent a bar in each legend item (this should never
     * be <code>null</code>). 
     */
    private transient Shape legendBar;
    
    /**
     * The default constructor.
     */
    public XYBarRenderer() {
        this(0.0);
    }

    /**
     * Constructs a new renderer.
     *
     * @param margin  the percentage amount to trim from the width of each bar.
     */
    public XYBarRenderer(double margin) {
        super();
        this.margin = margin;
        this.base = 0.0;
        this.useYInterval = false;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer(); 
        this.drawBarOutline = true;
        this.legendBar = new Rectangle2D.Double(-3.0, -5.0, 6.0, 10.0);
    }
    
    /**
     * Returns the base value for the bars.
     * 
     * @return The base value for the bars.
     */
    public double getBase() {
        return this.base;    
    }
    
    /**
     * Sets the base value for the bars and sends a {@link RendererChangeEvent}
     * to all registered listeners.  The base value is not used if the dataset's
     * y-interval is being used to determine the bar length.
     * 
     * @param base  the new base value.
     */
    public void setBase(double base) {
        this.base = base;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns a flag that determines whether the y-interval from the dataset is
     * used to calculate the length of each bar.
     * 
     * @return A boolean.
     */
    public boolean getUseYInterval() {
        return this.useYInterval;
    }
    
    /**
     * Sets the flag that determines whether the y-interval from the dataset is
     * used to calculate the length of each bar, and sends a 
     * {@link RendererChangeEvent} to all registered listeners..
     * 
     * @param use  the flag.
     */
    public void setUseYInterval(boolean use) {
        this.useYInterval = use;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the margin which is a percentage amount by which the bars are 
     * trimmed.
     *
     * @return The margin.
     */
    public double getMargin() {
        return this.margin;
    }
    
    /**
     * Sets the percentage amount by which the bars are trimmed and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param margin  the new margin.
     */
    public void setMargin(double margin) {
        this.margin = margin;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not bar outlines are drawn.
     * 
     * @return A boolean.
     */
    public boolean isDrawBarOutline() {
        return this.drawBarOutline;    
    }
    
    /**
     * Sets the flag that controls whether or not bar outlines are drawn and 
     * sends a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param draw  the flag.
     */
    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the gradient paint transformer (an object used to transform 
     * gradient paint objects to fit each bar.
     * 
     * @return A transformer (<code>null</code> possible).
     */    
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;    
    }
    
    /**
     * Sets the gradient paint transformer and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param transformer  the transformer (<code>null</code> permitted).
     */
    public void setGradientPaintTransformer(
            GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        notifyListeners(new RendererChangeEvent(this));
    }
     
    /**
     * Returns the shape used to represent bars in each legend item.
     * 
     * @return The shape used to represent bars in each legend item (never 
     *         <code>null</code>).
     */
    public Shape getLegendBar() {
        return this.legendBar;
    }
    
    /**
     * Sets the shape used to represent bars in each legend item.
     * 
     * @param bar  the bar shape (<code>null</code> not permitted).
     */
    public void setLegendBar(Shape bar) {
        if (bar == null) {
            throw new IllegalArgumentException("Null 'bar' argument.");
        }
        this.legendBar = bar;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Initialises the renderer and returns a state object that should be 
     * passed to all subsequent calls to the drawItem() method.  Here we 
     * calculate the Java2D y-coordinate for zero, since all the bars have 
     * their bases fixed at zero.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param dataset  the data.
     * @param info  an optional info collection object to return data back to 
     *              the caller.
     *
     * @return A state object.
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset dataset,
                                          PlotRenderingInfo info) {

        XYBarRendererState state = new XYBarRendererState(info);
        ValueAxis rangeAxis 
            = plot.getRangeAxisForDataset(plot.indexOf(dataset));
        state.setG2Base(
            rangeAxis.valueToJava2D(
                this.base, dataArea, plot.getRangeAxisEdge()
            )
        );
        return state;

    }

    /**
     * Returns a default legend item for the specified series.  Subclasses 
     * should override this method to generate customised items.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                XYSeriesLabelGenerator lg = getLegendItemLabelGenerator();
                String label = lg.generateLabel(dataset, series);
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
                Shape shape = this.legendBar;
                Paint paint = getSeriesPaint(series);
                Paint outlinePaint = getSeriesOutlinePaint(series);
                Stroke outlineStroke = getSeriesOutlineStroke(series);
                result = new LegendItem(label, description, toolTipText, 
                        urlText, shape, paint, outlineStroke, outlinePaint);
            }
        }
        return result;
    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
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

        if (!getItemVisible(series, item)) {
            return;   
        }
        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;

        double value0;
        double value1;
        if (this.useYInterval) {
            value0 = intervalDataset.getStartYValue(series, item);
            value1 = intervalDataset.getEndYValue(series, item);
        }
        else {
            value0 = this.base;
            value1 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(value0) || Double.isNaN(value1)) {
            return;
        }

        double translatedValue0 = rangeAxis.valueToJava2D(
            value0, dataArea, plot.getRangeAxisEdge()
        );
        double translatedValue1 = rangeAxis.valueToJava2D(
            value1, dataArea, plot.getRangeAxisEdge()
        );

        RectangleEdge location = plot.getDomainAxisEdge();
        Number startXNumber = intervalDataset.getStartX(series, item);
        if (startXNumber == null) {
            return;
        }
        double translatedStartX = domainAxis.valueToJava2D(
            startXNumber.doubleValue(), dataArea, location
        );

        Number endXNumber = intervalDataset.getEndX(series, item);
        if (endXNumber == null) {
            return;
        }
        double translatedEndX = domainAxis.valueToJava2D(
            endXNumber.doubleValue(), dataArea, location
        );

        double translatedWidth = Math.max(
            1, Math.abs(translatedEndX - translatedStartX)
        );
        double translatedHeight = Math.abs(translatedValue1 - translatedValue0);

        if (getMargin() > 0.0) {
            double cut = translatedWidth * getMargin();
            translatedWidth = translatedWidth - cut;
            translatedStartX = translatedStartX + cut / 2;
        }

        Rectangle2D bar = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(
                Math.min(translatedValue0, translatedValue1), 
                Math.min(translatedStartX, translatedEndX),
                translatedHeight, translatedWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(
                Math.min(translatedStartX, translatedEndX), 
                Math.min(translatedValue0, translatedValue1), 
                translatedWidth, translatedHeight);
        }

        Paint itemPaint = getItemPaint(series, item);
        if (getGradientPaintTransformer() 
                != null && itemPaint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) itemPaint;
            itemPaint = getGradientPaintTransformer().transform(gp, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);
        if (isDrawBarOutline() 
                && Math.abs(translatedEndX - translatedStartX) > 3) {
            Stroke stroke = getItemOutlineStroke(series, item);
            Paint paint = getItemOutlinePaint(series, item);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);                
            }
        }
        
        // TODO: we need something better for the item labels
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(
                g2, orientation, dataset, series, item, bar.getCenterX(), 
                bar.getY(), value1 < 0.0
            );
        }

        // add an entity for the item...
        if (info != null) {
            EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                XYToolTipGenerator generator 
                    = getToolTipGenerator(series, item);
                if (generator != null) {
                    tip = generator.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(dataset, series, item);
                }
                XYItemEntity entity = new XYItemEntity(
                    bar, dataset, series, item, tip, url
                );
                entities.add(entity);
            }
        }

    }

    /**
     * Returns the lower and upper bounds (range) of the x-values in the 
     * specified dataset.  Since this renderer uses the x-interval in the 
     * dataset, this is taken into account for the range.
     * 
     * @param dataset  the dataset (<code>null</code> permitted).
     * 
     * @return The range (<code>null</code> if the dataset is 
     *         <code>null</code> or empty).
     */
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findDomainBounds(dataset, true);
        }
        else {
            return null;
        }
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        XYBarRenderer result = (XYBarRenderer) super.clone();
        if (this.gradientPaintTransformer != null) {
            result.gradientPaintTransformer = (GradientPaintTransformer)
                ObjectUtilities.clone(this.gradientPaintTransformer);
        }
        return result;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     * 
     * @param obj  the object to test against (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYBarRenderer that = (XYBarRenderer) obj;
        if (this.base != that.base) {
            return false;
        }
        if (this.drawBarOutline != that.drawBarOutline) {
            return false;
        }
        if (this.margin != that.margin) {
            return false;
        }
        if (this.useYInterval != that.useYInterval) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.gradientPaintTransformer, that.gradientPaintTransformer)
        ) {
            return false;
        }
        if (!ShapeUtilities.equal(this.legendBar, that.legendBar)) {
            return false;   
        }
        return true;
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
        this.legendBar = SerialUtilities.readShape(stream);
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
        SerialUtilities.writeShape(this.legendBar, stream);
    }

}
