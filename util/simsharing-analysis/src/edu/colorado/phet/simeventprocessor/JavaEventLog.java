// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
public class JavaEventLog implements Iterable<JavaEntry> {
    private String machineID;
    private String sessionID;
    private long serverTime;
    public List<JavaEntry> lines = new ArrayList<JavaEntry>();
    public final File file;

    public JavaEventLog( File file ) {
        this.file = file;
    }

    public JavaEventLog( String machineID, String sessionID, long serverTime, List<JavaEntry> lines, File file ) {
        this.machineID = machineID;
        this.sessionID = sessionID;
        this.serverTime = serverTime;
        this.lines = lines;
        this.file = file;
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
            JavaEntry entry = JavaEntry.parse( line );
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

    public Iterator<JavaEntry> iterator() {
        return lines.iterator();
    }

    public JavaEventLog getWithoutSystemEvents() {
        return removeItems( new Function1<JavaEntry, Boolean>() {
            public Boolean apply( JavaEntry entry ) {
                return entry.actor.toLowerCase().startsWith( "system" );
            }
        } );
    }

    public JavaEventLog removeItems( Function1<JavaEntry, Boolean> filter ) {
        return new JavaEventLog( machineID, sessionID, serverTime, new ObservableList<JavaEntry>( lines ).removeItems( filter ), file );
    }

    public int getLastTime() {
        return (int) lines.get( lines.size() - 1 ).timeMilliSec;
    }

    public int getNumberOfEvents( final long time ) {
        return getNumberOfEvents( time, new Function1.Constant<JavaEntry, Boolean>( true ) );
    }

    public int getNumberOfEvents( final long time, Function1<JavaEntry, Boolean> matches ) {
        JavaEventLog log = removeItems( new Function1<JavaEntry, Boolean>() {
            public Boolean apply( JavaEntry entry ) {
                return entry.timeMilliSec > time;
            }
        } );
        JavaEventLog user = log.getWithoutSystemEvents();
        JavaEventLog keep = user.keepItems( matches );
        return keep.size();
    }

    public JavaEventLog keepItems( Function1<JavaEntry, Boolean> matches ) {
        return new JavaEventLog( machineID, sessionID, serverTime, new ObservableList<JavaEntry>( lines ).keepItems( matches ), file );
    }

    public int size() {
        return lines.size();
    }

    //How many events in the list happened in the log
    public int getNumberOfEvents( final long time, EntryList eventsOfInterest ) {
        JavaEventLog log = removeItems( new Function1<JavaEntry, Boolean>() {
            public Boolean apply( JavaEntry entry ) {
                return entry.timeMilliSec > time;
            }
        } );
        JavaEventLog user = log.getWithoutSystemEvents();
        int count = 0;
        for ( JavaEntry eventOfInterest : eventsOfInterest ) {
            if ( user.containsMatch( eventOfInterest ) ) {
                count++;
            }
        }
        return count;
    }

    private boolean containsMatch( JavaEntry event ) {
        for ( JavaEntry line : lines ) {
            if ( line.matches( event.actor, event.event, event.parameters ) ) { return true; }
        }
        return false;
    }

    //Which of the specified events of interest are in our list?
    public EntryList find( EntryList eventsOfInterest ) {
        EntryList list = new EntryList();
        for ( JavaEntry entry : eventsOfInterest ) {
            if ( containsMatch( entry ) ) {
                list.add( entry );
            }
        }
        return list;
    }

    public long getServerStartTime() {
        return serverTime;
    }

    public String getOSName() {
        return getStartMessage().get( "osName" ).getOrElse( "?" );
    }

    public String getOSVersion() {
        return getStartMessage().get( "osVersion" ).getOrElse( "?" );
    }

    public String brief() {
        final JavaEntry startMessage = getStartMessage();
        return startMessage.get( "name" ) + " " + startMessage.get( "version" ).get() + " startTime = " + new Date( serverTime ) + ", epoch = " + serverTime + ", study = " + startMessage.get( "study" ) + ", userID = " + startMessage.get( "id" ) + ", events = " + size() + ", timeUsed = " + minutesUsed() + " minutes, machineID = " + machineID + ", sessionID = " + sessionID;
    }

    public int minutesUsed() {
        return getLastTime() / 1000 / 60;
    }

    public JavaEntry getStartMessage() {
        return getFirstEntry( "system", "started" );
    }

    private JavaEntry getFirstEntry( String system, String started ) {
        for ( JavaEntry line : lines ) {
            if ( line.matches( system, started ) ) {
                return line;
            }
        }
        return null;
    }

    public String getID() {
        return getStartMessage().get( "id" ).getOrElse( "None" );
    }

    public String getSimName() {
        return getStartMessage().get( "name" ).get();
    }

    public String getSimVersion() {
        return getStartMessage().get( "version" ).get();
    }

    public String getMachineID() {
        return machineID;
    }

    public String getSessionID() {
        return sessionID;
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
               ", sourceFile=" + file +
               '}';
    }
}