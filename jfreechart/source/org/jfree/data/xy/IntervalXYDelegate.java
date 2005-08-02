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
 * -----------------------
 * IntervalXYDelegate.java
 * -----------------------
 * (C) Copyright 2004, 2005, by Andreas Schroeder and Contributors.
 *
 * Original Author:  Andreas Schroeder;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes (from 31-Mar-2004)
 * --------------------------
 * 31-Mar-2004 : Version 1 (AS);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 18-Aug-2004 : Moved from org.jfree.data --> org.jfree.data.xy (DG);
 * 04-Nov-2004 : Added argument check for setIntervalWidth() method (DG);
 * 17-Nov-2004 : New methods to reflect changes in DomainInfo (DG);
 * 11-Jan-2005 : Removed deprecated methods in preparation for the 1.0.0 
 *               release (DG);
 * 21-Feb-2005 : Made public and added equals() method (DG);
 * 
 */

package org.jfree.data.xy;

import java.io.Serializable;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A class for delegating xy-interval issues to. 
 * Enhances a XYDataset to an XYIntervalDataset. The decorator pattern
 * was not used because of the several possibly implemented interfaces of 
 * the decorated instance (e.g. TableXYDataset, RangeInfo, DomainInfo etc.).
 * <p>
 * This class calculates the minimal interval width between two items. This 
 * width influences the width of bars displayed with this dataset. 
 * <p>
 * The width can be set manually or calculated automatically. The switch
 * autoWidth allows to determine which behavior is used. The behavior is 
 * transparent: The width is always calculated automatically in the background 
 * without affecting the manually set width. The switch simply determines which 
 * value is used. <br> As default manually set width, 1.0 is used. <br> If there
 * is only one item in the series, the auto width calculation fails and falls 
 * back on the manually set interval width (which is itself defaulted to 1.0). 
 * 
 * @author andreas.schroeder
 */
