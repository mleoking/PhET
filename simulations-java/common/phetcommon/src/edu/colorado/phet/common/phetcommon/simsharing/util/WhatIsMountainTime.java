// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Look up ip address
 * See http://www.whatismyip.com/faq/automation.asp
 *
 * @author Sam Reid
 */
public class WhatIsMountainTime {

    public static boolean enabled = Boolean.getBoolean( System.getProperty( "readTime", "true" ) );

    public static String whatIsMountainTime() {
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( new URL( "http://tycho.usno.navy.mil/cgi-bin/timer.pl" ).openConnection().getInputStream() ) );
            String line = reader.readLine().trim();
            while ( line != null ) {
                line = reader.readLine().trim();
                if ( line.endsWith( "Mountain Time" ) ) {
                    reader.close();
                    try {
                        return parseLine( line ).getTime() + "";
                    }
                    catch ( ParseException e ) {
                        e.printStackTrace();
                        return "unparseable: " + line;
                    }
                }
            }
            reader.close();
            return "no time line found";
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private static Date parseLine( String line ) throws ParseException {
        int start = line.indexOf( ">" ) + 1;
        int end = line.lastIndexOf( "\t" );

        //TODO: have to add the year ourselves.  This is not good, it will have to be updated every year and rebuild sims.  Maybe we could find a server that includes the year?
        //Maybe could switch to a server like NTP or http://developer.yahoo.com/util/timeservice/V1/getTime.html
        String substring = line.substring( start, end ) + " 2012";
        System.out.println( "substring = " + substring );

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM. dd, hh:mm:ss a z yyyy" );
//        System.out.println( "simpleDateFormat.format( new Date(  ) ) = " + simpleDateFormat.format( new Date() ) );
        Date date = simpleDateFormat.parse( substring );
        return date;
    }

    public static void main( String[] args ) throws ParseException {
        String s = "<BR>Jan. 13, 05:08:54 PM MST\tMountain Time";
        Date time = parseLine( s );
        Date now = new Date();

        System.out.println( "now.getTime() = " + now.getTime() );
        System.out.println( "parsed.getTime() = " + time.getTime() );
    }
}