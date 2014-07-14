package edu.colorado.phet.reids.admin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 24, 2010
 * Time: 11:47:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportFrame {
    private JFrame frame = new JFrame();
    private TimesheetModel selection;

    public ReportFrame( TimesheetModel selection ) {
        this.selection = selection;
        final Hashtable<String, Long> table = new Hashtable<String, Long>();
        for ( int i = 0; i < selection.getEntryCount(); i++ ) {
            Entry e = selection.getEntry( i );
            Long time = table.get( e.getCategory() );
            if ( time == null ) { time = 0L; }
            time = time + e.getElapsedSeconds();
            table.put( e.getCategory(), time );
        }
//        text += table;

        ArrayList<String> sortedKeys = new ArrayList<String>( table.keySet() );
        Collections.sort( sortedKeys, new Comparator<String>() {
            public int compare( String o1, String o2 ) {
                return -Double.compare( table.get( o1 ), table.get( o2 ) );//reverse so most used appear first
            }
        } );

        String text = "Total time: " + Util.secondsToElapsedTimeString( selection.getTotalTimeSeconds() ) + "\n";
        text += "Number of entries: " + selection.getEntryCount() + "\n";
        text += "Numer of categories: " + sortedKeys.size() + "\n";
//        text += selection.toCSV();

        text += "\n";
        for ( String key : sortedKeys ) {
            System.out.println( key + "\t" + Util.secondsToElapsedTimeString( table.get( key ) ) );
            text += key + "\t" + Util.secondsToElapsedTimeString( table.get( key ) ) + "\n";
        }
        text += "\n";
        for ( String key : sortedKeys ) {
            System.out.println( key + "\t" + Util.secondsToElapsedTimeString( table.get( key ) ) );
            text += key + "\t" + Util.secondsToElapsedTimeString( table.get( key ) ) + "\n";
            Entry[] taskLines = getTaskLines( key );
            for ( Entry taskLine : taskLines ) {
                if ( taskLine.getNotes().trim().length() > 0 ) {
                    text += "\t" + Util.secondsToElapsedTimeString( taskLine.getElapsedSeconds() ) + "\t" + taskLine.getNotes() + "\n";
                }
            }
        }

        text += "\n\n-------------------------------\n" +
                "By day:\n";
        text += getHoursPerDay( selection );

        frame.setContentPane( new JScrollPane( new JTextArea( text ) ) );
        frame.setSize( 800, 600 );
    }

    private String getHoursPerDay( TimesheetModel selection ) {
        int minDay = Integer.MAX_VALUE;
        int maxDay = Integer.MIN_VALUE;
        Hashtable<Integer, Long> timesPerDay = new Hashtable<Integer, Long>();
        for ( int i = 0; i < selection.getEntryCount(); i++ ) {
            Entry entry = selection.getEntry( i );
            entry.getStartDate();

            Calendar cal = Calendar.getInstance();
            cal.setTime( entry.getStartDate() );
            int day = cal.get( Calendar.DAY_OF_MONTH );
            minDay = Math.min( minDay, day );
            maxDay = Math.max( maxDay, day );

            if ( !timesPerDay.containsKey( day ) ) {
                timesPerDay.put( day, 0L );
            }
            timesPerDay.put( day, timesPerDay.get( day ) + entry.getElapsedSeconds() );

        }
        if ( minDay < 1 ) {
            throw new RuntimeException( "Weird day: " + minDay );
        }
        String s = "Times per day (reported in hours.hh)\n";
        for ( int i = 1; i <= maxDay; i++ ) {
            long seconds = timesPerDay.containsKey( i ) ? timesPerDay.get( i ) : 0;
            s += "" + i + ":\t" + new DecimalFormat( "0.00" ).format( seconds / 60.0 / 60.0 ) + "\n";
        }
        return s;
    }

    //Gets the tasks completed in the specified category, sorted by time taken
    private Entry[] getTaskLines( String category ) {
        Entry[] entries = selection.getEntriesForCategory( category );
        Arrays.sort( entries, new Comparator<Entry>() {
            public int compare( Entry o1, Entry o2 ) {
                return -Double.compare( o1.getElapsedSeconds(), o2.getElapsedSeconds() );
            }
        } );
        return entries;
    }

    public void setVisible( boolean b ) {
        frame.setVisible( b );
    }
}
