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
 * NumberTickUnit.java
 * -------------------
 * (C) Copyright 2001-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes (from 19-Dec-2001)
 * --------------------------
 * 19-Dec-2001 : Added standard header (DG);
 * 01-May-2002 : Updated for changed to TickUnit class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 08-Nov-2002 : Moved to new package com.jrefinery.chart.axis (DG);
 * 09-Jan-2002 : Added a new constructor (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 *
 */

package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * A numerical tick unit.
 */
public class NumberTickUnit extends TickUnit implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 3849459506627654442L;
    
    /** A formatter for the tick unit. */
    private NumberFormat formatter;

    /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     */
    public NumberTickUnit(double size) {
        this(size, NumberFormat.getNumberInstance());
    }

    /**
     * Creates a new number tick unit.
     *
     * @param size  the size of the tick unit.
     * @param formatter  a number formatter for the tick unit.
     */
    public NumberTickUnit(double size, NumberFormat formatter) {
        super(size);
        this.formatter = formatter;
    }

    /**
     * Converts a value to a string.
     *
     * @param value  the value.
     *
     * @return The formatted string.
     */
    public String valueToString(double value) {
        return this.formatter.format(value);
    }

}
