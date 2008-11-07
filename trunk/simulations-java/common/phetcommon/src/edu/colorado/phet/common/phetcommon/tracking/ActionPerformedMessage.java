package edu.colorado.phet.common.phetcommon.tracking;

public class ActionPerformedMessage extends TrackingMessage {
    public ActionPerformedMessage( SessionID sessionID, String actionName ) {
        this( sessionID, actionName, null );
    }

    public ActionPerformedMessage( SessionID sessionID, String actionName, String response ) {
        super( sessionID, "action-performed" );
        addField( new TrackingMessageField( "name", actionName ) );
        addField( new TrackingMessageField( "system-response", response ) );
    }
}
