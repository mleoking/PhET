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
 * ------------------
 * KeyedValues2D.java
 * ------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 28-Oct-2002 : Version 1 (DG);
 * 12-Jan-2005 : Updated Javadocs (DG);
 *
 */

package org.jfree.data;

import java.util.List;

/**
 * An extension of the {@link Values2D} interface where a unique key is 
 * associated with the row and column indices.
 */
public interface KeyedValues2D extends Values2D {

    /**
     * Returns the row key for a given index.
     *
     * @param row  the row index (zero-based).
     *
     * @return The row key.
     * 
     * @throws IndexOutOfBoundsException if <code>row</code> is out of bounds.
     */
    public Comparable getRowKey(int row);

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key.
     *
     * @return The row index, or <code>-1</code> if the key is unrecognised.
     */
    public int getRowIndex(Comparable key);

    /**
     * Returns the row keys.
     *
     * @return The keys.
     */
    public List getRowKeys();

    /**
     * Returns the column key for a given index.
     *
     * @param column  the column index (zero-based).
     *
     * @return The column key.
     * 
     * @throws IndexOutOfBoundsException if <code>row</code> is out of bounds.
     */
    public Comparable getColumnKey(int column);

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key.
     *
     * @return The column index, or <code>-1</code> if the key is unrecognised.
     */
    public int getColumnIndex(Comparable key);

    /**
     * Returns the column keys.
     *
     * @return The keys.
     */
    public List getColumnKeys();

    /**
     * Returns the value associated with the specified keys.  This method 
     * should return <code>null</code> if either of the keys is not found.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The value.
     * 
     * @throws UnknownKeyException if either key is not recognised.
     */
    public Number getValue(Comparable rowKey, Comparable columnKey);

}
