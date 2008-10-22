package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;


public class Tracker {

    public void postMessage( final TrackingMessage trackingInformation ) {
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    new TrackingManager().postMessage( trackingInformation );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
    }

}
