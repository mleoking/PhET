package edu.colorado.phet.reids.admin;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import edu.colorado.phet.build.FileUtils;

/**
 * Created by: Sam
 * Feb 11, 2008 at 8:03:40 AM
 */
public class TimesheetUtil {
    public static void main( String[] args ) throws IOException, ParseException {
        File csv = new File( "C:\\Users\\Sam\\Desktop\\phet1.csv" );
        String expectedHeader = "Date,Id,Task Name,Used Time";
        String file = FileUtils.loadFileAsString( csv );
        StringTokenizer lineTokenizer = new StringTokenizer( file, "\n" );
        String header = lineTokenizer.nextToken();
        ArrayList entries = new ArrayList();
        while ( lineTokenizer.hasMoreTokens() ) {
            String line = lineTokenizer.nextToken();
            entries.add( Entry.parseEntry( line ) );
        }
        System.out.println( "entries = " + entries );
    }

    public static class Entry {
        private Date date;
        private String taskName;
        private double timeHours;

        public Entry( Date date, String taskName, double timeHours ) {
            this.date = date;
            this.taskName = taskName;
            this.timeHours = timeHours;
        }

        public String toString() {
            return "date=" + date + ", taskName=" + taskName + ", timeHours=" + timeHours;
        }

        public static Entry parseEntry( String line ) throws ParseException {
            StringTokenizer st = new StringTokenizer( line, "," );

            String date = st.nextToken();
            System.out.println( "date = " + date );

            int id = Integer.parseInt( st.nextToken() );
            System.out.println( "id=" + id );

            String taskName = st.nextToken();
            System.out.println( "taskName = " + taskName );

            String usedTime = st.nextToken();
            System.out.println( "usedTime = " + usedTime );

            StringTokenizer dateTok = new StringTokenizer( date, "-" );
            GregorianCalendar gc = new GregorianCalendar( Integer.parseInt( dateTok.nextToken() ), Integer.parseInt( dateTok.nextToken() ), Integer.parseInt( dateTok.nextToken() ) );
            return new Entry( gc.getTime(), taskName, Double.parseDouble( usedTime ) );
        }
    }

}
