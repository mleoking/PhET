// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import com.google.code.morphia.annotations.Indexed;

/**
 * @author Sam Reid
 */
public class LatestIndex {
    private @Indexed SessionID sessionID;
    private int index;

    public LatestIndex( SessionID sessionID, int index ) {
        this.sessionID = sessionID;
        this.index = index;
    }

    public LatestIndex() {
    }

    public int getIndex() {
        return index;
    }
}