public class IntervalXYDelegate implements DomainInfo, Serializable, 
                                           Cloneable, PublicCloneable {
    
    /** For serialization. */
    private static final long serialVersionUID = -685166711639592857L;
    
    /**
     * The dataset to enhance. 
     */
    private XYDataset dataset;

    /**
     * A flag to indicate whether the width should be calculated automatically.
     */
    private boolean autoWidth;
    
    /**
     * A factor that determines the position of the gap between two bars - only
     * relevant if the data is dispalyed with a bar renderer.
     */
    private double intervalPositionFactor; 
    
    /**
     * The manually set interval width.
     */
    private double intervalWidth;
    
    /**
     * The automatically calculated interval width.
     */
    private double autoIntervalWidth;
    
    /**
     * The lower value of the interval. Only used for autoWidth.
     */
    private double lowerBound;
    
    /**
     * The upper value of the interval. Only used for autoWidth.
     */
    private double upperBound;
    
    /**
     * Creates an XYIntervalDelegate.
     * 
     * @param dataset the dataset for which this interval delegate works.
     */
    public IntervalXYDelegate(XYDataset dataset) {
        this(dataset, true);
    }
    
    /**
     * Creates a new delegate for the specified dataset.
     * 
     * @param dataset  the dataset for which this interval delegate works.
     * @param autoWidth  a flag that controls whether the interval width is 
     *                   calculated automatically.
     */
    public IntervalXYDelegate(XYDataset dataset, boolean autoWidth) {
        this.autoWidth = autoWidth;
        this.dataset = dataset;
        this.intervalPositionFactor = 0.5;
        this.autoWidth = autoWidth;
        this.autoIntervalWidth = Double.POSITIVE_INFINITY; 
        this.intervalWidth = 1.0;
    }
    
    /**
     * Returns whether the interval width is automatically calculated or not.
     * 
     * @return Whether the width is automatically calculated or not.
     */
    public boolean isAutoWidth() {
        return this.autoWidth;
    }
    
    /**
     * Sets the flag that indicates whether the interval width is automatically
     * calculated or not. 
     * 
     * @param b  a boolean.
     */
    public void setAutoWidth(boolean b) {
        this.autoWidth = b;
    }
    
    /**
     * Returns the interval position factor. 
     * 
     * @return The interval position factor.
     */
    public double getIntervalPositionFactor() {
        return this.intervalPositionFactor;
    }

    /**
     * Sets the interval position factor. Must be between 0.0 and 1.0 inclusive.
     * If the factor is 0.5, the gap is in the middle of the x values. If it
     * is lesser than 0.5, the gap is farther to the left and if greater than
     * 0.5 it gets farther to the right.
     *  
     * @param d  the new interval position factor (in the range 
     *           <code>0.0</code> to <code>1.0</code> inclusive).
     */
    public void setIntervalPositionFactor(double d) {
        if (d < 0.0 || 1.0 < d) {
            throw new IllegalArgumentException(
                "Argument 'd' outside valid range."
            );
        }
        this.intervalPositionFactor = d;
    }

    /**
     * Sets the manual interval width. 
     * 
     * @param w  the width (negative values not permitted).
     */
    public void setIntervalWidth(double w) {
        if (w < 0.0) {
            throw new IllegalArgumentException("Negative 'w' argument.");
        }
        this.intervalWidth = w;
    }
    
    /**
     * Returns the full interval width. For behavior of this method, see
     * the class comments. 
     * 
     * @return The interval width to use.
     */
    public double getIntervalWidth() {
        if (isAutoWidth() && !Double.isInfinite(this.autoIntervalWidth)) {
            // everything is fine: autoWidth is on, and an autoIntervalWidth 
            // was set.
            return this.autoIntervalWidth;
        }
        else {
            // either autoWidth is off or autoIntervalWidth was not set.
            return this.intervalWidth;
        }
    }

    /**
     * Returns the start x value based on the intervalWidth and the 
     * intervalPositionFactor.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The start value based on the intervalWidth and the 
     *         intervalPositionFactor.
     */
    public Number getStartX(int series, int item) {
        Number startX = null;
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            startX = new Double(x.doubleValue() 
                     - (getIntervalPositionFactor() * getIntervalWidth())); 
        }
        return startX;
    }
    
    
    /**
     * Returns the end x value based on the intervalWidth and the 
     * intervalPositionFactor.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The end value based on the intervalWidth and the 
     *         intervalPositionFactor.
     */
    public Number getEndX(int series, int item) {
        Number endX = null;
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            endX = new Double(
                x.doubleValue() 
                + ((1.0 - getIntervalPositionFactor()) * getIntervalWidth())
            ); 
        }
        return endX;
    }

    /**
     * Returns the minimum x-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         x-interval is taken into account.
     * 
     * @return The minimum value.
     */
    public double getDomainLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getLowerBound();
        }
        return result;
    }

    /**
     * Returns the maximum x-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether or not the
     *                         x-interval is taken into account.
     * 
     * @return The maximum value.
     */
    public double getDomainUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getUpperBound();
        }
        return result;
    }

    /**
     * Returns the range of the values in this dataset's domain.
     *
     * @param includeInterval  a flag that determines whether or not the 
     *                         x-interval should be taken into account.
     * 
     * @return The range.
     */
    public Range getDomainBounds(boolean includeInterval) {
        Range range = DatasetUtilities.iterateDomainBounds(
            this.dataset, includeInterval
        );
        if (this.dataset.getSeriesCount() == 1 
            && this.dataset.getItemCount(0) == 1) {
            /* if there is only one interval value, so add some space to the 
             * left and the right - otherwise one bar looks like a background 
             * coloration.
             */
            range = new Range(
                range.getLowerBound() - getIntervalWidth(), 
                range.getUpperBound() + getIntervalWidth()
            );
        }
        return range;
    }
    
    /**
     * Updates the interval width if an item is added.  That is, relaxes the 
     * interval width to the minimum of the actual interval width, the 
     * distance between the actual x value and the previous x value and the 
     * distance between the next x value and the actual x value. 
     * 
     * @param item the number of the item.
     * @param series the number of the series
     * 
     */
    public void itemAdded(int series, int item) {
        double x = this.dataset.getXValue(series, item);
        
        if (item > 0) {
            double before = this.dataset.getXValue(series, item - 1);
            double delta = x - before;
            if (delta < this.autoIntervalWidth) {
                this.autoIntervalWidth = delta;
                this.lowerBound = before;
                this.upperBound = x;
            }
        }
        
        if (item + 1 < this.dataset.getItemCount(series)) {
            double after = this.dataset.getXValue(series, item + 1);
            double delta = after - x;
            if (delta < this.autoIntervalWidth) {
                this.autoIntervalWidth = delta;
                this.lowerBound = x;
                this.upperBound = after;
            }
        }
    }
    
    /**
     * Updates the interval width if an item is removed. That is, enlarges the
     * interval width to the new interval minimum if the removed value was
     * part of the minimum. For performance reason this method should be called 
     * only if the x value was definitely removed from the series.
     *  
     * @param x the x value of the removed item (that doesn't occur twice)
     */
    public void itemRemoved(double x) {
        if (x == this.lowerBound || x == this.upperBound) {
            recalculateIntervalWidth();
        }
    }
    
    /**
     * Recalculate the minimum width "from scratch".
     */
    private void recalculateIntervalWidth() {
        this.autoIntervalWidth = Double.POSITIVE_INFINITY;
        
        for (int series = 0, seriesCount = this.dataset.getSeriesCount(); 
            series < seriesCount; series++) {
          
            calculateSeries(series);
        }
    }
    
    /**
     * Calculates the interval width for a given series.
     *  
     * @param series  the series index.
     */
    private void calculateSeries(int series) {
        int totalCount = this.dataset.getItemCount(series);
        for (int item = 1, itemCount = totalCount; item < itemCount; item++) {
                
            double lower = this.dataset.getXValue(series, item - 1);
            double upper = this.dataset.getXValue(series, item);
            double delta = upper - lower;
                
            if (delta < this.autoIntervalWidth) {
                this.autoIntervalWidth = delta;
                this.lowerBound = lower;
                this.upperBound = upper;
            }
        }
    }
    
    /**
     * Convenience method for XYSeriesCollection.
     * 
     * @param series  the series index.
     */
    public void seriesAdded(int series) {
        calculateSeries(series);
    }
    
    /**
     * A convenience method for {@link XYSeriesCollection} which is called 
     * whenever a series is removed - the interval width is recalculated.
     */
    public void seriesRemoved() {
        recalculateIntervalWidth();
    }
    
    /**
     * Tests the delegate for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof IntervalXYDelegate)) {
            return false;   
        }
        IntervalXYDelegate that = (IntervalXYDelegate) obj;
        if (this.autoWidth != that.autoWidth) {
            return false;   
        }
        if (this.intervalPositionFactor != that.intervalPositionFactor) {
            return false;   
        }
        if (this.intervalWidth != that.intervalWidth) {
            return false;   
        }
        return true;
    }
    
    /**
     * @return A clone of this delegate.
     * 
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
