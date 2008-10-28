package edu.colorado.phet.common.phetcommon.tracking;

public class StateChangedMessage extends TrackingMessage {
    public StateChangedMessage( SessionID sessionID, String name, String oldValue, String newValue ) {
        super( sessionID );
        addEntry( new TrackingEntry( "name", name ) );
        addEntry( new TrackingEntry( "old-value", oldValue ) );
        addEntry( new TrackingEntry( "new-value", newValue ) );
    }
}
