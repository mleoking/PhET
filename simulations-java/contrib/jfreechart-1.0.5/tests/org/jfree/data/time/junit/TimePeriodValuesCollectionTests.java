/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
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
 * ------------------------------------
 * TimePeriodValuesCollectionTests.java
 * ------------------------------------
 * (C) Copyright 2005-2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValuesCollectionTests.java,v 1.1.2.2 2007/03/08 13:57:09 mungady Exp $
 *
 * Changes
 * -------
 * 11-Mar-2005 : Version 1 (DG);
 * 08-Mar-2007 : Added testGetSeries() (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;

/**
 * Some tests for the {@link TimePeriodValuesCollection} class.
 */
public class TimePeriodValuesCollectionTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(TimePeriodValuesCollectionTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TimePeriodValuesCollectionTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * A test for bug report 1161340.  I wasn't able to reproduce the problem
     * with this test.
     */
    public void test1161340() {
        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        TimePeriodValues v1 = new TimePeriodValues("V1");
        v1.add(new Day(11, 3, 2005), 1.2);
        v1.add(new Day(12, 3, 2005), 3.4);
        dataset.addSeries(v1);
        assertEquals(1, dataset.getSeriesCount());
        dataset.removeSeries(v1);
        assertEquals(0, dataset.getSeriesCount());
        
        TimePeriodValues v2 = new TimePeriodValues("V2");
        v1.add(new Day(5, 3, 2005), 1.2);
        v1.add(new Day(6, 3, 2005), 3.4);
        dataset.addSeries(v2);
        assertEquals(1, dataset.getSeriesCount());
    }
    
    /**
     * Tests the equals() method.
     */
    public void testEquals() {
        
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValuesCollection c2 = new TimePeriodValuesCollection();
        assertTrue(c1.equals(c2));
        
        c1.setDomainIsPointsInTime(!c1.getDomainIsPointsInTime());
        assertFalse(c1.equals(c2));
        c2.setDomainIsPointsInTime(c1.getDomainIsPointsInTime());
        assertTrue(c1.equals(c2));
        
        c1.setXPosition(TimePeriodAnchor.END);
        assertFalse(c1.equals(c2));
        c2.setXPosition(TimePeriodAnchor.END);
        assertTrue(c1.equals(c2));
        
        TimePeriodValues v1 = new TimePeriodValues("Test");
        TimePeriodValues v2 = new TimePeriodValues("Test");
        
        c1.addSeries(v1);
        assertFalse(c1.equals(c2));
        c2.addSeries(v2);
        assertTrue(c1.equals(c2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValuesCollection c2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            c2 = (TimePeriodValuesCollection) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(c1, c2);
    }

    /**
     * Some basic checks for the getSeries() method.
     */
    public void testGetSeries() {
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValues s1 = new TimePeriodValues("Series 1");
        c1.addSeries(s1);
        assertEquals("Series 1", c1.getSeries(0).getKey());
        
        boolean pass = false;
        try {
            c1.getSeries(-1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
        
        pass = false;
        try {
            c1.getSeries(1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }

}
