package edu.colorado.phet.reids.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import edu.colorado.phet.reids.admin.util.FileUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 20, 2010
 * Time: 5:11:37 PM
 * To change this template use File | Settings | File Templates.
 */
class TimesheetModel {
    private ArrayList<TimeListener> timeListeners = new ArrayList<TimeListener>();
    private boolean dirty = false;

    public void addDirtyListener( DirtyListener dirtyListener ) {
        dirtyListeners.add( dirtyListener );
    }

    public Entry[] getEntriesForCategory( String category ) {
        ArrayList<Entry> e = new ArrayList<Entry>();
        for ( Entry entry : entries ) {
            if ( entry.getCategory().equals( category ) ) { e.add( entry ); }
        }
        return e.toArray( new Entry[0] );
    }

    //dirty if changes haven't been saved

    public static interface DirtyListener {
        void dirtyChanged();
    }

    private ArrayList<DirtyListener> dirtyListeners = new ArrayList<DirtyListener>();
    private TimeListener timeListenerAdapter = new TimeListener() {
        public void timeChanged() {
            notifyTimeChanged();
            setDirty();
        }
    };

    private void notifyTimeChanged() {
        for ( TimeListener timeListener : timeListeners ) {
            timeListener.timeChanged();
        }
    }

    private ArrayList<ClockedInListener> clockedInListeners = new ArrayList<ClockedInListener>();
    private ClockedInListener clockedInListenerAdapter = new ClockedInListener() {
        public void clockedInChanged() {
            for ( ClockedInListener clockedInListener : clockedInListeners ) {
                clockedInListener.clockedInChanged();
            }
        }
    };

    public String toCSV() {
        String s = "Start,End,Category,Notes,Report\n";
        for ( Entry entry : entries ) {
            s += entry.toCSV() + "\n";
        }

        return s.substring( 0, s.length() - 1 );
    }

    public void loadCSV( String str ) {
        clear();
        StringTokenizer stringTokenizer = new StringTokenizer( str, "\n" );
        String header = stringTokenizer.nextToken();
        System.out.println( "header = " + header );
        while ( stringTokenizer.hasMoreTokens() ) {
            String line = stringTokenizer.nextToken();
            if ( line.trim().length() > 0 ) {
                Entry e = Entry.parseCSV( line );
                addEntry( e );
            }
        }
    }

    private void clear() {
        while ( getEntryCount() > 0 ) {
            removeEntry( 0 );
        }
    }

    private void removeEntry( int index ) {
        entries.remove( index );
        setDirty();
        //todo: notify somebody?
    }

    private void setDirty() {
        if ( !isDirty() ) {
            dirty = true;
            for ( DirtyListener dirtyListener : dirtyListeners ) {
                dirtyListener.dirtyChanged();
            }
        }
    }

    public void loadCSV( File file ) throws IOException {
        loadCSV( FileUtils.loadFileAsString( file ) );
        setClean();
    }

    public Entry getEntry( int index ) {
        return entries.get( index );
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setClean() {
        if ( dirty ) {
            dirty = false;
            for ( DirtyListener dirtyListener : dirtyListeners ) {
                dirtyListener.dirtyChanged();
            }
        }
    }

    public static interface ClockedInListener {
        public void clockedInChanged();
    }

    public void clockOut() {
        getLastEntry().clockOut();
        setDirty();
    }

    public long getTotalTimeSeconds() {
        long sum = 0;
        for ( Entry entry : entries ) {
            sum += entry.getElapsedSeconds();
        }
        return sum;
    }

    public long getTimeInInterval( long start, long end ) {
        long sum = 0;
        for ( Entry entry : entries ) {

            //Skip summary from previous year so entries don't double (just for "worked today" calculation)
            if ( !entry.getCategory().equals( "summary-previous" ) ) {
                long value = entry.getTimeInInterval( start, end );
                sum += value;
            }
        }
        return sum;
    }

    public long getSecondsToday() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set( GregorianCalendar.HOUR_OF_DAY, 0 );
        cal.set( GregorianCalendar.MINUTE, 0 );
        cal.set( GregorianCalendar.SECOND, 0 );
        cal.set( GregorianCalendar.MILLISECOND, 0 );
        long secondsAtDayStart = cal.getTimeInMillis() / 1000;
        long currentSeconds = System.currentTimeMillis() / 1000;
        //System.out.println("secondsAtDayStart = " + secondsAtDayStart+", currentSec = "+currentSeconds);
        return getTimeInInterval( secondsAtDayStart, currentSeconds );
    }

    public void addTimeListener( TimeListener timeListener ) {
        timeListeners.add( timeListener );
    }

    public void addClockedInListener( ClockedInListener clockedInListener ) {
        clockedInListeners.add( clockedInListener );
    }

    public static interface ItemAddedListener {
        void itemAdded( Entry entry );
    }

    public static interface TimeListener {
        void timeChanged();
    }

    private ArrayList<ItemAddedListener> itemAddedListeners = new ArrayList<ItemAddedListener>();

    public void addItemAddedListener( ItemAddedListener listener ) {
        itemAddedListeners.add( listener );
    }

    private ArrayList<Entry> entries = new ArrayList<Entry>();

    public void addEntry( Entry entry ) {
        entries.add( entry );
        entry.addTimeListener( timeListenerAdapter );
        entry.addClockedInListener( clockedInListenerAdapter );
        for ( ItemAddedListener itemAddedListener : itemAddedListeners ) {
            itemAddedListener.itemAdded( entry );
        }//todo: remove listeners after item is closed?
        notifyTimeChanged();
        setDirty();
    }

    public Entry getLastEntry() {
        return entries.get( entries.size() - 1 );
    }

    public void startNewEntry( String category ) {
        startNewEntry( category, "" );
    }

    public void startNewEntry( String category, String notes ) {
        boolean clockedIn = isClockedIn();
        if ( getEntryCount() > 0 ) { getLastEntry().clockOut(); }
        Entry e = new Entry( clockedIn ? getLastEntry().getEndSeconds() : Util.currentTimeSeconds(), Util.currentTimeSeconds(), category, notes, false, true );
        addEntry( e );
        for ( ClockedInListener clockedInListener : clockedInListeners ) {
            clockedInListener.clockedInChanged();//todo: could rewrite this so no clocked-out notifications are sent
        }
        setDirty();
    }

    public boolean isClockedIn() {
        return getEntryCount() > 0 && getLastEntry().isRunning();
    }

    public int getEntryCount() {
        return entries.size();
    }

    public void startNewTask() {
        startNewEntry( getEntryCount() > 0 ? getLastEntry().getCategory() : "" );
    }
}
