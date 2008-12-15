package edu.colorado.phet.common.phetcommon.tracking;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * TrackingMessage is a tracking message sent by a simulation.
 * A message consists of fields, which are name/value pairs.
 * This is the base class for all messages, and populates fields common to all messages.
 * 
 * @author Sam Reid
 * @author Chris Malley
 */
public class TrackingMessage {

    //versioning the tracking system will allow us to analyze data across version changes
    //for example, we may stop tracking certain things in a newer version of the tracker
    //having the version will allow us to know that those messages are gone by design
    public static final String TRACKER_VERSION = "0.00.01";

    //versioning the messages allows us to manage data after changing message content 
    public static final String MESSAGE_VERSION = "0.00.01";

    // messages for things in Preferences dialog
    public static final String PREFERENCES_DIALOG_VISIBLE = "preferences-dialog-visible";
    public static final String PREFERENCES_OK_PRESSED = "preferences-ok-pressed";
    public static final String PREFERENCES_CANCEL_PRESSED = "preferences-cancel-pressed";
    public static final String UPDATES_ENABLED = "updates-enabled";
    public static final String TRACKING_ENABLED = "tracking-enabled";
    public static final String CHECK_FOR_UPDATE_PRESSED = "check-for-update-pressed";
    public static final String HELP_CHECK_FOR_UPDATE_SELETED = "help-check-for-update-selected";
    public static final String TRACKING_DETAILS_PRESSED = "tracking-details-pressed";

    // messages for things in update dialogs
    public static final String AUTO_CHECK_FOR_UPDATES = "auto-check-for-updates"; 
    public static final String UPDATE_NOW_PRESSED = "update-now-pressed";
    public static final String ASK_ME_LATER_PRESSED = "ask-me-later-update-pressed";
    public static final String SKIP_UPDATE_PRESSED = "skip-update-pressed";
    public static final String UPDATES_CANCEL_PRESSED = "updates-cancel-pressed";
    public static final String UPDATES_TRY_IT_PRESSED = "updates-try-it-pressed";
    
    // messages related to ending a session
    public static final String PHET_FRAME_CLOSING = "phet-frame-closing";
    public static final String FILE_EXIT_SELECTED = "file-exit-selected";
    public static final String MAC_OSX_QUIT_SELECTED = "mac-osx-quit";

    private final ArrayList fields = new ArrayList();

    public TrackingMessage( SessionID sessionID, String messageType ) {
        addField( new TrackingMessageField( "session-id", sessionID.toString() ) );
        addField( new TrackingMessageField( "timestamp", System.currentTimeMillis() + "" ) );
        addField( new TrackingMessageField( "message-type", messageType ) );
    }

    public void addFields( TrackingMessageField[] list ) {
        fields.addAll( Arrays.asList( list ) );
    }

    public void addField( TrackingMessageField field ) {
        fields.add( field );
    }

    public TrackingMessageField getField( int i ) {
        return (TrackingMessageField) fields.get( i );
    }

    public int getFieldCount() {
        return fields.size();
    }

    public String toHumanReadable() {
        String text = "";
        for ( int i = 0; i < getFieldCount(); i++ ) {
            if ( i > 0 ) {
                text += "\n";
            }
            text += getField( i ).getName() + "=" + getField(i).getValue();
        }
        return text;
    }
}
