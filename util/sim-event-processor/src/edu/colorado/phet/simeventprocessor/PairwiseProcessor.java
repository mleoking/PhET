// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class PairwiseProcessor {
    public ArrayList<EntryPair> process( EventLog eventLog ) {
        ArrayList<EntryPair> entries = new ArrayList<EntryPair>();
        Entry last = null;
        for ( Entry eventLine : eventLog ) {
            if ( last != null ) {
                entries.add( new EntryPair( last, eventLine ) );
            }
            last = eventLine;
        }

        return entries;
    }
}