package edu.colorado.phet.testing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CheckTestCoverage {

    public static void main( String[] args ) throws IOException {

        //todo: use google docs api to auto-download latest version of spreadsheet
        //http://code.google.com/apis/documents/docs/2.0/developers_guide_java.html

        //for now, just download manually

        Spreadsheet spreadsheet = Spreadsheet.load( new File( "C:\\Users\\Owner\\Desktop\\iom.csv" ) );
        System.out.println( "Test IDs = " + Arrays.asList( spreadsheet.listValues( "Test ID" ) ) );
        System.out.println( "Testers = " + spreadsheet.getUniqueValues( "Tester" ) );
        System.out.println( "Scenarios = " + spreadsheet.getUniqueValues( "Scenario" ) );

        showData( "Test Results", "PASS", spreadsheet );
        showData( "Test Results", "FAIL", spreadsheet );

    }

    private static void showData( String key, String value, Spreadsheet spreadsheet ) {
        Entry[] matches = spreadsheet.getMatches( key, value );
        System.out.println( value + " [" + matches.length + "]: " + Arrays.asList( getValues( matches, "Test ID" ) ) );
    }

    private static String[] getValues( Entry[] matches, String key ) {
        HashSet collection = new HashSet();
        for ( int i = 0; i < matches.length; i++ ) {
            collection.add( matches[i].getValue( key ) );
        }
        return (String[]) collection.toArray( new String[collection.size()] );
    }
}
