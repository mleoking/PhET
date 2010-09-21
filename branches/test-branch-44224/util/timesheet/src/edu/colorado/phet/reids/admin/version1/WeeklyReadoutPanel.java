package edu.colorado.phet.reids.admin.version1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 15, 2008 at 8:35:41 PM
 */
public class WeeklyReadoutPanel extends JPanel {
    private JLabel readoutLabel;
    private String message;
    private TimesheetData data;

    public WeeklyReadoutPanel( String message, TimesheetData data ) {
        this.message = message;
        this.data = data;

        readoutLabel = new JLabel();
        add( readoutLabel );

        data.addListener( new TimesheetData.Adapter() {
            public void timeChanged() {
                updateReadoutLabel();
            }
        } );
        updateReadoutLabel();
    }

    private void updateReadoutLabel() {
        Calendar startOfWorkMondayMorning = getMostRecentMondayMorning();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "M/dd/yy" );
        String f = simpleDateFormat.format( startOfWorkMondayMorning.getTime() );
        TimesheetData cat = selectData( data );
        TimesheetData t = cat.getEntriesAfter( startOfWorkMondayMorning.getTime() );
        long time = t.getTotalTimeMillis();
        readoutLabel.setText( "" + message + " as of (" + f + ") " + TimesheetApp.toString( time ) );
    }

    protected TimesheetData selectData( TimesheetData data ) {
        return data;
    }

    public static Calendar getMostRecentMondayMorning() {
        Calendar now = new GregorianCalendar();
        int dayOfWeek = now.get( Calendar.DAY_OF_WEEK );
        //monday=2
        int daysSinceMonday = dayOfWeek - 2;
        if(daysSinceMonday<0){//since -1 on Sunday 
            daysSinceMonday+=7;
        }
        Calendar startOfWorkMondayMorning = new GregorianCalendar( now.get( Calendar.YEAR ), 1, 1 );
        startOfWorkMondayMorning.set( Calendar.DAY_OF_YEAR, now.get( Calendar.DAY_OF_YEAR ) - daysSinceMonday );
        startOfWorkMondayMorning.set( Calendar.HOUR_OF_DAY, 0 );
        startOfWorkMondayMorning.set( Calendar.MINUTE, 0 );
        startOfWorkMondayMorning.set( Calendar.SECOND, 0 );
        return startOfWorkMondayMorning;
    }

    public static void main( String[] args ) {
        new WeeklyReadoutPanel( "hello", new TimesheetData() );
    }
}
