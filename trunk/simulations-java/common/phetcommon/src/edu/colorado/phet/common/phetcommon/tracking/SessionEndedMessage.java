package edu.colorado.phet.common.phetcommon.tracking;

public class SessionEndedMessage extends TrackingMessage {
    public SessionEndedMessage( SessionID sessionID) {
        super( sessionID, "session-ended" );
    }
}
