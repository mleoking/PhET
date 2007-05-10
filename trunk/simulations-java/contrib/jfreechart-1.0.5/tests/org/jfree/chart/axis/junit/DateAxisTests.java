/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * ------------------
 * DateAxisTests.java
 * ------------------
 * (C) Copyright 2003-2006, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DateAxisTests.java,v 1.1.2.1 2006/10/03 15:41:22 mungady Exp $
 *
 * Changes
 * -------
 * 22-Apr-2003 : Version 1 (DG);
 * 07-Jan-2005 : Added test for hashCode() method (DG);
 * 25-Sep-2005 : New tests for bug 1564977 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.data.time.DateRange;
import org.jfree.ui.RectangleEdge;

/**
 * Tests for the {@link DateAxis} class.
 */
public class DateAxisTests extends TestCase {

    static class MyDateAxis extends DateAxis {
        /**
         * Creates a new instance.
         * 
         * @param label  the label.
         */
        public MyDateAxis(String label) {
            super(label);
        }
        public Date previousStandardDate(Date d, DateTickUnit unit) {
            return super.previousStandardDate(d, unit);
        }
    }
    
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DateAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DateAxisTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        DateAxis a1 = new DateAxis("Test");
        DateAxis a2 = new DateAxis("Test");
        assertTrue(a1.equals(a2));
        assertFalse(a1.equals(null));
        assertFalse(a1.equals("Some non-DateAxis object"));
        
