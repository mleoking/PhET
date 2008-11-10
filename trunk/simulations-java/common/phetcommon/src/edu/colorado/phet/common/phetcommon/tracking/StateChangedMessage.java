package edu.colorado.phet.common.phetcommon.tracking;

/**
 * Tracking message sent when the state of something changes.
 * This message is analogous to Swing's StateChanged interface.
 * <p>
 * The client is responsible for choosing a name that uniquely identifies the action.
 * We try to send both the old and new state values, but the old value is optional.
 * All values are represented as Strings. In the case of a complex value (eg, Color), 
 * we will define a String representation of the datatype.
 *
 * @author Sam Reid
 */
public class StateChangedMessage extends TrackingMessage {
    public StateChangedMessage( SessionID sessionID, String name, String oldValue, String newValue ) {
        super( sessionID, "state-changed" );
        addField( new TrackingMessageField( "name", name ) );
        addField( new TrackingMessageField( "old-value", oldValue ) );
        addField( new TrackingMessageField( "new-value", newValue ) );
    }
}
