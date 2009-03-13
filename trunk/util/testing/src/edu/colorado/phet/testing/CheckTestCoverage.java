package edu.colorado.phet.testing;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CheckTestCoverage {

    public static void main( String[] args ) throws IOException {

        //todo: use google docs api to auto-download latest version of spreadsheet
        //http://code.google.com/apis/documents/docs/2.0/developers_guide_java.html

        //for now, just download manually

        Spreadsheet spreadsheet = Spreadsheet.load( new File( "C:\\Users\\Owner\\Desktop\\iom.csv" ) );
        List testIDs = Arrays.asList( spreadsheet.listValues( "Test ID" ) );
        HashSet testIDSet = new HashSet( testIDs );

        System.out.println( "Test IDs = " + testIDSet );
        System.out.println( "Testers = " + spreadsheet.getUniqueValues( "Tester" ) );
        System.out.println( "Scenarios = " + spreadsheet.getUniqueValues( "Scenario" ) );

        List sorted = new ArrayList( testIDSet );
        Collections.sort( sorted );
        for ( Iterator iterator = sorted.iterator(); iterator.hasNext(); ) {
            String s = (String) iterator.next();
            analyzeTest( spreadsheet, s );
        }
    }

    private static void analyzeTest( Spreadsheet spreadsheet, final String testID ) {
        Spreadsheet entries = spreadsheet.getMatches( "Test ID", testID );
        System.out.println( testID );
        System.out.println( "\tTotal tests: " + entries.size() );

        Spreadsheet linuxTests = entries.getLowercaseSubstringMatches( "OS", "linux" );
        Spreadsheet macTests = entries.getLowercaseSubstringMatches( "OS", "mac" );
        Spreadsheet windowTests = entries.getLowercaseSubstringMatches( "OS", "windows" );
        System.out.println( "\tMac tests: " + macTests.size() );
        System.out.println( "\tWindows tests: " + windowTests.size() );
        System.out.println( "\tLinux tests: " + linuxTests.size() );

//        Spreadsheet fails = entries.getMatches( "Test Results", "FAIL" );
        Spreadsheet sorted = entries.sortByDate().keepColumns( new String[]{"Timestamp", "Test Results", "OS", "Scenario", "Java Version", "Flash Version"} );
        System.out.println( sorted );

    }


}
