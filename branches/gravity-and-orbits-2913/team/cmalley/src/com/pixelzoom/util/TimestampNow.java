package com.pixelzoom.util;

import java.util.Date;

/**
 * Gets the timestamp that corresponds to "now".
 */
public class TimestampNow {

    public static void main( String[] args ) {
        Date date = new Date();
        System.out.println( date.toString() );
        System.out.println( date.getTime() + " ms since Epoch" );
        System.out.println( (int)( date.getTime() / 1000L ) + " sec since Epoch" );
    }
}
