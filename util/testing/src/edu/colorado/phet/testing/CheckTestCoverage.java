package edu.colorado.phet.testing;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CheckTestCoverage {

    public static void main( String[] args ) throws IOException {

        //todo: use google docs api to auto-download latest version of spreadsheet
        //http://code.google.com/apis/documents/docs/2.0/developers_guide_java.html

        //for now, just download manually

        Spreadsheet spreadsheet = Spreadsheet.load( new File( "C:\\Users\\Owner\\Desktop\\iom.csv" ) );
        List testIDs = Arrays.asList( spreadsheet.listValues( "Test ID" ) );
        HashSet testIDSet = new HashSet( testIDs );
        System.out.println( "Test IDs = " + testIDs );
        System.out.println( "Testers = " + spreadsheet.getUniqueValues( "Tester" ) );
        System.out.println( "Scenarios = " + spreadsheet.getUniqueValues( "Scenario" ) );

        for ( Iterator iterator = testIDSet.iterator(); iterator.hasNext(); ) {
            String s = (String) iterator.next();
            analyzeTest( spreadsheet, s );
        }
    }

    private static void analyzeTest( Spreadsheet spreadsheet, final String testID ) {
        Spreadsheet entries = spreadsheet.getMatches( new Spreadsheet.Matcher() {
            public boolean matches( Entry e ) {
                return e.getValue( "Test ID" ).equals( testID );
            }
        } );
        System.out.println( testID + "\n\tnumTests=" + entries.size() );
        Spreadsheet macTests = entries.getMatches( new Spreadsheet.Matcher() {
            public boolean matches( Entry e ) {
                return e.getValue( "OS" ).toLowerCase().indexOf( "mac" ) >= 0;
            }
        } );
        System.out.println( "\tMac tests: " + macTests.size() );

    }


}
