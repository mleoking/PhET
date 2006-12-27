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
 * -------------------------------------------
 * DefaultStatisticalCategoryDatasetTests.java
 * -------------------------------------------
 * (C) Copyright 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 05-Feb-2005 : Version 1 (DG);
 *
 */

package org.jfree.data.statistics.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;

/**
 * Tests for the {@link DefaultStatisticalCategoryDataset} class.
 */
public class DefaultStatisticalCategoryDatasetTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultStatisticalCategoryDatasetTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DefaultStatisticalCategoryDatasetTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        DefaultStatisticalCategoryDataset d2 
            = new DefaultStatisticalCategoryDataset();
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

    }

    /**
     * Some checks for cloning.
     */
    public void testCloning() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        d1.add(1.1, 2.2, "R1", "C1");
        d1.add(3.3, 4.4, "R1", "C2");
        d1.add(null, new Double(5.5), "R1", "C3");
        d1.add(new Double(6.6), null, "R2", "C3");
        DefaultStatisticalCategoryDataset d2 = null;
        try {
            d2 = (DefaultStatisticalCategoryDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            fail(e.toString());
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }

    /**
     * Check serialization of a default instance.
     */
    public void testSerialization1() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        d1.add(1.1, 2.2, "R1", "C1");
        d1.add(3.3, 4.4, "R1", "C2");
        d1.add(null, new Double(5.5), "R1", "C3");
        d1.add(new Double(6.6), null, "R2", "C3");
        DefaultStatisticalCategoryDataset d2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultStatisticalCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(d1, d2);
    }
    
    /**
     * Check serialization of a more complex instance.
     */
    public void testSerialization2() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        d1.add(1.2, 3.4, "Row 1", "Column 1");
        DefaultStatisticalCategoryDataset d2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultStatisticalCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(d1, d2);
    }
    
}
