package edu.colorado.phet.testing;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckTestCoverage {

    public static void main( String[] args ) throws IOException {
        CSVReader reader = new CSVReader( new FileReader( new File( "C:\\Users\\Owner\\Desktop\\iom.csv" ) ) );
        List myEntries = reader.readAll();
        System.out.println( "myEntries = " + myEntries );
        ArrayList entries = new ArrayList();
        for ( int i = 1; i < myEntries.size(); i++ ) {
            String[] strings = (String[]) myEntries.get( i );
            System.out.println( Arrays.asList( strings ) );

            Entry entry = new Entry( (String[]) myEntries.get( 0 ), (String[]) myEntries.get( i ) );
            entries.add( entry );
        }

        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            System.out.println( "entry[" + i + "]=" + entry );
        }


        Spreadsheet spreadsheet = new Spreadsheet( (Entry[]) entries.toArray( new Entry[entries.size()] ) );
        String[] keys = spreadsheet.listValues( "Test ID" );
        System.out.println( "Arrays.asList(keys = " + Arrays.asList( keys ) );
    }
}
