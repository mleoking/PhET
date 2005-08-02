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
 * ---------------------------------
 * NormalDistributionFunction2D.java
 * ---------------------------------
 * (C)opyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 25-May-2004 : Version 1(DG);
 * 
 */

package org.jfree.data.function;

/**
 * A normal distribution function.
 */
public class NormalDistributionFunction2D implements Function2D {

    /** The mean. */
    private double mean;

    /** The standard deviation. */
    private double std;

    /**
     * Constructs a new normal distribution function.
     *
     * @param mean  the mean.
     * @param std  the standard deviation.
     */
    public NormalDistributionFunction2D(double mean, double std) {
        this.mean = mean;
        this.std = std;
    }

    /**
     * Returns the function value.
     *
     * @param x  the x-value.
     *
     * @return The value.
     */
    public double getValue(double x) {
        return Math.exp(
            -1.0 
            * (x - this.mean) * (x - this.mean) / (2 * this.std * this.std))
            / Math.sqrt(2 * Math.PI * this.std * this.std
        );
    }

}
