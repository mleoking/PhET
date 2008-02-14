package edu.colorado.phet.reids.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by: Sam
 * Feb 13, 2008 at 10:31:44 PM
 */
public class TimesheetData implements TimesheetDataEntry.Listener {
    private ArrayList entries = new ArrayList();

    public void addEntry( TimesheetDataEntry entry ) {
        entries.add( entry );
        entry.addListener( this );
        notifyEntryAdded( entry );
    }

    public String toCSV() {
        String s = TimesheetDataEntry.getCSVHeader() + "\n";
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            s += timesheetDataEntry.toCSV() + "\n";
        }
        return s;
    }

    public TimesheetDataEntry getEntry( int i ) {
        return (TimesheetDataEntry) entries.get( i );
    }

    public int getNumEntries() {
        return entries.size();
    }

    public long getTotalElapsedTimeMillis() {
        long sum = 0;
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry entry = (TimesheetDataEntry) entries.get( i );
            sum += entry.getElapsedTimeMillis();
        }
        return sum;
    }

    public void changed() {
        notifyTimeChanged();
    }

    public void runningChanged() {
    }

    public void stopAllEntries() {
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            timesheetDataEntry.setRunning( false );
        }
    }

    public void startNewEntry() {
        stopAllEntries();
        TimesheetDataEntry e = new TimesheetDataEntry( new Date(), new Date(), "", "" );
        addEntry( e );
        e.setRunning( true );
    }

    public void loadCSV( String str ) {
        clear();
        StringTokenizer stringTokenizer = new StringTokenizer( str, "\n" );
        String header = stringTokenizer.nextToken();
        System.out.println( "header = " + header );
        while ( stringTokenizer.hasMoreTokens() ) {
            String line = stringTokenizer.nextToken();
            if ( line.trim().length() > 0 ) {
                TimesheetDataEntry e = TimesheetDataEntry.parseCSV( line );
                addEntry( e );
            }
        }
    }

    private void clear() {
        while ( entries.size() > 0 ) {
            removeEntry( 0 );
        }
    }

    private void removeEntry( int i ) {
        TimesheetDataEntry entry = (TimesheetDataEntry) entries.remove( i );
        notifyEntryRemoved( entry );
    }

    private void notifyEntryRemoved( TimesheetDataEntry entry ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.timeEntryRemoved( entry );
        }
    }

    public static interface Listener {
        void timeEntryAdded( TimesheetDataEntry e );

        void timeChanged();

        void timeEntryRemoved( TimesheetDataEntry entry );
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyEntryAdded( TimesheetDataEntry e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).timeEntryAdded( e );
        }
    }

    public void notifyTimeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).timeChanged();
        }
    }
}
