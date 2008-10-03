package edu.colorado.phet.common.phetcommon.application;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class TrackingSystem {
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
            String str = value.replace( " ", "%20" );
            return str;
        }
    }

    public static class TrackingInfo {
        private TrackingEntry[] entries;

        public TrackingInfo( PhetApplicationConfig config ) {
            entries = new TrackingEntry[]{
                    new TrackingEntry( "version", config.getVersion().toString() ),
                    new TrackingEntry( "project", config.getProjectName() ),
                    new TrackingEntry( "sim", config.getFlavor() ),
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
        return "http://phet.colorado.edu/tracking/phet-tracking.php?" + info.toPHP();
    }

    public static void main( String[] args ) {
        PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "nuclear-physics" ), "alpha-radiation" );
        String s = new TrackingSystem().getTrackingURL( new TrackingInfo( config ) );
        System.out.println( "s = " + s );
        new TrackingSystem().postTrackingInfo( new TrackingInfo( config ) );
    }

}
