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
 * -----------------
 * QuarterTests.java
 * -----------------
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
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 11-Jan-2005 : Added check for non-clonability (DG);
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

import org.jfree.data.time.Quarter;
import org.jfree.data.time.TimePeriodFormatException;

/**
 * Tests for the {link Quarter} class.
 */
public class QuarterTests extends TestCase {

    /** A quarter. */
    private Quarter q1Y1900;

    /** A quarter. */
    private Quarter q2Y1900;

    /** A quarter. */
    private Quarter q3Y9999;

    /** A quarter. */
    private Quarter q4Y9999;

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(QuarterTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public QuarterTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        this.q1Y1900 = new Quarter(1, 1900);
        this.q2Y1900 = new Quarter(2, 1900);
        this.q3Y9999 = new Quarter(3, 9999);
        this.q4Y9999 = new Quarter(4, 9999);
    }

    /**
     * Check that a Quarter instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Quarter quarter = new Quarter();
        assertTrue(quarter.equals(quarter));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Quarter q1 = new Quarter(2, 2002);
        Quarter q2 = new Quarter(2, 2002);
        assertTrue(q1.equals(q2));
    }

    /**
     * In GMT, the end of Q1 2002 is java.util.Date(1017619199999L).  Use this
     * to check the quarter constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Quarter q1 = new Quarter(new Date(1017619199999L), zone);
        Quarter q2 = new Quarter(new Date(1017619200000L), zone);

        assertEquals(1, q1.getQuarter());
        assertEquals(1017619199999L, q1.getLastMillisecond(zone));

        assertEquals(2, q2.getQuarter());
        assertEquals(1017619200000L, q2.getFirstMillisecond(zone));

    }

    /**
     * In Istanbul, the end of Q1 2002 is java.util.Date(1017608399999L).  Use
     * this to check the quarter constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Istanbul");
        Quarter q1 = new Quarter(new Date(1017608399999L), zone);
        Quarter q2 = new Quarter(new Date(1017608400000L), zone);

        assertEquals(1, q1.getQuarter());
        assertEquals(1017608399999L, q1.getLastMillisecond(zone));

        assertEquals(2, q2.getQuarter());
        assertEquals(1017608400000L, q2.getFirstMillisecond(zone));

    }

    /**
     * Set up a quarter equal to Q1 1900.  Request the previous quarter, it 
     * should be null.
     */
    public void testQ1Y1900Previous() {
        Quarter previous = (Quarter) this.q1Y1900.previous();
        assertNull(previous);
    }

    /**
     * Set up a quarter equal to Q1 1900.  Request the next quarter, it should
     * be Q2 1900.
     */
    public void testQ1Y1900Next() {
        Quarter next = (Quarter) this.q1Y1900.next();
        assertEquals(this.q2Y1900, next);
    }

    /**
     * Set up a quarter equal to Q4 9999.  Request the previous quarter, it 
     * should be Q3 9999.
     */
    public void testQ4Y9999Previous() {
        Quarter previous = (Quarter) this.q4Y9999.previous();
        assertEquals(this.q3Y9999, previous);
    }

    /**
     * Set up a quarter equal to Q4 9999.  Request the next quarter, it should 
     * be null.
     */
    public void testQ4Y9999Next() {
        Quarter next = (Quarter) this.q4Y9999.next();
        assertNull(next);
    }

    /**
     * Test the string parsing code...
     */
    public void testParseQuarter() {

        Quarter quarter = null;

        // test 1...
        try {
            quarter = Quarter.parseQuarter("Q1-2000");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        assertEquals(1, quarter.getQuarter());
        assertEquals(2000, quarter.getYear().getYear());

        // test 2...
        try {
            quarter = Quarter.parseQuarter("2001-Q2");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        assertEquals(2, quarter.getQuarter());
        assertEquals(2001, quarter.getYear().getYear());

        // test 3...
        try {
            quarter = Quarter.parseQuarter("Q3, 2002");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        assertEquals(3, quarter.getQuarter());
        assertEquals(2002, quarter.getYear().getYear());

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Quarter q1 = new Quarter(4, 1999);
        Quarter q2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(q1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            q2 = (Quarter) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(q1, q2);

    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        Quarter q1 = new Quarter(2, 2003);
        Quarter q2 = new Quarter(2, 2003);
        assertTrue(q1.equals(q2));
        int h1 = q1.hashCode();
        int h2 = q2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * The {@link Quarter} class is immutable, so should not be 
     * {@link Cloneable}.
     */
    public void testNotCloneable() {
        Quarter q = new Quarter(2, 2003);
        assertFalse(q instanceof Cloneable);
    }

}
