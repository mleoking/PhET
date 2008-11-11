package edu.colorado.phet.common.phetcommon.updates;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * DefaultVersionChecker gets the most up-to-date version information from the PhET website.
 * The version information lives in a properties file in the sim's directory on the website.
 * For example, for faraday, the file is faraday.properties.
 */
public class DefaultVersionChecker implements IVersionChecker {
    
    public PhetVersion getVersion( String project ) throws IOException {
        String read = readURL( HTMLUtils.getProjectPropertiesURL( project ) );
        Properties properties = new Properties();
        properties.load( new ByteArrayInputStream( read.getBytes() ) );

        String major = properties.getProperty( PhetResources.PROPERTY_VERSION_MAJOR );
        String minor = properties.getProperty( PhetResources.PROPERTY_VERSION_MINOR );
        String dev = properties.getProperty( PhetResources.PROPERTY_VERSION_DEV );
        String rev = properties.getProperty( PhetResources.PROPERTY_VERSION_REVISION );

        return new PhetVersion( major, minor, dev, rev );
    }

    private String readURL( String urlLocation ) throws IOException {
        BufferedReader in = new BufferedReader( new InputStreamReader( new URL( urlLocation ).openStream() ) );

        String inputLine;
        String totalText = "";

        while ( ( inputLine = in.readLine() ) != null ) {
            if ( totalText.length() > 0 ) {
                totalText += "\n";
            }
            totalText += inputLine;
        }
        in.close();
        return totalText;
    }

    public static void main( String[] args ) throws IOException {
        System.out.println( "UpdateManager.main" );
        PhetVersion phetVersionInfo = new DefaultVersionChecker().getVersion( "balloons" );
        System.out.println( "phetVersionInfo = " + phetVersionInfo );
    }
}
