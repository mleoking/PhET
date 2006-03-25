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
 * -------------------------
 * CategoryToPieDataset.java
 * -------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * $Id$
 *
 * Changes
 * -------
 * 23-Jan-2003 : Version 1 (DG);
 * 30-Jul-2003 : Pass through DatasetChangeEvent (CZ);
 * 29-Jan-2004 : Replaced 'extract' int with TableOrder (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for the 1.0.0 
 *               release (DG);
 *
 */

package org.jfree.data.category;

import java.util.List;

import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.PieDataset;
import org.jfree.util.TableOrder;

/**
 * A {@link PieDataset} implementation that obtains its data from one row or 
 * column of a {@link CategoryDataset}.
 *
 */
public class CategoryToPieDataset extends AbstractDataset 
                                  implements PieDataset, DatasetChangeListener {

    /** The source. */
    private CategoryDataset source;

    /** The extract type. */
    private TableOrder extract;

    /** The row or column index. */
    private int index;

    /**
     * An adaptor class that converts any {@link CategoryDataset} into a 
     * {@link PieDataset}, by taking the values from a single row or column.
     *
     * @param source  the source dataset (<code>null</code> permitted).
     * @param extract  extract data from rows or columns? (<code>null</code> 
     *                 not permitted).
     * @param index  the row or column index.
     */
    public CategoryToPieDataset(CategoryDataset source, 
                                TableOrder extract, 
                                int index) {
        if (extract == null) {
            throw new IllegalArgumentException("Null 'extract' argument.");
        }
        this.source = source;
        this.source.addChangeListener(this);
        this.extract = extract;
        this.index = index;
    }

    /**
     * Returns the number of items (values) in the collection.  If the 
     * underlying dataset is <code>null</code>, this method returns zero.
     *
     * @return The item count.
     */
    public int getItemCount() {
        int result = 0;
        if (this.source != null) {
            if (this.extract == TableOrder.BY_ROW) {
                result = this.source.getColumnCount();
            }
            else if (this.extract == TableOrder.BY_COLUMN) {
                result = this.source.getRowCount();
            }
        }
        return result;
    }

    /**
     * Returns a value.
     *
     * @param item  the item index (zero-based).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(int item) {
        Number result = null;
        if (this.source != null) {
            if (this.extract == TableOrder.BY_ROW) {
                result = this.source.getValue(this.index, item);
            }
            else if (this.extract == TableOrder.BY_COLUMN) {
                result = this.source.getValue(item, this.index);
            }
        }
        return result;
    }

    /**
     * Returns a key.
     *
     * @param index  the item index (zero-based).
     *
     * @return The key.
     */
    public Comparable getKey(int index) {
        Comparable result = null;
        if (this.extract == TableOrder.BY_ROW) {
            result = this.source.getColumnKey(index);
        }
        else if (this.extract == TableOrder.BY_COLUMN) {
            result = this.source.getRowKey(index);
        }
        return result;
    }

    /**
     * Returns the index for a given key.
     *
     * @param key  the key.
     *
     * @return The index.
     */
    public int getIndex(Comparable key) {
        int result = -1;
        if (this.extract == TableOrder.BY_ROW) {
            result = this.source.getColumnIndex(key);
        }
        else if (this.extract == TableOrder.BY_COLUMN) {
            result = this.source.getRowIndex(key);
        }
        return result;
    }

    /**
     * Returns the keys.
     *
     * @return The keys.
     */
    public List getKeys() {
        List result = null;
        if (this.extract == TableOrder.BY_ROW) {
            result = this.source.getColumnKeys();
        }
        else if (this.extract == TableOrder.BY_COLUMN) {
            result = this.source.getRowKeys();
        }
        return result;
    }

    /**
     * Returns the value for a given key.  If the key is not recognised, the 
     * method should return <code>null</code> (but note that <code>null</code> 
     * can be associated with a valid key also).
     *
     * @param key  the key.
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getValue(Comparable key) {
        Number result = null;
        int keyIndex = getIndex(key);
        if (this.extract == TableOrder.BY_ROW) {
            result = this.source.getValue(this.index, keyIndex);
        }
        else if (this.extract == TableOrder.BY_COLUMN) {
            result = this.source.getValue(keyIndex, this.index);
        }
        return result;
    }
    
    /**
     * Passes the {@link DatasetChangeEvent} through.
     * 
     * @param event  the event.
     */
    public void datasetChanged (DatasetChangeEvent event) {
        fireDatasetChanged();
    }
     
}
