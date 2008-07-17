package edu.colorado.phet.reids.admin.util;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import edu.colorado.phet.reids.admin.TimesheetData;

/**
 * Created by: Sam
 * Feb 15, 2008 at 9:18:44 AM
 */
public class ConvertBatch {
    public static void main( String[] args ) throws IOException, ParseException {
        String[] files = new String[]{
                "C:\\Users\\Sam\\Desktop\\csvimport\\timecard-1-1-2008.csv",
                "C:\\Users\\Sam\\Desktop\\csvimport\\timecard-1-7-2008.csv",
                "C:\\Users\\Sam\\Desktop\\csvimport\\timecard-1-14-2008.csv",
                "C:\\Users\\Sam\\Desktop\\csvimport\\timecard-1-21-2008.csv",
                "C:\\Users\\Sam\\Desktop\\csvimport\\timecard-1-28-2008.csv",
                "C:\\Users\\Sam\\Desktop\\csvimport\\timecard-2-4-2008.csv"
        };
        TimesheetData timesheetData = new TimesheetData();
        for ( int i = 0; i < files.length; i++ ) {
            TimesheetData d = ConvertExcelCSV.load( new File( files[i] ) );
            timesheetData.addAll( d );
        }
        timesheetData.addAll( ConvertTimecult.readData( new File( "C:\\Users\\Sam\\Desktop\\phet-timesheet (2).tmt" ) ) );
        FileUtils.writeString( new File( "C:\\Users\\Sam\\Desktop\\phet-timesheet-converted.csv" ), timesheetData.toCSV() );
    }
}
