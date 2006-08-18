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
 * ----------------
 * SecondTests.java
 * ----------------
 * (C) Copyright 2002-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 29-Jan-2002 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Oct-2003 : Added serialization test (DG);
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

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.date.MonthConstants;

/**
 * Tests for the {@link Second} class.
 */
public class SecondTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(SecondTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public SecondTests(String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * Test that a Second instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        Second second = new Second();
        assertTrue(second.equals(second));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        Day day1 = new Day(29, MonthConstants.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Second second1 = new Second(34, minute1);
        Day day2 = new Day(29, MonthConstants.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        Second second2 = new Second(34, minute2);
        assertTrue(second1.equals(second2));
    }

    /**
     * In GMT, the 4.55:59pm on 21 Mar 2002 is java.util.Date(1016729759000L).
     * Use this to check the Second constructor.
     */
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Second s1 = new Second(new Date(1016729758999L), zone);
        Second s2 = new Second(new Date(1016729759000L), zone);

        assertEquals(58, s1.getSecond());
        assertEquals(1016729758999L, s1.getLastMillisecond(zone));

        assertEquals(59, s2.getSecond());
        assertEquals(1016729759000L, s2.getFirstMillisecond(zone));

    }

    /**
     * In Chicago, the 4.55:59pm on 21 Mar 2002 is 
     * java.util.Date(1016751359000L). Use this to check the Second constructor.
     */
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("America/Chicago");
        Second s1 = new Second(new Date(1016751358999L), zone);
        Second s2 = new Second(new Date(1016751359000L), zone);

        assertEquals(58, s1.getSecond());
        assertEquals(1016751358999L, s1.getLastMillisecond(zone));

        assertEquals(59, s2.getSecond());
        assertEquals(1016751359000L, s2.getFirstMillisecond(zone));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Second s1 = new Second();
        Second s2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            s2 = (Second) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(s1, s2);

    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        Second s1 = new Second(13, 45, 5, 1, 2, 2003);
        Second s2 = new Second(13, 45, 5, 1, 2, 2003);
        assertTrue(s1.equals(s2));
        int h1 = s1.hashCode();
        int h2 = s2.hashCode();
        assertEquals(h1, h2);
    }

    /**
     * The {@link Second} class is immutable, so should not be 
     * {@link Cloneable}.
     */
    public void testNotCloneable() {
        Second s = new Second(13, 45, 5, 1, 2, 2003);
        assertFalse(s instanceof Cloneable);
    }

}
