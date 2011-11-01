// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * State for a single parse run.
 *
 * @author Sam Reid
 */
public class ParseState {
    private String machineID;
    private String sessionID;
    private long serverTime;
    private EventLine lastEventLine = null;
    private HashMap<Long, Pair<EventLine, EventLine>> times = new HashMap<Long, Pair<EventLine, EventLine>>();

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
            EventLine eventLine = EventLine.parse( line );
            if ( eventLine.matches( "module", "activated" ) ) {
                System.out.println( "Switched tab to: " + eventLine.getParameter( "name" ) + " after " + eventLine.time / 1000 + " sec" );
            }
            if ( lastEventLine != null ) {
                times.put( lastEventLine.time - eventLine.time, new Pair<EventLine, EventLine>( lastEventLine, eventLine ) );
            }

            lastEventLine = eventLine;
        }
    }

    //Read the third token, the value in a "name = value" list
    private String readValue( String line ) {
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        stringTokenizer.nextToken();//machineID
        stringTokenizer.nextToken();//=
        return stringTokenizer.nextToken();
    }

    public void parseFinished() {
        ArrayList<Long> keys = new ArrayList<Long>( times.keySet() );
        Collections.sort( keys, new Comparator<Long>() {
            public int compare( Long a, Long b ) {
                return Double.compare( a, b );
            }
        } );
//        Collections.reverse( keys );
        System.out.println( "longest time: " + times.get( keys.get( 0 ) ) );
        System.out.println( "second longest time: " + times.get( keys.get( 1 ) ) );
        System.out.println( "third longest time: " + times.get( keys.get( 2 ) ) );
    }
}