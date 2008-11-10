package edu.colorado.phet.common.phetcommon.tracking;

import java.io.IOException;

/**
 * ITrackingService is the interface implemented by all tracking services.
 * A tracking service delivers a tracking message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ITrackingService {

    /**
     * Delivers a tracking message to PhET.
     * @param message
     */
    public void postMessage( TrackingMessage message ) throws IOException;
}
