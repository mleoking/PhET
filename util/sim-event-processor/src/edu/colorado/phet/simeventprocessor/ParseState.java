// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.Arrays;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;

/**
 * State for a single parse run.
 *
 * @author Sam Reid
 */
public class ParseState {
    private String machineID;
    private String sessionID;
    private long serverTime;

    //Parse a single line
    public void parseLine( String line ) {
        if ( line.startsWith( "machineID" ) ) {
            machineID = readValue( line );
        }
        else if ( line.startsWith( "sessionID" ) ) {
            sessionID = readValue( line );
        }
        else if ( line.startsWith( "serverTime" ) ) {
            serverTime = Long.parseLong( readValue( line ) );
        }
        else {
            parseEventLine( line );
        }
    }

    //Parse a line that is an event
    private void parseEventLine( String line ) {
        StringTokenizer tokenizer = new StringTokenizer( line, "\t" );
        long time = Long.parseLong( tokenizer.nextToken() );
        String object = tokenizer.nextToken();
        String event = tokenizer.nextToken();
        Parameter[] parameters = tokenizer.hasMoreTokens() ? Parameter.parseParameters( tokenizer.nextToken() ) : new Parameter[0];
        EventLine eventLine = new EventLine( time, object, event, parameters );
        System.out.println( "eventLine = " + eventLine );
    }

    //Read the third token, the value in a "name = value" list
    private String readValue( String line ) {
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        stringTokenizer.nextToken();//machineID
        stringTokenizer.nextToken();//=
        return stringTokenizer.nextToken();
    }

    //A single line that represents an event.
    private class EventLine {
        public final long time;
        public final String object;
        public final String event;
        public final Parameter[] parameters;

        public EventLine( long time, String object, String event, Parameter[] parameters ) {
            this.time = time;
            this.object = object;
            this.event = event;
            this.parameters = parameters;
        }

        @Override public String toString() {
            return "EventLine{" +
                   "time=" + time +
                   ", object='" + object + '\'' +
                   ", event='" + event + '\'' +
                   ", parameters=" + ( parameters == null ? null : Arrays.asList( parameters ) ) +
                   '}';
        }
    }
}
