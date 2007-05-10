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
 * -----------
 * Vector.java
 * -----------
 * (C) Copyright 2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: Vector.java,v 1.1.2.1 2007/01/30 16:29:10 mungady Exp $
 *
 * Changes
 * -------
 * 30-Jan-2007 : Version 1 (DG);
 *
 */

package org.jfree.experimental.data.xy;

import java.io.Serializable;

/**
 * A vector.
 */
public class Vector implements Serializable {
    
    /** The vector x. */
    private double x;
    
    /** The vector y. */
    private double y;
    
    /** 
     * Creates a new instance of <code>Vector</code>.
     *
     * @param x  the x-component.
     * @param y  the y-component.  
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns the x-value.
     * 
     * @return The x-value.
     */
    public double getX() {
        return this.x;
    }
    
    /**
     * Returns the y-value.
     * 
     * @return The y-value.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Tests this vector for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> not permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Vector)) {
            return false;
        }
        Vector that = (Vector) obj;
        if (this.x != that.x) {
            return false;
        }
        if (this.y != that.y) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this instance.
     * 
     * @return A hash code.
     */
    public int hashCode() {
        int result = 193;
        long temp = Double.doubleToLongBits(this.x);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y);
        result = 37 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
}
