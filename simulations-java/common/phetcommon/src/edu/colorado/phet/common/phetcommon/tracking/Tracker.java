package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;


public class Tracker {
    private AbstractTrackingInfo trackingInformation;
    private Trackable trackable;

    public Tracker( Trackable trackable ) {
        this.trackable = trackable;
    }

    public void startTracking() {
        Thread t = new Thread( new Runnable() {
            public void run() {
                trackingInformation = trackable.getTrackingInformation();
                try {
                    new TrackingManager().postTrackingInfo( trackingInformation );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
    }

    public AbstractTrackingInfo getTrackingInformation() {
        return trackingInformation;
    }
}
