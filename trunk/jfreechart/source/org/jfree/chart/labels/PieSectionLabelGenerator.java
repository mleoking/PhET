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
 * -----------------------------
 * PieSectionLabelGenerator.java
 * -----------------------------
 * (C) Copyright 2001-2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 30-Oct-2002 : Category is now a Comparable instance (DG);
 * 07-Mar-2003 : Changed to KeyedValuesDataset and added pieIndex 
 *               parameter (DG);
 * 21-Mar-2003 : Updated Javadocs (DG);
 * 24-Apr-2003 : Switched around PieDataset and KeyedValuesDataset (DG);
 * 13-Aug-2003 : Added clone() method (DG);
 * 19-Aug-2003 : Renamed PieToolTipGenerator --> PieItemLabelGenerator (DG);
 * 11-Nov-2003 : Removed clone() method (DG);
 * 30-Jan-2004 : Added generateSectionLabel() method (DG);
 * 15-Apr-2004 : Moved generateToolTip() method into separate interface and 
 *               renamed this interface PieSectionLabelGenerator (DG);
 *
 */

package org.jfree.chart.labels;

import org.jfree.data.general.PieDataset;

/**
 * Interface for a label generator for plots that use data from 
 * a {@link PieDataset}.
 */
public interface PieSectionLabelGenerator {

    /**
     * Generates a label for a pie section.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param key  the section key (<code>null</code> not permitted).
     * 
     * @return The label (possibly <code>null</code>).
     */
    public String generateSectionLabel(PieDataset dataset, Comparable key);
        
}
