package edu.colorado.phet.common.phetcommon.statistics;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * StatisticsMessage is the base class for statistics messages sent by a simulation.
 * It populates fields common to all messages.
 * A message consists of fields, which are name/value pairs.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StatisticsMessage {

    private final ArrayList fields = new ArrayList();

    public StatisticsMessage( String messageType, String messageVersion ) {
        addField( new StatisticsMessageField( "Message type", "message_type", messageType ) );
        addField( new StatisticsMessageField( "Message version", "message_version", messageVersion ) );
        addField( new StatisticsMessageField( "Simulation type", "sim_type", "java" ) );
    }

    public void addFields( StatisticsMessageField[] list ) {
        fields.addAll( Arrays.asList( list ) );
    }

    public void addField( StatisticsMessageField field ) {
        fields.add( field );
    }

    public StatisticsMessageField getField( int i ) {
        return (StatisticsMessageField) fields.get( i );
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
            text += getField( i ).getHumanReadableName() + ": " + getField(i).getValue();
        }
        return text;
    }
}
