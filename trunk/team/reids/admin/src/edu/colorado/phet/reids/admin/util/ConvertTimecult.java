package edu.colorado.phet.reids.admin.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.colorado.phet.reids.admin.TimesheetData;
import edu.colorado.phet.reids.admin.TimesheetDataEntry;

/**
 * Created by: Sam
 * Feb 15, 2008 at 9:25:32 AM
 */
public class ConvertTimecult {
    public static void main( String[] args ) throws IOException {
        File f = new File( "C:\\Users\\Sam\\Desktop\\phet-timesheet (2).tmt" );
        TimesheetData data = readData( f );
//        FileUtils.writeString( "C:\\Users\\Sam\\Desktop\\phet-timesheet-timecult-out.txt",data.toCSV() );
    }

    public static TimesheetData readData( File f ) throws IOException {
        String text = FileUtils.loadFileAsString( f );

        HashMap map = getTaskMap( text );
        System.out.println( "map = " + map );
        TimesheetData data = getTimesheetData( map, text );
        System.out.println( "data = " + data );
        return data;
    }

    private static TimesheetData getTimesheetData( HashMap map, String text ) {
        TimesheetData data = new TimesheetData();
        StringTokenizer lineTok = new StringTokenizer( text, "\n\r" );
        while ( lineTok.hasMoreTokens() ) {
            String line = lineTok.nextToken();

            if ( line.startsWith( "<timeRec duration=" ) ) {
                long duration = getLong( "duration", line );
                long startTime = getLong( "startTime", line );
                String notes = getString( "notes", line );
                int taskID = getInt( "taskId", line );
                data.addEntry( new TimesheetDataEntry( new Date( startTime ), new Date( startTime + duration ), (String) map.get( new Integer( taskID ) ), notes ) );
            }
        }
        return data;
    }

    private static int getInt( String s, String line ) {
        return Integer.parseInt( getString( s, line ) );
    }

    private static long getLong( String s, String line ) {
        return Long.parseLong( getString( s, line ) );
    }

    private static String getString( String s, String line ) {
        String key = s + "=\"";
        int index = line.indexOf( key );
        String sub = line.substring( index + key.length() );
        int closeQuote = sub.indexOf( "\"" );
        return sub.substring( 0, closeQuote );
    }

    private static HashMap getTaskMap( String text ) {

        StringTokenizer lineTok = new StringTokenizer( text, "\n\r" );
        HashMap taskMap = new HashMap();
        while ( lineTok.hasMoreTokens() ) {
            String line = lineTok.nextToken();

            if ( line.startsWith( "<task id=" ) && !line.startsWith( "<task id=\"idle" ) ) {
                StringTokenizer ste = new StringTokenizer( line, "\"" );
                ste.nextToken();
                int number = Integer.parseInt( ste.nextToken() );
                ste.nextToken();
                String category = ste.nextToken();
                taskMap.put( new Integer( number ), category );
            }

        }
        return taskMap;
    }
}
