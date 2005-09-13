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
 * ---------------------------------------
 * StandardPieItemLabelGeneratorTests.java
 * ---------------------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 18-Mar-2003 : Version 1 (DG);
 * 13-Aug-2003 : Added clone tests (DG);
 * 04-Mar-2004 : Added test for equals() method (DG);
 *
 */

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.StandardPieItemLabelGenerator;

/**
 * Tests for the {@link StandardPieItemLabelGenerator} class.
 */
public class StandardPieItemLabelGeneratorTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(StandardPieItemLabelGeneratorTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public StandardPieItemLabelGeneratorTests(String name) {
        super(name);
    }

    /**
     * Test that the equals() method distinguishes all fields.
     */
    public void testEquals() {
        StandardPieItemLabelGenerator g1 = new StandardPieItemLabelGenerator();
        StandardPieItemLabelGenerator g2 = new StandardPieItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new StandardPieItemLabelGenerator(
            "{0}", new DecimalFormat("#,##0.00"), 
            NumberFormat.getPercentInstance()
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardPieItemLabelGenerator(
            "{0}", new DecimalFormat("#,##0.00"), 
            NumberFormat.getPercentInstance()
        );
        assertTrue(g1.equals(g2));

        g1 = new StandardPieItemLabelGenerator(
            "{0} {1}", new DecimalFormat("#,##0.00"), 
            NumberFormat.getPercentInstance()
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardPieItemLabelGenerator(
            "{0} {1}", new DecimalFormat("#,##0.00"), 
            NumberFormat.getPercentInstance()
        );
        assertTrue(g1.equals(g2));
    
        g1 = new StandardPieItemLabelGenerator(
            "{0} {1}", new DecimalFormat("#,##0"), 
            NumberFormat.getPercentInstance()
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardPieItemLabelGenerator(
            "{0} {1}", new DecimalFormat("#,##0"), 
            NumberFormat.getPercentInstance()
        );
        assertTrue(g1.equals(g2));

        g1 = new StandardPieItemLabelGenerator(
            "{0} {1}", new DecimalFormat("#,##0"), new DecimalFormat("0.000%")
        );
        assertFalse(g1.equals(g2));
        g2 = new StandardPieItemLabelGenerator(
            "{0} {1}", new DecimalFormat("#,##0"), new DecimalFormat("0.000%")
        );
        assertTrue(g1.equals(g2));
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        StandardPieItemLabelGenerator g1 = new StandardPieItemLabelGenerator();
        StandardPieItemLabelGenerator g2 = null;
        try {
            g2 = (StandardPieItemLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        StandardPieItemLabelGenerator g1 = new StandardPieItemLabelGenerator();
        StandardPieItemLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            g2 = (StandardPieItemLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);

    }

}
