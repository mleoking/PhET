package edu.colorado.phet.reids.admin.version1;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by: Sam
 * Apr 21, 2008 at 3:14:35 PM
 */
public class DailyHours {
    public void show( TimesheetData data ) {
        Date dayOfStart = data.getMinTime();
        Date dayOfEnd = getNextDay( new Date() );
        String s="Date\tHours\n";
        for ( Date date = dayOfStart; date.before( dayOfEnd ); date = getNextDay( date ) ) {
            long ms = getSecondsWorkedOnDay( data, date );
//            DateFormat dateFormat=new SimpleDateFormat();
            if ( ms > 0 ) {
                s+=( DateFormat.getDateInstance().format( date ) + "\t" + new DecimalFormat("0.00").format( msToHours( ms ) ))+"\n";
            }
        }
        System.out.println( s );
        SummaryButton.displayText( "Daily Hours",s );
    }

    private double msToHours( long ms ) {
        return (double) ms / 1000.0 / 60.0 / 60.0;
    }

    private Date getNextDay( Date date ) {
        Calendar c = new GregorianCalendar();
        c.setTime( date );
        if ( c.get( Calendar.DAY_OF_YEAR ) > 354 ) {
//            new RuntimeException( "This code will probably fail near the end of the year" ).printStackTrace();
        }
        c.add( Calendar.DAY_OF_YEAR, 1 );//todo: fails over year changes
        return c.getTime();
    }

    private long getSecondsWorkedOnDay( TimesheetData data, Date dayOfYear ) {
        long sum = 0;
        for ( int i = 0; i < data.getNumEntries(); i++ ) {
            TimesheetDataEntry x = data.getEntry( i );
            if ( isDaySame( x, dayOfYear ) ) {
                sum += x.getElapsedTimeMillis();
            }
        }
        return sum;
    }

    private boolean isDaySame( TimesheetDataEntry entry, Date day ) {
        DateFormat formatter = DateFormat.getDateInstance();
        String a = formatter.format( entry.getStartTime() );
        String b = formatter.format( day );
//        System.out.println( "a=" + a );
//        System.out.println( "b=" + b );
        return a.equals( b );
    }

    public static final long dayMS = 1000 * 60 * 60 * 24;

    public static void main( String[] args ) {
        TimesheetData data = new TimesheetData();
        data.addEntry( new TimesheetDataEntry( new Date( 0 ), new Date( 1000 * 60 * 60 ), "mycat", "notes" ) );
        data.addEntry( new TimesheetDataEntry( new Date( dayMS ), new Date( dayMS + 1000 * 60 * 60 ), "mycat", "notes" ) );
        data.addEntry( new TimesheetDataEntry( new Date( dayMS * 2 ), new Date( dayMS * 2 + 1000 * 60 * 60 * 2 ), "mycat", "notes" ) );
        data.addEntry( new TimesheetDataEntry( new Date( dayMS * 3 ), new Date( dayMS * 3 + 1000 * 60 * 60 * 3 ), "mycat", "notes" ) );
        data.addEntry( new TimesheetDataEntry( new Date( dayMS * 4 ), new Date( (long) ( dayMS * 4 + 1000 * 60 * 60 * 4.75 ) ), "mycat", "notes" ) );
        new DailyHours().show( data );
    }
}
