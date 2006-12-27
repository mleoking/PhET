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
 * -------------------
 * SignalsDataset.java
 * -------------------
 * (C) Copyright 2002-2004, by Sylvain Vieujot and Contributors.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1 (SV);
 * 07-Oct-2002 : Added Javadocs (DG);
 */

package org.jfree.data.xy;


/**
 * An interface that adds signal information to an {@link XYDataset}.
 *
 * @author Sylvain Vieujot
 */
public interface SignalsDataset extends XYDataset {

    /** Useful constant indicating trade recommendation. */
    public static final int ENTER_LONG = 1;

    /** Useful constant indicating trade recommendation. */
    public static final int ENTER_SHORT = -1;

    /** Useful constant indicating trade recommendation. */
    public static final int EXIT_LONG = 2;

    /** Useful constant indicating trade recommendation. */
    public static final int EXIT_SHORT = -2;

    /**
     * Returns the type.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The type.
     */
    public int getType(int series, int item);

    /**
     * Returns the level.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The level.
     */
    public double getLevel(int series, int item);

}
