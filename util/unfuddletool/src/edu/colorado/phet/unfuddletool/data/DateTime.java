package edu.colorado.phet.unfuddletool.data;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateTime {
    public String rawString;

    public java.util.Date date;

    public DateTime( String raw ) {
        rawString = raw;

        SimpleDateFormat format = new SimpleDateFormat( "z yyyy-MM-dd-hh:mm:ss" );

        date = format.parse( "GMT " + raw.replace( 'T', '-' ), new ParsePosition( 0 ) );
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
}
