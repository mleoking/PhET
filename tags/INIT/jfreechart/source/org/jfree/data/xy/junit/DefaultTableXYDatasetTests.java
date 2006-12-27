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
 * -------------------------------
 * DefaultTableXYDatasetTests.java
 * -------------------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 23-Dec-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * Tests for the {@link DefaultTableXYDataset} class.
 */
public class DefaultTableXYDatasetTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultTableXYDatasetTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DefaultTableXYDatasetTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        DefaultTableXYDataset d1 = new DefaultTableXYDataset();
        XYSeries s1 = new XYSeries("Series 1", true, false);
        s1.add(1.0, 1.1);
        s1.add(2.0, 2.2);
        d1.addSeries(s1);
        
        DefaultTableXYDataset d2 = new DefaultTableXYDataset();
        XYSeries s2 = new XYSeries("Series 1", true, false);
        s2.add(1.0, 1.1);
        s2.add(2.0, 2.2);
        d2.addSeries(s2);
        
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

        s1.add(3.0, 3.3);
        assertFalse(d1.equals(d2));

        s2.add(3.0, 3.3);
        assertTrue(d1.equals(d2));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {        
        DefaultTableXYDataset d1 = new DefaultTableXYDataset();
        XYSeries s1 = new XYSeries("Series 1", true, false);
        s1.add(1.0, 1.1);
        s1.add(2.0, 2.2);
        d1.addSeries(s1);
        
        DefaultTableXYDataset d2 = null;
        try {
            d2 = (DefaultTableXYDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DefaultTableXYDataset d1 = new DefaultTableXYDataset();
        XYSeries s1 = new XYSeries("Series 1", true, false);
        s1.add(1.0, 1.1);
        s1.add(2.0, 2.2);
        d1.addSeries(s1);
        
        DefaultTableXYDataset d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultTableXYDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);

    }

}
