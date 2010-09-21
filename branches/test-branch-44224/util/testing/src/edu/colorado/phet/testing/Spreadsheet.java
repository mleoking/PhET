package edu.colorado.phet.testing;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Spreadsheet {
    private ArrayList entries = new ArrayList();

    public Spreadsheet( Entry[] entries ) {
        this.entries.addAll( Arrays.asList( entries ) );
    }

    public HashSet getUniqueValues( String key ) {
        return new HashSet( Arrays.asList( listValues( key ) ) );
    }

    public String[] listValues( String key ) {
        ArrayList values = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            values.add( entry.getValue( key ) );
        }
        return (String[]) values.toArray( new String[values.size()] );
    }

    public int size() {
        return entries.size();
    }

    public Spreadsheet getLowercaseSubstringMatches( final String key, final String value ) {
        return getMatches( new Spreadsheet.Matcher() {
            public boolean matches( Entry e ) {
                return e.getValue( key ).toLowerCase().indexOf( value ) >= 0;
            }
        } );
    }

    public Spreadsheet sortByDate() {
        ArrayList entryArray = new ArrayList();
        entryArray.addAll( entries );
        Collections.sort( entryArray, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                Entry a = (Entry) o1;
                Entry b = (Entry) o2;
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "M/d/yyyy H:m:s" );
                    Date adate = simpleDateFormat.parse( a.getValue( "Timestamp" ) );
                    Date bdate = simpleDateFormat.parse( b.getValue( "Timestamp" ) );
                    return adate.compareTo( bdate );
                }
                catch( ParseException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }

            }
        } );
        return new Spreadsheet( (Entry[]) entryArray.toArray( new Entry[entryArray.size()] ) );
    }

    public Spreadsheet keepColumns( String[] strings ) {
        ArrayList reducedEntries=new ArrayList( );
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            reducedEntries.add(entry.keepColumns(strings));
        }

        return new Spreadsheet( (Entry[]) reducedEntries.toArray( new Entry[reducedEntries.size()] ) );
    }

    public static interface Matcher {
        boolean matches( Entry e );
    }

    public Spreadsheet getMatches( Matcher matcher ) {
        ArrayList matches = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            if ( matcher.matches( entry ) ) {
                matches.add( entry );
            }
        }
        return new Spreadsheet( (Entry[]) matches.toArray( new Entry[matches.size()] ) );
    }

    public Spreadsheet getMatches( String key, String value ) {
        ArrayList matches = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            if ( entry.getValue( key ).equals( value ) ) {
                matches.add( entry );
            }
        }
        return new Spreadsheet( (Entry[]) matches.toArray( new Entry[matches.size()] ) );
    }

    public static Spreadsheet load( File file ) throws IOException {
        CSVReader reader = new CSVReader( new FileReader( file ) );
        List myEntries = reader.readAll();
        ArrayList entries = new ArrayList();

        //start at 1 since 0 is header row
        for ( int i = 1; i < myEntries.size(); i++ ) {
            Entry entry = new Entry( (String[]) myEntries.get( 0 ), (String[]) myEntries.get( i ) );
            entries.add( entry );
        }


        Spreadsheet spreadsheet = new Spreadsheet( (Entry[]) entries.toArray( new Entry[entries.size()] ) );
        return spreadsheet;
    }

    public String toString() {
        String s = "";
        Entry key = (Entry) entries.get( 0 );
        String[] keys = key.getKeys();
        for ( int i = 0; i < keys.length; i++ ) {
            String k = keys[i];
            s += k + "\t";
        }
        s += "\n";
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry e = (Entry) entries.get( i );
            for ( int j = 0; j < keys.length; j++ ) {
                String key1 = keys[j];
                s += e.getValue( key1 ) + "\t";
            }
            s += "\n";
        }
        return s;
    }
}
