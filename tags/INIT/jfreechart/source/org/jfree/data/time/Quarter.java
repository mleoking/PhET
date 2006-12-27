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
 * ------------
 * Quarter.java
 * ------------
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
 * 18-Dec-2001 : Changed order of parameters in constructor (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 29-Jan-2002 : Added a new method parseQuarter(String) (DG);
 * 14-Feb-2002 : Fixed bug in Quarter(Date) constructor (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to 
 *               evaluate with reference to a particular time zone (DG);
 * 19-Mar-2002 : Changed API for TimePeriod classes (DG);
 * 24-Jun-2002 : Removed main method (just test code) (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
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
 * Defines a quarter (in a given year).  The range supported is Q1 1900 to 
 * Q4 9999.  This class is immutable, which is a requirement for all 
 * {@link RegularTimePeriod} subclasses.
 */
public class Quarter extends RegularTimePeriod implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 3810061714380888671L;
    
    /** Constant for quarter 1. */
    public static final int FIRST_QUARTER = 1;

    /** Constant for quarter 4. */
    public static final int LAST_QUARTER = 4;

    /** The first month in each quarter. */
    public static final int[] FIRST_MONTH_IN_QUARTER = {
        0, MonthConstants.JANUARY, MonthConstants.APRIL, MonthConstants.JULY, 
        MonthConstants.OCTOBER
    };

    /** The last month in each quarter. */
    public static final int[] LAST_MONTH_IN_QUARTER = {
        0, MonthConstants.MARCH, MonthConstants.JUNE, MonthConstants.SEPTEMBER, 
        MonthConstants.DECEMBER
    };

    /** The year in which the quarter falls. */
    private Year year;

    /** The quarter (1-4). */
    private int quarter;

    /**
     * Constructs a new Quarter, based on the current system date/time.
     */
    public Quarter() {
        this(new Date());
    }

    /**
     * Constructs a new quarter.
     *
     * @param year  the year (1900 to 9999).
     * @param quarter  the quarter (1 to 4).
     */
    public Quarter(int quarter, int year) {
        this(quarter, new Year(year));
    }

    /**
     * Constructs a new quarter.
     *
     * @param quarter  the quarter (1 to 4).
     * @param year  the year (1900 to 9999).
     */
    public Quarter(int quarter, Year year) {
        if ((quarter < FIRST_QUARTER) && (quarter > LAST_QUARTER)) {
            throw new IllegalArgumentException("Quarter outside valid range.");
        }
        this.year = year;
        this.quarter = quarter;
    }

    /**
     * Constructs a new Quarter, based on a date/time and the default time zone.
     *
     * @param time  the date/time.
     */
    public Quarter(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Constructs a Quarter, based on a date/time and time zone.
     *
     * @param time  the date/time.
     * @param zone  the zone.
     */
    public Quarter(Date time, TimeZone zone) {

        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        int month = calendar.get(Calendar.MONTH) + 1;
        this.quarter = SerialDate.monthCodeToQuarter(month);
        this.year = new Year(calendar.get(Calendar.YEAR));

    }

    /**
     * Returns the quarter.
     *
     * @return The quarter.
     */
    public int getQuarter() {
        return this.quarter;
    }

    /**
     * Returns the year.
     *
     * @return The year.
     */
    public Year getYear() {
        return this.year;
    }

    /**
     * Returns the quarter preceding this one.
     *
     * @return The quarter preceding this one (or null if this is Q1 1900).
     */
    public RegularTimePeriod previous() {

        Quarter result;
        if (this.quarter > FIRST_QUARTER) {
            result = new Quarter(this.quarter - 1, this.year);
        }
        else {
            Year prevYear = (Year) this.year.previous();
            if (prevYear != null) {
                result = new Quarter(LAST_QUARTER, prevYear);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    /**
     * Returns the quarter following this one.
     *
     * @return The quarter following this one (or null if this is Q4 9999).
     */
    public RegularTimePeriod next() {

        Quarter result;
        if (this.quarter < LAST_QUARTER) {
            result = new Quarter(this.quarter + 1, this.year);
        }
        else {
            Year nextYear = (Year) this.year.next();
            if (nextYear != null) {
                result = new Quarter(FIRST_QUARTER, nextYear);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    /**
     * Returns a serial index number for the quarter.
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.year.getYear() * 4L + this.quarter;
    }

    /**
     * Tests the equality of this Quarter object to an arbitrary object.
     * Returns true if the target is a Quarter instance representing the same
     * quarter as this object.  In all other cases, returns false.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> if quarter and year of this and the object are
     *         the same.
     */
    public boolean equals(Object obj) {

        if (obj != null) {
            if (obj instanceof Quarter) {
                Quarter target = (Quarter) obj;
                return (
                    (this.quarter == target.getQuarter()) 
                    && (this.year.equals(target.getYear()))
                );
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
     * /Chapter3.pdf</code>
     * 
     * @return A hash code.
     */
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.quarter;
        result = 37 * result + this.year.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Quarter object relative
     * to the specified object:
     *
     * negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Quarter object
        // --------------------------------------------
        if (o1 instanceof Quarter) {
            Quarter q = (Quarter) o1;
            result = this.year.getYear() - q.getYear().getYear();
            if (result == 0) {
                result = this.quarter - q.getQuarter();
            }
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
     * Returns a string representing the quarter (e.g. "Q1/2002").
     *
     * @return A string representing the quarter.
     */
    public String toString() {
        return "Q" + this.quarter + "/" + this.year;
    }

    /**
     * Returns the first millisecond in the Quarter, evaluated using the
     * supplied calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return The first millisecond in the Quarter.
     */
    public long getFirstMillisecond(Calendar calendar) {

        int month = Quarter.FIRST_MONTH_IN_QUARTER[this.quarter];
        Day first = new Day(1, month, this.year.getYear());
        return first.getFirstMillisecond(calendar);

    }

    /**
     * Returns the last millisecond of the Quarter, evaluated using the
     * supplied calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return The last millisecond of the Quarter.
     */
    public long getLastMillisecond(Calendar calendar) {

        int month = Quarter.LAST_MONTH_IN_QUARTER[this.quarter];
        int eom = SerialDate.lastDayOfMonth(month, this.year.getYear());
        Day last = new Day(eom, month, this.year.getYear());
        return last.getLastMillisecond(calendar);

    }

    /**
     * Parses the string argument as a quarter.
     * <P>
     * This method should accept the following formats: "YYYY-QN" and "QN-YYYY",
     * where the "-" can be a space, a forward-slash (/), comma or a dash (-).
     * @param s A string representing the quarter.
     *
     * @return The quarter.
     */
    public static Quarter parseQuarter(String s) {

        // find the Q and the integer following it (remove both from the
        // string)...
        int i = s.indexOf("Q");
        if (i == -1) {
            throw new TimePeriodFormatException("Missing Q.");
        }

        if (i == s.length() - 1) {
            throw new TimePeriodFormatException("Q found at end of string.");
        }

        String qstr = s.substring(i + 1, i + 2);
        int quarter = Integer.parseInt(qstr);
        String remaining = s.substring(0, i) + s.substring(i + 2, s.length());

        // replace any / , or - with a space
        remaining = remaining.replace('/', ' ');
        remaining = remaining.replace(',', ' ');
        remaining = remaining.replace('-', ' ');

        // parse the string...
        Year year = Year.parseYear(remaining.trim());
        Quarter result = new Quarter(quarter, year);
        return result;

    }

}
