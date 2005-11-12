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
 * --------------------------
 * DefaultHighLowDataset.java
 * --------------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 21-Mar-2002 : Version 1 (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 06-May-2004 : Now extends AbstractXYDataset and added new methods from 
 *               HighLowDataset (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 *
 */

package org.jfree.data.xy;

import java.util.Date;

/**
 * A simple implementation of the {@link OHLCDataset}.
 */
public class DefaultHighLowDataset extends AbstractXYDataset 
                                   implements OHLCDataset {

    /** The series key. */
    private Comparable seriesKey;

    /** Storage for the dates. */
    private Date[] date;

    /** Storage for the high values. */
    private Number[] high;

    /** Storage for the low values. */
    private Number[] low;

    /** Storage for the open values. */
    private Number[] open;

    /** Storage for the close values. */
    private Number[] close;

    /** Storage for the volume values. */
    private Number[] volume;

    /**
     * Constructs a new high/low/open/close dataset.
     * <p>
     * The current implementation allows only one series in the dataset.
     * This may be extended in a future version.
     *
     * @param seriesKey  the key for the series.
     * @param date  the dates.
     * @param high  the high values.
     * @param low  the low values.
     * @param open  the open values.
     * @param close  the close values.
     * @param volume  the volume values.
     */
    public DefaultHighLowDataset(Comparable seriesKey,
                                 Date[] date,
                                 double[] high, final double[] low,
                                 double[] open, final double[] close,
                                 double[] volume) {

        this.seriesKey = seriesKey;
        this.date = date;
        this.high = createNumberArray(high);
        this.low = createNumberArray(low);
        this.open = createNumberArray(open);
        this.close = createNumberArray(close);
        this.volume = createNumberArray(volume);

    }

    /**
     * Returns the for the series stored in this dataset.
     *
     * @param i  the index of the series. Currently ignored.
     * 
     * @return The key for this series.
     */
    public Comparable getSeriesKey(int i) {
        return this.seriesKey;
    }

    /**
     * Returns the x-value for one item in a series.  The value returned is a 
     * <code>Long</code> instance generated from the underlying 
     * <code>Date</code> object.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The x-value.
     */
    public Number getX(int series, int item) {
        return new Long(this.date[item].getTime());
    }

    /**
     * Returns the x-value for one item in a series, as a Date.
     * <p>
     * This method is provided for convenience only.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The x-value as a Date.
     */
    public Date getXDate(int series, int item) {
        return this.date[item];
    }

    /**
     * Returns the y-value for one item in a series.
     * <p>
     * This method (from the {@link XYDataset} interface) is mapped to the 
     * {@link #getCloseValue(int, int)} method.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The y-value.
     */
    public Number getY(int series, int item) {
        return getClose(series, item);
    }

    /**
     * Returns the high-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The high-value.
     */
    public Number getHigh(int series, int item) {
        return this.high[item];
    }

    /**
     * Returns the high-value (as a double primitive) for an item within a 
     * series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The high-value.
     */
    public double getHighValue(int series, int item) {
        double result = Double.NaN;
        Number high = getHigh(series, item);
        if (high != null) {
            result = high.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the low-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The low-value.
     */
    public Number getLow(int series, int item) {
        return this.low[item];
    }

    /**
     * Returns the low-value (as a double primitive) for an item within a 
     * series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The low-value.
     */
    public double getLowValue(int series, int item) {
        double result = Double.NaN;
        Number low = getLow(series, item);
        if (low != null) {
            result = low.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the open-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The open-value.
     */
    public Number getOpen(int series, int item) {
        return this.open[item];
    }

    /**
     * Returns the open-value (as a double primitive) for an item within a 
     * series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The open-value.
     */
    public double getOpenValue(int series, int item) {
        double result = Double.NaN;
        Number open = getOpen(series, item);
        if (open != null) {
            result = open.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the close-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The close-value.
     */
    public Number getClose(int series, int item) {
        return this.close[item];
    }

    /**
     * Returns the close-value (as a double primitive) for an item within a 
     * series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The close-value.
     */
    public double getCloseValue(int series, int item) {
        double result = Double.NaN;
        Number close = getClose(series, item);
        if (close != null) {
            result = close.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the volume-value for one item in a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The volume-value.
     */
    public Number getVolume(int series, int item) {
        return this.volume[item];
    }

    /**
     * Returns the volume-value (as a double primitive) for an item within a 
     * series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The volume-value.
     */
    public double getVolumeValue(int series, int item) {
        double result = Double.NaN;
        Number volume = getVolume(series, item);
        if (volume != null) {
            result = volume.doubleValue();   
        }
        return result;   
    }

    /**
     * Returns the number of series in the dataset.
     * <p>
     * This implementation only allows one series.
     *
     * @return The number of series.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the index (zero-based) of the series.
     *
     * @return The number of items in the specified series.
     */
    public int getItemCount(int series) {
        return this.date.length;
    }

    /**
     * Constructs an array of Number objects from an array of doubles.
     *
     * @param data  the double values to convert.
     *
     * @return The data as an array of Number objects.
     */
    public static Number[] createNumberArray(double[] data) {
        Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }
        return result;
    }

}
