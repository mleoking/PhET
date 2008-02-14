package edu.colorado.phet.reids.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        this.startTime = startTime;
        notifyListener();
    }

    public long getElapsedTimeMillis() {
        if ( endTime != null && startTime != null ) {
            return ( endTime.getTime() - startTime.getTime() );
        }
        else {
            return 0;
        }
    }

    public boolean isTimeEntrySet() {
        return endTime != null && startTime != null;
    }

    public boolean isRunning() {
        return running;
    }

    public String toCSV() {
        return startTime.toString() + "," + endTime.toString() + "," + TimesheetApp.toString( getElapsedTimeMillis() ) + "," + category + "," + notes;
    }

    public static String getCSVHeader() {
        return "Start Time" + "," + "End Time" + "," + "Elapsed Time" + "," + "Category" + "," + "Notes";
    }

    public static TimesheetDataEntry parseCSV( String line ) {
        StringTokenizer st = new StringTokenizer( line, "," );
        DateFormat d = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        try {
            final Date start = d.parse( st.nextToken() );
            final Date end = d.parse( st.nextToken() );
            st.nextToken();
            //everything after 4th comma is notes (which may have commas)
            int index1 = line.indexOf( ',', 0 );
            int index2 = line.indexOf( ',', index1+1 );
            int index3 = line.indexOf( ',', index2+1 );
            int index4 = line.indexOf( ',', index3+1 );
            return new TimesheetDataEntry( start, end, st.nextToken(), line.substring( index4 +1) );
        }
        catch( ParseException e ) {
            e.printStackTrace();
            throw new RuntimeException( e);
        }
    }

    public static interface Listener {
        void changed();

        void runningChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).changed();
        }
    }

    public void setEndTime( Date endTime ) {
        this.endTime = endTime;
        notifyListener();
    }

    public void setCategory( String category ) {
        this.category = category;
        notifyListener();
    }

    public void setNotes( String notes ) {
        this.notes = notes;
        notifyListener();
    }
}
