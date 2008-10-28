package edu.colorado.phet.common.phetcommon.tracking;

import java.util.ArrayList;
import java.util.Arrays;

public class TrackingMessage {
    private ArrayList entries = new ArrayList();

    //versioning the tracking system will allow us to analyze data across version changes
    //for example, we may stop tracking certain things in a newer version of the tracker
    //having the version will allow us to know that those messages are gone by design
    public static final String TRACKER_VERSION = "0.00.01";

    //versioning the messages allows us to manage data after changing message content 
    public static final String MESSAGE_VERSION = "0.00.01";

    // names for actions and state changes
    public static final String PREFERENCES_DIALOG_VISIBLE = "preferences-dialog-visible";
    public static final String UPDATES_ENABLED = "updates-enabled";
    public static final String TRACKING_ENABLED = "tracking-enabled";
    public static final String MANUAL_CHECK_FOR_UPDATES = "manual-check-for-updates";
    public static final String AUTO_CHECK_FOR_UPDATES = "auto-check-for-updates";
    public static final String DIRECTED_TO_WEBSITE_FOR_UPDATE = "directed-to-website-for-update";
    public static final String ASK_ME_LATER_PRESSED = "ask-me-later-update-pressed";
    public static final String SKIP_UPDATE_PRESSED = "skip-update-pressed";
    public static final String PHET_FRAME_CLOSING = "phet-frame-closing";
    public static final String FILE_EXIT_SELECTED = "file-exit-selected";

    public TrackingMessage( SessionID sessionID, String messageType ) {
        addEntry( new TrackingEntry( "session-id", sessionID.toString() ) );
        addEntry( new TrackingEntry( "timestamp", System.currentTimeMillis() + "" ) );
        addEntry( new TrackingEntry( "message-type", messageType ) );
    }

    public void addAllEntries( TrackingEntry[] list ) {
        entries.addAll( Arrays.asList( list ) );
    }

    public void addEntry( TrackingEntry entry ) {
        entries.add( entry );
    }

    public String toPHP() {
        String php = "";
        for ( int i = 0; i < getEntryCount(); i++ ) {
            if ( i > 0 ) {
                php += "&";
            }
            php += getEntry( i ).toPHP();
        }
        return php;
    }

    private TrackingEntry getEntry( int i ) {
        return (TrackingEntry) entries.get( i );
    }

    public int getEntryCount() {
        return entries.size();
    }

    public String toHumanReadable() {
        String text = "";
        for ( int i = 0; i < getEntryCount(); i++ ) {
            if ( i > 0 ) {
                text += "\n";
            }
            text += getEntry( i ).toHumanReadable();
        }
        return text;
    }
}
