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
    private boolean hasChanges = false;

    public void addEntry( TimesheetDataEntry entry ) {
        entries.add( entry );
        entry.addListener( this );
        notifyEntryAdded( entry );
        setChanged( true );
    }

    public String toString() {
        return toCSV();
    }

    public String toCSV() {
        String s = TimesheetDataEntry.getCSVHeader() + "\n";
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            s += timesheetDataEntry.toCSV() + "\n";
        }
        return s;
    }

    public void clearChanges() {
        setChanged( false );
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

    public void timeChanged() {
        setChanged( true );
        notifyTimeChanged();
    }

    private void setChanged( boolean ch ) {
        if ( this.hasChanges != ch ) {
            this.hasChanges = ch;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.notifyHasChangesStateChanged();
            }
        }
    }

    public void runningChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).timeEntryRunningChanged();
        }
    }

    public void categoryChanged() {
        setChanged( true );
    }

    public void notesChanged() {
        setChanged( true );
    }

    public void selectionChanged() {
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).selectionChanged();
        }
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
        stopAllEntries();
        while ( entries.size() > 0 ) {
            removeEntry( 0 );
        }
    }

    private void removeEntry( int i ) {
        TimesheetDataEntry entry = (TimesheetDataEntry) entries.remove( i );
        notifyEntryRemoved( entry );
        setChanged( true );
    }

    private void notifyEntryRemoved( TimesheetDataEntry entry ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.timeEntryRemoved( entry );
        }
    }

    public int getNumCategories() {
        return getCategories().size();
    }

    public ArrayList getCategories() {
        ArrayList cat = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            if ( !cat.contains( timesheetDataEntry.getCategory() ) ) {
                cat.add( timesheetDataEntry.getCategory() );
            }
        }
        return cat;
    }

    public long getTotalTimeMillis( String s ) {
        long sum = 0;
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            if ( timesheetDataEntry.getCategory().equals( s ) ) {
                sum += timesheetDataEntry.getElapsedTimeMillis();
            }
        }
        return sum;
    }

    public void setSelection( TimesheetDataEntry entry ) {
        clearSelection();
        entry.setSelected( true );
    }

    private void clearSelection() {
        for ( int i = 0; i < entries.size(); i++ ) {
            ( (TimesheetDataEntry) entries.get( i ) ).setSelected( false );
        }
    }

    public void pauseAll() {
        for ( int i = 0; i < entries.size(); i++ ) {
            ( (TimesheetDataEntry) entries.get( i ) ).setRunning( false );
        }
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public TimesheetDataEntry[] getSelectedEntries() {
        ArrayList x = new ArrayList();
        for ( int i = 0; i < this.entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            if ( timesheetDataEntry.isSelected() ) {
                x.add( timesheetDataEntry );
            }
        }
        return (TimesheetDataEntry[]) x.toArray( new TimesheetDataEntry[0] );
    }

    public void removeEntries( TimesheetDataEntry[] selected ) {
        for ( int i = 0; i < selected.length; i++ ) {
            removeEntry( entries.indexOf( selected[i] ) );
        }
    }

    public boolean isRunning() {
        for ( int i = 0; i < entries.size(); i++ ) {
            TimesheetDataEntry timesheetDataEntry = (TimesheetDataEntry) entries.get( i );
            if ( timesheetDataEntry.isRunning() ) {
                return true;
            }
        }
        return false;
    }

    public void insertNewEntry() {

        TimesheetDataEntry[] s = getSelectedEntries();
        assert s.length > 0;
        int lastSelectedIndex = entries.indexOf( s[s.length - 1] );
        TimesheetDataEntry newEntry = new TimesheetDataEntry( new Date(), new Date(), "", "" );
        clearSelection();
        newEntry.setSelected( true );
        insertEntry( newEntry, lastSelectedIndex + 1 );
    }

    private void insertEntry( TimesheetDataEntry newEntry, int index ) {
        newEntry.addListener( this );
        entries.add( index, newEntry );
        notifyEntryInserted( newEntry );
        setChanged( true );
        notifySelectionChanged();
    }

    private void notifyEntryInserted( TimesheetDataEntry e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.timeEntryInserted( e );
        }
    }

    public void addAll( TimesheetData d ) {
        for ( int i = 0; i < d.entries.size(); i++ ) {
            addEntry( (TimesheetDataEntry) d.entries.get( i ) );
        }
    }

    public static interface Listener {
        void timeEntryAppended( TimesheetDataEntry e );

        void timeChanged();

        void timeEntryRemoved( TimesheetDataEntry entry );

        void notifyHasChangesStateChanged();

        void timeEntryRunningChanged();

        void selectionChanged();

        void timeEntryInserted( TimesheetDataEntry e );
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyEntryAdded( TimesheetDataEntry e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).timeEntryAppended( e );
        }
    }

    public void notifyTimeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).timeChanged();
        }
    }

    public static class Adapter implements Listener {
        public void timeEntryAppended( TimesheetDataEntry e ) {
        }

        public void timeChanged() {
        }

        public void timeEntryRemoved( TimesheetDataEntry entry ) {
        }

        public void notifyHasChangesStateChanged() {
        }

        public void timeEntryRunningChanged() {
        }

        public void selectionChanged() {
        }

        public void timeEntryInserted( TimesheetDataEntry e ) {
        }
    }
}
