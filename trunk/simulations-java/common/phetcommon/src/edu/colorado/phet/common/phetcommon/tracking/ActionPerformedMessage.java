package edu.colorado.phet.common.phetcommon.tracking;

/**
 * Tracking message sent when the user performs some action.
 * This message is analogous to Swing's ActionListener interface.
 * <p>
 * The client is responsible for choosing a name that uniquely identifies the action.
 * The action may involve an optional system response.
 * 
 * @author Sam Reid
 */
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
