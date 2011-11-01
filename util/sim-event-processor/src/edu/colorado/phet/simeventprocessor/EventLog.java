// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * State for a single parse run.
 *
 * @author Sam Reid
 */
public class EventLog implements Iterable<Entry> {
    private String machineID;
    private String sessionID;
    private long serverTime;
    private List<Entry> lines = new ArrayList<Entry>();

    public EventLog() {
    }

    public EventLog( String machineID, String sessionID, long serverTime, List<Entry> lines ) {
        this.machineID = machineID;
        this.sessionID = sessionID;
        this.serverTime = serverTime;
        this.lines = lines;
    }

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
            Entry entry = Entry.parse( line );
            lines.add( entry );
        }
    }

    //Read the third token, the value in a "name = value" list
    private String readValue( String line ) {
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        stringTokenizer.nextToken();//machineID
        stringTokenizer.nextToken();//=
        return stringTokenizer.nextToken();
    }

    public Iterator<Entry> iterator() {
        return lines.iterator();
    }

    public EventLog removeSystemEvents() {
        return removeItems( new Function1<Entry, Boolean>() {
            public Boolean apply( Entry entry ) {
                return entry.object.toLowerCase().startsWith( "system" );
            }
        } );
    }

    public EventLog removeItems( Function1<Entry, Boolean> filter ) {
        return new EventLog( machineID, sessionID, serverTime, new ObservableList<Entry>( lines ).removeItems( filter ) );
    }

    public long getLastTime() {
        return lines.get( lines.size() - 1 ).time;
    }

    public int getNumberOfEvents( final long time ) {
        EventLog log = removeItems( new Function1<Entry, Boolean>() {
            public Boolean apply( Entry entry ) {
                return entry.time > time;
            }
        } );
        EventLog user = log.removeSystemEvents();
        return user.size();
    }

    private int size() {
        return lines.size();
    }
}