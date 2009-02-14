package edu.colorado.phet.testing;

import java.util.ArrayList;
import java.util.Arrays;

public class Spreadsheet {
    private ArrayList entries = new ArrayList();

    public Spreadsheet( Entry[] entries ) {
        this.entries.addAll( Arrays.asList( entries ) );
    }

    public String[] listValues( String key ) {
        ArrayList values = new ArrayList();
        for ( int i = 0; i < entries.size(); i++ ) {
            Entry entry = (Entry) entries.get( i );
            values.add( entry.getValue( key ) );
        }
        return (String[]) values.toArray( new String[values.size()] );
    }
}
