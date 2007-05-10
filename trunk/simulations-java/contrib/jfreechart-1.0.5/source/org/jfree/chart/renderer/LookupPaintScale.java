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
 * ---------------------
 * LookupPaintScale.java
 * ---------------------
 * (C) Copyright 2006, 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LookupPaintScale.java,v 1.1.2.3 2007/03/09 15:59:21 mungady Exp $
 *
 * Changes
 * -------
 * 05-Jul-2006 : Version 1 (DG);
 * 31-Jan-2007 : Fixed serialization support (DG);
 * 09-Mar-2007 : Fixed cloning (DG);
 * 
 */

package org.jfree.chart.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jfree.io.SerialUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;

/**
 * A paint scale that uses a lookup table to associate paint instances
 * with data value ranges.
 * 
 * @since 1.0.4
 */
public class LookupPaintScale 
        implements PaintScale, PublicCloneable, Serializable {

    /**
     * Stores the paint for a value.
     */
    class PaintItem implements Comparable, Serializable {
        
        /** The value. */
        Number value;
        
        /** The paint. */
        transient Paint paint;
        
        /**
         * Creates a new instance.
         * 
         * @param value  the value.
         * @param paint  the paint.
         */
        public PaintItem(Number value, Paint paint) {
            this.value = value;
            this.paint = paint;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object obj) {
            PaintItem that = (PaintItem) obj;
            double d1 = this.value.doubleValue();
            double d2 = that.value.doubleValue();
            if (d1 > d2) {
                return 1;
            }
            if (d1 < d2) {
                return -1;
            }
            return 0;
        }

        /**
         * Tests this item for equality with an arbitrary object.
         * 
         * @param obj  the object (<code>null</code> permitted).
         * 
         * @return A boolean.
         */
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof PaintItem)) {
                return false;
            }
            PaintItem that = (PaintItem) obj;
            if (!this.value.equals(that.value)) {
                return false;
            }
            if (!PaintUtilities.equal(this.paint, that.paint)) {
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
            SerialUtilities.writePaint(this.paint, stream);
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
        }
        
    }
    
    /** The lower bound. */
    private double lowerBound;
    
    /** The upper bound. */
    private double upperBound;
    
    /** The default paint. */
    private transient Paint defaultPaint; 
    
    /** The lookup table. */
    private List lookupTable;
    
    /**
     * Creates a new paint scale.
     */
    public LookupPaintScale() {
        this(0.0, 1.0, Color.lightGray);    
    }
    
    /**
     * Creates a new paint scale with the specified default paint.
     * 
     * @param lowerBound  the lower bound.
     * @param upperBound  the upper bound.
     * @param defaultPaint  the default paint (<code>null</code> not 
     *     permitted).
     */
    public LookupPaintScale(double lowerBound, double upperBound, 
            Paint defaultPaint) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException(
                    "Requires lowerBound < upperBound.");
        }
        if (defaultPaint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.defaultPaint = defaultPaint;
        this.lookupTable = new java.util.ArrayList();
    }
    
    /**
     * Returns the default paint (never <code>null</code>).
     * 
     * @return The default paint.
     */
    public Paint getDefaultPaint() {
        return this.defaultPaint;
    }
    
    /**
     * Returns the lower bound.
     * 
     * @return The lower bound.
     * 
     * @see #getUpperBound()
     */
    public double getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Returns the upper bound.
     * 
     * @return The upper bound.
     * 
     * @see #getLowerBound()
     */
    public double getUpperBound() {
        return this.upperBound;
    }

    /**
     * Adds an entry to the lookup table.  Any values from <code>n</code> up
     * to but not including the next value in the table take on the specified
     * <code>paint</code>.
     * 
     * @param value  the data value.
     * @param paint  the paint.
     */
    public void add(Number value, Paint paint) {
        PaintItem item = new PaintItem(value, paint);
        int index = Collections.binarySearch(this.lookupTable, item);
        if (index >= 0) {
            this.lookupTable.set(index, item);
        }
        else {
            this.lookupTable.add(-(index + 1), item);
        }
    }
    
    /**
     * Returns the paint associated with the specified value.
     * 
     * @param value  the value.
     * 
     * @return The paint.
     * 
     * @see #getDefaultPaint()
     */
    public Paint getPaint(double value) {
        
        // handle value outside bounds...
        if (value < this.lowerBound) {
            return this.defaultPaint;
        }
        if (value > this.upperBound) {
            return this.defaultPaint;
        }

        // for value in bounds, do the lookup...
        Paint result = this.defaultPaint;
        int index = this.lookupTable.size();
        boolean done = false;
        while (index > 0 && !done) {
            PaintItem item = (PaintItem) this.lookupTable.get(--index);
            if (value >= item.value.doubleValue()) {
                result = item.paint;
                done = true;
            }
        }
        return result;
    }
    
    /**
     * Tests this instance for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LookupPaintScale)) {
            return false;
        }
        LookupPaintScale that = (LookupPaintScale) obj;
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (!PaintUtilities.equal(this.defaultPaint, that.defaultPaint)) {
            return false;
        }
        if (!this.lookupTable.equals(that.lookupTable)) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a clone of the instance.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem cloning the
     *     instance.
     */
    public Object clone() throws CloneNotSupportedException {
        LookupPaintScale clone = (LookupPaintScale) super.clone();
        clone.lookupTable = new java.util.ArrayList(this.lookupTable);
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
        SerialUtilities.writePaint(this.defaultPaint, stream);
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
        this.defaultPaint = SerialUtilities.readPaint(stream);
    }

}
