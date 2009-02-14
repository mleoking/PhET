package edu.colorado.phet.testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CheckTestCoverage {

    public static void main( String[] args ) throws IOException {

        //todo: use google docs api to auto-download latest version of spreadsheet
        //http://code.google.com/apis/documents/docs/2.0/developers_guide_java.html

        //for now, just download manually

        Spreadsheet spreadsheet = Spreadsheet.load( new File( "C:\\Users\\Owner\\Desktop\\iom.csv" ) );
        String[] keys = spreadsheet.listValues( "Test ID" );
        System.out.println( "Declared Tests = " + Arrays.asList( keys ) );

        showData( "Test Results", "PASSED", spreadsheet );
        showData( "Test Results", "FAILED", spreadsheet );
        showData( "Test Results", "", spreadsheet );

    }

    private static void showData( String key, String value, Spreadsheet spreadsheet ) {
        Entry[] matches = spreadsheet.getMatches( key, value );
        System.out.println( value + " [" + matches.length + "]: " + Arrays.asList( getValues( matches, "Test ID" ) ) );
    }

    private static String[] getValues( Entry[] matches, String key ) {
        ArrayList list = new ArrayList();
        for ( int i = 0; i < matches.length; i++ ) {
            list.add( matches[i].getValue( key ) );
        }
        return (String[]) list.toArray( new String[list.size()] );
    }
}
