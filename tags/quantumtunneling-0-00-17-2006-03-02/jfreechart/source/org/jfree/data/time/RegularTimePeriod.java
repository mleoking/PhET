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
 * ----------------------
 * RegularTimePeriod.java
 * ----------------------
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
 * 26-Feb-2002 : Changed getStart(), getMiddle() and getEnd() methods to 
 *               evaluate with reference to a particular time zone (DG);
 * 29-May-2002 : Implemented MonthConstants interface, so that these constants 
 *               are conveniently available (DG);
 * 10-Sep-2002 : Added getSerialIndex() method (DG);
 * 10-Jan-2003 : Renamed TimePeriod --> RegularTimePeriod (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package (DG);
 * 29-Apr-2004 : Changed getMiddleMillisecond() methods to fix bug 943985 (DG);
 * 25-Nov-2004 : Added utility methods (DG);
 *
 */

package org.jfree.data.time;

import java.lang.reflect.Constructor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jfree.date.MonthConstants;

/**
 * An abstract class representing a unit of time.  Convenient methods are 
 * provided for calculating the next and previous time periods.  Conversion 
 * methods are defined that return the first and last milliseconds of the time 
 * period.  The results from these methods are timezone dependent.
 * <P>
 * This class is immutable, and all subclasses should be immutable also.
 */
public abstract class RegularTimePeriod implements TimePeriod, Comparable, 
                                                   MonthConstants {

    /**
     * Creates a time period that includes the specified millisecond, assuming 
     * the given time zone.
     * 
     * @param c  the time period class.
     * @param millisecond  the time.
     * @param zone  the time zone.
     * 
     * @return The time period.
     */
    public static RegularTimePeriod createInstance(Class c, Date millisecond, 
                                                   TimeZone zone) {
        RegularTimePeriod result = null;
        try {
            Constructor constructor = c.getDeclaredConstructor(
                new Class[] {Date.class, TimeZone.class}
            );
            result = (RegularTimePeriod) constructor.newInstance(
                new Object[] {millisecond, zone}
            );
        }
        catch (Exception e) {
            // do nothing, so null is returned            
        }
        return result;  
    }
    
    /**
     * Returns a subclass of {@link RegularTimePeriod} that is smaller than
     * the specified class.
     * 
     * @param c  a subclass of {@link RegularTimePeriod}.
     * 
     * @return A class.
     */
    public static Class downsize(Class c) {
        if (c.equals(Year.class)) {
            return Quarter.class;
        }
        else if (c.equals(Quarter.class)) {
            return Month.class;
        }
        else if (c.equals(Month.class)) {
            return Day.class;
        }
        else if (c.equals(Day.class)) {
            return Hour.class;
        }
        else if (c.equals(Hour.class)) {
            return Minute.class;
        }
        else if (c.equals(Minute.class)) {
            return Second.class;
        }
        else if (c.equals(Second.class)) {
            return Millisecond.class;
        }
        else {
            return Millisecond.class;
        }
    }
    
    /**
     * Returns the time period preceding this one, or <code>null</code> if some
     * lower limit has been reached.
     *
     * @return The previous time period (possibly <code>null</code>).
     */
    public abstract RegularTimePeriod previous();

    /**
     * Returns the time period following this one, or <code>null</code> if some
     * limit has been reached.
     *
     * @return The next time period (possibly <code>null</code>).
     */
    public abstract RegularTimePeriod next();

    /**
     * Returns a serial index number for the time unit.
     *
     * @return The serial index number.
     */
    public abstract long getSerialIndex();

    //////////////////////////////////////////////////////////////////////////

    /** The default time zone. */
    public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getDefault();

    /** A working calendar (recycle to avoid unnecessary object creation). */
    public static final Calendar WORKING_CALENDAR 
        = Calendar.getInstance(DEFAULT_TIME_ZONE);

    /**
     * Returns the date/time that marks the start of the time period.
     *
     * @return The start date/time.
     */
    public Date getStart() {
        return new Date(getFirstMillisecond());
    }

    /**
     * Returns the date/time that marks the end of the time period.
     *
     * @return The end date/time.
     */
    public Date getEnd() {
        return new Date(getLastMillisecond());
    }

    /**
     * Returns the first millisecond of the time period, evaluated in the 
     * default time zone.
     *
     * @return The first millisecond of the time period.
     */
    public long getFirstMillisecond() {
        return getFirstMillisecond(DEFAULT_TIME_ZONE);
    }

    /**
     * Returns the first millisecond of the time period, evaluated within a 
     * specific time zone.
     *
     * @param zone  the time zone.
     *
     * @return The first millisecond of the time period.
     */
    public long getFirstMillisecond(TimeZone zone) {
        WORKING_CALENDAR.setTimeZone(zone);
        return getFirstMillisecond(WORKING_CALENDAR);
    }

    /**
     * Returns the first millisecond of the time period, evaluated using the 
     * supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return The first millisecond of the time period.
     */
    public abstract long getFirstMillisecond(Calendar calendar);

    /**
     * Returns the last millisecond of the time period, evaluated in the 
     * default time zone.
     *
     * @return The last millisecond of the time period.
     */
    public long getLastMillisecond() {
        return getLastMillisecond(DEFAULT_TIME_ZONE);
    }

    /**
     * Returns the last millisecond of the time period, evaluated within a 
     * specific time zone.
     *
     * @param zone  the time zone.
     *
     * @return The last millisecond of the time period.
     */
    public long getLastMillisecond(TimeZone zone) {
        WORKING_CALENDAR.setTimeZone(zone);
        return getLastMillisecond(WORKING_CALENDAR);
    }

    /**
     * Returns the last millisecond of the time period, evaluated using the 
     * supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return The last millisecond of the time period.
     */
    public abstract long getLastMillisecond(Calendar calendar);

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated in the default time zone.
     *
     * @return The middle millisecond.
     */
    public long getMiddleMillisecond() {
        long m1 = getFirstMillisecond();
        long m2 = getLastMillisecond();
        return m1 + (m2 - m1) / 2;
    }

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated within a specific time zone.
     *
     * @param zone  the time zone.
     *
     * @return The middle millisecond.
     */
    public long getMiddleMillisecond(TimeZone zone) {
        long m1 = getFirstMillisecond(zone);
        long m2 = getLastMillisecond(zone);
        return m1 + (m2 - m1) / 2;
    }

    /**
     * Returns the millisecond closest to the middle of the time period,
     * evaluated using the supplied calendar (which incorporates a timezone).
     *
     * @param calendar  the calendar.
     *
     * @return The middle millisecond.
     */
    public long getMiddleMillisecond(Calendar calendar) {
        long m1 = getFirstMillisecond(calendar);
        long m2 = getLastMillisecond(calendar);
        return m1 + (m2 - m1) / 2;
    }

    /**
     * Returns a string representation of the time period.
     *
     * @return The string.
     */
    public String toString() {
        return String.valueOf(getStart());
    }

}
