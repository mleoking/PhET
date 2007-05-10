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
 * --------------------
 * VectorXYDataset.java
 * --------------------
 * (C) Copyright 2007, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: VectorXYDataset.java,v 1.1.2.1 2007/01/30 16:29:11 mungady Exp $
 *
 * Changes
 * -------
 * 30-Jan-2007 : Version 1 (DG);
 *
 */

package org.jfree.experimental.data.xy;

import org.jfree.data.xy.XYDataset;


/**
 * An extension of the {@link XYDataset} interface that allows a vector to be
 * defined at a specific (x, y) location.
 */
public interface VectorXYDataset extends XYDataset {

    /**
     * Returns the x-component of the vector.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The x-component of the vector.
     */
    public double getDeltaXValue(int series, int item);

    /**
     * Returns the y-component of the vector.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The y-component of the vector.
     */
    public double getDeltaYValue(int series, int item);
    
    /**
     * Returns the x-component of the vector.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The x-component of the vector.
     */
    public Number getDeltaX(int series, int item);

    /**
     * Returns the y-component of the vector.
     * 
     * @param series  the series index.
     * @param item  the item index.
     * 
     * @return The y-component of the vector.
     */
    public Number getDeltaY(int series, int item);


}
