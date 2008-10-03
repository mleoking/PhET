package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class TrackingSystem {
    private String phetURL = "http://phet.colorado.edu";
    private String trackingPath = "tracking";
    private String trackingScript = "phet-tracking.php";

    public TrackingSystem() {

    }

    public static class TrackingEntry {
        private String key;
        private String value;

        public TrackingEntry( String key, String value ) {
            this.key = key;
            this.value = value;
        }

        public String toPHP() {
            return key + "=" + valueToPHP( value );
        }

        private String valueToPHP( String value ) {
            if ( value == null ) {
                return "null";
            }
            else {
                String str = value.replace( " ", "%20" );
                return str;
            }
        }
    }

    public static class TrackingInfo {
        private TrackingEntry[] entries;

        public TrackingInfo( PhetApplicationConfig config ) {
            entries = new TrackingEntry[]{
                    new TrackingEntry( "version", config.getVersion().toString() ),
                    new TrackingEntry( "project", config.getProjectName() ),
                    new TrackingEntry( "sim", config.getFlavor() ),

                    new SystemProperty( "os.name" ),
                    new SystemProperty( "os.version" ),
                    new SystemProperty( "os.arch" ),

                    new SystemProperty( "javawebstart.version" ),
                    new SystemProperty( "java.version" ),
                    new SystemProperty( "java.vendor" ),

                    new SystemProperty( "user.country" ),
                    new SystemProperty( "user.timezone" ),
                    new TrackingEntry( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
            };
        }

        public String toPHP() {
            String php = "";
            for ( int i = 0; i < entries.length; i++ ) {
                if ( php.length() > 0 ) {
                    php += "&";
                }
                php += entries[i].toPHP();
            }
            return php;
        }

    }

    private static class SystemProperty extends TrackingEntry {
        public SystemProperty( String s ) {
            super( s, System.getProperty( s ) );
        }
    }

    public void postTrackingInfo( TrackingInfo trackingInfo ) {
        String a = getTrackingURL( trackingInfo );
        try {
            URL url = new URL( a );
            InputStream inputStream = url.openStream();
            inputStream.close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private String getTrackingURL( TrackingInfo info ) {
        return phetURL + "/" + trackingPath + "/" + trackingScript + "?" + info.toPHP();
    }

    public static void main( String[] args ) {
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        while ( keys.hasMoreElements() ) {
            String o = keys.nextElement().toString();
            System.out.println( o + " = " + p.getProperty( o ) );
        }
        PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "nuclear-physics" ), "alpha-radiation" );
        String s = new TrackingSystem().getTrackingURL( new TrackingInfo( config ) );
        System.out.println( "s = " + s );
        new TrackingSystem().postTrackingInfo( new TrackingInfo( config ) );
    }

}
