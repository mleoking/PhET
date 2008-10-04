package edu.colorado.phet.common.phetcommon.updates;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetVersionInfo;

public class DefaultVersionChecker implements IVersionChecker {
    public PhetVersionInfo getVersion( String project ) throws IOException {
        String read = readURL( "http://phet.colorado.edu/sims/" + project + "/" + project + ".properties" );
        Properties properties = new Properties();
        properties.load( new ByteArrayInputStream( read.getBytes() ) );

        String major = properties.getProperty( PhetApplicationConfig.PROPERTY_VERSION_MAJOR );
        String minor = properties.getProperty( PhetApplicationConfig.PROPERTY_VERSION_MINOR );
        String dev = properties.getProperty( PhetApplicationConfig.PROPERTY_VERSION_DEV );
        String rev = properties.getProperty( PhetApplicationConfig.PROPERTY_VERSION_REVISION );

        return new PhetVersionInfo( major, minor, dev, rev );
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
        PhetVersionInfo phetVersionInfo = new DefaultVersionChecker().getVersion( "balloons" );
        System.out.println( "phetVersionInfo = " + phetVersionInfo );
    }
}
