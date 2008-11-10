package edu.colorado.phet.common.phetcommon.tracking;

/**
 * Tracking message sent when the simulation exits, indicating the end of the session.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SessionEndedMessage extends TrackingMessage {
    public SessionEndedMessage( SessionID sessionID) {
        super( sessionID, "session-ended" );
    }
}
