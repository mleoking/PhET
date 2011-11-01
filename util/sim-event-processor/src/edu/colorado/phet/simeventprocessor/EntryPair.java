// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * @author Sam Reid
 */
public class EntryPair extends Pair<Entry, Entry> implements Comparable<EntryPair> {
    public Long elapsedTime;

    public EntryPair( Entry _1, Entry _2 ) {
        super( _1, _2 );
        elapsedTime = _2.time - _1.time;
    }

    public int compareTo( EntryPair o ) {
        return elapsedTime.compareTo( o.elapsedTime );
    }

    @Override public String toString() {
        return "Time between entries: " + new DecimalFormat( "0.00" ).format( ( _2.time - _1.time ) / 1000.0 ) + " sec , firstEntry = " + _1 + ", secondEntry = " + _2;
    }
}
