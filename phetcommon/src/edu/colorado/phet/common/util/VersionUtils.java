/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.view.PhetFrame;

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

        public String toString() {
            return "Build Number = " + buildNumber + ", Build Time = " + buildTime;
        }
    }

    /* To be used in conjuction with this ant task:

        <target name="Synchronize Build meta info">
        <tstamp>
            <format property="jar.creation.time" pattern="d-MMMM-yyyy h:mm aa" locale="en" timezone="MST"></format>
        </tstamp>
        <echo file="build.time.stamp.txt" message="${jar.creation.time}"></echo>
        <copy file="build.time.stamp.txt" tofile="${data}/build.time.stamp.txt"></copy>
        <copy file="build.number" tofile="${data}/build.number"/>
    </target>

    */
    public static void showBuildNumber( PhetFrame frame ) {
        VersionInfo vi = readVersionInfo();
        JOptionPane.showMessageDialog( frame, "Build number=" + vi.getBuildNumber() + "\n" + "BuildTime=" + vi.getBuildTime() );
    }

    public static VersionInfo readVersionInfo() {
        VersionUtils vu = new VersionUtils();
        ClassLoader cl = vu.getClass().getClassLoader();
        URL buildNumberURL = cl.getResource( "build.number" );

        System.out.println( "PhET Application Loading, BuildURL = " + buildNumberURL );
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

        InputStream buildTimeURL = cl.getResourceAsStream( "build.time.stamp" );
        String buildTimeStr = "-1";
        try {
            buildTimeStr = new BufferedReader( new InputStreamReader( buildTimeURL ) ).readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        VersionInfo vi = new VersionInfo( buildNum, buildTimeStr );
        System.out.println( "PhET Application Loaded: " + vi );
        return vi;
    }

}
