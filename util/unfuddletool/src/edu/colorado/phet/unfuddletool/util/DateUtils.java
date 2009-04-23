package edu.colorado.phet.unfuddletool.util;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    public static String compactDate( Date lastDate ) {
        Calendar newCal = new GregorianCalendar();
        Calendar oldCal = new GregorianCalendar();
        oldCal.setTime( lastDate );

        StringBuffer buf = new StringBuffer();

        if ( newCal.get( Calendar.YEAR ) != oldCal.get( Calendar.YEAR ) ) {
            ( new SimpleDateFormat( "E MMM dd HH:mm:ss yyyy" ) ).format( lastDate, buf, new FieldPosition( 0 ) );
        }
        else if ( newCal.get( Calendar.MONTH ) != oldCal.get( Calendar.MONTH ) ) {
            ( new SimpleDateFormat( "E MMM dd HH:mm:ss" ) ).format( lastDate, buf, new FieldPosition( 0 ) );
        }
        else if ( newCal.get( Calendar.DAY_OF_MONTH ) != oldCal.get( Calendar.DAY_OF_MONTH ) ) {
            ( new SimpleDateFormat( "E dd HH:mm:ss" ) ).format( lastDate, buf, new FieldPosition( 0 ) );
        }
        else {
            ( new SimpleDateFormat( "'Today' HH:mm:ss" ) ).format( lastDate, buf, new FieldPosition( 0 ) );
        }

        return buf.toString();
    }
}
