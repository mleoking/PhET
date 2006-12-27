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
 * ---------
 * Year.java
 * ---------
 * (C) Copyright 2001-2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 14-Nov-2001 : Override for toString() method (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 29-Jan-2002 : Worked on parseYear() method (DG);
 * 14-Feb-2002 : Fixed bug in Year(Date) constructor (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to 
 *               evaluate with reference to a particular time zone (DG);
 * 19-Mar-2002 : Changed API for TimePeriod classes (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
 * 05-Mar-2003 : Fixed bug in getFirstMillisecond() picked up in JUnit 
 *               tests (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package, and implemented 
 *               Serializable (DG);
 * 21-Oct-2003 : Added hashCode() method (DG);
 * 
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.date.MonthConstants;
import org.jfree.date.SerialDate;

/**
 * Represents a year in the range 1900 to 9999.  This class is immutable, which
 * is a requirement for all {@link RegularTimePeriod} subclasses.
 */
public class Year extends RegularTimePeriod implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -7659990929736074836L;
    
    /** The year. */
    private int year;

    /**
     * Creates a new <code>Year</code>, based on the current system date/time.
     */
    public Year() {
        this(new Date());
    }

    /**
     * Creates a time period representing a single year.
     *
     * @param year  the year.
     */
    public Year(int year) {

        // check arguments...
        if ((year < SerialDate.MINIMUM_YEAR_SUPPORTED)
            || (year > SerialDate.MAXIMUM_YEAR_SUPPORTED)) {

            throw new IllegalArgumentException(
                "Year constructor: year (" + year + ") outside valid range.");
        }

        // initialise...
        this.year = year;

    }

    /**
     * Creates a new <code>Year</code>, based on a particular instant in time, 
     * using the default time zone.
     *
     * @param time  the time.
     */
    public Year(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Constructs a year, based on a particular instant in time and a time zone.
     *
     * @param time  the time.
     * @param zone  the time zone.
     */
    public Year(Date time, TimeZone zone) {

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.year = calendar.get(Calendar.YEAR);

    }

    /**
     * Returns the year.
     *
     * @return The year.
     */
    public int getYear() {
        return this.year;
    }

    /**
     * Returns the year preceding this one.
     *
     * @return The year preceding this one (or <code>null</code> if the 
     *         current year is 1900).
     */
    public RegularTimePeriod previous() {
        if (this.year > SerialDate.MINIMUM_YEAR_SUPPORTED) {
            return new Year(this.year - 1);
        }
        else {
            return null;
        }
    }

    /**
     * Returns the year following this one.
     *
     * @return The year following this one (or <code>null</code> if the current
     *         year is 9999).
     */
    public RegularTimePeriod next() {
        if (this.year < SerialDate.MAXIMUM_YEAR_SUPPORTED) {
            return new Year(this.year + 1);
        }
        else {
            return null;
        }
    }

    /**
     * Returns a serial index number for the year.
     * <P>
     * The implementation simply returns the year number (e.g. 2002).
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.year;
    }

    /**
     * Returns the first millisecond of the year, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return The first millisecond of the year.
     */
    public long getFirstMillisecond(Calendar calendar) {
        Day jan1 = new Day(1, MonthConstants.JANUARY, this.year);
        return jan1.getFirstMillisecond(calendar);
    }

    /**
     * Returns the last millisecond of the year, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return The last millisecond of the year.
     */
    public long getLastMillisecond(Calendar calendar) {
        Day dec31 = new Day(31, MonthConstants.DECEMBER, this.year);
        return dec31.getLastMillisecond(calendar);
    }

    /**
     * Tests the equality of this <code>Year</code> object to an arbitrary 
     * object.  Returns <code>true</code> if the target is a <code>Year</code>
     * instance representing the same year as this object.  In all other cases,
     * returns <code>false</code>.
     *
     * @param object  the object.
     *
     * @return <code>true</code> if the year of this and the object are the 
     *         same.
     */
    public boolean equals(Object object) {
        if (object != null) {
            if (object instanceof Year) {
                Year target = (Year) object;
                return (this.year == target.getYear());
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    /**
     * Returns a hash code for this object instance.  The approach described by
     * Joshua Bloch in "Effective Java" has been used here:
     * <p>
     * <code>http://developer.java.sun.com/developer/Books/effectivejava
     *     /Chapter3.pdf</code>
     * 
     * @return A hash code.
     */
    public int hashCode() {
        int result = 17;
        int c = this.year;
        result = 37 * result + c;
        return result;
    }

    /**
     * Returns an integer indicating the order of this <code>Year</code> object
     * relative to the specified object:
     *
     * negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Year object
        // -----------------------------------------
        if (o1 instanceof Year) {
            Year y = (Year) o1;
            result = this.year - y.getYear();
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (o1 instanceof RegularTimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        }

        // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        else {
            // consider time periods to be ordered after general objects
            result = 1;
        }

        return result;

    }

    /**
     * Returns a string representing the year..
     *
     * @return A string representing the year.
     */
    public String toString() {
        return Integer.toString(this.year);
    }

    /**
     * Parses the string argument as a year.
     * <P>
     * The string format is YYYY.
     *
     * @param s  a string representing the year.
     *
     * @return <code>null</code> if the string is not parseable, the year 
     *         otherwise.
     */
    public static Year parseYear(String s) {

        // parse the string...
        int y;
        try {
            y = Integer.parseInt(s.trim());
        }
        catch (NumberFormatException e) {
            throw new TimePeriodFormatException("Cannot parse string.");
        }

        // create the year...
        try {
            return new Year(y);
        }
        catch (IllegalArgumentException e) {
            throw new TimePeriodFormatException("Year outside valid range.");
        }
    }

}