        // tickUnit 
        a1.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 7));
        assertFalse(a1.equals(a2));
        a2.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 7));
        assertTrue(a1.equals(a2));

        // dateFormatOverride 
        a1.setDateFormatOverride(new SimpleDateFormat("yyyy"));
        assertFalse(a1.equals(a2));
        a2.setDateFormatOverride(new SimpleDateFormat("yyyy"));
        assertTrue(a1.equals(a2));

        // tickMarkPosition
        a1.setTickMarkPosition(DateTickMarkPosition.END);
        assertFalse(a1.equals(a2));
        a2.setTickMarkPosition(DateTickMarkPosition.END);
        assertTrue(a1.equals(a2));
        
        // timeline
        a1.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        assertFalse(a1.equals(a2));
        a2.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        assertTrue(a1.equals(a2));
        
    }
    
    /**
     * A test for bug report 1472942.  The DateFormat.equals() method is not
     * checking the range attribute.
     */
    public void test1472942() {
        DateAxis a1 = new DateAxis("Test");
        DateAxis a2 = new DateAxis("Test");
        assertTrue(a1.equals(a2));

        // range
        a1.setRange(new Date(1L), new Date(2L));
        assertFalse(a1.equals(a2));
        a2.setRange(new Date(1L), new Date(2L));
        assertTrue(a1.equals(a2));
    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashCode() {
        DateAxis a1 = new DateAxis("Test");
        DateAxis a2 = new DateAxis("Test");
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        DateAxis a1 = new DateAxis("Test");
        DateAxis a2 = null;
        try {
            a2 = (DateAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Test that the setRange() method works.
     */
    public void testSetRange() {

        DateAxis axis = new DateAxis("Test Axis");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1999, Calendar.JANUARY, 3);
        Date d1 = calendar.getTime();
        calendar.set(1999, Calendar.JANUARY, 31);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);

        DateRange range = (DateRange) axis.getRange();
        assertEquals(d1, range.getLowerDate());
        assertEquals(d2, range.getUpperDate());

    }

    /**
     * Test that the setMaximumDate() method works.
     */
    public void testSetMaximumDate() {

        DateAxis axis = new DateAxis("Test Axis");
        Date date = new Date();
        axis.setMaximumDate(date);
        assertEquals(date, axis.getMaximumDate());

    }

    /**
     * Test that the setMinimumDate() method works.
     */
    public void testSetMinimumDate() {

        DateAxis axis = new DateAxis("Test Axis");
        Date d1 = new Date();
        Date d2 = new Date(d1.getTime() + 1);
        axis.setMaximumDate(d2);
        axis.setMinimumDate(d1);
        assertEquals(d1, axis.getMinimumDate());

    }
    
    /**
     * Tests two doubles for 'near enough' equality.
     * 
     * @param d1  number 1.
     * @param d2  number 2.
     * @param tolerance  maximum tolerance.
     * 
     * @return A boolean.
     */
    private boolean same(double d1, double d2, double tolerance) {
        return (Math.abs(d1 - d2) < tolerance);
    }
    
    /**
     * Test the translation of Java2D values to data values.
     */
    public void testJava2DToValue() {
        DateAxis axis = new DateAxis();
        axis.setRange(50.0, 100.0); 
        Rectangle2D dataArea = new Rectangle2D.Double(10.0, 50.0, 400.0, 300.0);
        double y1 = axis.java2DToValue(75.0, dataArea, RectangleEdge.LEFT);  
        assertTrue(same(y1, 95.8333333, 1.0)); 
        double y2 = axis.java2DToValue(75.0, dataArea, RectangleEdge.RIGHT);   
        assertTrue(same(y2, 95.8333333, 1.0)); 
        double x1 = axis.java2DToValue(75.0, dataArea, RectangleEdge.TOP);   
        assertTrue(same(x1, 58.125, 1.0)); 
        double x2 = axis.java2DToValue(75.0, dataArea, RectangleEdge.BOTTOM);   
        assertTrue(same(x2, 58.125, 1.0)); 
        axis.setInverted(true);
        double y3 = axis.java2DToValue(75.0, dataArea, RectangleEdge.LEFT);  
        assertTrue(same(y3, 54.1666667, 1.0)); 
        double y4 = axis.java2DToValue(75.0, dataArea, RectangleEdge.RIGHT);   
        assertTrue(same(y4, 54.1666667, 1.0)); 
        double x3 = axis.java2DToValue(75.0, dataArea, RectangleEdge.TOP);   
        assertTrue(same(x3, 91.875, 1.0)); 
        double x4 = axis.java2DToValue(75.0, dataArea, RectangleEdge.BOTTOM);   
        assertTrue(same(x4, 91.875, 1.0));   
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        DateAxis a1 = new DateAxis("Test Axis");
        DateAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (DateAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = a1.equals(a2);
        assertTrue(b);

    }
    
    /**
     * A basic check for the testPreviousStandardDate() method when the
     * tick unit is 1 year.
     */
    public void testPreviousStandardDateYear() {
        MyDateAxis axis = new MyDateAxis("Year");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2006, Calendar.AUGUST, 25, 12, 0);
        Date d1 = calendar.getTime();
        calendar.set(2007, Calendar.MAY, 20, 12, 0);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);
        DateTickUnit unit = new DateTickUnit(DateTickUnit.YEAR, 1);
        axis.setTickUnit(unit);
        
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        Date psd1 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd1);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DATE));
        
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        Date psd2 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd2);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DATE));

        axis.setTickMarkPosition(DateTickMarkPosition.END);
        Date psd3 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd3);
        assertEquals(2005, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.DECEMBER, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DATE));
    }

    /**
     * A basic check for the testPreviousStandardDate() method when the
     * tick unit is 1 month.
     */
    public void testPreviousStandardDateMonth() {
        MyDateAxis axis = new MyDateAxis("Month");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2006, Calendar.AUGUST, 25, 12, 0);
        Date d1 = calendar.getTime();
        calendar.set(2007, Calendar.MAY, 20, 12, 0);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);
        DateTickUnit unit = new DateTickUnit(DateTickUnit.MONTH, 1);
        axis.setTickUnit(unit);
        
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        Date psd1 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd1);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DATE));
        
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        Date psd2 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd2);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(16, calendar.get(Calendar.DATE));

        axis.setTickMarkPosition(DateTickMarkPosition.END);
        Date psd3 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd3);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, calendar.get(Calendar.MONTH));
        assertEquals(31, calendar.get(Calendar.DATE));
    }

    /**
     * A basic check for the testPreviousStandardDate() method when the
     * tick unit is 1 day.
     */
    public void testPreviousStandardDateDay() {
        MyDateAxis axis = new MyDateAxis("Day");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2006, Calendar.AUGUST, 25, 12, 0);
        Date d1 = calendar.getTime();
        calendar.set(2007, Calendar.MAY, 20, 12, 0);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);
        DateTickUnit unit = new DateTickUnit(DateTickUnit.DAY, 1);
        axis.setTickUnit(unit);
        
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        Date psd1 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd1);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        Date psd2 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd2);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));

        axis.setTickMarkPosition(DateTickMarkPosition.END);
        Date psd3 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd3);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(24, calendar.get(Calendar.DATE));
    }

    /**
     * A basic check for the testPreviousStandardDate() method when the
     * tick unit is 1 hour.
     */
    public void testPreviousStandardDateHour() {
        MyDateAxis axis = new MyDateAxis("Hour");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2006, Calendar.AUGUST, 25, 12, 0);
        Date d1 = calendar.getTime();
        calendar.set(2007, Calendar.MAY, 20, 12, 0);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);
        DateTickUnit unit = new DateTickUnit(DateTickUnit.HOUR, 1);
        axis.setTickUnit(unit);
        
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        Date psd1 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd1);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
        
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        Date psd2 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd2);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));

        axis.setTickMarkPosition(DateTickMarkPosition.END);
        Date psd3 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd3);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
    }

    /**
     * A basic check for the testPreviousStandardDate() method when the
     * tick unit is 1 minute.
     */
    public void testPreviousStandardDateMinute() {
        MyDateAxis axis = new MyDateAxis("Minute");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(2006, Calendar.AUGUST, 25, 12, 0, 10);
        Date d1 = calendar.getTime();
        calendar.set(2007, Calendar.MAY, 20, 12, 0, 10);
        Date d2 = calendar.getTime();
        axis.setRange(d1, d2);
        DateTickUnit unit = new DateTickUnit(DateTickUnit.MINUTE, 1);
        axis.setTickUnit(unit);
        
        axis.setTickMarkPosition(DateTickMarkPosition.START);
        Date psd1 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd1);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(12, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, calendar.get(Calendar.MINUTE));
        
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        Date psd2 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd2);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(Calendar.MINUTE));

        axis.setTickMarkPosition(DateTickMarkPosition.END);
        Date psd3 = axis.previousStandardDate(d1, unit);
        calendar.setTime(psd3);
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.AUGUST, calendar.get(Calendar.MONTH));
        assertEquals(25, calendar.get(Calendar.DATE));
        assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(59, calendar.get(Calendar.MINUTE));
    }

}
