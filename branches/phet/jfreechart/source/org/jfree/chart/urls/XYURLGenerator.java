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
 * XYURLGenerator.java
 * -------------------
 * (C) Copyright 2002-2005, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 20-Jan-2005 : Minor Javadoc update (DG);
 * 
 */

package org.jfree.chart.urls;

import org.jfree.data.xy.XYDataset;

/**
 * Interface for a URL generator for plots that uses data from an 
 * {@link XYDataset}.
 *
 * @author Richard Atkinson
 */
public interface XYURLGenerator {

    /**
     * Generates a URL for a particular item within a series. As a guideline, 
     * the URL should be valid within the context of an XHTML 1.0 document.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A string containing the generated URL (possibly 
     *         <code>null</code>).
     */
    public String generateURL(XYDataset dataset, int series, int item);

}
