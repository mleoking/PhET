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
 * --------------------------------
 * DefaultCategoryDatasetTests.java
 * --------------------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 23-Mar-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Tests for the {@link DefaultCategoryDataset} class.
 */
public class DefaultCategoryDatasetTests extends TestCase {
    
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DefaultCategoryDatasetTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DefaultCategoryDatasetTests(String name) {
        super(name);
    }
    
    /**
     * Some checks for the getValue() method.
     */
    public void testGetValue() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "R1", "C1");
        assertEquals(new Double(1.0), d.getValue("R1", "C1"));
        boolean pass = false;
        try {
            d.getValue("XX", "C1");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
        
        pass = false;
        try {
            d.getValue("R1", "XX");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
    }
    
    /**
     * Some checks for the incrementValue() method.
     */
    public void testIncrementValue() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(1.0, "R1", "C1");
        d.incrementValue(2.0, "R1", "C1");
        assertEquals(new Double(3.0), d.getValue("R1", "C1"));
        
        // increment a null value
        d.addValue(null, "R2", "C1");
        d.incrementValue(2.0, "R2", "C1");
        assertEquals(new Double(2.0), d.getValue("R2", "C1"));
        
        // increment an unknown row
        boolean pass = false;
        try {
            d.incrementValue(1.0, "XX", "C1");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
        
        // increment an unknown column
        pass = false;
        try {
            d.incrementValue(1.0, "R1", "XX");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass); 
    }
    
    /**
     * Some tests for the getRowCount() method.
     */
    public void testGetRowCount() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        assertTrue(d.getRowCount() == 0);
        
        d.addValue(1.0, "R1", "C1");
        assertTrue(d.getRowCount() == 1);
        
        d.addValue(1.0, "R2", "C1");
        assertTrue(d.getRowCount() == 2);
        
        d.addValue(2.0, "R2", "C1");
        assertTrue(d.getRowCount() == 2);
        
        // a row of all null values is still counted...
        d.setValue(null, "R2", "C1");
        assertTrue(d.getRowCount() == 2);
    }

    /**
     * Some tests for the getColumnCount() method.
     */
    public void testGetColumnCount() {
        DefaultCategoryDataset d = new DefaultCategoryDataset();
        assertTrue(d.getColumnCount() == 0);
        
        d.addValue(1.0, "R1", "C1");
        assertTrue(d.getColumnCount() == 1);
        
        d.addValue(1.0, "R1", "C2");
        assertTrue(d.getColumnCount() == 2);
        
        d.addValue(2.0, "R1", "C2");
        assertTrue(d.getColumnCount() == 2);
        
        // a column of all null values is still counted...
        d.setValue(null, "R1", "C2");
        assertTrue(d.getColumnCount() == 2);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        DefaultCategoryDataset d1 = new DefaultCategoryDataset();
        d1.setValue(23.4, "R1", "C1");
        DefaultCategoryDataset d2 = new DefaultCategoryDataset();
        d2.setValue(23.4, "R1", "C1");
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

        d1.setValue(36.5, "R1", "C2");
        assertFalse(d1.equals(d2));
        d2.setValue(36.5, "R1", "C2");
        assertTrue(d1.equals(d2));

        d1.setValue(null, "R1", "C1");
        assertFalse(d1.equals(d2));
        d2.setValue(null, "R1", "C1");
        assertTrue(d1.equals(d2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DefaultCategoryDataset d1 = new DefaultCategoryDataset();
        d1.setValue(23.4, "R1", "C1");
        DefaultCategoryDataset d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);

    }
    
}
