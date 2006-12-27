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
 * -----------
 * Marker.java
 * -----------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Nicolas Brodu;
 *
 * $Id$
 *
 * Changes
 * -------
 * 20-May-2005 : Version 1 (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import org.jfree.ui.LengthAdjustmentType;

/**
 * A marker for a category.
 */
public class CategoryMarker extends Marker {

    /** The category key. */
    private Comparable key;
    
    /** 
     * A hint that the marker should be drawn as a line rather than a region. 
     */
    private boolean drawAsLine = false;
    
    public CategoryMarker(Comparable key) {
        this(key, Color.gray, new BasicStroke(1.0f));    
    }
    
    /**
     * Creates a new marker.
     * 
     * @param key  the key.
     * @param paint  the paint (<code>null</code> not permitted).
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public CategoryMarker(Comparable key, Paint paint, Stroke stroke) {
        this(key, paint, stroke, paint, stroke, 0.5f);
    }
    
    /**
     * Creates a new category marker.
     * 
     * @param key  the key.
     * @param paint  the paint (<code>null</code> not permitted).
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param outlinePaint  the outline paint (<code>null</code> permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> permitted).
     * @param alpha  the alpha transparency.
     */
    public CategoryMarker(Comparable key, Paint paint, Stroke stroke, 
                          Paint outlinePaint, Stroke outlineStroke, 
                          float alpha) {
        super(paint, stroke, paint, stroke, alpha);
        this.key = key;
        setLabelOffsetType(LengthAdjustmentType.EXPAND);
    }
    
    /**
     * Returns the key.
     * 
     * @return The key.
     */
    public Comparable getKey() {
        return this.key;   
    }
    
    public boolean getDrawAsLine() {
        return this.drawAsLine;   
    }
    
}
