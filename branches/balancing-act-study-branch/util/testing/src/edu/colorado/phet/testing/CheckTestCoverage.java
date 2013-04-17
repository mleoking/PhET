package edu.colorado.phet.testing;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CheckTestCoverage {

    public static void main( String[] args ) throws IOException {
        if ( args.length != 1 ) {
            throw new RuntimeException( "Need to specify as an argument the absolute path of the CSV file downloaded from google docs" );
        }

        //todo: use google docs api to auto-download latest version of spreadsheet
        //http://code.google.com/apis/documents/docs/2.0/developers_guide_java.html

        //for now, just download manually

        Spreadsheet spreadsheet = Spreadsheet.load( new File( args[0] ) );
        List testIDs = Arrays.asList( spreadsheet.listValues( "Test ID" ) );
        HashSet testIDSet = new HashSet( testIDs );

//        System.out.println( "Test IDs = " + testIDSet );
//        System.out.println( "Testers = " + spreadsheet.getUniqueValues( "Tester" ) );
//        System.out.println( "Scenarios = " + spreadsheet.getUniqueValues( "Scenario" ) );
//        System.out.println( "" );

        printCoverageHeadings();
        List sorted = new ArrayList( testIDSet );
        Collections.sort( sorted );
        for ( Iterator iterator = sorted.iterator(); iterator.hasNext(); ) {
            String s = (String) iterator.next();
            printCoverageLine(spreadsheet, s);
        }
    }

    private static void analyzeTest( Spreadsheet spreadsheet, final String testID ) {
        Spreadsheet entries = spreadsheet.getMatches( "Test ID", testID );
        System.out.println( testID );
        System.out.println( "\tTotal tests: " + entries.size() );

        Spreadsheet linuxTests = entries.getLowercaseSubstringMatches( "OS", "linux" );
        Spreadsheet macTestsTenFour = entries.getLowercaseSubstringMatches( "OS", "mac os x 10.4" );
        Spreadsheet macTestsTenFive = entries.getLowercaseSubstringMatches( "OS", "mac os x 10.5" );
        Spreadsheet windowTests = entries.getLowercaseSubstringMatches( "OS", "windows" );
        System.out.println( "\tMac 10.4 tests: " + macTestsTenFour.size() );
        System.out.println( "\tMac 10.5 tests: " + macTestsTenFive.size() );
        System.out.println( "\tWindows tests: " + windowTests.size() );
        System.out.println( "\tLinux tests: " + linuxTests.size() );

//        Spreadsheet fails = entries.getMatches( "Test Results", "FAIL" );
//        Spreadsheet sorted = entries.sortByDate().keepColumns( new String[]{"Timestamp", "Test Results", "OS", "Scenario", "Java Version", "Flash Version"} );
//        System.out.println( sorted );

    }

    /**
     * Output the headings for an spreadsheet that depicts test coverage in
     * CSV (comma separated) format.
     */
    private static void printCoverageHeadings() {
        System.out.print( "Test ID," );
        
        // OS
        System.out.print( " Windows XP," );
        System.out.print( " Windows Vista," );
        System.out.print( " Mac 10.4," );
        System.out.print( " Mac 10.5," );
        System.out.print( " Linux," );
        
        // Java versions
        System.out.print( " Java 1.6," );
        System.out.print( " Java 1.5," );
        System.out.print( " Java 1.4," );
        
        // Flash versions
        System.out.print( " Flash 10," );
        System.out.print( " Flash 9," );
        System.out.print( " Flash 8," );
        
        // Deployment
        System.out.print( " Installer," );
        System.out.print( " Individually Downloaded," );
        System.out.print( " Web site," );
        
        // Language
        System.out.print( " Spanish" );
        
        System.out.print("\n");
    }
    
    /**
     * Output a line of test coverage information in CSV (comma separated) format.
     * 
     * @param spreadsheet
     * @param testID
     */
    private static void printCoverageLine( Spreadsheet spreadsheet, final String testID ) {
    	
    	// MAINTENANCE NOTE! These columns must align with the headings.
        Spreadsheet entries = spreadsheet.getMatches( "Test ID", testID );

        // OS tests
        Spreadsheet windowsXpTests = entries.getLowercaseSubstringMatches( "OS", "windows xp" );
        Spreadsheet windowsVistaTests = entries.getLowercaseSubstringMatches( "OS", "windows vista" );
        Spreadsheet macTestsTenFourTests = entries.getLowercaseSubstringMatches( "OS", "mac os x 10.4" );
        Spreadsheet macTestsTenFiveTest = entries.getLowercaseSubstringMatches( "OS", "mac os x 10.5" );
        Spreadsheet linuxTests = entries.getLowercaseSubstringMatches( "OS", "linux" );
        
        // Java version tests
        Spreadsheet javaOneSixTests = entries.getLowercaseSubstringMatches( "Java Version", "1.6.0_" );
        Spreadsheet javaOneFiveTests = entries.getLowercaseSubstringMatches( "Java Version", "1.5.0_" );
        Spreadsheet javaOneFourTests = entries.getLowercaseSubstringMatches( "Java Version", "1.4.2_" );
        
        // Flash tests
        Spreadsheet flashTenTests = entries.getLowercaseSubstringMatches( "Flash Version", "10.0" );
        Spreadsheet flashNineTests = entries.getLowercaseSubstringMatches( "Flash Version", "9.0" );
        Spreadsheet flashEightTests = entries.getLowercaseSubstringMatches( "Flash Version", "8.0" );
        
        // Deployment scenario
        Spreadsheet installerTests = entries.getLowercaseSubstringMatches( "Scenario", "full local phet" );
        Spreadsheet downloadedTests = entries.getLowercaseSubstringMatches( "Scenario", "downloaded" );
        Spreadsheet webSiteTests = entries.getLowercaseSubstringMatches( "Scenario", "phet web site" );
        
        // Check if done in spanish (not perfectly reliable)
        Spreadsheet spanishTests = entries.getLowercaseSubstringMatches( "Comments", "spanish" );
        
        // Print the grid.
        System.out.print(testID + ", ");
        
        System.out.print(windowsXpTests.size() + ", ");
        System.out.print(windowsVistaTests.size() + ", ");
        System.out.print(macTestsTenFourTests.size() + ", ");
        System.out.print(macTestsTenFiveTest.size() + ", ");
        System.out.print(linuxTests.size() + ", ");
        
        System.out.print(javaOneSixTests.size() + ", ");
        System.out.print(javaOneFiveTests.size() + ", ");
        System.out.print(javaOneFourTests.size() + ", ");
        
        System.out.print(flashTenTests.size() + ", ");
        System.out.print(flashNineTests.size() + ", ");
        System.out.print(flashEightTests.size() + ", ");
        
        System.out.print(installerTests.size() + ", ");
        System.out.print(downloadedTests.size() + ", ");
        System.out.print(webSiteTests.size() + ", ");

        System.out.print(spanishTests.size());
        
        System.out.print("\n");
    }

}
