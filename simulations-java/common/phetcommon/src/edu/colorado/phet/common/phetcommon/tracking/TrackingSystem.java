package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class TrackingSystem {
    private String phetURL = "http://phet.colorado.edu";
    private String trackingPath = "tracking";
    private String trackingScript = "phet-tracking.php";

    public void postTrackingInfo( TrackingInfo trackingInfo ) throws IOException {
        try {
            new URL( getTrackingURL( trackingInfo ) ).openStream().close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    private String getTrackingURL( TrackingInfo info ) {
        return phetURL + "/" + trackingPath + "/" + trackingScript + "?" + info.toPHP();
    }

    public static void main( String[] args ) throws IOException {
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
