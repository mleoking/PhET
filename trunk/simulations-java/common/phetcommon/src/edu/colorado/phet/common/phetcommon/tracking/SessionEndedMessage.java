package edu.colorado.phet.common.phetcommon.tracking;

/**
 * Tracking message sent when the simulation exits, indicating the end of the session.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SessionEndedMessage extends TrackingMessage {
    
    // Versioning the messages allows us to manage data after changing message content.
    // If the content of this message is changed, you'll need to increment the version number.
    public static final String MESSAGE_VERSION = "1";
    
    public SessionEndedMessage( SessionID sessionID ) {
        super( sessionID, "session_ended", MESSAGE_VERSION );
    }
}
