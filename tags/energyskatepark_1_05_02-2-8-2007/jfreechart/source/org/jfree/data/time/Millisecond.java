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
 * ----------------
 * Millisecond.java
 * ----------------
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
 * 19-Dec-2001 : Added new constructors as suggested by Paul English (DG);
 * 26-Feb-2002 : Added new getStart() and getEnd() methods (DG);
 * 29-Mar-2002 : Fixed bug in getStart(), getEnd() and compareTo() methods (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Jan-2003 : Changed base class and method names (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package and implemented 
 *               Serializable (DG);
 * 21-Oct-2003 : Added hashCode() method (DG);
 *
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a millisecond.  This class is immutable, which is a requirement 
 * for all {@link RegularTimePeriod} subclasses.
 */
public class Millisecond extends RegularTimePeriod implements Serializable {

    /** For serialization. */
    static final long serialVersionUID = -5316836467277638485L;
    
    /** A constant for the first millisecond in a second. */
    public static final int FIRST_MILLISECOND_IN_SECOND = 0;

    /** A constant for the last millisecond in a second. */
    public static final int LAST_MILLISECOND_IN_SECOND = 999;

    /** The millisecond. */
    private int millisecond;

    /** The second. */
    private Second second;

    /**
     * Constructs a millisecond based on the current system time.
     */
    public Millisecond() {
        this(new Date());
    }

    /**
     * Constructs a millisecond.
     *
     * @param millisecond  the millisecond (0-999).
     * @param second  the second.
     */
    public Millisecond(int millisecond, Second second) {
        this.millisecond = millisecond;
        this.second = second;
    }

    /**
     * Creates a new millisecond.
     * 
     * @param millisecond  the millisecond (0-999).
     * @param second  the second (0-59).
     * @param minute  the minute (0-59).
     * @param hour  the hour (0-23).
     * @param day  the day (1-31).
     * @param month  the month (1-12).
     * @param year  the year (1900-9999).
     */    
    public Millisecond(int millisecond, int second, int minute, int hour,
                       int day, int month, int year) {
                           
        this(millisecond, new Second(second, minute, hour, day, month, year));
    
    }

    /**
     * Constructs a millisecond.
     *
     * @param time  the time.
     */
    public Millisecond(Date time) {
        this(time, RegularTimePeriod.DEFAULT_TIME_ZONE);
    }

    /**
     * Creates a millisecond.
     *
     * @param time  the instant in time.
     * @param zone  the time zone.
     */
    public Millisecond(Date time, TimeZone zone) {

        this.second = new Second(time, zone);
        Calendar calendar = Calendar.getInstance(zone);
        calendar.setTime(time);
        this.millisecond = calendar.get(Calendar.MILLISECOND);

    }

    /**
     * Returns the second.
     *
     * @return The second.
     */
    public Second getSecond() {
        return this.second;
    }

    /**
     * Returns the millisecond.
     *
     * @return The millisecond.
     */
    public long getMillisecond() {
        return this.millisecond;
    }

    /**
     * Returns the millisecond preceding this one.
     *
     * @return The millisecond preceding this one.
     */
    public RegularTimePeriod previous() {

        RegularTimePeriod result = null;

        if (this.millisecond != FIRST_MILLISECOND_IN_SECOND) {
            result = new Millisecond(this.millisecond - 1, this.second);
        }
        else {
            Second previous = (Second) this.second.previous();
            if (previous != null) {
                result = new Millisecond(LAST_MILLISECOND_IN_SECOND, previous);
            }
        }
        return result;

    }

    /**
     * Returns the millisecond following this one.
     *
     * @return The millisecond following this one.
     */
    public RegularTimePeriod next() {

        RegularTimePeriod result = null;
        if (this.millisecond != LAST_MILLISECOND_IN_SECOND) {
            result = new Millisecond(this.millisecond + 1, this.second);
        }
        else {
            Second next = (Second) this.second.next();
            if (next != null) {
                result = new Millisecond(FIRST_MILLISECOND_IN_SECOND, next);
            }
        }
        return result;

    }

    /**
     * Returns a serial index number for the millisecond.
     *
     * @return The serial index number.
     */
    public long getSerialIndex() {
        return this.second.getSerialIndex() * 1000L + this.millisecond;
    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is a Millisecond object
     * representing the same millisecond as this instance.
     *
     * @param obj  the object to compare
     *
     * @return <code>true</code> if milliseconds and seconds of this and object
     *      are the same.
     */
    public boolean equals(Object obj) {

        if (obj instanceof Millisecond) {
            Millisecond m = (Millisecond) obj;
            return ((this.millisecond == m.getMillisecond())
                    && (this.second.equals(m.getSecond())));
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
     * @return A hashcode.
     */
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.millisecond;
        result = 37 * result + this.second.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Millisecond object
     * relative to the specified object:
     *
     * negative == before, zero == same, positive == after.
     *
     * @param obj  the object to compare
     *
     * @return negative == before, zero == same, positive == after.
     */
    public int compareTo(Object obj) {

        int result;
        long difference;

        // CASE 1 : Comparing to another Second object
        // -------------------------------------------
        if (obj instanceof Millisecond) {
            Millisecond ms = (Millisecond) obj;
            difference = getFirstMillisecond() - ms.getFirstMillisecond();
            if (difference > 0) {
                result = 1;
            }
            else {
                if (difference < 0) {
                    result = -1;
                }
                else {
                    result = 0;
                }
            }
        }

        // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        else if (obj instanceof RegularTimePeriod) {
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
     * Returns the first millisecond of the time period.
     *
     * @return The first millisecond of the time period.
     */
    public long getFirstMillisecond() {
        return this.second.getFirstMillisecond() + this.millisecond;
    }

    /**
     * Returns the first millisecond of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return The first millisecond of the time period.
     */
    public long getFirstMillisecond(Calendar calendar) {
        return this.second.getFirstMillisecond(calendar) + this.millisecond;
    }

    /**
     * Returns the last millisecond of the time period.
     *
     * @return The last millisecond of the time period.
     */
    public long getLastMillisecond() {
        return this.second.getFirstMillisecond() + this.millisecond;
    }

    /**
     * Returns the last millisecond of the time period.
     *
     * @param calendar  the calendar.
     *
     * @return The last millisecond of the time period.
     */
    public long getLastMillisecond(Calendar calendar) {
        return this.second.getFirstMillisecond(calendar) + this.millisecond;
    }

}
