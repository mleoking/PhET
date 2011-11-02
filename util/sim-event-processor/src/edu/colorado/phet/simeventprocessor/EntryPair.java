// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * @author Sam Reid
 */
public class EntryPair extends Pair<Entry, Entry> implements Comparable<EntryPair> {
    public Long elapsedTimeMillis;

    //Elapsed time in seconds
    public final double time;

    public final String brief;

    public EntryPair( Entry _1, Entry _2 ) {
        super( _1, _2 );
        elapsedTimeMillis = _2.timeMilliSec - _1.timeMilliSec;
        time = elapsedTimeMillis / 1000.0;

        brief = _1.brief() + " -> " + _2.brief();
    }

    public int compareTo( EntryPair o ) {
        return elapsedTimeMillis.compareTo( o.elapsedTimeMillis );
    }

    @Override public String toString() {
        return "Time between entries: " + new DecimalFormat( "0.00" ).format( ( _2.timeMilliSec - _1.timeMilliSec ) / 1000.0 ) + " sec , firstEntry = " + _1 + ", secondEntry = " + _2;
    }
}
