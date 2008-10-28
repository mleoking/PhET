package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

public class Tracker {
    private String trackingPath = "tracking";
    private String trackingScript = "phet-tracking.php";

    public void postMessage( TrackingMessage trackingInfo ) throws IOException {
        try {
            new URL( getTrackingURL( trackingInfo ) ).openStream().close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    private String getTrackingURL( TrackingMessage info ) {
        return PhetCommonConstants.PHET_HOME_URL + "/" + trackingPath + "/" + trackingScript + "?" + info.toPHP();
    }

}
