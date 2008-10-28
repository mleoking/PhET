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

    public static final class MessageType {//enum
        private String name;

        public MessageType( String name ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static final MessageType UNKNOWN_TYPE = new MessageType( "unknown" );

    public static final MessageType SIM_LAUNCHED = new MessageType( "sim-launched" );
    public static String PREFERENCES_DIALOG_VISIBLE = "preferences-dialog-visible";
    public static final MessageType UPDATES_ENABLED = new MessageType( "updates-enabled" );
    public static final MessageType UPDATES_DISABLED = new MessageType( "updates-disabled" );
    public static final MessageType TRACKING_ENABLED = new MessageType( "tracking-enabled" );
    public static final MessageType TRACKING_DISABLED = new MessageType( "tracking-disabled" );//we should never see this message
    public static final MessageType MANUAL_CHECK_FOR_UPDATES = new MessageType( "manual-check-for-updates" );
    public static final MessageType AUTO_CHECK_FOR_UPDATES = new MessageType( "auto-check-for-updates" );
    public static final MessageType DIRECTED_TO_WEBSITE_FOR_UPDATE = new MessageType( "directed-to-website-for-update" );
    public static final MessageType ASK_ME_LATER_PRESSED = new MessageType( "ask-me-later-pressed" );
    public static final MessageType SKIP_UPDATE_PRESSED = new MessageType( "skip-update-pressed" );

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
