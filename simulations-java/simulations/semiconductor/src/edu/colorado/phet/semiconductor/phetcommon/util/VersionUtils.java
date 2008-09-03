/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.util;

import edu.colorado.phet.semiconductor.phetcommon.application.PhetApplication;

import java.io.*;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Sep 15, 2003
 * Time: 2:53:11 AM
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
        String name = app.getApplicationDescriptor().getName();
        ClassLoader cl = app.getClass().getClassLoader();
        URL buildNumberURL = cl.getResource( ( name != null ? name + "." : "" ) + "build.number" );
        System.out.println( "buildNumberURL = " + buildNumberURL );
        if( buildNumberURL == null ) {
            return new VersionInfo( -1, "Not Recorded" );
        }
        int buildNum = -1;
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( buildNumberURL.openStream() ) );
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

        InputStream buildTimeURL = cl.getResourceAsStream( ( name != null ? name + "." : "" ) + "build.time.stamp" );
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
