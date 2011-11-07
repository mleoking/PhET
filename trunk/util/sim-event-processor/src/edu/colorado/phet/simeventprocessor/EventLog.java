// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.Option;
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
    public List<Entry> lines = new ArrayList<Entry>();
    public final File sourceFile;

    public EventLog( File sourceFile ) {
        this.sourceFile = sourceFile;
    }

    public EventLog( String machineID, String sessionID, long serverTime, List<Entry> lines, File sourceFile ) {
        this.machineID = machineID;
        this.sessionID = sessionID;
        this.serverTime = serverTime;
        this.lines = lines;
        this.sourceFile = sourceFile;
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

    public EventLog getWithoutSystemEvents() {
        return removeItems( new Function1<Entry, Boolean>() {
            public Boolean apply( Entry entry ) {
                return entry.actor.toLowerCase().startsWith( "system" );
            }
        } );
    }

    public EventLog removeItems( Function1<Entry, Boolean> filter ) {
        return new EventLog( machineID, sessionID, serverTime, new ObservableList<Entry>( lines ).removeItems( filter ), sourceFile );
    }

    public int getLastTime() {
        return (int) lines.get( lines.size() - 1 ).timeMilliSec;
    }

    public int getNumberOfEvents( final long time ) {
        return getNumberOfEvents( time, new Function1.Constant<Entry, Boolean>( true ) );
    }

    public int getNumberOfEvents( final long time, Function1<Entry, Boolean> matches ) {
        EventLog log = removeItems( new Function1<Entry, Boolean>() {
            public Boolean apply( Entry entry ) {
                return entry.timeMilliSec > time;
            }
        } );
        EventLog user = log.getWithoutSystemEvents();
        EventLog keep = user.keepItems( matches );
        return keep.size();
    }

    public EventLog keepItems( Function1<Entry, Boolean> matches ) {
        return new EventLog( machineID, sessionID, serverTime, new ObservableList<Entry>( lines ).keepItems( matches ), sourceFile );
    }

    public int size() {
        return lines.size();
    }

    //How many events in the list happened in the log
    public int getNumberOfEvents( final long time, EntryList eventsOfInterest ) {
        EventLog log = removeItems( new Function1<Entry, Boolean>() {
            public Boolean apply( Entry entry ) {
                return entry.timeMilliSec > time;
            }
        } );
        EventLog user = log.getWithoutSystemEvents();
        int count = 0;
        for ( Entry eventOfInterest : eventsOfInterest ) {
            if ( user.containsMatch( eventOfInterest ) ) {
                count++;
            }
        }
        return count;
    }

    private boolean containsMatch( Entry event ) {
        for ( Entry line : lines ) {
            if ( line.matches( event.actor, event.event, event.parameters ) ) { return true; }
        }
        return false;
    }

    //Which of the specified events of interest are in our list?
    public EntryList find( EntryList eventsOfInterest ) {
        EntryList list = new EntryList();
        for ( Entry entry : eventsOfInterest ) {
            if ( containsMatch( entry ) ) {
                list.add( entry );
            }
        }
        return list;
    }

    public long getServerStartTime() {
        return serverTime;
    }

    public String brief() {
        final Entry startMessage = getStartMessage();
        return startMessage.get( "name" ) + " " + startMessage.get( "version" ).get() + " startTime = " + new Date( serverTime ) + ", epoch = " + serverTime + ", study = " + startMessage.get( "study" ) + ", userID = " + startMessage.get( "id" ) + ", events = " + size() + ", timeUsed = " + minutesUsed() + " minutes, machineID = " + machineID + ", sessionID = " + sessionID;
    }

    public int minutesUsed() {
        return getLastTime() / 1000 / 60;
    }

    private Entry getStartMessage() {
        return getFirstEntry( "system", "started" );
    }

    private Entry getFirstEntry( String system, String started ) {
        for ( Entry line : lines ) {
            if ( line.matches( system, started ) ) {
                return line;
            }
        }
        return null;
    }

    public Option<String> getID() {
        return getStartMessage().get( "id" );
    }

    public String getSimName() {
        return getStartMessage().get( "name" ).get();
    }

    public String getMachineID() {
        return machineID;
    }

    public String getStudy() {
        return getStartMessage().get( "study" ).get();
    }

    @Override public String toString() {
        return "EventLog{" +
               "machineID='" + machineID + '\'' +
               ", sessionID='" + sessionID + '\'' +
               ", serverTime=" + serverTime +
               ", lines=" + lines +
               ", sourceFile=" + sourceFile +
               '}';
    }
}