package edu.colorado.phet.unfuddletool.data;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime {
    public String rawString;

    public java.util.Date date;

    public DateTime( String raw ) {
        rawString = raw;
        //System.out.println( "Reading date: " + raw );
        if( raw == null ) {
            date = new Date();
            return;
        }

        if ( raw.endsWith( "Z" ) ) {
            SimpleDateFormat format = new SimpleDateFormat( "z yyyy-MM-dd-HH:mm:ss" );
            date = format.parse( "GMT " + raw.replace( 'T', '-' ), new ParsePosition( 0 ) );
        }
        else {
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd-HH:mm:ss-z" );
            String tmp = raw.replace( 'T', '-' );
            int lastIdx = tmp.lastIndexOf( '-' );
            tmp = tmp.substring( 0, lastIdx ) + "-GMT" + tmp.substring( lastIdx );
            //System.out.println( "Date tmp: " + tmp );
            date = format.parse( tmp, new ParsePosition( 0 ) );
        }

        //System.out.println( "Writing date: " + date );
    }

    public String toString() {
        return date.toString();
    }

    public java.util.Date getDate() {
        return date;
    }

    public boolean equals( DateTime other ) {
        return getDate().equals( other.getDate() );
    }

    public static void main( String[] args ) {
        String[] tests = new String[]{"2009-05-10T11:43:15Z", "2009-05-10T11:43:15Z", "2009-05-10T04:43:15-07:00", "2009-05-10T12:10:21Z"};

        for ( int i = 0; i < tests.length; i++ ) {
            String test = tests[i];

            System.out.println( test + " => " + ( new DateTime( test ) ) );
        }
    }
}
