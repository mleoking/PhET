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
 * --------------
 * YearTests.java
 * --------------
 * (C) Copyright 2001-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 16-Nov-2001 : Version 1 (DG);
 * 19-Mar-2002 : Added tests for constructor that uses java.util.Date to ensure
 *               it is consistent with the getStart() and getEnd() methods (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 11-Jan-2005 : Added test for non-clonability (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.TimePeriodFormatException;
import org.jfree.data.time.Year;

/**
 * Tests for the {@link Year} class.
 */
public class YearTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(YearTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public YearTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * Check that a Year instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Year year = new Year();
        assertTrue(year.equals(year));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Year year1 = new Year(2002);
        Year year2 = new Year(2002);
        assertTrue(year1.equals(year2));
    }

    /**
     * In GMT, the end of 2001 is java.util.Date(1009843199999L).  Use this to 
     * check the year constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Date d1 = new Date(1009843199999L);
        Date d2 = new Date(1009843200000L);
        Year y1 = new Year(d1, zone);
        Year y2 = new Year(d2, zone);

        assertEquals(2001, y1.getYear());
        assertEquals(1009843199999L, y1.getLastMillisecond(zone));

        assertEquals(2002, y2.getYear());
        assertEquals(1009843200000L, y2.getFirstMillisecond(zone));

    }

    /**
     * In Los Angeles, the end of 2001 is java.util.Date(1009871999999L).  Use 
     * this to check the year constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        Year y1 = new Year(new Date(1009871999999L), zone);
        Year y2 = new Year(new Date(1009872000000L), zone);

        assertEquals(2001, y1.getYear());
        assertEquals(1009871999999L, y1.getLastMillisecond(zone));

        assertEquals(2002, y2.getYear());
        assertEquals(1009872000000L, y2.getFirstMillisecond(zone));

    }

    /**
     * Set up a year equal to 1900.  Request the previous year, it should be
     * null.
     */
    public void test1900Previous() {
        Year current = new Year(1900);
        Year previous = (Year) current.previous();
        assertNull(previous);
    }

    /**
     * Set up a year equal to 1900.  Request the next year, it should be 1901.
     */
    public void test1900Next() {
        Year current = new Year(1900);
        Year next = (Year) current.next();
        assertEquals(1901, next.getYear());
    }

    /**
     * Set up a year equal to 9999.  Request the previous year, it should be 
     * 9998.
     */
    public void test9999Previous() {
        Year current = new Year(9999);
        Year previous = (Year) current.previous();
        assertEquals(9998, previous.getYear());
    }

    /**
     * Set up a year equal to 9999.  Request the next year, it should be null.
     */
    public void test9999Next() {
        Year current = new Year(9999);
        Year next = (Year) current.next();
        assertNull(next);
    }

    /**
     * Tests the year string parser.
     */
    public void testParseYear() {

        Year year = null;

        // test 1...
        try {
            year = Year.parseYear("2000");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        assertEquals(2000, year.getYear());

        // test 2...
        try {
            year = Year.parseYear(" 2001 ");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        assertEquals(2001, year.getYear());

        // test 3...
        try {
            year = Year.parseYear("99");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        assertEquals(1900, year.getYear());

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Year y1 = new Year(1999);
        Year y2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(y1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            y2 = (Year) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(y1, y2);

    }
    
    /**
     * The {@link Year} class is immutable, so should not be {@link Cloneable}.
     */
    public void testNotCloneable() {
        Year y = new Year(1999);
        assertFalse(y instanceof Cloneable);
    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        Year y1 = new Year(1988);
        Year y2 = new Year(1988);
        assertTrue(y1.equals(y2));
        int h1 = y1.hashCode();
        int h2 = y2.hashCode();
        assertEquals(h1, h2);
    }

}
