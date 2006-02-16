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
 * ----------
 * Month.java
 * ----------
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
 * 14-Nov-2001 : Added method to get year as primitive (DG);
 *               Override for toString() method (DG);
 * 18-Dec-2001 : Changed order of parameters in constructor (DG);
 * 19-Dec-2001 : Added a new constructor as suggested by Paul English (DG);
 * 29-Jan-2002 : Worked on the parseMonth() method (DG);
 * 14-Feb-2002 : Fixed bugs in the Month constructors (DG);
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to 
 *               evaluate with reference to a particular time zone (DG);
 * 19-Mar-2002 : Changed API for TimePeriod classes (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 04-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package, and implemented 
 *               Serializable (DG);
 * 21-Oct-2003 : Added hashCode() method (DG);
 * 01-Nov-2005 : Fixed bug 1345383 (argument check in constructor) (DG);
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
 * Represents a single month.  This class is immutable, which is a requirement
 * for all {@link RegularTimePeriod} subclasses.
 */
public class Month extends RegularTimePeriod implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -5090216912548722570L;

    /** The month (1-12). */
    private int month;

    /** The year in which the month falls. */
    private Year year;

    /**
     * Constructs a new Month, based on the current system time.
     */
    public Month() {
        this(new Date());
    }

    /**
     * Constructs a new month instance.
     *
     * @param month  the month (in the range 1 to 12).
     * @param year  the year.
     */
    public Month(int month, int year) {
        this(month, new Year(year));
    }

    /**
     * Constructs a new month instance.
     *
     * @param month  the month (in the range 1 to 12).
     * @param year  the year.
     */
    public Month(int month, Year year) {

        if ((month < 1) || (month > 12)) {
            throw new IllegalArgumentException("Month outside valid range.");
        }
        this.month = month;
        this.year = year;

    }

    /**
     * Constructs a new month instance, based on a date/time and the default 
     * time zone.
     *
     * @param time  the date/time.
     */
    public Month(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Constructs a Month, based on a date/time and a time zone.
     *
     * @param time  the date/time.
     * @param zone  the time zone.
     */
    public Month(Date time, TimeZone zone) {
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.year = new Year(calendar.get(Calendar.YEAR));
    }

    /**
     * Returns the year in which the month falls.
     *
     * @return The year in which the month falls (as a Year object).
     */
    public Year getYear() {
        return this.year;
    }

    /**
     * Returns the year in which the month falls.
     *
     * @return The year in which the monht falls (as an int).
     */
    public int getYearValue() {
        return this.year.getYear();
    }

    /**
     * Returns the month.  Note that 1=JAN, 2=FEB, ...
     *
     * @return The month.
     */
    public int getMonth() {
        return this.month;
    }

    /**
     * Returns the month preceding this one.
     *
     * @return The month preceding this one.
     */
    public RegularTimePeriod previous() {

        Month result;
        if (this.month != MonthConstants.JANUARY) {
            result = new Month(this.month - 1, this.year);
        }
        else {
            Year prevYear = (Year) this.year.previous();
            if (prevYear != null) {
                result = new Month(MonthConstants.DECEMBER, prevYear);
            }
            else {
                result = null;
            }
        }
        return result;

    }

    /**
     * Returns the month following this one.
     *
     * @return The month following this one.
     */
    public RegularTimePeriod next() {
        Month result;
        if (this.month != MonthConstants.DECEMBER) {
            result = new Month(this.month + 1, this.year);
        }
        else {
            Year nextYear = (Year) this.year.next();
            if (nextYear != null) {
                result = new Month(MonthConstants.JANUARY, nextYear);
            }
            else {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns a serial index number for the month.
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.year.getYear() * 12L + this.month;
    }

    /**
     * Returns a string representing the month (e.g. "January 2002").
     * <P>
     * To do: look at internationalisation.
     *
     * @return A string representing the month.
     */
    public String toString() {
        return SerialDate.monthCodeToString(this.month) + " " + this.year;
    }

    /**
     * Tests the equality of this Month object to an arbitrary object.
     * Returns true if the target is a Month instance representing the same
     * month as this object.  In all other cases, returns false.
     *
     * @param obj  the object.
     *
     * @return <code>true</code> if month and year of this and object are the 
     *         same.
     */
    public boolean equals(Object obj) {

        if (obj != null) {
            if (obj instanceof Month) {
                Month target = (Month) obj;
                return (
                    (this.month == target.getMonth()) 
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
        result = 37 * result + this.month;
        result = 37 * result + this.year.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Month object relative to
     * the specified
     * object: negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object o1) {

        int result;

        // CASE 1 : Comparing to another Month object
        // --------------------------------------------
        if (o1 instanceof Month) {
            Month m = (Month) o1;
            result = this.year.getYear() - m.getYear().getYear();
            if (result == 0) {
                result = this.month - m.getMonth();
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
     * Returns the first millisecond of the month, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return The first millisecond of the month.
     */
    public long getFirstMillisecond(Calendar calendar) {
        Day first = new Day(1, this.month, this.year.getYear());
        return first.getFirstMillisecond(calendar);
    }

    /**
     * Returns the last millisecond of the month, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  the calendar.
     *
     * @return The last millisecond of the month.
     */
    public long getLastMillisecond(Calendar calendar) {
        int eom = SerialDate.lastDayOfMonth(this.month, this.year.getYear());
        Day last = new Day(eom, this.month, this.year.getYear());
        return last.getLastMillisecond(calendar);
    }

    /**
     * Parses the string argument as a month.
     * <P>
     * This method is required to accept the format "YYYY-MM".  It will also
     * accept "MM-YYYY". Anything else, at the moment, is a bonus.
     *
     * @param s  the string to parse.
     *
     * @return <code>null</code> if the string is not parseable, the month 
     *         otherwise.
     */
    public static Month parseMonth(String s) {

        Month result = null;
        if (s != null) {

            // trim whitespace from either end of the string
            s = s.trim();

            int i = Month.findSeparator(s);
            if (i != -1) {
                String s1 = s.substring(0, i).trim();
                String s2 = s.substring(i + 1, s.length()).trim();

                Year year = Month.evaluateAsYear(s1);
                int month;
                if (year != null) {
                    month = SerialDate.stringToMonthCode(s2);
                    if (month == -1) {
                        throw new TimePeriodFormatException(
                            "Can't evaluate the month."
                        );
                    }
                    result = new Month(month, year);
                }
                else {
                    year = Month.evaluateAsYear(s2);
                    if (year != null) {
                        month = SerialDate.stringToMonthCode(s1);
                        if (month == -1) {
                            throw new TimePeriodFormatException(
                                "Can't evaluate the month."
                            );
                        }
                        result = new Month(month, year);
                    }
                    else {
                        throw new TimePeriodFormatException(
                            "Can't evaluate the year."
                        );
                    }
                }

            }
            else {
                throw new TimePeriodFormatException(
                    "Could not find separator."
                );
            }

        }
        return result;

    }

    /**
     * Finds the first occurrence of ' ', '-', ',' or '.'
     *
     * @param s  the string to parse.
     * 
     * @return <code>-1</code> if none of the characters where found, the
     *      position of the first occurence otherwise.
     */
    private static int findSeparator(String s) {

        int result = s.indexOf('-');
        if (result == -1) {
            result = s.indexOf(',');
        }
        if (result == -1) {
            result = s.indexOf(' ');
        }
        if (result == -1) {
            result = s.indexOf('.');
        }
        return result;
    }

    /**
     * Creates a year from a string, or returns null (format exceptions 
     * suppressed).
     *
     * @param s  the string to parse.
     *
     * @return <code>null</code> if the string is not parseable, the year 
     *         otherwise.
     */
    private static Year evaluateAsYear(String s) {

        Year result = null;
        try {
            result = Year.parseYear(s);
        }
        catch (TimePeriodFormatException e) {
            // suppress
        }
        return result;

    }

}
