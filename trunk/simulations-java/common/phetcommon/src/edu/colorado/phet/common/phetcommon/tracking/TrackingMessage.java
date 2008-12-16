package edu.colorado.phet.common.phetcommon.tracking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * TrackingMessage is the base class for tracking messages sent by a simulation.
 * It populates fields common to all messages.
 * A message consists of fields, which are name/value pairs.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class TrackingMessage {

    private final ArrayList fields = new ArrayList();

    public TrackingMessage( SessionID sessionID, String messageType, String messageVersion ) {
        final long currentTime = System.currentTimeMillis();
        addField( new TrackingMessageField( "message_type", messageType ) );
        addField( new TrackingMessageField( "sim_type", "java" ) );
        addField( new TrackingMessageField( "message_version", messageVersion ) );
        addField( new TrackingMessageField( "message_sent_time", currentTime + "" ) );
        // for debug purposes, so that message_sent_time is human-readable
        addField( new TrackingMessageField( "message_sent_time_debug", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date( currentTime ) ) ) );
        addField( new TrackingMessageField( "session_id", sessionID.toString() ) );
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
