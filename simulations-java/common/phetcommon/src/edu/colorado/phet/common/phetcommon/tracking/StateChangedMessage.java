package edu.colorado.phet.common.phetcommon.tracking;

public class StateChangedMessage extends TrackingMessage {
    public StateChangedMessage( SessionID sessionID, String name, String oldValue, String newValue ) {
        super( sessionID, "state-changed" );
        addField( new TrackingMessageField( "name", name ) );
        addField( new TrackingMessageField( "old-value", oldValue ) );
        addField( new TrackingMessageField( "new-value", newValue ) );
    }
}
