package edu.colorado.phet.reids.admin.version1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 13, 2008 at 10:32:16 PM
 */
public class TimesheetDataEntry {
    private Date startTime;
    private Date endTime;
    private String category;
    private String notes;
    private ArrayList listeners = new ArrayList();
    private boolean running = false;
    private Timer timer;
    //    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat( "EEE MMM d HH:mm:ss z yyyy" );
    public static final DateFormat DISPLAY_FORMAT = new SimpleDateFormat( "M/d/yyyy h:mm:ss a" );
    public static final DateFormat STORAGE_FORMAT = new SimpleDateFormat( "M/d/yyyy h:mm:ss a" );
    private boolean selected = false;

    private void updateEndTime() {
        setEndTime( new Date() );
    }

    public TimesheetDataEntry( Date startTime, Date endTime, String category, String notes ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.notes = notes;
    }

    public void setRunning( boolean running ) {
        if ( running != this.running ) {
            this.running = running;
            if ( running ) {
                if ( timer == null ) {
                    timer = new Timer( 1000, new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            updateEndTime();
                        }
                    } );
                }
                timer.start();
            }
            else {
                timer.stop();
            }
            notifyRunningChanged();
        }
    }

    private void notifyRunningChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.runningChanged();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.selectionChanged();
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getCategory() {
        return category;
    }

    public String getNotes() {
        return notes;
    }

    public void setStartTime( Date startTime ) {
        if ( !this.startTime.equals( startTime ) ) {
            this.startTime = startTime;
            notifyTimeChanged();
        }
    }

    public long getElapsedTimeMillis() {
        return ( endTime.getTime() - startTime.getTime() );
    }

    public boolean isRunning() {
        return running;
    }

    public String toCSV() {
        return STORAGE_FORMAT.format( startTime ) + "," + STORAGE_FORMAT.format( endTime ) + "," + TimesheetApp.toString( getElapsedTimeMillis() ) + "," + category + ",\"" + notes + "\"";
    }

    public static String getCSVHeader() {
        return "Start Time" + "," + "End Time" + "," + "Elapsed Time" + "," + "Category" + "," + "Notes";
    }

    public static TimesheetDataEntry parseCSV( String line ) {
//        System.out.println( "line = " + line );
        StringTokenizer st = new StringTokenizer( line, "," );
        try {
            final Date start = STORAGE_FORMAT.parse( st.nextToken() );
            final Date end = STORAGE_FORMAT.parse( st.nextToken() );
            st.nextToken();//skip elapsed time
            //everything inside the quotes is notes
            int startQuote = line.indexOf( '"' );
            int endQuote = line.indexOf( '"', startQuote + 1 );
            String category = "";
            try {
                category = st.nextToken();
                if ( category.startsWith( "\"" ) && category.endsWith( "\"" ) ) {
                    category = "";//no category specified
                }
            }
            catch( NoSuchElementException e ) {
            }
            return new TimesheetDataEntry( start, end, category, line.substring( startQuote + 1, endQuote ) );
        }
        catch( ParseException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public boolean startsAfter( Date time ) {
        return getStartTime().after( time );
    }

    public static long getTotalTime( TimesheetDataEntry[] t ) {
        long time = 0;
        for ( int i = 0; i < t.length; i++ ) {
            time += t[i].getElapsedTimeMillis();
        }
        return time;
    }

    public TimesheetDataEntry copy() {
        return new TimesheetDataEntry( new Date( getStartTime().getTime() ), new Date( getEndTime().getTime() ), category, notes );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public boolean overlapsWith(TimesheetDataEntry other) {
        //time periods overlap if either contains any points of the other
        return this.containsEndPoint(other) || other.containsEndPoint(this);
    }

    private boolean containsEndPoint(TimesheetDataEntry other) {
        return contains(other.getStartTime())|| contains(other.getEndTime());
    }

    private boolean contains(Date time) {
        return time.after(getStartTime())&&time.before(getEndTime());
    }

    public boolean hasSameStartOrSameEnd(TimesheetDataEntry other) {
        return other.getStartTime().equals(getStartTime())|| other.getEndTime().equals(getEndTime());
    }

    public static interface Listener {
        void timeChanged();

        void runningChanged();

        void categoryChanged();

        void notesChanged();

        void selectionChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyTimeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).timeChanged();
        }
    }

    public void setEndTime( Date endTime ) {
        if ( !this.endTime.equals( endTime ) ) {
            this.endTime = endTime;
            notifyTimeChanged();
        }
    }

    public void setCategory( String category ) {
        if ( !this.category.equals( category ) ) {
            this.category = category;
            notifyCategoryChanged();
        }
    }

    private void notifyCategoryChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).categoryChanged();
        }
    }

    public void setNotes( String notes ) {
        if ( !this.notes.equals( notes ) ) {
            this.notes = notes;
            notifyNotesChanged();
        }
    }

    private void notifyNotesChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).notesChanged();
        }
    }

    public static class Adapter implements Listener {
        public void timeChanged() {
        }

        public void runningChanged() {
        }

        public void categoryChanged() {
        }

        public void notesChanged() {
        }

        public void selectionChanged() {
        }
    }

    public String toString() {
        return toCSV();
    }
}
