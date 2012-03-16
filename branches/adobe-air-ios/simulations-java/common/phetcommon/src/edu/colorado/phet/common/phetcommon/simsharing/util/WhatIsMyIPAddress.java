// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Look up ip address
 * See http://www.whatismyip.com/faq/automation.asp
 *
 * @author Sam Reid
 */
public class WhatIsMyIPAddress {

    public static boolean enabled = Boolean.getBoolean( System.getProperty( "readIp", "false" ) );

    public static String whatIsMyIPAddress() {
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( new URL( "http://automation.whatismyip.com/n09230945.asp" ).openConnection().getInputStream() ) );
            final String line = reader.readLine();
            reader.close();
            return line;
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}