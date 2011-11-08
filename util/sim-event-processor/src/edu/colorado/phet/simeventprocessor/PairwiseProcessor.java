// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * @author Sam Reid
 */
public class PairwiseProcessor implements IProguardKeepClass {
    public ArrayList<EntryPair> process( JavaEventLog eventLog ) {
        ArrayList<EntryPair> entries = new ArrayList<EntryPair>();
        JavaEntry last = null;
        for ( JavaEntry eventLine : eventLog ) {
            if ( last != null ) {
                entries.add( new EntryPair( last, eventLine ) );
            }
            last = eventLine;
        }

        return entries;
    }
}