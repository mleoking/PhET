package edu.colorado.phet.reids.admin.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.colorado.phet.reids.admin.TimesheetData;
import edu.colorado.phet.reids.admin.TimesheetDataEntry;

/**
 * Created by: Sam
 * Feb 15, 2008 at 8:26:04 AM
 */
public class ConvertExcelCSV {
    public static void main( String[] args ) throws IOException, ParseException {
        File src = new File( "C:\\Users\\Sam\\Desktop\\csvimport" );
        File dst = new File( "C:\\Users\\Sam\\Desktop\\csv-processed" );
        dst.mkdirs();
        for ( int i = 0; i < src.listFiles().length; i++ ) {
            File f = src.listFiles()[i];
            TimesheetData converted = load( f );
            FileUtils.writeString( new File( dst, f.getName() ), converted.toCSV() );
        }
    }

    public static TimesheetData load( File file ) throws ParseException, IOException {
        return convert( FileUtils.loadFileAsString( file ) );
    }

    public static TimesheetData convert( String input ) throws ParseException {
        TimesheetData data = new TimesheetData();
        StringTokenizer lineTokenizer = new StringTokenizer( input, "\n" );
        while ( lineTokenizer.hasMoreTokens() ) {
            String line = lineTokenizer.nextToken();
            if ( isDataEntry( line ) ) {
                data.addEntry( toEntry( line ) );
            }
        }
        return data;
    }

    private static TimesheetDataEntry toEntry( String line ) throws ParseException {
        System.out.println( "line = " + line );
        StringTokenizer st = new StringTokenizer( line, "," );
        String startTime = st.nextToken();
        String endTime = st.nextToken();
        String category = st.nextToken();
        int commaIndex = line.indexOf( ',', 0 );
        commaIndex = line.indexOf( ',', commaIndex + 1 );
        commaIndex = line.indexOf( ',', commaIndex + 1 );
//        commaIndex = line.indexOf( ',', commaIndex+1 );
        String notes = line.substring( commaIndex + 1 );
        notes = parseNotes( notes );
        if ( notes.startsWith( "\"" ) && notes.endsWith( "\"" ) ) {
            notes = notes.substring( 1, notes.length() - 1 );
        }
        notes = removeExtraStringAtEnd( notes );
//        int lastComma=notes.lastIndexOf( ',' );
//        notes=notes.substring( 0,lastComma );
//        notes=removeExtraStringAtEnd( notes );
        DateFormat dateFormat = new SimpleDateFormat();
        final TimesheetDataEntry dataEntry = new TimesheetDataEntry( dateFormat.parse( startTime ), dateFormat.parse( endTime ), category, notes );
        System.out.println( "dataEntry = " + dataEntry );
        return dataEntry;
    }

    private static String removeExtraStringAtEnd( String notes ) {
        while ( notes.endsWith( "," ) || notes.endsWith( "\r" ) || notes.endsWith( " " ) ) {
            notes = notes.substring( 0, notes.length() - 1 );
        }
        return notes;
    }

    private static boolean isDataEntry( String line ) {
        if ( line.startsWith( "#" ) ) {
            return false;
        }
        try {
            Integer.parseInt( line.substring( 0, 1 ) );
            return true;
        }
        catch( NumberFormatException e ) {
            return false;
        }
    }

    public static String parseNotes( String string ) {
        Pattern pattern = Pattern.compile( ",\\d+:\\d+:\\d+" );
//        Pattern pattern = Pattern.compile( "worked" );
        Matcher m = pattern.matcher( string );
        boolean found = m.find();
        int start = m.start();
//        System.out.println( "start = " + start );
        String substring = string.substring( 0, start );
//        System.out.println( "substring = " + substring );
        return substring;
    }
}
