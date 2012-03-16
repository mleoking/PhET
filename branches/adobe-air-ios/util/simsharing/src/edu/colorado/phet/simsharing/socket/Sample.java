// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;

/**
 * @author Sam Reid
 */
public class Sample<T extends SimState> implements Serializable {
    public final T state;
    public final int totalSampleCount;

    public Sample( T state, int totalSampleCount ) {
        this.state = state;
        this.totalSampleCount = totalSampleCount;
    }
}