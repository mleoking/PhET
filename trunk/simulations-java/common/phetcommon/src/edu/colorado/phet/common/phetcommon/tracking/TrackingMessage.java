package edu.colorado.phet.common.phetcommon.tracking;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * TrackingMessage is a tracking message sent by a simulation.
 * A message consists of fields, which are name/value pairs.
 * This is the base class for all messages, and populates fields common to all messages.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TrackingMessage {

    //versioning the tracking system will allow us to analyze data across version changes
    //for example, we may stop tracking certain things in a newer version of the tracker
    //having the version will allow us to know that those messages are gone by design
    public static final String TRACKER_VERSION = "0.00.01";

    //versioning the messages allows us to manage data after changing message content 
    public static final String MESSAGE_VERSION = "0.00.01";

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
