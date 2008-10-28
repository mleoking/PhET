package edu.colorado.phet.common.phetcommon.tracking;

public class SessionEndedMessage extends TrackingMessage {
    public SessionEndedMessage( SessionID sessionID, long currentTimeMillis ) {
        super( sessionID, "session-ended" );
        addEntry( new TrackingEntry( "timestamp", String.valueOf( currentTimeMillis ) ) );
    }
}
