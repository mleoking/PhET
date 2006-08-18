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
 * -------------------------------------
 * StandardPieSectionLabelGenerator.java
 * -------------------------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 09-Nov-2004 : Version 1, derived from StandardPieItemLabelGenerator (DG);
 * 
 */

package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.NumberFormat;

import org.jfree.data.general.PieDataset;

/**
 * A standard item label generator for plots that use data from a 
 * {@link PieDataset}.
 * <p>
 * For the label format, use {0} where the pie section key should be inserted,
 * {1} for the absolute section value and {2} for the percent amount of the pie
 * section, e.g. <code>"{0} = {1} ({2})"</code> will display as  
 * <code>apple = 120 (5%)</code>.
 */
public class StandardPieSectionLabelGenerator 
    extends AbstractPieItemLabelGenerator
    implements PieSectionLabelGenerator, Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 3064190563760203668L;
    
    /** The default section label format. */
    public static final String DEFAULT_SECTION_LABEL_FORMAT = "{0} = {1}";

    /**
     * Creates an item label generator using default number formatters.
     */
    public StandardPieSectionLabelGenerator() {
        this(
            "{0} = {1}", 
            NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        );
    }

    /**
     * Creates an item label generator.
     * 
     * @param labelFormat  the label format.
     */
    public StandardPieSectionLabelGenerator(String labelFormat) {
        this(
            labelFormat, NumberFormat.getNumberInstance(), 
            NumberFormat.getPercentInstance()
        );   
    }
    
    /**
     * Creates an item label generator using the specified number formatters.
     *
     * @param labelFormat  the label format string (<code>null</code> not 
     *                     permitted).
     * @param numberFormat  the format object for the values (<code>null</code>
     *                      not permitted).
     * @param percentFormat  the format object for the percentages 
     *                       (<code>null</code> not permitted).
     */
    public StandardPieSectionLabelGenerator(String labelFormat,
                                         NumberFormat numberFormat, 
                                         NumberFormat percentFormat) {

        super(labelFormat, numberFormat, percentFormat);

    }

    /**
     * Generates a label for a pie section.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param key  the section key (<code>null</code> not permitted).
     * 
     * @return The label (possibly <code>null</code>).
     */
    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        return super.generateSectionLabel(dataset, key);
    }

    /**
     * Generates a tool tip text item for one section in a pie chart.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param key  the section key (<code>null</code> not permitted).
     *
     * @return The tool tip text (possibly <code>null</code>).
     */
    public String generateToolTip(PieDataset dataset, Comparable key) {
        return super.generateSectionLabel(dataset, key);
    }

    /**
     * Tests the generator for equality with an arbitrary object.
     *
     * @param obj  the object to test against (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardPieSectionLabelGenerator)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }

    /**
     * Returns an independent copy of the generator.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  should not happen.
     */
    public Object clone() throws CloneNotSupportedException {      
        return super.clone();
    }

}
