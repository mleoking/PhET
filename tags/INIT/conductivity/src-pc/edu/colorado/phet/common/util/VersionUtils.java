/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.io.*;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Sep 15, 2003
 * Time: 2:53:11 AM
 * Copyright (c) Sep 15, 2003 by Sam Reid
 */
public class VersionUtils {
    public static class VersionInfo {
        int buildNumber;
        String buildTime;

        public VersionInfo( int buildNumber, String buildTime ) {
            this.buildNumber = buildNumber;
            this.buildTime = buildTime;
        }

        public int getBuildNumber() {
            return buildNumber;
        }

        public String getBuildTime() {
            return buildTime;
        }
    }

    public static VersionInfo readVersionInfo( PhetApplication app ) {
        ClassLoader cl = app.getClass().getClassLoader();
        URL buildNumberURL = cl.getResource( "build.number" );
        System.out.println( "buildNumberURL = " + buildNumberURL );
        int buildNum = -1;
        try {
            if( buildNumberURL == null ) {
                return null;
            }
            InputStream stream = buildNumberURL.openStream();
            if( stream == null ) {
                return null;
            }
            BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
            String line = br.readLine();
            while( line != null ) {
                if( line.toLowerCase().startsWith( "build.number=" ) ) {
                    String number = line.substring( "build.number=".length() );
                    buildNum = Integer.parseInt( number );
                }
                line = br.readLine();
            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

        InputStream buildTimeURL = cl.getResourceAsStream( "build.time.stamp.txt" );
        String buildTimeStr = "-1";
        try {
            buildTimeStr = new BufferedReader( new InputStreamReader( buildTimeURL ) ).readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return new VersionInfo( buildNum, buildTimeStr );
    }
}
